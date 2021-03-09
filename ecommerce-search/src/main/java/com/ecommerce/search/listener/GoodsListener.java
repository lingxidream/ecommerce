package com.ecommerce.search.listener;

import com.ecommerce.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author wyr
 * @version 1.0
 * @name: GoodsListener
 * @description
 * @date 2021/3/9 15:57
 */
@Component
public class GoodsListener {
    @Resource
    private SearchService searchService;

    /**
     * 处理insert和update的消息
     * @param id
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ecommerce.create.index.queue",durable = "true"),
            exchange = @Exchange(
                    value = "ecommerce.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.insert","item.update"}
    ))
    public void listenCreate(Long id) throws IOException {
        if(id == null){
            return;
        }
        this.searchService.createIndex(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ecommerce.delete.index.queue",durable = "true"),
            exchange = @Exchange(
                    value = "ecommerce.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = "item.delete"
    ))
    public void listenDelete(Long id) {
        if(id == null){
            return;
        }
        this.searchService.deleteIndex(id);
    }
}
