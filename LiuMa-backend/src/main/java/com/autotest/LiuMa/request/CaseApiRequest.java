package com.autotest.LiuMa.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CaseApiRequest {
    private String id; // 用例id uuid

    private Long index; //用例执行顺序 从1开始

    private String caseId; // 用例id uuid

    private String apiId; // APIid  uuid

    private JSONArray header; // 请求头
    private String description;

    private JSONArray header;

    private JSONObject body; // 请求data

    private JSONArray query; // 请求query

    private JSONArray rest;

    private JSONArray assertion; //断言格式

    private JSONArray relation; // 关联取值

    private JSONArray controller; // 逻辑控件

}
