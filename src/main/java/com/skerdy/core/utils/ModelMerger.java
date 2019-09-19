package com.skerdy.core.utils;

import java.lang.reflect.Field;
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
        System.out.println("INCOMING Object " + ((T)dto).toString() );

        System.out.println("EXISTING Object " + ((T)entity).toString() );


        for (Field field : secondFields) {
            field.setAccessible(true);
            boolean foundSimilarField = false;
            for (Field field1 : firstFields) {
                field1.setAccessible(true);
                if (field.getName().equals(field1.getName())) {
                    foundSimilarField = true;
                    Object value1 = field1.get(dto);
                    Object value2 = field.get(entity);
                    Object value = value1 != null ? value1 : value2;
                    field.set(returnValue, value);
                    break;
                }
            }

            if(!foundSimilarField){
                field.set(returnValue, field.get(entity));
            }
        }
        System.out.println("MERGED Object " + ((T)returnValue).toString() );
        return (U) returnValue;
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }


}
