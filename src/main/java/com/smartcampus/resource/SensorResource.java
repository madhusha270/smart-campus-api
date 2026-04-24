package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Collection;
import java.util.List;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getSensors(@QueryParam("type") String type) {
        if (type != null && !type.isBlank()) {
            List<Sensor> filtered = store.getSensorsByType(type);
            return Response.ok(filtered).build();
        }
        Collection<Sensor> all = store.getAllSensors();
        return Response.ok(all).build();
    }

    @POST
    public Response registerSensor(Sensor sensor, @Context UriInfo uriInfo) {
        
        String roomId = sensor.getRoomId();
        
        if (roomId == null || roomId.isBlank() || !store.roomExists(roomId)) {
            throw new LinkedResourceNotFoundException("Room", roomId);
        }

        store.addSensor(sensor);

        Room room = store.getRoom(roomId);
        if (room != null) {
            room.addSensorId(sensor.getId());
        }

        URI location = uriInfo.getAbsolutePathBuilder()
                              .path(sensor.getId())
                              .build();

        return Response.created(location)
                       .entity(sensor)
                       .build();
    }

    @Path("/{sensorId}/read")
    public SensorReadingResource getSensorReadings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
