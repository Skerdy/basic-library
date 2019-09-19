package com.skerdy.core.basic.paginated;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

@NoRepositoryBean
public interface PaginatedRepository<T, U> extends JpaRepository<T, U>, JpaSpecificationExecutorWithProjection<T> {
}
