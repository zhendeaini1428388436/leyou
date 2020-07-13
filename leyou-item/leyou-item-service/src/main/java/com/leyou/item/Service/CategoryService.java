package com.leyou.item.Service;

import com.leyou.item.pojo.Category;

import java.util.List;

public interface CategoryService {
    List<Category> queryCategoriesByPid(Long pid);
    List<String> queryCategoryByIds(List<Long> ids);
}
