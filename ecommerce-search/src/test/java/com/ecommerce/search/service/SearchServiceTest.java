package com.ecommerce.search.service;

import com.ecommerce.EcommerceSearchApplication;
import com.ecommerce.common.pojo.PageResult;
import com.ecommerce.item.bo.SpuBo;
import com.ecommerce.item.pojo.Spu;
import com.ecommerce.search.client.GoodsClient;
import com.ecommerce.search.pojo.Goods;
import com.ecommerce.search.repository.GoodsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author wyr
 * @version 1.0
 * @name: SearchServiceTest
 * @description
 * @date 2021/3/2 17:16
 */
@SpringBootTest(classes = EcommerceSearchApplication.class)
@RunWith(SpringRunner.class)
public class SearchServiceTest {
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private SearchService searchService;
    @Resource
    private GoodsClient goodsClient;


    @Test
    public void bulidGoods() {
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);

        Integer page = 1;
        int rows = 100;

        while (rows==100) {
            PageResult<SpuBo> pageResult = this.goodsClient.querySupBoByPage(null,true, page, rows);
            List<Goods> goods = pageResult.getItems().stream().map(spuBo -> {
                try {
                    return this.searchService.bulidGoods((Spu) spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            this.goodsRepository.saveAll(goods);
            page++;
            rows = pageResult.getItems().size();
        }
    }
}