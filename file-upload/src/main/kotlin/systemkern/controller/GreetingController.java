package systemkern.controller;

import org.springframework.web.bind.annotation.*;


@RestController
public class GreetingController {


    @GetMapping("/hello")
    public String helloJava() {

        System.out.println(System.getProperty("java.io.tmpdir"));
        return "Kotlin - Java connection is done!!";
    }
}