package com.example.demo;

import com.example.demo.model.RespRekv;
import com.example.demo.model.Response;
import com.example.demo.service.CreateTableService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final CreateTableService service;

    public TestController(CreateTableService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public void createTable() {
        service.test();
    }

    @GetMapping("/respRekv")
    public Response<RespRekv> getRespRekv() {
        return service.getRespRekv();
    }
}
