package com.leyou.item.Service;

import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;

import java.util.List;

public interface GoodsService {
    PageResult<SpuBo> queryGoods(String key, Boolean saleable, Integer page, Integer rows);

    void saveSpuBo(SpuBo spuBo);

    SpuDetail querySpuDetailBySid(Long spuId);

    List<Sku> querySkuBySid(Long spuId);

    void updateSpuBo(SpuBo spuBo);

    Spu querySpuById(Long id);

    Sku querySkuById(Long skuId);
}
