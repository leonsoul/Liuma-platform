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

    @PostMapping("/api")
    public String uploadHandler( @RequestParam MultipartFile file, @RequestParam String platformType, @RequestParam String project_id, @RequestParam String module_id, HttpServletRequest request) {
        String userId = request.getSession().getAttribute("userId").toString();
        Map<String, String> assistMap = new HashMap<>();
        assistMap.put("userId", userId);
        assistMap.put("projectId", project_id);
        assistMap.put("moduleId", module_id);

        StringBuilder stringBuilder ;
        try {
            if (file!=null) {
                InputStream bb = file.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(bb);
                BufferedReader reader = new BufferedReader(streamReader);
                String line;
                stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                bb.close();
                System.out.println(stringBuilder);
                if (apiImportService.verifyApi(stringBuilder.toString(), platformType)){
                    return apiImportService.saveImportApi(stringBuilder.toString(), platformType, assistMap) + "";
                }
                else{
                    return "import api error";
                }
            } else {
                return "need upload file";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unknown error";
    }
    @PostMapping("/api_other")
    public String uploadImportHandler(@RequestBody ApiImportRequest apiImportRequest, HttpServletRequest request) {
        String userId = request.getSession().getAttribute("userId").toString();
        String projectId = apiImportRequest.getProject_id();
        String module_id = apiImportRequest.getModule_id();
        String request_url = apiImportRequest.getRequest_url();

        StringBuilder stringBuilder ;
        try {
            String pythonScript = scriptDirPath + "demo.py";
            String[] command = {"python3", pythonScript, "-p",projectId,"-m", module_id,"-ru", request_url, "-u", userId};
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
