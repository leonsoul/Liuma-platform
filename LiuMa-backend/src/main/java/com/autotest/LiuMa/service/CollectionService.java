package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.exception.LMException;
import com.autotest.LiuMa.database.domain.Collection;
import com.autotest.LiuMa.database.domain.CollectionCase;
import com.autotest.LiuMa.database.domain.Device;
import com.autotest.LiuMa.database.mapper.CollectionCaseMapper;
import com.autotest.LiuMa.database.mapper.CollectionMapper;
import com.autotest.LiuMa.database.mapper.DeviceMapper;
import com.autotest.LiuMa.dto.CollectionCaseDTO;
import com.autotest.LiuMa.dto.CollectionDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class CollectionService {

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private CollectionCaseMapper collectionCaseMapper;

    @Resource
    private DeviceMapper deviceMapper;

    public void saveCollection(CollectionDTO collectionDTO) {
        // 先处理集合下用例
        boolean hasAndroidCase = false;
        boolean hasAppleCase = false;
        List<CollectionCase> collectionCases = new ArrayList<>();
        if(collectionDTO.getId().equals("") || collectionDTO.getId() == null){ // 新增集合
            collectionDTO.setId(UUID.randomUUID().toString());
            collectionDTO.setCreateTime(System.currentTimeMillis());
            collectionDTO.setUpdateTime(System.currentTimeMillis());
            collectionDTO.setCreateUser(collectionDTO.getUpdateUser());
            collectionMapper.addCollection(collectionDTO);
        }else{ // 修改集合
            collectionDTO.setUpdateTime(System.currentTimeMillis());
            collectionMapper.updateCollection(collectionDTO);
        }
        // 收集测试用例
        for(CollectionCaseDTO collectionCaseDTO: collectionDTO.getCollectionCases()){
            if("android".equals(collectionCaseDTO.getCaseSystem())){
                hasAndroidCase = true;
            }
            if("apple".equals(collectionCaseDTO.getCaseSystem())){
                hasAppleCase = true;
            }
            // 将该用例存到集合用例的数据库中
            CollectionCase collectionCase = new CollectionCase();
            collectionCase.setId(UUID.randomUUID().toString());
            collectionCase.setIndex(collectionCaseDTO.getIndex());
            collectionCase.setCollectionId(collectionDTO.getId());
            collectionCase.setCaseId(collectionCaseDTO.getCaseId());
            collectionCases.add(collectionCase);
        }
        if(hasAndroidCase & hasAppleCase){
            throw new LMException("同一集合不能同时选择不同系统的测试用例");
        }
        if(hasAndroidCase || hasAppleCase){
            if(collectionDTO.getDeviceId() == null || collectionDTO.getDeviceId().equals("")){
                throw new LMException("集合下若包含APP测试则执行设备必选");
            }
            Device device = deviceMapper.getDeviceById(collectionDTO.getDeviceId());
            if(hasAndroidCase && !device.getSystem().equals("android")){
                throw new LMException("所选设备系统与集合下的测试用例类型不匹配");
            }
            if(hasAppleCase && !device.getSystem().equals("apple")){
                throw new LMException("所选设备系统与集合下的测试用例类型不匹配");
            }
        }

        if(collectionDTO.getId().equals("") || collectionDTO.getId() == null){ // 新增集合
            collectionDTO.setId(UUID.randomUUID().toString());
            collectionDTO.setCreateTime(System.currentTimeMillis());
            collectionDTO.setUpdateTime(System.currentTimeMillis());
            collectionDTO.setCreateUser(collectionDTO.getUpdateUser());
            collectionMapper.addCollection(collectionDTO);
        }else{ // 修改集合
            collectionDTO.setUpdateTime(System.currentTimeMillis());
            collectionMapper.updateCollection(collectionDTO);
        }

        collectionCases.forEach(item -> {
            item.setCollectionId(collectionDTO.getId());
        });

        collectionCaseMapper.deleteCollectionCase(collectionDTO.getId());  //先删除全部集合用例
        if(collectionCases.size() > 0) {
            collectionCaseMapper.addCollectionCase(collectionCases);
        }
    }

    public Map<String, Boolean> getCollectionCaseTypes(String collectionId){
        List<String> caseTypes = collectionCaseMapper.getCollectionCaseTypes(collectionId);
        Map<String, Boolean> caseTypeMap= new HashMap<>();
        if(caseTypes.contains("android")){
            caseTypeMap.put("hasAndroid", true);
        }else {
            caseTypeMap.put("hasAndroid", false);
        }
        if(caseTypes.contains("apple")){
            caseTypeMap.put("hasApple", true);
        }else {
            caseTypeMap.put("hasApple", false);
        }
        if(caseTypes.contains(null)){
            caseTypeMap.put("needEnvironment", true);
        }else {
            caseTypeMap.put("needEnvironment", false);
        }
        return caseTypeMap;
    }

    public void deleteCollection(Collection collection) {
        collectionCaseMapper.deleteCollectionCase(collection.getId());
        collectionMapper.deleteCollection(collection.getId());
    }

    public CollectionDTO getCollectionDetail(String collectionId) {
        CollectionDTO collectionDTO = collectionMapper.getCollectionDetail(collectionId);
        List<CollectionCaseDTO> collectionCaseDTOS = collectionCaseMapper.getCollectionCaseList(collectionId);
        collectionDTO.setCollectionCases(collectionCaseDTOS);

        return collectionDTO;
    }

    public List<CollectionDTO> getCollectionList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return collectionMapper.getCollectionList(request);
    }
}
