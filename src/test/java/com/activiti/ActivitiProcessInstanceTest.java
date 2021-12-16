package com.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zhangy
 * @date 2021/12/7 10:30
 * @apiNote: https://www.cnblogs.com/zsg88/p/12175805.html
 * Activiti流程实例的运行过程
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitiProcessInstanceTest {

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private TaskService taskService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private HistoryService historyService;

    @Test
    public void deploy(){
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
     * 根据流程定义key启动流程实例
     * 每个执行是单独的流程实例，不同的实例之间不会相互干扰
     * 在任务节点表ACT_RU_TASK生成任务实例数据
     */
    @Test
    public void startProcessInstanceOne(){
        //启动流程实例
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myProcess_1");
        System.out.println("流程实例ID:"+instance.getId());
        System.out.println("流程定义ID:"+instance.getProcessDefinitionId());
    }

    /**
     * 启动流程实例的时候指定businesskey(业务标识)
     */
    @Test
    public void startProcessInstanceTwo(){
        //启动流程实例
        //第一个参数：是指流程定义key
        //第二个参数：业务标识businessKey,业务标识来源于业务系统。通常为业务表的主键，根据业务标识来关联查询业务系统的数据。
        //启动流程实例时，指定的businesskey，就会在ACT_RU_EXECUTION流程实例的执行表中存储businesskey
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myProcess_1","bus001");
        System.out.println("流程实例ID:"+instance.getId());
        System.out.println("流程定义ID:"+instance.getProcessDefinitionId());
    }

    /**
     * 查询指定流程的所有实例
     * 可以查看流程实例的状态，当前运行结点等信息。
     */
    @Test
    public void processInstanceQuery() {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("myProcess_1")
                .list();
        for (ProcessInstance instance : list) {
            System.out.println("流程实例id： " + instance.getProcessInstanceId());
            System.out.println("所属流程定义id： " + instance.getProcessDefinitionId());
            System.out.println("是否执行完成： " + instance.isEnded());
            System.out.println("是否暂停： " + instance.isSuspended());
            System.out.println("当前活动标识 ： " + instance.getActivityId());
        }
    }

    /**
     * 删除已经部署的流程定义
     * delete from ACT_RE_DEPLOYMENT 流程部署信息表;
     * ACT_RE_PROCDEF 流程定义数据表;
     * ACT_GE_BYTEARRAY 二进制数据表;
     */
    @Test
    public void deleteProcessDefinition(){
        //执行删除流程定义  参数代表流程部署的id
        //1.当我们正在执行的这一套流程没有完全审批结束的时候，此时如果要删除流程定义信息就会失败。
        //2.如果要求强制删除,可以使用repositoryService.deleteDeployment("fc4c1350-5d55-11ec-b332-00ff104c97c8", true);
        //参数true代表级联删除，此时就会先删除没有完成的流程结点，最后就可以删除流程定义信息 false的值代表不级联
        repositoryService.deleteDeployment("8c053ad7-5e45-11ec-a538-00ff104c97c8", true);
//        repositoryService.deleteDeployment("fc4c1350-5d55-11ec-b332-00ff104c97c8");
    }

    /**
     * 删除流程实例
     */
    @Test
    public void deleteProcessInstance(){
        String processInstanceId = "214244e7-5e48-11ec-9084-00ff104c97c8";
        //当前流程实例没有完全结束的时候，删除流程实例就会失败
        runtimeService.deleteProcessInstance(processInstanceId,"删除原因");
    }

    /**
     * 操作流程的挂起、激活
     */
    @Test
    public void activateProcessDefinitionById(){
        //查询流程定义对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myProcess_1")
                .singleResult();
        //当前流程定义的实例是否都为暂停状态
        boolean suspended = processDefinition.isSuspended();
        String processDefinitionId = processDefinition.getId();
        if(suspended){
            //挂起状态则激活
            repositoryService.activateProcessDefinitionById(processDefinitionId,true,new Date());
            System.out.println("流程定义："+processDefinitionId+"激活");
        }else{
            //激活状态则挂起
            repositoryService.suspendProcessDefinitionById(processDefinitionId,true,new Date());
            System.out.println("流程定义："+processDefinitionId+"挂起");
        }
    }

    /**
     * 单个流程实例的挂起，激活
     */
    @Test
    public void activateProcessInstanceById(){
        //查询流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("95056757-5e4b-11ec-a952-00ff104c97c8")
                .singleResult();
        //当前流程定义的实例是否都为暂停状态
        boolean suspended = processInstance.isSuspended();
        String processInstanceId = processInstance.getId();
        if(suspended){
            //激活
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程："+processInstanceId+"激活");
        }else{
            //挂起
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程："+processInstanceId+"挂起");
        }
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask(){
        //任务ID
        String taskId = "b0bfa6f0-5e4f-11ec-ba42-00ff104c97c8";
        taskService.complete(taskId);
        System.out.println("完成任务：任务ID："+taskId);
    }



}
