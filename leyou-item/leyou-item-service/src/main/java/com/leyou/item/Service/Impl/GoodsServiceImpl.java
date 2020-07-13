package com.leyou.item.Service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.Service.CategoryService;
import com.leyou.item.Service.GoodsService;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import jdk.nashorn.internal.ir.CallNode;
import org.apache.commons.lang.StringUtils;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final Logger logger= LoggerFactory.getLogger(GoodsServiceImpl.class);

    @Override
    public PageResult<SpuBo> queryGoods(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //模糊查询条件
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+ key +"%");
        }

        //是否上架条件
        if (saleable !=null){
            criteria.andEqualTo("saleable",saleable);
        }

        //分页条件
        PageHelper.startPage(page,rows);


        //执行结果
        List<Spu> spus = spuMapper.selectByExample(example);
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);

        //将结果list<spu>转换为List<SpuBo>
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            //copy共同属性到新的对象
            BeanUtils.copyProperties(spu,spuBo);
            //根据brand_id查询品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            //根据cids 查询category名称
            List<String> cnames = categoryService.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            String canme = StringUtils.join(cnames, "-");
            spuBo.setCname(canme);

            return spuBo;
        }).collect(Collectors.toList());

        //返回分页结果集
        return new PageResult<SpuBo>(spuPageInfo.getTotal(),spuBos);
    }


    @Transactional
    public void saveSpuBo(SpuBo spuBo) {
        //先增加spu
        spuBo.setId(null);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuMapper.insertSelective(spuBo);
        //再增加SpuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());

        spuDetailMapper.insertSelective(spuDetail);

        saveSkuandStock(spuBo);

        sendMsg("insert",spuBo.getId());

    }

    //封装一个发送消息的方法
    private void sendMsg(String type,Long id) {
        try {
            this.amqpTemplate.convertAndSend("item."+type,id);
        } catch (AmqpException e) {
            logger.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }
    }

    //抽取一个方法
    private void saveSkuandStock(SpuBo spuBo) {
        List<Sku> skus = spuBo.getSkus();
        skus.forEach(sku -> {
            //再增加skus
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);

            //再增加stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);

        });
    }

    @Override
    public SpuDetail querySpuDetailBySid(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    @Override
    public List<Sku> querySkuBySid(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        //利用skuId 查询库存信息
        skus.forEach(sku1 -> {
            Stock stock = stockMapper.selectByPrimaryKey(sku1.getId());
            sku1.setStock(stock.getStock());

        });
        return skus;
    }

    @Transactional
    public void updateSpuBo(SpuBo spuBo) {
        //查询要被删除的sku

        List<Sku> skus = spuBo.getSkus();
        skus.forEach(sku -> {
            //删除stock
            stockMapper.deleteByPrimaryKey(sku.getId());
            //删除sku
            skuMapper.delete(sku);
        });

        //新增sku
        //新增stock
        saveSkuandStock(spuBo);


        //根据spuId更新spu,但是默认值不能更新
        spuBo.setSaleable(null);
        spuBo.setValid(null);
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());

        spuMapper.updateByPrimaryKeySelective(spuBo);

        //根据spuId更新spu_detail
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        sendMsg("update",spuBo.getId());
    }

    @Override
    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }

    @Override
    public Sku querySkuById(Long skuId) {
        return this.skuMapper.selectByPrimaryKey(skuId);
    }


}
