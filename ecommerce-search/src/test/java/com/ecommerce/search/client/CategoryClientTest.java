package com.ecommerce.search.client;

import com.ecommerce.EcommerceSearchApplication;
import com.ecommerce.common.pojo.PageResult;
import com.ecommerce.item.bo.SpuBo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author wyr
 * @version 1.0
 * @name: CategoryClientTest
 * @description
 * @date 2021/3/2 16:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EcommerceSearchApplication.class)
public class CategoryClientTest {
    @Resource
   private CategoryClient categoryClient;
    @Resource
    private GoodsClient goodsClient;

    @Test
    public void test(){
//
//        List<String> strings = this.categoryClient.queryNamesByIds(Arrays.asList(1L, 2L, 3L));
//        strings.forEach(System.out::print);

        PageResult<SpuBo> pageResult = this.goodsClient.querySupBoByPage(null, true, 1, 100);
        System.out.println(pageResult.getTotalPage());
    }

}
