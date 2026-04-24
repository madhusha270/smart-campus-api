package com.smartcampus.provider;

import com.smartcampus.exception.SensorUnavailableException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("error", "Forbidden");
        error.put("status", Response.Status.FORBIDDEN.getStatusCode());
        error.put("message", ex.getMessage());
        error.put("sensorId", ex.getSensorId());
        error.put("currentStatus", ex.getStatus());

        return Response.status(Response.Status.FORBIDDEN)
                       .entity(error)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
