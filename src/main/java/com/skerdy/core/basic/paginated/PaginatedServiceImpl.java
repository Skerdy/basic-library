package com.skerdy.core.basic.paginated;

import com.skerdy.core.basic.BasicServiceImpl;
import com.skerdy.core.basic.dtomanager.DtoScanner;
import com.skerdy.core.data.entity.BaseEntity;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;


public abstract class PaginatedServiceImpl<T extends BaseEntity, DTO, P, ID> extends BasicServiceImpl<T, DTO, ID> implements PaginatedService<DTO, P, ID> {

    private final PaginatedRepository<T, ID> repository;

    private BasicSpecification<T> baseSpecification;

    private Class<P> pClass;

    public PaginatedServiceImpl(
            PaginatedRepository<T, ID> repository,
            BasicSpecification<T> baseSpecification,
            ModelMapper modelMapper,
            Class<T> entityClass,
            Class<P> pClass,
            DtoScanner dtoScanner
    ) {
        super(repository, modelMapper, entityClass, dtoScanner);
        this.repository = repository;
        this.baseSpecification = baseSpecification;
        this.pClass = pClass;
    }

    @Override
    public Page<P> findPaginated(PageRequest pageRequest) {
        return repository.findAll(Specification.where(this.baseSpecification.base()), pClass, pageRequest);
    }
}
