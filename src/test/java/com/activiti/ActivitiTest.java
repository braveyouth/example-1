package com.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * @author zhangy
 * @date 2021/12/7 10:30
 * @apiNote:
 */
@SpringBootTest
public class ActivitiTest {

    public ProcessEngine getProcessEngine(){
        //1.创建ProcessEngineConfiguration对象
        //第一个参数:配置文件名称
        //第二个参数是processEngineConfiguration的bean的id
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        //创建ProcessEngine对象
        return configuration.buildProcessEngine();
    }

    /**
     * 生成表
     */
    @Test
    public void testGenTable(){
        //2.创建ProcesEngine对象
        ProcessEngine processEngine = getProcessEngine();
        //3.输出processEngine对象
        System.out.println(processEngine);
    }

    /**
     * 部署流程
     */
    @Test
    public void deploy(){
        //2.创建ProcessEngine对象
        ProcessEngine processEngine = getProcessEngine();
        //3.得到RepositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //4.进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/ProcessLeave.bpmn")  //添加bpmn资源
                .addClasspathResource("processes/ProcessLeave.png")   //添加bpmn资源图片
                .name("请假申请单流程")
                .deploy();
        System.out.println(deployment.getName());
        System.out.println(deployment.getId());
    }

    /**
     * 启动一个流程实例
     */
    @Test
    public void startProcessInstance(){
        //2.创建ProcessEngine对象
        ProcessEngine processEngine = getProcessEngine();
        //3.得到RuntimeService实例
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //4.启动流程实例
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myProcess");
        System.out.println("流程实例ID:"+instance.getId());
        System.out.println("流程定义ID:"+instance.getProcessDefinitionId());
    }

    /**
     * 查询用户的任务列表
     */
    @Test
    public void taskQuery() {
        //2.创建ProcessEngine对象
        ProcessEngine processEngine = getProcessEngine();
        //3.得到TaskService实例
        TaskService taskService = processEngine.getTaskService();
        //4.根据流程定义的key,负责人assignee来实现当前用户的任务列表查询
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .taskAssignee("user2")
                .list();
        if(list!=null && list.size()>0){
            for(Task task:list){
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("执行对象ID:"+task.getExecutionId());
                System.out.println("流程定义ID:"+task.getProcessDefinitionId());
                System.out.println("getOwner:"+task.getOwner());
                System.out.println("getCategory:"+task.getCategory());
                System.out.println("getDescription:"+task.getDescription());
                System.out.println("getFormKey:"+task.getFormKey());
                Map<String, Object> map = task.getProcessVariables();
                for (Map.Entry<String, Object> m : map.entrySet()) {
                    System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                }
                for (Map.Entry<String, Object> m : task.getTaskLocalVariables().entrySet()) {
                    System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                }
            }
        }
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask(){
        //任务ID
        String taskId = "5002";
        //2.创建ProcessEngine对象
        ProcessEngine processEngine = getProcessEngine();
        //3.得到TaskService实例
        TaskService taskService = processEngine.getTaskService();
        taskService.complete(taskId);
        System.out.println("完成任务：任务ID："+taskId);
    }



}
