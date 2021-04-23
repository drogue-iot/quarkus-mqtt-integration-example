package io.drogue.iot.demo;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.reactive.RestSseElementType;

import io.drogue.iot.demo.data.DeviceEvent;
import io.smallrye.mutiny.Multi;

@Path("/events")
public class EventsResource {

    @Inject
    @Channel("event-stream")
    Multi<DeviceEvent> events;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.APPLICATION_JSON)
    public Multi<DeviceEvent> stream() {
        return this.events;
    }
}
