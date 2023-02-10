package com.autotest.LiuMa.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReportCollectionCaseTransDTO {

    private String status;

    private String transId;

    private String transName;

    private String content;

    private String description;

    private String exec;

    private String during;

    private String screenshotList;

    private Boolean showViewer = false;

}
