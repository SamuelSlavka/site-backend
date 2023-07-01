package com.backend.api.wiki;


import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Category;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.CategoryRepository;
import com.backend.api.wiki.repository.RevisionRepository;
import com.backend.api.wiki.repository.SectionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@ComponentScan(basePackages = "wiki")
public class ArticleConfig  implements WebMvcConfigurer {
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
    CommandLineRunner commandLineRunner(ArticleRepository articleRepository, CategoryRepository categoryRepository) {
        return args -> {
            Article tmpArticle = Article.builder().deleted(false).title("Art title").build();

            Article tmpArticle2 = Article.builder().deleted(false).title("Brt title").build();
            Section tmpSection2 = Section.builder().deleted(false).article(tmpArticle).sectionOrder(2).build();
            articleRepository.save(tmpArticle2);

            Article tmpArticle3 = Article.builder().deleted(false).title("Brt title").build();
            Section tmpSection3 = Section.builder().deleted(false).article(tmpArticle).sectionOrder(3).build();
            articleRepository.save(tmpArticle3);

            Category tmpCategory = Category.builder().deleted(false).categoryName("catName").build();
            Section tmpSection = Section.builder().deleted(false).article(tmpArticle).sectionOrder(1).build();
            Revision tmpRevision = Revision.builder().deleted(false).text("text").title("title").build();
            tmpSection.setRevisions(List.of(tmpRevision));
            tmpArticle.setCategories(List.of(tmpCategory));
            tmpArticle.setSections(List.of(tmpSection));
            articleRepository.save(tmpArticle);
        };
    }
}
