package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("/discovery")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response getApiDiscovery(@Context UriInfo uriInfo) {

        // Dynamically get the base URI (e.g., http://localhost:8080/cw/api/v1/)
        String base = uriInfo.getBaseUri().toString();
        
        
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        Map<String, Object> discovery = new LinkedHashMap<>();
        discovery.put("apiName", "Smart Campus Sensor & Room Management API");
        discovery.put("apiVersion", "1.0.0");
        discovery.put("description",
                "RESTful API for managing rooms, sensors, and sensor readings " +
                "across the university campus as part of the Smart Campus initiative.");
        
   
        discovery.put("framework", "JAX-RS (Jersey) on Apache Tomcat 9");

        Map<String, String> contact = new LinkedHashMap<>();
        contact.put("name", "Madhusha");
        contact.put("email", "student@live.westminster.ac.uk");
        contact.put("module", "5COSC022W — Client-Server Architectures");
        discovery.put("contact", contact);

        Map<String, Object> links = new LinkedHashMap<>();

  
        links.put("self", buildLink(base + "/discovery",
                "self", "GET", "This discovery endpoint"));
        
        links.put("rooms", buildLink(base + "/rooms",
                "rooms", "GET", "List all rooms on campus"));
        
        links.put("room_create", buildLink(base + "/rooms",
                "room_create", "POST", "Register a new room"));
        
        links.put("sensors", buildLink(base + "/sensors",
                "sensors", "GET", "List all sensors (supports ?type= filter)"));
        
        links.put("sensor_register", buildLink(base + "/sensors",
                "sensor_register", "POST", "Register a new sensor"));
        
        discovery.put("links", links);
        discovery.put("serverTime", Instant.now().toString());

        return Response.ok(discovery).build();
    }

    private Map<String, String> buildLink(String href, String rel,
                                          String method, String description) {
        Map<String, String> link = new LinkedHashMap<>();
        link.put("href", href);
        link.put("rel", rel);
        link.put("method", method);
        link.put("description", description);
        return link;
    }
}
