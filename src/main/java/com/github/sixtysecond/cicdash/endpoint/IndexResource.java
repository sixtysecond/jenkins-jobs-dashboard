package com.github.sixtysecond.cicdash.endpoint;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class IndexResource {

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Timed
    public Response index() {


        String fileName = "src/main/resources/index.html";
        File f = new File(fileName);
        try {
            return Response.status(Response.Status.OK)
                    .entity(new FileInputStream(f))
                    .build();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unable to load index")
                    .build();
        }

    }

}