package org.aayush.controller;

import org.aayush.service.MockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Aayush Srivastava
 */
@RestController
@RequestMapping("/redis")
public class MockController {

    @Autowired
    private MockService mockService;

    @RequestMapping(value = "/distributed-lock/{transactionId}", method = RequestMethod.GET)
    private String getLock(@PathVariable String transactionId) {
        return mockService.getMockValue(transactionId);
    }
}
