package com.leyou.item.Service;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.PageResult;

import java.util.List;

public interface BrandService {
    PageResult<Brand> queryBrands(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    void saveBrand(Brand brand, List<Long> cids);

    List<Brand> queryBrandByCid(Long cid);

    Brand queryBrandById(Long id);

}
