package com.leyou.item.Controller;

import com.leyou.item.Service.GoodsService;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> queryGoods(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    ){
        PageResult<SpuBo> spuBoPageResult=goodsService.queryGoods(key,saleable,page,rows);

        if (spuBoPageResult==null || CollectionUtils.isEmpty(spuBoPageResult.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuBoPageResult);

    }

    @PostMapping("goods")
    public ResponseEntity<Void> saveSpuBo(@RequestBody SpuBo spuBo){
        goodsService.saveSpuBo(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySid(@PathVariable("spuId") Long spuId){
        SpuDetail spuDetail=goodsService.querySpuDetailBySid(spuId);
        if (spuDetail==null){
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(spuDetail);
    }

    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySid(@RequestParam("id") Long spuId ){
        List<Sku> list=goodsService.querySkuBySid(spuId);
        if (CollectionUtils.isEmpty(list)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }

    @PutMapping("goods")
    public ResponseEntity<Void> updateSpuBo(@RequestBody SpuBo spuBo){
        goodsService.updateSpuBo(spuBo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu=this.goodsService.querySpuById(id);
        if (spu==null){
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(spu);
    }

    @GetMapping("sku/{skuId}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("skuId") Long skuId){
        Sku sku=this.goodsService.querySkuById(skuId);
        if (sku==null){
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(sku);

    }
}















