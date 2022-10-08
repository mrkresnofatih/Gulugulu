package com.mrkresnofatihdev.gulugulu.exceptions;

public class RecordNotFoundException extends Exception {
    public RecordNotFoundException() {
        super("Cannot Find Record In Persistence Store with Provided Identifier");
    }
}
