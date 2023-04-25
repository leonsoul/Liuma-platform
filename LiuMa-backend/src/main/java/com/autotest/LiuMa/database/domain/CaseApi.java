package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class CaseApi implements Serializable {
    private String id; // 用例id uuid

    private Long index; //用例执行顺序 从1开始

    private String caseId; // 用例id uuid

    private String apiId; // APIid  uuid

    private String description;

    private String header; // 请求头

    private String body; // 请求data

    private String query; // 请求query

    private String rest;

    private String assertion; //断言格式

    private String relation; // 关联取值

    private String controller; // 逻辑控件

}