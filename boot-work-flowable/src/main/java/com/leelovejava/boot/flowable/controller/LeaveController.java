package com.leelovejava.boot.flowable.controller;

import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 请假
 *
 * @author tianhao
 */
@Slf4j
@RestController
@RequestMapping(value = "leave")
public class LeaveController {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngine processEngine;

    /**
     *
     * 1. 首先启动一个请假的流程，以员工ID staffId 作为唯一标识，XML文件中会接收变量 leaveTask，
     * Flowable内部会进行数据库持久化，并返回一个流程Id processId ，
     * 用它可以查询工作流的整体情况，任务Id task为员工具体的请假任务
     * http://localhost:4000/leave/startLeaveProcess?staffId=37513
     * @author xiaofu
     * @description 启动流程
     * @date 2020/8/26 17:36
     * @return 创建请假流程 processId：5d835e4e-eb51-11ea-8450-005056c00001任务taskId:5d8fba64-eb51-11ea-8450-005056c00001
     */
    @RequestMapping(value = "startLeaveProcess")
    public String startLeaveProcess(String staffId) {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("leaveTask", staffId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Leave", map);
        StringBuilder sb = new StringBuilder();
        sb.append("创建请假流程 processId：" + processInstance.getId());
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(staffId).orderByTaskCreateTime().desc().list();
        // 注意：一个请假流程 processId中可以包含多个请假任务 taskId
        for (Task task : tasks) {
            sb.append("任务taskId:" + task.getId());
        }
        return sb.toString();
    }

    /**
     * 2. 用启动流程时返回的 processId 看一下一下当前的流程图
     * http://localhost:4000/leave/createProcessDiagramPic?processId=5d835e4e-eb51-11ea-8450-005056c00001
     * @author xiaofu
     * @description 生成流程图
     * @date 2020/8/27 14:29
     */
    @RequestMapping(value = "createProcessDiagramPic")
    public void createProcessDiagramPic(HttpServletResponse httpServletResponse, String processId) throws Exception {

        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        if (pi == null) {
            return;
        }
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();

        String InstanceId = task.getProcessInstanceId();
        List<Execution> executions = runtimeService
                .createExecutionQuery()
                .processInstanceId(InstanceId)
                .list();

        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        /**
         * 生成流程图
         */
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(
                // BpmnModel bpmnModel
                bpmnModel,
                // String imageType
                "png",
                // List<String> highLightedActivities
                activityIds,
                // List<String> highLightedFlows
                flows,
                // String activityFontName
                engconf.getActivityFontName(),
                // String labelFontName
                engconf.getLabelFontName(),
                // String annotationFontName
                engconf.getAnnotationFontName(),
                // ClassLoader customClassLoader
                engconf.getClassLoader(),
                // double scaleFactor
                1.0,
                // boolean drawSequenceFlowNameWithNoLabelDI
                true);
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int length = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 3.
     * @param taskId
     * @author xinzhifu
     * @description 批准
     * @date 2020/8/27 14:30
     */
    @RequestMapping(value = "applyTask")
    public String applyTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("checkResult", "通过");
        taskService.complete(taskId, map);
        return "申请审核通过~";
    }

    /**
     * 3
     * http://localhost:4000/leave/rejectTask?taskId=5d8fba64-eb51-11ea-8450-005056c00001
     * @param taskId
     * @author xinzhifu
     * @description 驳回
     * @date 2020/8/27 14:30
     * @return 申请审核驳回~
     */
    @RequestMapping(value = "rejectTask")
    public String rejectTask(String taskId) {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("checkResult", "驳回");
        taskService.complete(taskId, map);
        return "申请审核驳回~";
    }



}
