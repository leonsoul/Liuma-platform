package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.constants.DeviceStatus;
import com.autotest.LiuMa.common.constants.EngineType;
import com.autotest.LiuMa.common.constants.ReportSourceType;
import com.autotest.LiuMa.common.constants.ReportStatus;
import com.autotest.LiuMa.common.exception.LMException;
import com.autotest.LiuMa.database.domain.*;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.PlanCollectionDTO;
import com.autotest.LiuMa.dto.TaskDTO;
import com.autotest.LiuMa.request.RunRequest;
import com.autotest.LiuMa.websocket.config.WsSessionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class RunService {
    @Resource
    private TaskMapper taskMapper;

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private PlanCollectionMapper planCollectionMapper;

    @Resource
    private CollectionCaseMapper collectionCaseMapper;

    @Resource
    private DebugDataMapper debugDataMapper;

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private EngineMapper engineMapper;

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private DeviceService deviceService;

    public TaskDTO run(RunRequest runRequest) {
        // 新增任务
        TaskDTO task = new TaskDTO();
        task.setId(UUID.randomUUID().toString());
        // 判断app用例的设备是否可用
        if(runRequest.getDeviceId() != null && !runRequest.getDeviceId().equals("")){
            Device device = deviceMapper.getDeviceById(runRequest.getDeviceId());
            if((!device.getStatus().equals(DeviceStatus.ONLINE.toString())) &&
                    (!(device.getStatus().equals(DeviceStatus.USING.toString()) &&
                            runRequest.getRunUser().equals(device.getUser())))){
                // 设备空闲中或者设备在使用中且使用者是自己可以执行单个用例
                throw new LMException("设备非空闲状态 执行失败");
            }
            if(device.getStatus().equals(DeviceStatus.ONLINE.toString())) {
                // 使用中的设备执行测试不予修改状态 空闲中则进行修改
                device.setStatus(DeviceStatus.TESTING.toString());
                device.setUpdateTime(System.currentTimeMillis());
                device.setUser(task.getId());   // 测试中的设备使用者是任务id
                device.setTimeout(-1);  // 测试中设备不予超时
                deviceMapper.updateDevice(device);  // 占用设备
            }
        }
        // 调试执行则存储临时数据
        if(runRequest.getSourceType().equals(ReportSourceType.TEMP.toString())){
            DebugData debugData = new DebugData();
            debugData.setId(UUID.randomUUID().toString());
            debugData.setData(JSONObject.toJSONString(runRequest.getDebugData()));
            debugDataMapper.addDebugData(debugData);
            runRequest.setSourceId(debugData.getId());
        }
        // 新增任务，将任务存入数据库中。设置任务id，名称，状态，类型，引擎，项目id，任务执行者，任务更新者，创建时间，更新时间，
//        task = (TaskDTO) new Task();
        task.setId(UUID.randomUUID().toString());
        String runName = runRequest.getSourceName() +"-"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        task.setName(runName);
        task.setStatus(ReportStatus.PREPARED.toString()); //将任务状态置为已就绪
        task.setType(runRequest.getTaskType()); // 将任务类型置为run
        task.setEngineId(runRequest.getEngineId());
        task.setProjectId(runRequest.getProjectId());
        task.setCreateUser(runRequest.getRunUser());
        task.setUpdateUser(runRequest.getRunUser());
        task.setCreateTime(System.currentTimeMillis());
        task.setUpdateTime(System.currentTimeMillis());
        taskMapper.addTask(task);
        // 预设报告，将报告置入报告图中中。设置报告id，名称，任务的id，环境id，来源类型，来源id，报告的任务状态，项目id，任务执行者，任务更新者，创建时间，更新时间，
        Report report = new Report();
        report.setId(UUID.randomUUID().toString());
        report.setName(runName);
        report.setTaskId(task.getId());
        report.setEnvironmentId(runRequest.getEnvironmentId());
        report.setDeviceId(runRequest.getDeviceId());
        report.setSourceType(runRequest.getSourceType());
        report.setSourceId(runRequest.getSourceId());
        //将报告的任务状态置为已就绪
        report.setStatus(ReportStatus.PREPARED.toString());
        report.setProjectId(runRequest.getProjectId());
        report.setCreateUser(runRequest.getRunUser());
        report.setUpdateUser(runRequest.getRunUser());
        report.setCreateTime(System.currentTimeMillis());
        report.setUpdateTime(System.currentTimeMillis());
        reportMapper.addReport(report);
        // 统计报告用例数，初始化 统计报告
        ReportStatistics reportStatistics = new ReportStatistics();
        reportStatistics.setId(UUID.randomUUID().toString());
        reportStatistics.setReportId(report.getId());
        reportStatistics.setPassCount(0);
        reportStatistics.setErrorCount(0);
        reportStatistics.setFailCount(0);
        // 根据任务是计划还是集合，和任务id，收集用例总数
        Integer total = 0;
        if(runRequest.getSourceType().equals(ReportSourceType.PLAN.toString())){
            total = planCollectionMapper.getPlanCaseCount(runRequest.getSourceId());
        }else if(runRequest.getSourceType().equals(ReportSourceType.COLLECTION.toString())){
            total = collectionCaseMapper.getCollectionCaseCount(runRequest.getSourceId());
        }else {
            total = 1;
        }
        reportStatistics.setTotal(total);
        reportMapper.addReportStatistics(reportStatistics);
        task.setReportId(report.getId());
        if(runRequest.getEngineId().equals(EngineType.SYSTEM.toString())){
            List<Engine> engineList = engineMapper.getAllSystemEngine();
            for(Engine engine: engineList){ // 通知所有在线的引擎来获取任务
                try {
                    WebSocketSession session = WsSessionManager.get("engine", engine.getId());
                    JSONObject message = new JSONObject();
                    message.put("type", "start");
                    session.sendMessage(new TextMessage(message.toString()));
                }catch (Exception ignored){
                }
            }
        }else {
            try {
                WebSocketSession session = WsSessionManager.get("engine", runRequest.getEngineId());
                JSONObject message = new JSONObject();
                message.put("type", "start");
                session.sendMessage(new TextMessage(message.toString()));
            }catch (Exception ignored){
            }
        }
        return task;
    }

    public void stopDeviceWhenRunEnd(String taskId){
        TaskDTO task = taskMapper.getTaskDetail(taskId);
        if(task.getDeviceId() != null) {
            Device device = deviceService.getDeviceDetail(task.getDeviceId());
            if(device.getStatus().equals(DeviceStatus.TESTING.toString()) &&
                    task.getId().equals(device.getUser())){
                deviceService.coldDevice(device); //当前设备使用者仍然是该任务才会停用
            }
        }else if(task.getSourceType().equals(ReportSourceType.PLAN.toString())){
            List<PlanCollectionDTO> planCollections = planCollectionMapper.getPlanCollectionList(task.getSourceId());
            for(PlanCollectionDTO planCollectionDTO:planCollections){
                Collection collection = collectionMapper.getCollectionDetail(planCollectionDTO.getCollectionId());
                if(collection==null) return;
                if(collection.getDeviceId() != null){
                    Device device = deviceService.getDeviceDetail(collection.getDeviceId());
                    if(device.getStatus().equals(DeviceStatus.TESTING.toString()) &&
                            task.getId().equals(device.getUser())){
                        deviceService.coldDevice(device); //当前设备使用者仍然是该任务才会停用
                    }
                }
            }
        }
    }

}
