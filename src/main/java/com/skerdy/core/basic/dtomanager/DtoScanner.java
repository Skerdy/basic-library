package com.skerdy.core.basic.dtomanager;

import com.skerdy.core.exceptions.DtoValidationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.*;

@Component
public class DtoScanner {

    private final String testPackage = "com.skerdy.dtoIntegration.domain";
    private Map<Class<?>, List<DtoWrapper>> scannedDTOs;

    public DtoScanner() {
        this.scannedDTOs = new HashMap<>();
        scan();
        validateDtoUsage();
    }

    private Set<BeanDefinition> getBeanDefinitions(Class<? extends Annotation> componentType) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(componentType));
        return new HashSet<>(scanner.findCandidateComponents(testPackage));
    }

    private void scan() {
        Set<BeanDefinition> beanDefinitions = getBeanDefinitions(BaseDto.class);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            try {
                Class<?> dtoClass = Class.forName(beanDefinition.getBeanClassName());
                BaseDto baseDto = dtoClass.getDeclaredAnnotation(BaseDto.class);
                addDtoWrapper(baseDto.serviceClass(), new DtoWrapper(dtoClass, baseDto.method()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void addDtoWrapper(Class<?> serviceClass, DtoWrapper dtoWrapper) {
        System.out.println("FOUND METHOD => " + dtoWrapper.getBaseMethod());
        List<DtoWrapper> wrappers = this.scannedDTOs.get(serviceClass);
        if (wrappers == null) {
            wrappers = new ArrayList<>();
            wrappers.add(dtoWrapper);
            this.scannedDTOs.put(serviceClass, wrappers);
        } else {
            wrappers.add(dtoWrapper);
        }
    }

    public Class<?> getDtoClass(Class<?> serviceClass, BaseMethods baseMethod){
        List<DtoWrapper> dtos = this.scannedDTOs.get(serviceClass);
        if(dtos != null){
            for(DtoWrapper dtoWrapper: dtos){
                if(dtoWrapper.getBaseMethod().value().equals(baseMethod.value())){
                    return dtoWrapper.getDtoClass();
                }
            }
        }
        return getDtoClass(serviceClass, BaseMethods.INPUT);
    }

    private void validateDtoUsage(){
        for(Class<?> serviceClass : scannedDTOs.keySet()){
            if(!validateDtoSet(scannedDTOs.get(serviceClass))){
                throw new DtoValidationException(serviceClass);
            }
        }
    }

    private boolean validateDtoSet(List<DtoWrapper> dtos){
        for(DtoWrapper dtoWrapper : dtos){
            if(dtoWrapper.getBaseMethod().value().equals(BaseMethods.INPUT.value())){
                return true;
            }
        }
        return false;
    }


}
