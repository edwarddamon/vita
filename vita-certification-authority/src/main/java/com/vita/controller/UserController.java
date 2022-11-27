package com.vita.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Edward
 * @date 2022-11-27 20:53
 */
@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @GetMapping(value = "/test")
    public String test(){
        log.debug("come to test...");
        return "success";
    }

}
