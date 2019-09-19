package com.skerdy.core.basic;

import java.util.List;

public interface BasicService<DTO, ID> {

    List<Object> findAll();

    Object findById(ID id);

    Object save(DTO t);

    Object edit(DTO t);

    void deleteById(ID id);

}
