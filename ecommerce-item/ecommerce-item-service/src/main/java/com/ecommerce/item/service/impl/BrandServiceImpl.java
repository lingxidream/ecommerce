package com.ecommerce.item.service.impl;

import com.ecommerce.common.pojo.PageResult;
import com.ecommerce.item.mapper.BrandMapper;
import com.ecommerce.item.pojo.Brand;
import com.ecommerce.item.service.IBrandService;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BrandServiceImpl implements IBrandService {
    @Resource
    private BrandMapper brandMapper;

    @Override
    public PageResult<Brand> queryBrandInfo(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        // 初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        // 根据name模糊查询，或者根据首字母查询
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }

        //添加分页条件
        PageHelper.startPage(page,rows);

        //添加排序条件
        if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy+ " " + (desc?"desc":"asc"));
        }

        List<Brand> brands = brandMapper.selectByExample(example);
        //包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        //返回分页结果集
        return new PageResult<Brand>(pageInfo.getTotal(),pageInfo.getList());

    }

    @Override
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌brand
        this.brandMapper.insertSelective(brand);

        //新增中间表
        cids.forEach(cid -> {
            this.brandMapper.insertBrandAndCategory(cid,brand.getId());
        });
    }
}
