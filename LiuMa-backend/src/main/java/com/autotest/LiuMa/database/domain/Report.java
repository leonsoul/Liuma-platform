package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Report implements Serializable {
    private String id;

    private String name;

    private String taskId;

    private String environmentId;

    private String sourceType; // 来源类型，来自集合等等

    private String sourceId; // 来源id 使用uuid

    private Long startTime;

    private Long endTime;

    private String status;

    private String projectId;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

}