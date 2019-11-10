package com.skerdy.core.utils;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ModelMerger {

    @SuppressWarnings("unchecked")
    public static <T> T mergeObjects(T incomingEntity, T existingEntity) throws IllegalAccessException, InstantiationException {
        Class<?> entityClass = existingEntity.getClass();
        List<Field> entityFields = getAllFields(new LinkedList<>(), entityClass);
        Object returnValue = entityClass.newInstance();
        System.out.println("INCOMING Object " + incomingEntity.toString());
        System.out.println("EXISTING Object " + existingEntity.toString());

        for (Field field : entityFields) {
            field.setAccessible(true);
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
                    Method method = getMethodFromClass("get", entityClass, field.getType());
                    if (method != null) {
                        subObject = method.invoke(incomingEntity);
                        subObject1 = method.invoke(existingEntity);
                    }
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (subObject != null && subObject1 != null) {
                    try {
                        Method method = getMethodFromClass("set", entityClass, field.getType());
                        if (method != null) {
                            method.invoke(existingEntity, mergeObjects(subObject, subObject1));
                        }
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                // handle list or set cases
            } else if((field.getType().equals(List.class) && isFieldCascade(field))
            ||field.getType().equals(Set.class) && isFieldCascade(field) ){
                try {
                    mergeListFieldWithCascade(field,entityClass, incomingEntity, existingEntity);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                Object value1 = field.get(incomingEntity);
                Object value2 = field.get(existingEntity);
                Object value = value1 != null ? value1 : value2;
                field.set(existingEntity, value);
            }

        }
        System.out.println("MERGED Object " + existingEntity.toString());
        return existingEntity;
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

    private static <T> void mergeListFieldWithCascade(Field field, Class<?> entityClass, T incoming, T existing) throws IllegalAccessException, InvocationTargetException {
        Object incomingList = field.get(incoming);
        Object existingList = field.get(existing);
        final Class<?>[] listItemClass = {null};
        AtomicReference<Method> listItemEntitySetterMethod = new AtomicReference<>();
        Method getItemIdMethod = null;

        if(incomingList instanceof Collection && existingList instanceof Collection){
            Collection iList = (Collection) incomingList;
            Collection eList = (Collection) existingList;

            if(iList.isEmpty()){
                eList.forEach(member->{
                 if(listItemClass[0] == null){
                     listItemClass[0] = member.getClass();
                     if(listItemEntitySetterMethod.get() ==null){
                         listItemEntitySetterMethod.set(getMethodFromClass("set", listItemClass[0], entityClass));
                     }
                 }
                    try {
                        listItemEntitySetterMethod.get().invoke(member, new Object[]{null});
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                });
            } else if(eList.isEmpty()){
                eList.addAll(iList);
            } else {
                Collection newMembers = null;
                if(eList instanceof List){
                    newMembers = new ArrayList();
                } else {
                    newMembers = new HashSet();
                }
                for(Object eMember: eList){
                    // provide metadata
                    if(listItemClass[0] == null){
                        listItemClass[0] = eMember.getClass();
                        if(listItemEntitySetterMethod.get() ==null){
                            listItemEntitySetterMethod.set(getMethodFromClass("set", listItemClass[0], entityClass));
                        }
                        if(getItemIdMethod==null){
                            for (Method method : eMember.getClass().getMethods()) {
                                if (method.getName().equals("getId")) {
                                    getItemIdMethod = method;
                                }
                            }
                        }
                    }

                    boolean foundMember = false;
                    for(Object iMember: iList){
                        assert getItemIdMethod != null;
                        if(getItemIdMethod.invoke(iMember) ==null ){
                            newMembers.add(iMember);
                        }
                        if(getItemIdMethod.invoke(iMember) !=null && getItemIdMethod.invoke(iMember).equals(getItemIdMethod.invoke(eMember))){
                            foundMember = true;
                        }
                    }
                    if(!foundMember){
                        if(listItemEntitySetterMethod.get()!=null)
                        listItemEntitySetterMethod.get().invoke(eMember, new Object[]{null});
                        else {
                            eList.remove(eMember);
                        }
                    }
                }
                eList.addAll(newMembers);
            }
        }
    }

    private static boolean isFieldCascade(Field field){
        for (Annotation annotation: field.getAnnotations()){
            if(annotation.annotationType().equals(OneToMany.class)){
               for(CascadeType cascadeType: ((OneToMany)annotation).cascade()){
                   if(cascadeType.equals(CascadeType.ALL) || cascadeType.equals(CascadeType.PERSIST)){
                       return true;
                   }
               }
            }
        }
        return false;
    }


}
