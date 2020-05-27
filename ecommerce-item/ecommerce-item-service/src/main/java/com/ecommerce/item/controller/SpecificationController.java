package com.ecommerce.item.controller;

import com.ecommerce.item.pojo.SpecGroup;
import com.ecommerce.item.pojo.SpecParam;
import com.ecommerce.item.service.ISpecificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Resource
    private ISpecificationService specificationService;

    /**
     * 根据分类id查询商品规格组信息
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroup(@PathVariable("cid") Long cid){
        List<SpecGroup> specGroups = this.specificationService.querySpecGroup(cid);
        if(CollectionUtils.isEmpty(specGroups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specGroups);
    }

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParams(@RequestParam("gid") Long gid){
        List<SpecParam> params = this.specificationService.queryParam(gid);
        if(CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

}
