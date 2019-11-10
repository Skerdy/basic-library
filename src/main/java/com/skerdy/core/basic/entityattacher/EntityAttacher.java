package com.skerdy.core.basic.entityattacher;

import com.skerdy.core.basic.annotations.AttachFromDatabase;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class EntityAttacher<T> {

    private final ApplicationContext applicationContext;
    private Map<Field, JpaRepository> repositoryMap;
    private Map<Field, Object> dataMap;
    private T entity;

    public EntityAttacher(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;


    }

    public void init(T entity) {
        this.repositoryMap = new HashMap<>();
        this.dataMap = new HashMap<>();
        this.entity = entity;
        for (Field field : getAllFields(new ArrayList<>(), entity.getClass())) {
            if (field.getAnnotation(AttachFromDatabase.class) != null) {
                addEntityAttacherToMap(field, field.getAnnotation(AttachFromDatabase.class));
            }
        }
    }

    public T attachChildrenEntities() {
        for (Field field : this.repositoryMap.keySet()) {
            attachEntitiesFromDataSource(field);
        }
        return entity;
    }

    public T getEntityWithoutDetachedChildrens() {
        for (Field field : this.repositoryMap.keySet()) {
            stripEntityFromDetachedEntities(field);
        }
        return entity;
    }

    public boolean shouldAttach(){
        return !this.repositoryMap.isEmpty();
    }

    private void attachEntitiesFromDataSource(Field field) {
        Class<?> detachedEntityClass = field.getAnnotation(AttachFromDatabase.class).entity();
        Method fieldSetterMethod = getMethodFromClass("set", entity.getClass(), detachedEntityClass, true);
        Method getIdMethod = null;

        final Object[] detachedEntityId = {null};
        for (Method method : detachedEntityClass.getMethods()) {
            if (method.getName().equals("getId")) {
                getIdMethod = method;
            }
        }

        Object detachedEntities = this.dataMap.get(field);
        if (detachedEntities instanceof List) {
            JpaRepository detachedEntityRepository = this.repositoryMap.get(field);
            Method finalGetIdMethod = getIdMethod;
            Object attachedEntites = ((List) detachedEntities).stream().map(detachedEntity -> {
                if (finalGetIdMethod != null) {
                    try {
                        detachedEntityId[0] = finalGetIdMethod.invoke(detachedEntity);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                return detachedEntityRepository.findById(detachedEntityId[0]).orElse(detachedEntity);
            }).collect(Collectors.toList());
            try {
                fieldSetterMethod.invoke(entity, attachedEntites);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

    private void stripEntityFromDetachedEntities(Field field) {
        Class<?> detachedEntityClass = field.getAnnotation(AttachFromDatabase.class).entity();
        Method fieldGetterMethod = getMethodFromClass("get", entity.getClass(), detachedEntityClass, true);
        Method fieldSetterMethod = getMethodFromClass("set", entity.getClass(), detachedEntityClass, true);
        if (fieldGetterMethod != null) {
            try {
                this.dataMap.put(field, fieldGetterMethod.invoke(entity));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (fieldSetterMethod != null) {
            try {
                fieldSetterMethod.invoke(entity, new ArrayList<>());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private Method getMethodFromClass(String prefix, Class<?> clazz, Class<?> getterClazz, boolean isList) {
        String methodName = prefix + getterClazz.getSimpleName();
        if (isList) {
            methodName += "s";
        }
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }


    private void addEntityAttacherToMap(Field field, AttachFromDatabase attachFromDatabase) {
        if (this.repositoryMap == null) {
            this.repositoryMap = new HashMap<>();
        }
        String beanName = attachFromDatabase.entity().getSimpleName();
        beanName += "Repository";
        char beanChar[] = beanName.toCharArray();
        beanChar[0] = Character.toLowerCase(beanChar[0]);
        beanName = new String(beanChar);
        Object fieldRepository = this.applicationContext.getBean(beanName);
        if (fieldRepository instanceof JpaRepository) {
            this.repositoryMap.put(field, (JpaRepository) fieldRepository);
        }
    }

    private List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    public void setEntity(T entity){
        this.entity = entity;
    }

}
