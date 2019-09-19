package com.skerdy.core.data.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDTO<ID,U> extends AuditableDTO<U> {

    private ID id;

}
