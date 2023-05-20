package com.autotest.LiuMa.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiImportRequest {
    // 目标项目id
    private String project_id;
    // 目标接口模块
    private String module_id;
    // 导入的平台类型
    private String platformType;

    // 需要导入的接口路径
    private String request_url;
}
