package com.skerdy.core.data.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AuditableDTO<T> {

    protected T createdBy;

    protected Date creationDate;

    protected T lastModifiedBy;

    protected Date lastModifiedDate;

}
