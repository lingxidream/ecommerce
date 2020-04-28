package com.ecommerce.item.controller;

import com.ecommerce.common.pojo.PageResult;
import com.ecommerce.item.pojo.Brand;
import com.ecommerce.item.service.IBrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Resource
    private IBrandService brandService;

    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",required = false) Boolean desc
    ){
        PageResult<Brand> result = this.brandService.queryBrandInfo(key, page, rows, sortBy, desc);
        if(CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }


    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam("cids") List<Long> cids){
        brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
