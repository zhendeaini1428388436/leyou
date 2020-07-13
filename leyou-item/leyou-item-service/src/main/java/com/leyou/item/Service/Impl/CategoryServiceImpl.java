package com.leyou.item.Service.Impl;

import com.leyou.item.Service.CategoryService;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> queryCategoriesByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);

        return categoryMapper.select(category);
    }

    public List<String> queryCategoryByIds(List<Long> ids){
        List<Category> categories = categoryMapper.selectByIdList(ids);
        List<String> names = categories.stream().map(category -> category.getName()).collect(Collectors.toList());

        return names;
    }
}









