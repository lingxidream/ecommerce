package com.ecommerce.search.client;

import com.ecommerce.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wyr
 * @version 1.0
 * @name: BrandClient
 * @description
 * @date 2021/3/2 16:00
 */
@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
