package dev.struchkov.example;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class PostRepository implements PanacheRepositoryBase<Post, UUID> {

}
