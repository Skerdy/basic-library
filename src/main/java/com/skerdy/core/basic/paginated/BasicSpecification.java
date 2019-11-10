package com.skerdy.core.basic.paginated;

import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;

public abstract class BasicSpecification<T> {

    public Specification<T> base(HashMap<String, String> filter){ return null; }

}
