package com.ecommerce.item.service.impl;

import com.ecommerce.item.mapper.CategoryMapper;

import com.ecommerce.item.pojo.Category;
import com.ecommerce.item.service.ICategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> queryCategoryById(Long id) {
        Category category = new Category();
        category.setParentId(id);
        List<Category> select = categoryMapper.select(category);
        return select;
    }

    @Override
    public List<Category> queryByBrandId(Long id) {
        List<Category> categories = this.categoryMapper.queryByBrandId(id);
        return categories;
    }

    @Override
    public List<String> queryNameByBrandIds(List<Long> ids) {
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
        List<String> names = new ArrayList<>();
        for (Category item : categories) {
            names.add(item.getName());
        }
        return names;
    }
}
