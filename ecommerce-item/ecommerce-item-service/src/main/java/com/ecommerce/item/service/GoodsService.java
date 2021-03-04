package com.ecommerce.item.service;

import com.ecommerce.common.pojo.PageResult;
import com.ecommerce.item.bo.SpuBo;
import com.ecommerce.item.pojo.Sku;
import com.ecommerce.item.pojo.SpuDetail;

import java.util.List;

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

    /**
     * 添加商品信息
     * @param spuBo
     */
    void saveGoods(SpuBo spuBo);

    /**
     * 根据 spuid查询spu详情信息
     * @param spuId
     * @return
     */
    SpuDetail querySpuDetailBySpuId(Long spuId);

    /**
     * 根据spuId查询sku的集合
     * @param spuId
     * @return
     */
    List<Sku> querySkusBySpuId(Long spuId);

    /**
     * 修改商品信息
     * @param spuBo
     */
    void updateGoods(SpuBo spuBo);
}
