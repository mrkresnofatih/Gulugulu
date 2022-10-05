package com.mrkresnofatihdev.gulugulu.utilities;

import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHelper {
    public static <T> ResponseEntity<ResponseModel<T>> BuildOkResponse(T data) {
        return new ResponseEntity<>(new ResponseModel<>(data, null), HttpStatus.OK);
    }

    public static <T> ResponseEntity<ResponseModel<T>> BuildBadResponse(String errorMessage) {
        return new ResponseEntity<>(new ResponseModel<>(null, errorMessage), HttpStatus.BAD_REQUEST);
    }
}
