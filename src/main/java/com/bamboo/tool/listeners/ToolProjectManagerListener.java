package com.bamboo.tool.listeners;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bamboo.tool.components.api.entity.BambooClass;
import com.bamboo.tool.components.api.factory.FrameworkExecute;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.BambooToolConfig;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.SqliteConfig;
import com.bamboo.tool.db.service.ApiProjectService;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.util.StringUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Create by GuoQing
 * Date 2022/1/19 14:56
 * Description  项目启动关闭监听
 */
public class ToolProjectManagerListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        BambooToolConfig state = BambooToolComponent.getInstance().getState();
        ApiProjectService apiProjectService = ApplicationManager.getApplication().getService(ApiProjectService.class);
        ProjectInfo projectInfo=null;
        if(StringUtil.isNotEmpty(state.getProjectId())){
            ProjectInfo   currentProject = apiProjectService.queryProject(state.getProjectId());
           if(currentProject!=null){
               projectInfo=currentProject;
           }else{
               String projectId= UUID.randomUUID().toString();
               projectInfo=new ProjectInfo();
               projectInfo.setProjectId(projectId);
           }
        }else{
            String projectId= UUID.randomUUID().toString();
            projectInfo=new ProjectInfo();
            projectInfo.setProjectId(projectId);
        }
        projectInfo.setProjectPath(project.getBasePath());
        projectInfo.setProjectName(project.getName());
        projectInfo = apiProjectService.saveProject(projectInfo);
        state.setProjectInfo(projectInfo);
        state.setProjectId(projectInfo.getProjectId());
        final BambooService service = ApplicationManager.getApplication().getService(BambooService.class);
        service.initTable();
    }

    @Override
    public void projectClosing(@NotNull Project project) {

    }
}
