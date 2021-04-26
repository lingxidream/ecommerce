package com.ecommerce.user.api;

import com.ecommerce.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wyr
 * @version 1.0
 * @name: UserApi
 * @description
 * @date 2021/3/10 15:43
 */
@RequestMapping
public interface UserApi {
    @GetMapping("query")
    User queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    );
}
