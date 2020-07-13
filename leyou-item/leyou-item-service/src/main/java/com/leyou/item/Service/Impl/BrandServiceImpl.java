package com.leyou.item.Service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.Service.BrandService;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageResult<Brand> queryBrands(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        //模糊查询条件
        criteria.andLike("name","%"+ key +"%").orEqualTo("letter",key);
        //添加分页条件
        PageHelper.startPage(page,rows);
        //按照什么排序,是否降序
        if (StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy+" "+(desc? "desc":"asc"));

        }

        //根据条件查出List<Brand>
        List<Brand> list =brandMapper.selectByExample(example);
        //放进分页对象
        PageInfo<Brand> pageInfo =new PageInfo<>(list);


        return new PageResult<Brand>(pageInfo.getTotal(),pageInfo.getList());
    }

    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //先保存brand对象
        brandMapper.insertSelective(brand);

        //分别将brand对象的id   跟分类cid绑定在  category_brand这张中间表中
        cids.forEach(cid->{
            brandMapper.insertCategoryAndBrand(brand.getId(),cid);
        });
    }

    @Override
    public List<Brand> queryBrandByCid(Long cid) {
        return brandMapper.queryBrandByCid(cid);
    }

    @Override
    public Brand queryBrandById(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}
