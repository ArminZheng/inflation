package com.arminzheng.inflation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingress")
public class IngressController {

    @GetMapping("ping")
    public String ping() {
        return "pong";
    }

}
