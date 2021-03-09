package com.ecommerce.item.service.impl;

import com.ecommerce.common.pojo.PageResult;
import com.ecommerce.item.bo.SpuBo;
import com.ecommerce.item.mapper.*;
import com.ecommerce.item.pojo.Sku;
import com.ecommerce.item.pojo.Spu;
import com.ecommerce.item.pojo.SpuDetail;
import com.ecommerce.item.pojo.Stock;
import com.ecommerce.item.service.GoodsService;
import com.ecommerce.item.service.ICategoryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.transaction.Transaction;
import org.bouncycastle.jcajce.provider.util.SecretKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wyr
 * @version 1.0
 * @name: GoodsServiceImpl
 * @description
 * @date 2021/2/22 16:06
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);

    @Resource
    private SpuMapper spuMapper;

    @Autowired
    private ICategoryService categoryService;

    @Resource
    private BrandMapper brandMapper;
    @Resource
    private SpuDetailMapper spuDetailMapper;
    @Resource
    private SkuMapper skuMapper;
    @Resource
    private StockMapper stockMapper;

    @Resource
    private AmqpTemplate amqpTemplate;


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

    @Transactional
    @Override
    public void saveGoods(SpuBo spuBo) {
        //新增spu
        //spuBo.setId(null);
        spuBo.setCreateTime(new Date());
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);
        //新增spuDetail

        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);

        saveSkuAndStock(spuBo);

        sendMessage(spuBo.getId(),"insert");
    }

    private void saveSkuAndStock(SpuBo spuBo){
        //新增sku
        spuBo.getSkus().forEach(sku -> {
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            //新增库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });

    }


    @Override
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    @Override
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(sku);
        skus.forEach(s->{
            Stock stock = this.stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skus;
    }

    @Transactional
    @Override
    public void updateGoods(SpuBo spuBo) {
        //sku更新 先删除后新增
        //查询原来的sku信息
        List<Sku> skus = this.querySkusBySpuId(spuBo.getId());
        //删除sku信息
        if(!CollectionUtils.isEmpty(skus)){
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            // 删除以前库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);

            // 删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spuBo.getId());
            this.skuMapper.delete(record);
        }
        //新增sku信息
        this.saveSkuAndStock(spuBo);

        //修改spu
        spuBo.setLastUpdateTime(new Date());
        spuBo.setCreateTime(null);
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);

        //更新spudetail
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        sendMessage(spuBo.getId(),"update");
    }

    @Override
    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }

    @Override
    public void sendMessage(Long id, String type) {

        try {
            amqpTemplate.convertAndSend("item."+type,id);
        } catch (AmqpException e) {
            logger.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }

    }
}
