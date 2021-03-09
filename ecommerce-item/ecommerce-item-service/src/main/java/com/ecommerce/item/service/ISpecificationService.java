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
    List<SpecParam> queryParam(Long gid,Long cid,Boolean generic,Boolean searching);

    /**
     * 新增参数
     * @param param
     */
    void insertParam(SpecParam param);

    /**
     * 修改参数
     * @param param
     */
    void updateParams(SpecParam param);

    /**
     * 删除参数
     * @param cid
     */
    void deleteParam(Long cid);

    /**
     * 添加分组
     * @param group
     */
    void insertGroup(SpecGroup group);

    /**
     * 修改分组
     * @param param
     */
    void updateGroup(SpecGroup param);

    /**
     * 删除分组
     * @param cid
     */
    void deleteGroup(Long cid);

    List<SpecGroup> querySpecsByCid(Long cid);
}
