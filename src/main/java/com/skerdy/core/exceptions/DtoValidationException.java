package com.skerdy.core.exceptions;

import com.skerdy.core.utils.MessageUtils;

public class DtoValidationException extends RuntimeException {

    public DtoValidationException(Class<?> serviceClass) {
        super(MessageUtils.generateDtoValidationExceptionMessage(serviceClass));
    }
}
