package com.ecommerce.item.service.impl;

import com.ecommerce.item.mapper.SpecGroupMapper;
import com.ecommerce.item.mapper.SpecParamMapper;
import com.ecommerce.item.pojo.SpecGroup;
import com.ecommerce.item.pojo.SpecParam;
import com.ecommerce.item.service.ISpecificationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecificationServiceImpl implements ISpecificationService {
    @Resource
    private SpecGroupMapper specGroupMapper;
    @Resource
    private SpecParamMapper specParamMapper;


    /**
     * 根据商品分类id查询商品规格组信息
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> querySpecGroup(Long cid) {
        SpecGroup t = new SpecGroup();
        t.setCid(cid);
        return this.specGroupMapper.select(t);
    }

    @Override
    public List<SpecParam> queryParam(Long gid) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        return this.specParamMapper.select(param);
    }

    @Override
    public void insertParam(SpecParam param) {
        this.specParamMapper.insertSelective(param);
    }

    @Override
    public void updateParams(SpecParam param) {
        this.specParamMapper.updateByPrimaryKey(param);
    }

    @Override
    public void deleteParam(Long cid) {
        this.specParamMapper.deleteByPrimaryKey(cid);
    }

    @Override
    public void insertGroup(SpecGroup group){
        this.specGroupMapper.insert(group);
    }

    @Override
    public void updateGroup(SpecGroup param) {
        this.specGroupMapper.updateByPrimaryKey(param);
    }

    @Override
    public void deleteGroup(Long cid) {
        this.specGroupMapper.deleteByPrimaryKey(cid);
    }
}
