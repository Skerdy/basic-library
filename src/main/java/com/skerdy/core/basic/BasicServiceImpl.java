package com.skerdy.core.basic;

import com.google.common.collect.Lists;
import com.skerdy.core.basic.dtomanager.BaseMethods;
import com.skerdy.core.basic.dtomanager.DtoScanner;
import com.skerdy.core.basic.entityattacher.EntityAttacher;
import com.skerdy.core.data.entity.BaseEntity;
import com.skerdy.core.lifecycle.Operation;
import com.skerdy.core.lifecycle.ServiceLifeCycle;
import com.skerdy.core.utils.ModelMerger;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BasicServiceImpl<T extends BaseEntity, DTO, ID> implements BasicService<DTO, ID>, ServiceLifeCycle<T> {

    protected final ModelMapper modelMapper;
    private final Class<T> entityClass;
    private final DtoScanner dtoScanner;
    private CrudRepository<T, ID> repository;
    private EntityAttacher<T> entityAttacher;
    public final ApplicationContext applicationContext;

    public BasicServiceImpl(CrudRepository<T, ID> repository, ModelMapper modelMapper, Class<T> entityClass, DtoScanner dtoScanner, ApplicationContext applicationContext) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.entityClass = entityClass;
        this.dtoScanner = dtoScanner;
        this.applicationContext = applicationContext;
        this.entityAttacher = new EntityAttacher<>(this.applicationContext);

    }

    @Override
    public List<Object> findAll() {
        return Lists.newArrayList(repository.findAll().iterator()).stream().map(entity -> this.modelMapper.map(entity, getDtoClass(BaseMethods.FIND_ALL))).collect(Collectors.toList());
    }

    @Override
    public Object findById(ID id) {
        List<Error> errors = new ArrayList<>();
        T entity = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found"));
        Object dto = this.modelMapper.map(entity, getDtoClass(BaseMethods.FIND_BY_ID));
        onPreOperation(Operation.READ_ID, entity, dto, errors );
        return dto;
    }

    @Override
    public Object save(DTO dto) {
        List<Error> errors = new ArrayList<>();
        T entity = this.modelMapper.map(dto, this.entityClass);
        onPreOperation(Operation.CREATE, entity, dto, errors);
        if(!errors.isEmpty()){
            return getErrorResponse(errors);
        }
        this.entityAttacher.init(entity);
        if(entityAttacher.shouldAttach()){
            entity = entityAttacher.getEntityWithoutDetachedChildrens();
            entity.preSave();
            entity = repository.save(entity);
            entityAttacher.setEntity(entity);
            entity = entityAttacher.attachChildrenEntities();
            entity.preSave();
            entity = repository.save(entity);
        } else {
            entity.preSave();
            entity = repository.save(entity);
        }
        Object returnDto = this.modelMapper.map(entity, this.getDtoClass(BaseMethods.FIND_BY_ID));
        onAfterOperation(Operation.CREATE, entity, returnDto, errors);
        if(!errors.isEmpty()){
            return getErrorResponse(errors);
        }
        return returnDto;
    }

    @Override
    public Object edit(DTO dto) {
        List<Error> errors = new ArrayList<>();
        Method getIdMethod = null;
        T result = null;

        for (Method method : this.getDtoClass(BaseMethods.INPUT).getMethods()) {
            if (method.getName().equals("getId")) {
                getIdMethod = method;
            }
        }

        Object id;
        try {
            assert getIdMethod != null;
            id = getIdMethod.invoke(dto);
            T existingT = repository.findById((ID) id).orElse(null);
            assert existingT != null;
            T incomingEntity = this.modelMapper.map(dto, this.entityClass);
            incomingEntity.preSave();
            result = ModelMerger.mergeObjects(incomingEntity, existingT);
            onPreOperation(Operation.UPDATE, result, dto, errors);
            if(!errors.isEmpty()){
                return getErrorResponse(errors);
            }
            repository.save(result);

        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        assert result != null;
        Object returnDto = this.modelMapper.map(result, this.getDtoClass(BaseMethods.FIND_BY_ID));
        onAfterOperation(Operation.UPDATE, result, returnDto, errors);
        if(!errors.isEmpty()){
            return getErrorResponse(errors);
        }
        return returnDto;
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    private Class<?> getDtoClass(BaseMethods baseMethod) {
        return this.dtoScanner.getDtoClass(this.getClass(), baseMethod);
    }

    private ResponseEntity<List<Error>> getErrorResponse(List<Error> errors){
        return new ResponseEntity<>(errors, HttpStatus.EXPECTATION_FAILED);
    }
}
