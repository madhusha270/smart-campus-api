package com.smartcampus.provider;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        
        LOGGER.log(Level.SEVERE, "Unhandled Exception caught by Global Mapper:", ex);

        if (ex instanceof WebApplicationException) {
            return ((WebApplicationException) ex).getResponse();
        }

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("error", "Internal Server Error");
        error.put("status", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        error.put("message", "An unexpected error occurred on the server. Please contact support.");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(error)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
