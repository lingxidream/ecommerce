package com.ecommerce.item.service;

import com.ecommerce.item.pojo.SpecGroup;
import com.ecommerce.item.pojo.SpecParam;

import java.util.List;

public interface ISpecificationService {
    /**
     * 根据分类id查询分组
     * @param cid
     * @return
     */
    List<SpecGroup> querySpecGroup(Long cid);

    /**
     * 查询参数
     * @param gid
     * @return
     */
    List<SpecParam> queryParam(Long gid);
}
