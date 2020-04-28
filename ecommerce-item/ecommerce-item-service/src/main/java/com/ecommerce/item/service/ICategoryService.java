package com.ecommerce.item.service;

import com.ecommerce.item.pojo.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> queryCategoryById(Long id);
    List<Category> queryByBrandId(Long id);
}
