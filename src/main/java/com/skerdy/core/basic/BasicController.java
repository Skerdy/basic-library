package com.skerdy.core.basic;

import com.skerdy.core.permission.CheckPermission;
import com.skerdy.core.permission.Permissions;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class BasicController<DTO, ID> {

    private final BasicService<DTO, ID> basicService;

    public BasicController(BasicService<DTO, ID> basicService) {
        this.basicService = basicService;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @CheckPermission(permission = Permissions.READ)
    public List<Object> getAll() {
        return basicService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @CheckPermission(permission = Permissions.READ)
    public Object getById(@PathVariable ID id) {
        return basicService.findById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @CheckPermission(permission = Permissions.EDIT)
    public Object edit(@PathVariable Integer id, @RequestBody DTO dto) {
        return basicService.edit(dto);
    }

    @RequestMapping(method = RequestMethod.POST)
    @CheckPermission(permission = Permissions.WRITE)
    public Object save(@RequestBody DTO dto) {
        return basicService.save(dto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @CheckPermission(permission = Permissions.DELETE)
    public void delete(@PathVariable ID id) {
        basicService.deleteById(id);
    }
}