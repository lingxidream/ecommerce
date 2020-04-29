package com.ecommerce.item.service;

import com.ecommerce.common.pojo.PageResult;
import com.ecommerce.item.pojo.Brand;

import java.util.List;

public interface IBrandService {
    PageResult<Brand> queryBrandInfo(String key, Integer page, Integer rows, String sortBy, Boolean desc);
    void saveBrand(Brand brand, List<Long> cids);

    void updateBrand(Brand brand,List<Long> cids);

    void deleteBrand(Brand brand);
}
