package com.ecommerce.search.repository;

import com.ecommerce.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author wyr
 * @version 1.0
 * @name: GoodsReponsitory
 * @description
 * @date 2021/3/2 16:22
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
