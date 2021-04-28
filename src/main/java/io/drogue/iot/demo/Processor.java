package io.drogue.iot.demo;

import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.drogue.iot.demo.data.DeviceCommand;
import io.drogue.iot.demo.data.DeviceEvent;

@ApplicationScoped
public class Processor {

    private static final Logger LOG = LoggerFactory.getLogger(Processor.class);

    @Incoming("event-stream")
    @Outgoing("commands")
    public DeviceCommand process(DeviceEvent event) {

        var payload = event.getPayload();

        LOG.info("Received payload: {}", payload);

        if (!event.getPayload().equals("ping")) {
            return null;
        }

        var command = new DeviceCommand();

        command.setDeviceId(event.getDeviceId());
        command.setPayload("Pong".getBytes(StandardCharsets.UTF_8));

        return command;

    }

}
