package com.ecommerce.goods.service;

import com.ecommerce.goods.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author wyr
 * @version 1.0
 * @name: GoodsHtmlService
 * @description
 * @date 2021/3/8 18:01
 */
@Service
public class GoodsHtmlService {
    @Resource
    private GoodsService goodsService;
    @Resource
    private TemplateEngine templateEngine;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHtmlService.class);

    public void createHtml(Long spuId){
        PrintWriter writer = null;
        try {
            //获取数据
            Map<String, Object> supMap = this.goodsService.loadData(spuId);

            //创建thymeleaf上下文对象
            Context context = new Context();
            //把数据放入到上下文对象中
            context.setVariables(supMap);

            //创建输出流
            File file = new File("C:\\MyJava\\nginx-1.14.0\\html\\item\\"+spuId+".html");
            writer = new PrintWriter(file);

            //执行页面静态化方法
            templateEngine.process("item",context,writer);
        }catch (Exception e){
            LOGGER.error("页面静态化出错：{}，"+ e, spuId);
        }finally {
            if(writer != null){
                writer.close();
            }
        }
    }

    /**
     * 新建线程处理页面静态化
     * @param spuId
     */
    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(()->createHtml(spuId));
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }

    /**
     * 根据id删除本地生成的静态页面
     * @param id
     */
    public void deleteHtml(Long id) {
        File file = new File("C:\\project\\nginx-1.14.0\\html\\item\\", id + ".html");
        file.deleteOnExit();
    }
}
