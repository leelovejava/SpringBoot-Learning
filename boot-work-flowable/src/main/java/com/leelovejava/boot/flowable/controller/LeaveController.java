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
     * http://localhost:4000/leave/createProcessDiagramPic?processId=37513
     *
     * @author xiaofu
     * @description 启动流程
     * @date 2020/8/26 17:36
     */
    @RequestMapping(value = "startLeaveProcess")
    public String startLeaveProcess(String staffId) {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("leaveTask", staffId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Leave", map);
        StringBuilder sb = new StringBuilder();
        sb.append("创建请假流程 processId：" + processInstance.getId());
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(staffId).orderByTaskCreateTime().desc().list();
        for (Task task : tasks) {
            sb.append("任务taskId:" + task.getId());
        }
        return sb.toString();
    }

    /**
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
     * http://localhost:4000/leave/rejectTask?taskId=10086
     * @param taskId
     * @author xinzhifu
     * @description 驳回
     * @date 2020/8/27 14:30
     */
    @RequestMapping(value = "rejectTask")
    public String rejectTask(String taskId) {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("checkResult", "驳回");
        taskService.complete(taskId, map);
        return "申请审核驳回~";
    }


    /**
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
}
