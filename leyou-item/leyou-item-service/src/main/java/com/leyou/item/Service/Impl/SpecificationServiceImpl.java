package com.leyou.item.Service.Impl;

import com.leyou.item.Service.SpecificationService;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper paramMapper;

    @Override
    public List<SpecGroup> queryGroups(Long cid) {

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);

        return groupMapper.select(specGroup);
    }

    @Override
    public List<SpecParam> queryParam(Long gid, Long cid, Boolean generic, Boolean seraching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(seraching);


        return paramMapper.select(specParam);
    }

    @Override
    public void saveParam(SpecParam specParam) {
        paramMapper.insert(specParam);

    }

    @Override
    public void deleteParamById(Long id) {
        paramMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateParam(SpecParam specParam) {
        paramMapper.updateByPrimaryKey(specParam);
    }

    @Override
    public void saveGroup(SpecGroup group) {
        groupMapper.insert(group);
    }

    @Override
    public void deleteGroupById(Long id) {
        groupMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateGroup(SpecGroup group) {
        groupMapper.updateByPrimaryKey(group);
    }

    @Override
    public List<SpecGroup> queryGroupWithParam(Long cid) {
        List<SpecGroup> groups = this.queryGroups(cid);
        groups.forEach(group -> {
            List<SpecParam> params = this.queryParam(group.getId(), null, null, null);
            group.setParams(params);
        });

        return  groups;

    }

}
