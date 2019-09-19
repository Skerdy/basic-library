package com.skerdy.core.basic.paginated;

import com.skerdy.core.basic.BasicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface PaginatedService<DTO, P, ID> extends BasicService<DTO, ID> {
    Page<P> findPaginated(PageRequest request);
}
