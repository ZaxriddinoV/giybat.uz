package giybat.uz.util;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageImplUtil<T> {
    private List<T> posts;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;

    public PageImplUtil(List<T> posts, int currentPage, int pageSize, long totalElements, int totalPages) {
        this.posts = posts;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public PageImplUtil() {
    }
}
