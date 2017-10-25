package com.athaydes.osgi.api;

import com.google.protobuf.Message;

import java.util.function.Consumer;

/**
 *
 */
public interface MessageService extends Consumer<Message> {

    void addListener( Listener<? extends Message> listener );

}
