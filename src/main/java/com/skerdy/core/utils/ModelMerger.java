package com.skerdy.core.utils;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ModelMerger {

    @SuppressWarnings("unchecked")
    public static <T, U> U mergeObjects(T dto, U entity) throws IllegalAccessException, InstantiationException {
        Class<?> dtoclass = dto.getClass();
        Class<?> entityClass = entity.getClass();
        List<Field> firstFields = getAllFields(new LinkedList<>(), dtoclass);
        List<Field> secondFields = getAllFields(new LinkedList<>(), entityClass);
        Object returnValue = entityClass.newInstance();
        System.out.println("INCOMING Object " + dto.toString());

        System.out.println("EXISTING Object " + entity.toString());


        for (Field field : secondFields) {
            field.setAccessible(true);
            boolean foundSimilarField = false;
            for (Field field1 : firstFields) {
                field1.setAccessible(true);
                if (field.getName().equals(field1.getName())) {
                    foundSimilarField = true;
                    if (field.getType().getAnnotation(Entity.class) != null) {
                        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                        if (manyToOne != null && manyToOne.fetch().equals(FetchType.LAZY)) {
                            continue;
                        } else if (oneToOne != null && oneToOne.fetch().equals(FetchType.LAZY)) {
                            continue;
                        }
                        Object subObject = null;
                        Object subObject1 = null;
                        try {
                            Method method = getMethodFromClass("get", dtoclass, field.getType());
                            if (method != null) {
                                subObject = method.invoke(dto);
                            }

                            method = getMethodFromClass("get", entityClass, field1.getType());

                            if (method != null) {
                                subObject1 = method.invoke(entity);
                            }

                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        if (subObject != null && subObject1 != null) {
                            try {
                                Method method = getMethodFromClass("set", entityClass, field.getType());
                                if (method != null) {
                                    method.invoke(entity, mergeObjects(subObject, subObject1));
                                }
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Object value1 = field1.get(dto);
                        Object value2 = field.get(entity);
                        Object value = value1 != null ? value1 : value2;
                        field.set(entity, value);
                    }
                    break;
                }
            }

//            if (!foundSimilarField) {
//                field.set(returnValue, field.get(entity));
//            }
        }
        System.out.println("MERGED Object " + entity.toString());
        return entity;
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }

    private static Method getMethodFromClass(String prefix, Class<?> clazz, Class<?> getterClazz) {
        String methodName = prefix + getterClazz.getSimpleName();
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }


}
