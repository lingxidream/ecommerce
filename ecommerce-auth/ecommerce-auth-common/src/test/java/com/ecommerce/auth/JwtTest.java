package com.ecommerce.auth;

import com.ecommerce.auth.entity.UserInfo;
import com.ecommerce.auth.utils.JwtUtils;
import com.ecommerce.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author wyr
 * @version 1.0
 * @name: JwtTest
 * @description
 * @date 2021/3/10 15:04
 */
@SpringBootTest
public class JwtTest {
    private static final String pubKeyPath = "C:\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "C:\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTYxNTM2MDY2NH0.iU10E97yBsWKeoqMBkKN87Q1ntrd1mC5saBGBqq8G2SgxfePIsCQ3G1ATAP4oVuS_EkLzQRqmRchB5-hSphYx6XVImI3WZpWSfrX_hCGz5YNBeqDU4oUmg7ic_L-GaZHBeVAcSCeXpKu2TA2cL7YqMlbnHRD9-aJE_E7mVuTqkk";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
