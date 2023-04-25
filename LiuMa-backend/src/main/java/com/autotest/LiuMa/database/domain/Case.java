package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Case implements Serializable {
    private String id; // 用例id uuid

    private Long num; //用例管理里的NO

    private String name; // 用例名称

    private String level; //用例等级

    private String moduleId; // 模块id uuid

    private String projectId; // 项目id

    private String type; //类型 API\WEB

    private String thirdParty; // 第三方标识 默认""

    private String description; // 用例描述  默认 null

    private String environmentIds; // 环境列表 列表形式 uuid

    private String commonParam; // 配置信息 包含 导入函数【函数uuid】，导入公参，公用Header，公用Proxy
    private String system;

    private String commonParam;

    private Long createTime;//创建时间

    private Long updateTime; //更新时间

    private String createUser; //创建者

    private String updateUser;//更新者

    private String status;

}