package com.yuan.guli;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GuliWareApplicationTests {

    @Test
  public void contextLoads() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean p = passwordEncoder.matches("$2a$10$5CEfS0oNcGg6CETcL2N2u.pon1hYma0Ni2ibRn.mnVKkXdgZ/v5VO","1716827691lzy");
        System.out.println(p);
    }

}
