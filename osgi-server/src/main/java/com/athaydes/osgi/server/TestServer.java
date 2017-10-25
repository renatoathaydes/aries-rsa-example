package com.athaydes.osgi.server;

import com.athaydes.javanna.Javanna;
import com.athaydes.osgi.api.Listener;
import com.athaydes.osgi.api.MessageService;
import com.athaydes.osgi.api.messages.TestInfo;
import com.athaydes.osgi.api.messages.TestResult;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Component( service = MessageService.class )
public class TestServer implements MessageService {

    private final Map<Class<? extends Annotation>, List<Listener<?>>> listenerByType = new HashMap<>();

    @Activate
    public void start() {
        addListener( new TestInfoListener() );
    }

    @Override
    public void addListener( Listener<? extends Annotation> listener ) {
        listenerByType.merge( listener.messageType(), singletonList( listener ), ListHelper::concat );
    }

    @Override
    public void accept( Annotation message ) {
        List<Listener<?>> listeners = listenerByType.getOrDefault( message.annotationType(), emptyList() );

        if ( listeners.isEmpty() ) {
            System.out.println( "No listeners registered for message: " + message );
        } else {
            System.out.printf( "Posting message of type %s to %d listeners\n",
                    message.annotationType().getSimpleName(), listeners.size() );
            listeners.forEach( listener -> react( listener, message ) );
        }
    }

    private static <M extends Annotation> void react( Listener<M> listener,
                                                      Annotation message ) {
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
            TestResult result = Javanna.createAnnotation( TestResult.class, new HashMap<String, Object>() {{
                put( "testInfo", message );
                put( "success", true );
            }} );

            service.schedule( () -> accept( result ), 2, TimeUnit.SECONDS );
        }
    }

}
