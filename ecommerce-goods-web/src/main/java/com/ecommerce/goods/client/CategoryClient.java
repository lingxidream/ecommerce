package com.ecommerce.goods.client;

import com.ecommerce.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wyr
 * @version 1.0
 * @name: CategoryClient
 * @description
 * @date 2021/3/2 15:59
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
