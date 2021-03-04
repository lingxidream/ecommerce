package com.ecommerce.search.controller;

import com.ecommerce.common.pojo.PageResult;
import com.ecommerce.search.pojo.Goods;
import com.ecommerce.search.pojo.SearchRequest;
import com.ecommerce.search.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author wyr
 * @version 1.0
 * @name: SearchController
 * @description
 * @date 2021/3/3 15:32
 */
@RestController
@RequestMapping
public class SearchController {
    @Resource
    private SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest request){
        PageResult<Goods> result = this.searchService.search(request);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

}
