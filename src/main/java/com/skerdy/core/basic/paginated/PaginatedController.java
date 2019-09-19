package com.skerdy.core.basic.paginated;

import com.skerdy.core.basic.BasicController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


public class PaginatedController<DTO, ID, P> extends BasicController<DTO, ID> {

    private final PaginatedService<DTO, P, ID> basicService;

    public PaginatedController(PaginatedService<DTO, P, ID> basicService) {
        super(basicService);
        this.basicService = basicService;
    }

    @GetMapping
    public Page<P> getAllPaginated(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", required = false) Sort.Direction direction
    ) {
        Sort sort2 = new Sort(direction, sort);
        return basicService.findPaginated(PageRequest.of(page, size, sort2));
    }
}
