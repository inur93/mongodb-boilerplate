package com.vormadal.mongodb.exceptions;

/**
 * Created: 21-09-2018
 * author: Runi
 */

public class MorphiaException extends Exception {
    public MorphiaException(String msg){
        this(msg, null);
    }

    public MorphiaException(String msg, Throwable cause){
        super(msg, cause);
    }
}
