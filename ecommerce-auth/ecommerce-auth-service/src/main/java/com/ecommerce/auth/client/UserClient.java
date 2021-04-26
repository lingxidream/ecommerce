package com.ecommerce.auth.client;

import com.ecommerce.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wyr
 * @version 1.0
 * @name: UserClient
 * @description
 * @date 2021/3/10 15:44
 */
@FeignClient(value = "user-service")
public interface UserClient extends UserApi {
}
