package com.ecommerce.goods.client;

import com.ecommerce.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wyr
 * @version 1.0
 * @name: SpecificationClient
 * @description
 * @date 2021/3/2 16:02
 */
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi {
}
