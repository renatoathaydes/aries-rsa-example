package com.athaydes.osgi.server;

import com.athaydes.osgi.api.MessageService;
import com.athaydes.osgi.api.Messages.TestInfo;
import com.athaydes.osgi.api.Messages.TestResult;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, property = {
        "service.exported.interfaces=*",
        "aries.rsa.port=5556"
})
public class TestServer implements MessageService {

    private static final Logger log = LoggerFactory.getLogger(TestServer.class);

    @Activate
    public void start() {
        log.info("Started server");
    }

    @Deactivate
    public void stop() {
        log.info("Stopped server");
    }

    @Override
    public TestResult send(TestInfo message) {
        return TestResult.newBuilder()
                .setStatus(TestResult.Status.SUCCESS)
                .setInfo(message)
                .build();
    }

}
