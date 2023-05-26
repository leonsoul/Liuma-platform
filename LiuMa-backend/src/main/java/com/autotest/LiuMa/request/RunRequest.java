package com.autotest.LiuMa.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RunRequest {
    private String deviceId;
    private String engineId; // 引擎id
    private String environmentId; // 环境ID 使用uuid
    private String sourceType; // 来源类型，来自集合等等
    private String sourceId; // 来源id 使用uuid
    private String sourceName; //来源名称，如集合名称
    private String taskType;  // 任务类型，如run
    private String runUser; //任务执行者，String user = request.getSession().getAttribute("userId").toString();
    private String projectId; //项目id 使用uuid

    private CaseRequest debugData;

    private String user;
    private String planId;
}
