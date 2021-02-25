package com.ecommerce.item.service.impl;

import com.ecommerce.common.pojo.PageResult;
import com.ecommerce.item.bo.SpuBo;
import com.ecommerce.item.mapper.BrandMapper;
import com.ecommerce.item.mapper.SpuMapper;
import com.ecommerce.item.pojo.Spu;
import com.ecommerce.item.service.GoodsService;
import com.ecommerce.item.service.ICategoryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wyr
 * @version 1.0
 * @name: GoodsServiceImpl
 * @description
 * @date 2021/2/22 16:06
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private SpuMapper spuMapper;

    @Autowired
    private ICategoryService categoryService;

    @Resource
    private BrandMapper brandMapper;


    @Override
    public PageResult<SpuBo> querySpuBoByPage(String key,Boolean saleable,Integer page,Integer rows){
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 搜索条件
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        //分页条件
        PageHelper.startPage(page,rows);

        //查询
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        List<SpuBo> spuBos = new ArrayList<>();
        spus.forEach(l->{
            SpuBo temp = new SpuBo();
            BeanUtils.copyProperties(l,temp);
            //查询分类名称
            List<String> names = this.categoryService.queryNameByBrandIds(Arrays.asList(l.getCid1(), l.getCid2(), l.getCid3()));
            temp.setCname(StringUtils.join(names,"/"));

            //查询品牌名称
            temp.setBname(this.brandMapper.selectByPrimaryKey(l.getBrandId()).getName());

            spuBos.add(temp);
        });

        return new PageResult<>(pageInfo.getTotal(),spuBos);
    }
}
