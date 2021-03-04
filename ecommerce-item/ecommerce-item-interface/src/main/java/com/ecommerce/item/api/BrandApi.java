package com.ecommerce.item.api;

import com.ecommerce.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wyr
 * @version 1.0
 * @name: BrandApi
 * @description
 * @date 2021/3/2 15:55
 */
@RequestMapping("brand")
public interface BrandApi {
    @GetMapping("{id}")
    Brand queryBrandById(@PathVariable("id") Long id);
}
