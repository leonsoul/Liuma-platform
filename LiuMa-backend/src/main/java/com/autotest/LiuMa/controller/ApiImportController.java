package com.autotest.LiuMa.controller;


import com.autotest.LiuMa.request.ApiImportRequest;
import com.autotest.LiuMa.service.ApiImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/autotest/import")
public class ApiImportController {
    @Resource
    public ApiImportService apiImportService;
    @Value("${python.script.path}")
    private String scriptDirPath;

    @PostMapping(value="/api", consumes = {"multipart/form-data"})
    public void importApi( @RequestParam MultipartFile file, @RequestParam String
            sourceType, @RequestParam String projectId, @RequestParam String moduleId, HttpServletRequest request) {
        String userId = request.getSession().getAttribute("userId").toString();
        apiImportService.importApi(file, sourceType, projectId, moduleId, userId);
    }


    @PostMapping("/api_other")
    public String uploadImportHandler(@RequestBody ApiImportRequest apiImportRequest, HttpServletRequest request) {
        String userId = request.getSession().getAttribute("userId").toString();
        String projectId = apiImportRequest.getProject_id();
        String moduleId = apiImportRequest.getModule_id();
        String requestUrl = apiImportRequest.getRequest_url();

        StringBuilder stringBuilder ;
        try {
            String pythonScript = scriptDirPath + "demo.py";
            String[] command = {"python3", pythonScript, "-p",projectId,"-m", moduleId,"-ru", requestUrl, "-u", userId};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            System.out.println(Arrays.toString(command));
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            List<String> output = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }

            int exitCode = process.waitFor();
            String res = "";
            if (exitCode == 0) {
                System.out.println("Python script executed successfully!");
                for (String s : output) {
                    System.out.println(s);
                }
                res = "接口导入成功";
            } else {
                res = "Python script executed with errors. Exit code: " + exitCode;
                System.out.println("Python script executed with errors. Exit code: " + exitCode);
            }
            return res;
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "unknown error";
    }

}
