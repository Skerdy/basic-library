package com.skerdy.core.lifecycle;

import java.util.List;

public interface ServiceLifeCycle<T> {

    public void onPreOperation(Operation operation, T entity, Object dto, List<Error> errors);

    public void onAfterOperation(Operation operation, T entity, Object dto, List<Error> errors);

}
