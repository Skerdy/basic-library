package com.skerdy.core.basic.dtomanager;

public class DtoWrapper {

    private Class<?> dtoClass;
    private BaseMethods baseMethod;

    public DtoWrapper() {
    }

    public DtoWrapper(Class<?> dtoClass, BaseMethods baseMethod) {
        this.dtoClass = dtoClass;
        this.baseMethod = baseMethod;
    }

    public Class<?> getDtoClass() {
        return dtoClass;
    }

    public void setDtoClass(Class<?> dtoClass) {
        this.dtoClass = dtoClass;
    }

    public BaseMethods getBaseMethod() {
        return baseMethod;
    }

    public void setBaseMethod(BaseMethods baseMethod) {
        this.baseMethod = baseMethod;
    }
}
