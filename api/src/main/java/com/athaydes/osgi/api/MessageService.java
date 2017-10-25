package com.athaydes.osgi.api;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

/**
 *
 */
public interface MessageService extends Consumer<Annotation> {

    void addListener( Listener<? extends Annotation> listener );

}
