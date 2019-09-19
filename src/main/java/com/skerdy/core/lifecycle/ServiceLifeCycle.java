package com.skerdy.core.lifecycle;

public interface ServiceLifeCycle<T> {

    public void onPreOperation(Operation operation, T entity, Object dto);

    public void onAfterOperation(Operation operation, T entity, Object dto);

}
