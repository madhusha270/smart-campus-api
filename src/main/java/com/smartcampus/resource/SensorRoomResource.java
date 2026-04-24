package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Collection;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoomResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllRooms() {
        Collection<Room> rooms = store.getAllRooms();
        return Response.ok(rooms).build();
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Room ID is required").build();
        }
        
        if (store.getRoom(room.getId()) != null) {
            return Response.status(Response.Status.CONFLICT).entity("Room already exists").build();
        }
        
        if (room.getSensorIds() == null) {
            room.setSensorIds(new java.util.ArrayList<>());
        }
        
        store.addRoom(room);
        
        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(location).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Room not found").build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Room not found").build();
        }
        
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(roomId, room.getSensorIds().size());
        }
        
        store.removeRoom(roomId);
        return Response.noContent().build();
    }
}
