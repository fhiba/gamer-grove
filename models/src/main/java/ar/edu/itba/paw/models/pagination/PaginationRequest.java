package ar.edu.itba.paw.models.pagination;

public class PaginationRequest {

    public static final int DEFAULT_INITIAL_PAGE_NUMBER = 1;

    public static final int DEFAULT_PAGE_SIZE = 10;
    private int pageNumber;
    private int pageSize;

    // Constructor
    public PaginationRequest(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
    public PaginationRequest() {
        this.pageNumber = DEFAULT_INITIAL_PAGE_NUMBER;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }
    public PaginationRequest(int pageSize) {
        this.pageSize = pageSize;
        this.pageNumber = DEFAULT_INITIAL_PAGE_NUMBER;
    }
    // Getters and setters
    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}