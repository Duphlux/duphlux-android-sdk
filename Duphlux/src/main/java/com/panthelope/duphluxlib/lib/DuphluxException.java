package com.panthelope.duphluxlib.lib;

import java.io.Serializable;

/**
 * Created by ikenna on 05/06/2017.
 */

public class DuphluxException extends RuntimeException implements Serializable {

    public DuphluxException(String message) {
        super(message, null);
    }

    public DuphluxException(String message, Throwable e) {
        super(message, e);
    }

}
