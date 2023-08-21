package com.backend.api.wiki;


import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Category;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.CategoryRepository;
import com.backend.api.wiki.repository.SectionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@ComponentScan(basePackages = "wiki")
public class ArticleConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setFallbackPageable(PageRequest.of(0, 10, Sort.by("name").ascending()));
        resolvers.add(resolver);
    }

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customize() {
        return p -> p.setOneIndexedParameters(true);
    }

    @Bean
    CommandLineRunner commandLineRunner(SectionRepository sectionRepository, ArticleRepository articleRepository,
                                        CategoryRepository categoryRepository) {
        return args -> {
            Article tmpArticle = Article.builder().isPrivate(false).title("java tbd").build();

            Category tmpCategory = Category.builder().categoryName("catName").build();

            Section tmpSection = Section.builder().sectionOrder(0).depth(0).subsections(new HashSet<>()).build();
            tmpSection.setArticle(tmpArticle);
            Revision tmpRevision = Revision.builder().title("title").text("""
                    ## Java
                        final - cannot be modified or extended
                        static - the property belongs to the class not instance and is shared by all\s
                                                
                    """).build();

            Revision tmpRevision2 = Revision.builder().title("title").text("rev2").build();
            Revision tmpRevision3 = Revision.builder().title("title").text("re33").build();
            Revision tmpRevision4 = Revision.builder().title("title").text("re44").build();
            Section tmpSection2 = Section.builder().subsections(new HashSet<>()).latestRevision(tmpRevision2)
                    .revisions(List.of(tmpRevision2)).sectionOrder(1).depth(1).build();
            Section tmpSection3 = Section.builder().subsections(new HashSet<>()).latestRevision(tmpRevision3)
                    .revisions(List.of(tmpRevision3)).sectionOrder(1).depth(2).build();
            Section tmpSection4 = Section.builder().subsections(new HashSet<>()).latestRevision(tmpRevision4)
                    .revisions(List.of(tmpRevision4)).sectionOrder(2).depth(2).build();
            tmpSection2.setCreatedAt(LocalDateTime.now());
            tmpSection3.setCreatedAt(LocalDateTime.now());
            tmpSection3.setSuperSection(tmpSection2);
            tmpSection4.setSuperSection(tmpSection2);
            tmpSection2.setSuperSection(tmpSection);

            Set<Section> subs2 = tmpSection2.getSubsections();
            Set<Section> subs = tmpSection.getSubsections();

            tmpSection2.setArticle(tmpArticle);
            tmpSection3.setArticle(tmpArticle);
            tmpSection4.setArticle(tmpArticle);

            subs2.addAll(List.of(tmpSection3, tmpSection4));
            subs.add(tmpSection2);
            tmpSection2.setSubsections(subs2);
            tmpSection.setSubsections(subs);

            tmpSection.setCreatedAt(LocalDateTime.now());
            tmpSection.setRevisions(List.of(tmpRevision));
            tmpSection.setLatestRevision(tmpRevision);
            tmpArticle.setCategories(List.of(tmpCategory));
            tmpArticle.setSection(tmpSection);
            articleRepository.save(tmpArticle);
            articleRepository.flush();
        };
    }
}

