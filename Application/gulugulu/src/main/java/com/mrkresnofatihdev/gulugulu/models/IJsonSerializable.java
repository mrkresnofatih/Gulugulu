package com.mrkresnofatihdev.gulugulu.models;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IJsonSerializable {
    String toJsonSerialized() throws JsonProcessingException;
}
