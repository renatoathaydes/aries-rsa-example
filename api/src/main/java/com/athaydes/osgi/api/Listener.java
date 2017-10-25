package com.athaydes.osgi.api;

import com.google.protobuf.Message;

/**
 *
 */
public interface Listener<M extends Message> {

    Class<M> messageType();

    void react( M message );

}
