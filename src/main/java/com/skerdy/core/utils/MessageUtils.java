package com.skerdy.core.utils;

public class MessageUtils {


    public static String generateDtoValidationExceptionMessage(Class<?> serviceClass){
        StringBuilder message = new StringBuilder(" Validation of Dto-s failed for Service class: ");
        String serviceName = serviceClass.getSimpleName();
        message.append(serviceName).append(" . ")
                .append("Please consider creating a Dto for -> ").append(serviceName.contains("ServiceImpl")? serviceName.substring(0, serviceName.length()-11): serviceName)
                .append(" entity, anottated with @BaseDto class!");
        return message.toString();
    }

}
