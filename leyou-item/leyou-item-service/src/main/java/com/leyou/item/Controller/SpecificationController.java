package com.leyou.item.Controller;


import com.leyou.item.Service.SpecificationService;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroups(@PathVariable("cid") Long cid){
        List<SpecGroup> groups=specificationService.queryGroups(cid);
        if (CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(groups);
    }

    @PostMapping("group")
    public ResponseEntity<Void> saveGroup(@RequestBody SpecGroup group){
        specificationService.saveGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteGroupById(@PathVariable("id") Long id){
        specificationService.deleteGroupById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PutMapping("group")
    public ResponseEntity<Void> updateGroup(@RequestBody SpecGroup group){
        specificationService.updateGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParam(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean seraching


    ){
        List<SpecParam> params=specificationService.queryParam(gid,cid,generic,seraching);
        if (CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(params);
    }



    @PostMapping("param")
    public ResponseEntity<Void>  saveParam(@RequestBody SpecParam specParam){
        specificationService.saveParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("param/{id}")
    public ResponseEntity<Void> deleteParamById(@PathVariable("id") Long id){
        specificationService.deleteParamById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("param")
    public ResponseEntity<Void>  UpdateParam(@RequestBody SpecParam specParam){
        specificationService.updateParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupWithParam(@PathVariable("cid")Long cid){
        List<SpecGroup> groups=this.specificationService.queryGroupWithParam(cid);
        if (CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(groups);

    }

}
