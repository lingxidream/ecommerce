package com.ecommerce.goods.client;

import com.ecommerce.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wyr
 * @version 1.0
 * @name: GoodsClient
 * @description
 * @date 2021/3/2 15:57
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
