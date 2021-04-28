package io.drogue.iot.demo;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.drogue.iot.demo.data.DeviceCommand;
import io.quarkus.runtime.Startup;

@Startup
@ApplicationScoped
public class Sender {

    private static final Logger LOG = LoggerFactory.getLogger(Sender.class);

    @Incoming("commands")
    public void commands(DeviceCommand command) {
        LOG.info("Request to send device command: {}", command);
    }

}
