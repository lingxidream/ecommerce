package com.ecommerce.search.service;

import com.ecommerce.common.pojo.PageResult;
import com.ecommerce.item.pojo.*;
import com.ecommerce.search.client.BrandClient;
import com.ecommerce.search.client.CategoryClient;
import com.ecommerce.search.client.GoodsClient;
import com.ecommerce.search.client.SpecificationClient;
import com.ecommerce.search.pojo.Goods;
import com.ecommerce.search.pojo.SearchRequest;
import com.ecommerce.search.pojo.SearchResult;
import com.ecommerce.search.repository.GoodsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.naming.Name;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wyr
 * @version 1.0
 * @name: SearchService
 * @description
 * @date 2021/3/2 16:24
 */
@Service
public class SearchService {
    @Resource
    private BrandClient brandClient;
    @Resource
    private CategoryClient categoryClient;
    @Resource
    private GoodsClient goodsClient;
    @Resource
    private SpecificationClient specificationClient;
    @Resource
    private GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods bulidGoods(Spu spu) throws IOException {
        //创建Goods对象
        Goods goods = new Goods();

        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        //查询分类名称
        List<String> category = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //查询spu下的sku信息
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();
        List<Map<String,Object>> skuMapList = new ArrayList<>();
        //遍历sku信息，把信息加到map集合中
        skus.forEach(sku->{
            prices.add(sku.getPrice());
            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price", sku.getPrice());
            map.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(map);
        });

        //查询搜索参数
        List<SpecParam> params = this.specificationClient.queryParams(null, spu.getCid3(), null, true);

        //查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        // 获取通用的规格参数
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });
        //获取特殊规格参数
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {

        });

        // 定义map接收{规格参数名，规格参数值}
        Map<String, Object> paramMap = new HashMap<>();
        params.forEach(param -> {
            // 判断是否通用规格参数
            if (param.getGeneric()) {
                // 获取通用规格参数值
                String value = genericSpecMap.get(param.getId()).toString();
                // 判断是否是数值类型
                if (param.getNumeric()){
                    // 如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value, param);
                }
                // 把参数名和值放入结果集中
                paramMap.put(param.getName(), value);
            } else {
                paramMap.put(param.getName(), specialSpecMap.get(param.getId()));
            }
        });

        //设置参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle() + brand.getName() + StringUtils.join(category," "));
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(paramMap);

        return goods;



    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public SearchResult search(SearchRequest request) {
        String key = request.getKey();
        if(StringUtils.isBlank(request.getKey())){
            return null;
        }
        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //1 对key进行全文检索查询
        //queryBuilder.withQuery(QueryBuilders.matchQuery("all",key).operator(Operator.AND));
        BoolQueryBuilder boolQueryBuilder = bulidboolQueryBuilder(request);
        queryBuilder.withQuery(boolQueryBuilder);


        // 2、通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(
                new String[]{"id","skus","subTitle"},null
        ));
        // 3、 分页
        // 准备分页参数
        int page = request.getPage();
        int size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page-1,size));

        //添加品牌和分类的聚合条件
        String categoryAggName = "categories";
        String brandAggName = "brands";

        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //4 查询获取结果
        AggregatedPage<Goods> pageInfo = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());

        // 解析聚合结果集
        List<Map<String, Object>> categories = getCategoryAggResult(pageInfo.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(pageInfo.getAggregation(brandAggName));

        //判断分类聚合的结果集大小，等于1
        List<Map<String, Object>> specs = new ArrayList<>();
        if(categories.size() == 1){
            specs = getParamAggResult((Long) categories.get(0).get("id"),boolQueryBuilder);
        }

        //封装结果并返回
        return new SearchResult(pageInfo.getTotalElements(),pageInfo.getTotalPages(),pageInfo.getContent(),categories,brands,specs);
    }

    /**
     * 聚合出规格参数过滤条件
     * @param id
     * @param boolQueryBuilder
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long id, BoolQueryBuilder boolQueryBuilder) {
        //创建自定义查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //基于基本的查询条件，聚合规格参数
        queryBuilder.withQuery(boolQueryBuilder);
        //查询要聚合的规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, id, null, true);
        //添加聚合
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs."+param.getName()+".keyword"));
        });
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));

        //执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        //定义一个集合收集聚合结果集
        List<Map<String, Object>> paramMapList = new ArrayList<>();

        //解析聚合结果集
        Map<String, Aggregation> stringAggregationMap = goodsPage.getAggregations().asMap();
        stringAggregationMap.forEach((k,v)->{
            Map<String, Object> map =new HashMap<>();
            //放入规格参数名
            map.put("k",k);
            //收集规格参数值
            List<Object> option = new ArrayList<>();
            //解析每个聚合
            StringTerms terms = (StringTerms) v;
            // 遍历每个聚合中桶，把桶中key放入收集规格参数的集合中
            terms.getBuckets().forEach(bucket -> option.add(bucket.getKeyAsString()));
            map.put("options",option);
            paramMapList.add(map);
        });

        return paramMapList;

    }

    /**
     * 构建bool查询构建器
     * @param request
     * @return
     */
    private BoolQueryBuilder bulidboolQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));

        //添加过滤条件
        if(CollectionUtils.isEmpty(request.getFilter())){
            return boolQueryBuilder;
        }
        request.getFilter().forEach((k,v)->{
            String key = k;
            //如果过滤条件是品牌，过滤字段名是brandId
            if(StringUtils.equals("品牌",key)){
                key = "brandId";
            }else if(StringUtils.equals("分类",key)){
                //过滤条件是分类，规律字段为 cid3
                key = "cid3";
            }else {
                //规格参数名，过滤字段：specs.key.keyword
                key = "specs."+ key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,v));
        });

        return boolQueryBuilder;
    }

    /**
     * 解析分类
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        //处理聚合结果集
        LongTerms terms = (LongTerms) aggregation;
        //获取所有分类id桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        //定义一个品牌集合，搜集所有的品牌对象
        //List<Map<String, Object>> categories = new ArrayList<>();
        //解析所有的id桶，查询品牌
        return buckets.stream().map(bucket -> {
            long cid = bucket.getKeyAsNumber().longValue();
            List<String> name = this.categoryClient.queryNamesByIds(Collections.singletonList(cid));
            Map<String, Object> map = new HashMap<>();
            map.put("id", cid);
            map.put("nmae", name.get(0));
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 解析品牌聚合结果集
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        //处理聚合结果集
        LongTerms terms = (LongTerms) aggregation;
        //获取所有品牌id桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        return buckets.stream().map(bucket -> {
            return this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
    }


    /**
     * 根据商品id创建索引库
     * @param id
     * @throws IOException
     */
    public void createIndex(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuById(id);
        //构建商品
        Goods goods = this.bulidGoods(spu);
        //保存到索引库
        goodsRepository.save(goods);
    }

    /**
     * 根据商品id删除索引库
     * @param id
     */
    public void deleteIndex(Long id){
        this.goodsRepository.deleteById(id);
    }



}
