package ar.edu.itba.paw.models.pagination;

import java.util.List;

public class PaginatedDataWrapper<T>{
    private List<T> data;
    private int pageNumber;
    private int totalPages;
    private int totalCount;
    private int pageSize;


    public PaginatedDataWrapper(List<T> data, int pageNumber, int totalCount, int pageSize) {
        this.data = data;
        this.pageNumber = pageNumber;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    public int getPreviousPage() {
        return this.getPageNumber() >  1? this.getPageNumber() - 1: 1;
    }
    public int getNextPage() {
        return this.getPageNumber() <  1? this.getPageNumber() - 1: this.getTotalPages();
    }
    public int getFirstPage() {
        return 1;
    }
    public int getPageNumber() {
        return pageNumber;
    }

    public List<T> getData() {
        return data;
    }

    public int getPageSize() {
        return pageSize;
    }

}
