package com.ecommerce.item.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author wyr
 * @version 1.0
 * @name: CategoryApi
 * @description
 * @date 2021/3/2 15:47
 */
@RequestMapping("category")
public interface CategoryApi {
    @GetMapping("names")
    public List<String> queryNamesByIds(@RequestParam("ids")List<Long> ids);
}
