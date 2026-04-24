package com.smartcampus.provider;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("error", "Unprocessable Entity");
        error.put("status", 422);
        error.put("message", ex.getMessage());
        error.put("targetId", ex.getTargetId());
        error.put("resourceType", ex.getResourceType());

        return Response.status(422)
                       .entity(error)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
