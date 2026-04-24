package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings() {

        if (!store.sensorExists(sensorId)) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Sensor '" + sensorId + "' not found.")
                           .build();
        }

        List<SensorReading> readings = store.getReadings(sensorId);
        return Response.ok(readings).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Sensor '" + sensorId + "' not found.")
                           .build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        store.addReading(sensorId, reading);

        return Response.status(Response.Status.CREATED)
                       .entity(reading)
                       .build();
    }
}
