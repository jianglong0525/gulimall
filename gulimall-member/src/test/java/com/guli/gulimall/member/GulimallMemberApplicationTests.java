package com.guli.gulimall.member;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class GulimallMemberApplicationTests {

    @Test
    void contextLoads() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode("123456");
        System.out.println(encode);
        bCryptPasswordEncoder.matches("123456","$2a$10$QJcGpHlGSq8PTaPSwXrDMeaGbfoket7pF8gFXBXn2F0tocTdjnRje");
    }

}
