package com.smartcampus.provider;

import com.smartcampus.exception.RoomNotEmptyException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("error", "Conflict");
        error.put("status", Response.Status.CONFLICT.getStatusCode());
        error.put("message", ex.getMessage());
        error.put("roomId", ex.getRoomId());
        error.put("sensorCount", ex.getSensorCount());

        return Response.status(Response.Status.CONFLICT)
                       .entity(error)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
