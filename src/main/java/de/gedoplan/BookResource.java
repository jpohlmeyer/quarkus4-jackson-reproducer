package de.gedoplan;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tools.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectMapper; // Quarkus3.40
//import com.fasterxml.jackson.core.JsonProcessingException; // Quarkus3.40

import java.net.URI;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    ObjectMapper objectMapper;

    @POST
    @Transactional
    public Response create(Book book) {
        book.id = null;
        book.persist();
        return Response.created(URI.create("/books/" + book.id)).entity(book).build();
    }

    @PATCH
    @Path("{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_MERGE_PATCH_JSON)
//    @Consumes("application/merge-patch+json") // Quarkus3.40
    public Book patch(@PathParam("id") long id, String mergePatch)
//            throws JsonProcessingException // Quarkus3.40
    {
        Book book = Book.<Book>findByIdOptional(id).orElseThrow(NotFoundException::new);
        return objectMapper.readerForUpdating(book).readValue(mergePatch);
    }
}
