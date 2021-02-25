package com.ecommerce.item.service;

import com.ecommerce.common.pojo.PageResult;
import com.ecommerce.item.bo.SpuBo;

/**
 * @author wyr
 * @version 1.0
 * @name: GoodsService
 * @description
 * @date 2021/2/22 16:06
 */
public interface GoodsService {
    /**
     * 分页查询商品信息
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows);
}
