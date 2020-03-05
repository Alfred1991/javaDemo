package org.xiaofengcanyue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @SpringBootApplication 等价于使用
 * @Configuration,@EnableAutoConfiguration和@ComponentScan
 */

@RestController
@EnableAutoConfiguration
public class Example {

    @RequestMapping("/")
    String home() {
        return "Hello World";
    }

    public static void main(String[] args) {
        SpringApplication.run(Example.class,args);
    }

}
