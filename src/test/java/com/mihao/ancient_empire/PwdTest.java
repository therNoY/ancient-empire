package com.mihao.ancient_empire;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PwdTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void name() {
        System.out.println(passwordEncoder.encode("12345"));
        System.out.println(passwordEncoder.encode("12345"));
        System.out.println(passwordEncoder.encode("12345"));
        System.out.println(passwordEncoder.encode("12345"));
    }

    @Test
    public void name2() {
        System.out.println( passwordEncoder.matches("12345", passwordEncoder.encode("12345")));
    }
}
