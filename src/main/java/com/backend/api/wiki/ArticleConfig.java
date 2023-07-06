package com.backend.api.wiki;


import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Category;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
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
import java.util.List;

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
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customize() {
        return p -> p.setOneIndexedParameters(true);
    }

    @Bean
    CommandLineRunner commandLineRunner(ArticleRepository articleRepository, CategoryRepository categoryRepository) {
        return args -> {
            Article tmpArticle = Article.builder().deleted(false).createdAt(LocalDateTime.now().minusHours(1)).isPrivate(false).title("java tbd").build();

            Article tmpArticle2 = Article.builder().deleted(false).createdAt(LocalDateTime.now().minusHours(2)).isPrivate(false).title("angular tbd").build();
            articleRepository.save(tmpArticle2);

            Article tmpArticle3 = Article.builder().deleted(false).createdAt(LocalDateTime.now()).isPrivate(false).title("ci/cd tbd").build();
            articleRepository.save(tmpArticle3);

            Category tmpCategory = Category.builder().deleted(false).categoryName("catName").build();
            Section tmpSection = Section.builder().deleted(false).sectionOrder(0).depth(0).build();
            Section tmpSection3 = Section.builder().deleted(false).depth(1).sectionOrder(3).build();
            Section tmpSection2 = Section.builder().deleted(true).sectionOrder(2).depth(1).build();
            tmpSection.setSubsections(List.of(tmpSection3, tmpSection2));
            Revision tmpRevision = Revision.builder().deleted(false).text(
                    """
                            ## Java
                                final - cannot be modified or extended
                                static - the property belongs to the class not instance and is shared by all\s
                                                        
                            """
            ).build();
            tmpSection.setRevisions(List.of(tmpRevision));
            tmpSection.setLatestRevision(tmpRevision);
            tmpArticle.setCategories(List.of(tmpCategory));
            tmpArticle.setSection(tmpSection);
            articleRepository.save(tmpArticle);
        };
    }
}
