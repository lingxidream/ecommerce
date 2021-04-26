package com.ecommerce.auth.service;

import com.ecommerce.auth.client.UserClient;
import com.ecommerce.auth.config.JwtProperties;
import com.ecommerce.auth.entity.UserInfo;
import com.ecommerce.auth.utils.JwtUtils;
import com.ecommerce.user.pojo.User;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wyr
 * @version 1.0
 * @name: AuthService
 * @description
 * @date 2021/3/10 15:29
 */
@Service
@EnableConfigurationProperties
public class AuthService {
    @Resource
    private UserClient userClient;

    @Resource
    private JwtProperties properties;

    /**
     * 用户认证
     *
     * @param username
     * @param password
     * @return
     */

    public String authentication(String username, String password) {

        try {
            // 调用微服务，执行查询
            User user = this.userClient.queryUser(username, password);
            // 如果查询结果为null，则直接返回null
            if (user == null) {
                return null;
            }
            // 如果有查询结果，则生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()), this.properties.getPrivateKey(), this.properties.getExpire());
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
