package com.yuan.guli;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Md5Test {

    @Test
    public void test() throws NoSuchAlgorithmException {
        String mds_str = DigestUtils.md5Hex("666");
        System.out.println(mds_str); //fae0b27c451c728867a567e8c1bb4e53

        //盐值加密:可以自己定义盐值
     //   String s = Md5Crypt.md5Crypt("123456".getBytes(),"1$zktzktzkt");
        String s = Md5Crypt.md5Crypt("123456".getBytes());
        System.out.println(s);


        //密码编码器：
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode("123456");
        boolean p = bCryptPasswordEncoder.matches("123456", "$2a$10$hH4gCT4o4ULrvEmgIDE6nu2PFXBkuPsRDCGdCAjSQ8PuI9CoyPDJS");
        System.out.println("编码加密："+encode);
        System.out.println("是否匹配："+p);

    }
}
