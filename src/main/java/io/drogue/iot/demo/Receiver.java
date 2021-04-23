package io.drogue.iot.demo;

import static io.cloudevents.core.CloudEventUtils.mapData;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import io.drogue.iot.demo.data.DeviceEvent;
import io.drogue.iot.demo.data.TtnUplink;
import io.quarkus.runtime.Startup;
import io.smallrye.reactive.messaging.annotations.Broadcast;

@Startup
@ApplicationScoped
public class Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

    @Inject
    ObjectMapper objectMapper;

    @Incoming("telemetry")
    @Outgoing("event-stream")
    @Broadcast
    public DeviceEvent process(Message<byte[]> rawMessage) {

        // we always ack, as we don't care about errors in this demo

        rawMessage.ack();

        // start processing

        EventFormat format = EventFormatProvider
                .getInstance()
                .resolveFormat(JsonFormat.CONTENT_TYPE);

        CloudEvent event = format.deserialize(rawMessage.getPayload());

        LOG.info("Received event: {}", event);

        var ttnMessage = mapData(
                event,
                PojoCloudEventDataMapper.from(this.objectMapper, TtnUplink.class)
        );
        var uplink = ttnMessage.getValue().getUplinkMessage();

        // create device event

        var device = new DeviceEvent();
        device.setDeviceId(event.getExtension("device").toString());
        device.setTimestamp(uplink.getReceivedAt());
        device.setPayload(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(uplink.getPayload())).toString());

        // done

        return device;
    }

}