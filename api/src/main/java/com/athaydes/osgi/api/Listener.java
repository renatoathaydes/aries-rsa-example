package com.athaydes.osgi.api;

import java.lang.annotation.Annotation;

/**
 *
 */
public interface Listener<M extends Annotation> {

    Class<M> messageType();

    void react( M message );

}
