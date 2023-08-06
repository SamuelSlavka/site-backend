package com.backend.api.wiki.repository;

import com.backend.api.core.repository.OwnedRepository;
import com.backend.api.wiki.entity.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends OwnedRepository<Category, String> {
}
