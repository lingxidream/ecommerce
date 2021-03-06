package com.ecommerce.item.bo;

import com.ecommerce.item.pojo.Sku;
import com.ecommerce.item.pojo.Spu;
import com.ecommerce.item.pojo.SpuDetail;
import lombok.Data;

import java.util.List;

/**
 * @author wyr
 * @version 1.0
 * @name: SpuBo
 * @description
 * @date 2020/11/16 14:55
 */
@Data
public class SpuBo extends Spu {
    String cname;// 商品分类名称

    String bname;// 品牌名称

    SpuDetail spuDetail;//商品详情

    List<Sku> skus;//sku列表
}
