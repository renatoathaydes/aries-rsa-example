package com.athaydes.osgi.client;

import com.athaydes.javanna.Javanna;
import com.athaydes.osgi.api.Listener;
import com.athaydes.osgi.api.MessageService;
import com.athaydes.osgi.api.messages.TestInfo;
import com.athaydes.osgi.api.messages.TestResult;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
@Component( immediate = true )
public class MessageSender {

    @Reference
    volatile MessageService messageService;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Activate
    public void init() {
        executor.submit( () -> {
            messageService.accept( Javanna.createAnnotation( TestInfo.class, new HashMap<String, Object>() {{
                put( "testName", "First test" );
                put( "testClass", String.class );
            }} ) );

            messageService.addListener( new MessageReceiver() );
        } );
    }

    private static class MessageReceiver implements Listener<TestResult> {
        @Override
        public Class<TestResult> messageType() {
            return TestResult.class;
        }

        @Override
        public void react( TestResult message ) {
            System.out.println( "Client received a message: " + message );
        }
    }

}
