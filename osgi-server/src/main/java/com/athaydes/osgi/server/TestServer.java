package com.athaydes.osgi.server;

import com.athaydes.osgi.api.Listener;
import com.athaydes.osgi.api.MessageService;
import com.athaydes.osgi.api.Messages.TestInfo;
import com.athaydes.osgi.api.Messages.TestResult;
import com.google.protobuf.Message;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Component( immediate = true )
public class TestServer implements MessageService {

    private final Map<Class<? extends Message>, List<Listener<?>>> listenerByType = new HashMap<>();

    @Activate
    public void start() {
        addListener( new TestInfoListener() );
    }

    @Override
    public void addListener( Listener<? extends Message> listener ) {
        listenerByType.merge( listener.messageType(), singletonList( listener ), ListHelper::concat );
    }

    @Override
    public void accept( Message message ) {
        List<Listener<?>> listeners = listenerByType.getOrDefault( message.getClass(), emptyList() );

        if ( listeners.isEmpty() ) {
            System.out.println( "No listeners registered for message: " + message );
        } else {
            System.out.printf( "Posting message of type %s to %d listeners\n",
                    message.getClass().getSimpleName(), listeners.size() );
            listeners.forEach( listener -> react( listener, message ) );
        }
    }

    private static <M extends Message> void react( Listener<M> listener,
                                                   Message message ) {
        listener.react( listener.messageType().cast( message ) );
    }

    private class TestInfoListener implements Listener<TestInfo> {

        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        @Override
        public Class<TestInfo> messageType() {
            return TestInfo.class;
        }

        @Override
        public void react( TestInfo message ) {
            TestResult result = TestResult.newBuilder()
                    .setInfo( message )
                    .setStatus( TestResult.Status.SUCCESS )
                    .build();

            service.schedule( () -> accept( result ), 2, TimeUnit.SECONDS );
        }
    }

}
