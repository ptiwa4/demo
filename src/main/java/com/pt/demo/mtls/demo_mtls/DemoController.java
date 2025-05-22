package com.pt.demo.mtls.demo_mtls;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/api/demo")
    public String getDemoMessage() {
        return "Hello from the secured API!";
    }
}