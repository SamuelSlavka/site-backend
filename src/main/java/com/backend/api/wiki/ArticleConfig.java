package com.backend.api.wiki;


import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Category;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.model.RevisionCreationDto;
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

            Section sec = Section.builder().sectionOrder(0).depth(0).subsections(new HashSet<>()).build();
            sec.setArticle(tmpArticle);
            Revision tmpRevision = Revision.builder().title("title").text("""
                    ## Java
                        final - cannot be modified or extended
                        static - the property belongs to the class not instance and is shared by all\s
                                                
                    """).build();

            Revision rev2 = Revision.builder().title("title").text("rev2").build();
            Revision rev3 = Revision.builder().title("title").text("re33").build();
            Revision rev4 = Revision.builder().title("title").text("re44").build();
            Section sec2 = Section.builder().subsections(new HashSet<>()).latestRevision(rev2).revisions(List.of(rev2))
                    .sectionOrder(0).depth(1).build();
            Section sec3 = Section.builder().subsections(new HashSet<>()).latestRevision(rev3).revisions(List.of(rev3))
                    .sectionOrder(0).depth(2).build();
            Section sec4 = Section.builder().subsections(new HashSet<>()).latestRevision(rev4).revisions(List.of(rev4))
                    .sectionOrder(1).depth(2).build();
            Section sec5 = Section.builder().subsections(new HashSet<>())
                    .latestRevision(new Revision(new RevisionCreationDto("a", "b"))).sectionOrder(1).depth(1).build();
            Section sec6 = Section.builder().subsections(new HashSet<>()).sectionOrder(2).depth(1)
                    .latestRevision(new Revision(new RevisionCreationDto("a", "b"))).build();
            Section sec7 = Section.builder().subsections(new HashSet<>()).sectionOrder(3).depth(1)
                    .latestRevision(new Revision(new RevisionCreationDto("a", "b"))).build();
            Section sec8 = Section.builder().subsections(new HashSet<>()).sectionOrder(4).depth(1)
                    .latestRevision(new Revision(new RevisionCreationDto("a", "b"))).build();
            sec2.setCreatedAt(LocalDateTime.now());
            sec3.setCreatedAt(LocalDateTime.now());
            sec5.create("creator");
            sec6.create("creator");
            sec7.create("creator");
            sec8.create("creator");

            sec3.setSuperSection(sec2);
            sec4.setSuperSection(sec2);
            sec2.setSuperSection(sec);
            sec5.setSuperSection(sec);
            sec6.setSuperSection(sec);
            sec7.setSuperSection(sec);
            sec8.setSuperSection(sec);

            Set<Section> subs2 = sec2.getSubsections();
            Set<Section> subs = sec.getSubsections();

            sec2.setArticle(tmpArticle);
            sec3.setArticle(tmpArticle);
            sec4.setArticle(tmpArticle);

            subs2.addAll(List.of(sec3, sec4));
            subs.addAll(List.of(sec2, sec5, sec6, sec7, sec8));
            sec2.setSubsections(subs2);
            sec.setSubsections(subs);

            sec.setCreatedAt(LocalDateTime.now());
            sec.setRevisions(List.of(tmpRevision));
            sec.setLatestRevision(tmpRevision);
            tmpArticle.setCategories(List.of(tmpCategory));
            tmpArticle.setSection(sec);
            articleRepository.save(tmpArticle);
            articleRepository.flush();
        };
    }
}

