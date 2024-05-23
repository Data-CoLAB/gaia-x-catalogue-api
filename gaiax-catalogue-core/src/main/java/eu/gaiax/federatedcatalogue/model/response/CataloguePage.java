package eu.gaiax.federatedcatalogue.model.response;

import org.springframework.data.domain.Page;

public class CataloguePage<T> {

    private static final long serialVersionUID = -1053250768433954193L;

    private int currentPage;

    private int numberOfElement;

    private int size;

    private int totalPages;

    private long totalElements;

    private Iterable<T> content;

    private CataloguePage() {
    }

    public static <C> CataloguePage<C> of(Iterable<C> content, int currentPage, int numberOfElement, int size, int totalPages,
                                     long totalElements) {
        CataloguePage<C> page = new CataloguePage<>();
        page.content = content;
        page.currentPage = currentPage;
        page.numberOfElement = numberOfElement;
        page.size = size;
        page.totalPages = totalPages;
        page.totalElements = totalElements;
        return page;
    }
    public static <C> CataloguePage<C> of(Iterable<C> content, Page page) {
        return of(content, page.getNumber(), page.getNumberOfElements(), page.getSize(), page.getTotalPages(), page.getTotalElements());
    }

    public static <C> CataloguePage<C> of(Iterable<C> content, CataloguePage page) {
        return of(content, page.getCurrentPage(), page.getNumberOfElement(), page.getSize(), page.getTotalPages(), page.getTotalElements());
    }

    public static <C> CataloguePage<C> of(Page page) {
        return of(page.getContent(), page.getNumber(), page.getNumberOfElements(), page.getSize(), page.getTotalPages(), page.getTotalElements());
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public Iterable<T> getContent() {
        return content;
    }

    public int getNumberOfElement() {
        return numberOfElement;
    }

    public long getTotalElements() {
        return totalElements;
    }

}
