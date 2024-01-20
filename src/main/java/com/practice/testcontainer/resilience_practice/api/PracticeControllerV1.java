package com.practice.testcontainer.resilience_practice.api;

import com.practice.testcontainer.resilience_practice.service.PracticeServiceV1;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/v1")
public class PracticeControllerV1 {

    @Autowired
    private PracticeServiceV1 service;
    @GetMapping
    public String getString(){
        return service.getString("run service at v1 : value=" + UUID.randomUUID());
    }
}
