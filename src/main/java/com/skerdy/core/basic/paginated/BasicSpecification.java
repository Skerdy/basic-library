package com.skerdy.core.basic.paginated;

import org.springframework.data.jpa.domain.Specification;

public abstract class BasicSpecification<T> {

    public Specification<T> base(){ return null; }

}
