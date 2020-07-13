package com.leyou.item.Service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

public interface SpecificationService {
    List<SpecGroup> queryGroups(Long cid);

    List<SpecParam> queryParam(Long gid, Long cid, Boolean generic, Boolean seraching);

    void saveParam(SpecParam specParam);

    void deleteParamById(Long id);

    void updateParam(SpecParam specParam);

    void saveGroup(SpecGroup group);

    void deleteGroupById(Long id);

    void updateGroup(SpecGroup group);


    List<SpecGroup> queryGroupWithParam(Long cid);
}
