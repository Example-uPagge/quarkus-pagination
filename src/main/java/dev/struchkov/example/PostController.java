package dev.struchkov.example;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository repository;

    @GET
    @ReactiveTransactional
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Page<Post>> getAll(
            @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit
    ) {
        final PanacheQuery<Post> pageQuery = repository.findAll().page(offset, limit);
        return Uni.combine().all().unis(
                pageQuery.list(),
                pageQuery.count(),
                pageQuery.pageCount(),
                pageQuery.hasNextPage(),
                Uni.createFrom().item(pageQuery.hasPreviousPage())
        ).asTuple().onItem().transform(
                t -> {
                    final List<Post> content = t.getItem1();
                    final Long totalElements = t.getItem2();
                    final Integer totalPages = t.getItem3();
                    final Boolean hasNextPage = t.getItem4();
                    final Boolean hasPrevPage = t.getItem5();

                    return Page.<Post>builder()
                            .content(content)
                            .totalElements(totalElements)
                            .totalPages(totalPages)
                            .pageCount(offset)
                            .pageSize(limit)
                            .nextPage(hasNextPage)
                            .prevPage(hasPrevPage)
                            .build();
                }
        );
    }

    @GET
    @Path("true")
    @ReactiveTransactional
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Page<Post>> getAllTrue(
            @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit
    ) {
        final PanacheQuery<Post> pageQuery = repository.findAll().page(offset, limit);
        return Uni.combine().all().unis(
                pageQuery.list(),
                pageQuery.count()
        ).asTuple().onItem().transform(
                t -> {
                    final List<Post> posts = t.getItem1();
                    final Long totalElements = t.getItem2();
                    final int totalPages = (int) Math.ceil((double) totalElements / limit);

                    final int currentPageSize = posts.size();
                    final int currentCountShowElements = limit * (offset + 1) - (limit + currentPageSize);

                    final boolean hasPrevPage = offset > 0 && totalElements > 0;
                    final boolean hasNextPage = currentCountShowElements < totalElements;

                    return Page.<Post>builder()
                            .content(posts)
                            .prevPage(hasPrevPage)
                            .nextPage(hasNextPage)
                            .totalPages(totalPages)
                            .totalElements(totalElements)
                            .pageCount(offset)
                            .pageSize(currentPageSize)
                            .build();
                }
        );
    }

}