package com.backend.api.core.repository;

import com.backend.api.core.entity.OwnedEntity;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@NoRepositoryBean
public interface OwnedRepository<T, ID> extends SoftDeletableRepository<T, ID> {
    default void create(T entity, String creator) {
        Assert.notNull(entity, "The entity must not be null!");
        Assert.isInstanceOf(OwnedEntity.class, entity, "The entity must be owned!");

        ((OwnedEntity)entity).setCreatedAt(LocalDateTime.now());
        ((OwnedEntity)entity).setCreatedBy(creator);
        save(entity);
    }
}
