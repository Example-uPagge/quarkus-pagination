package dev.struchkov.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {

    private Integer totalPages;
    private Long totalElements;
    private Integer pageSize;
    private Integer pageCount;
    private boolean nextPage;
    private boolean prevPage;
    private List<T> content;

}
