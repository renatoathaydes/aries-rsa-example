package com.athaydes.osgi.server;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ListHelper {

    public static <T> List<T> concat( List<T> first, List<T> second ) {
        List<T> result = new ArrayList<>( first.size() + second.size() );
        result.addAll( first );
        result.addAll( second );
        return result;
    }

}
