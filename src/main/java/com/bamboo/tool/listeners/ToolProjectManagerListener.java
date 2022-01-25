package com.bamboo.tool.listeners;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.BambooToolConfig;
import com.bamboo.tool.config.model.ProjectInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/19 14:56
 * Description  项目启动关闭监听
 */
public class ToolProjectManagerListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        BambooToolConfig state = BambooToolComponent.getInstance().getState();
        String projectsPath = state.getProjectSavePath() + "bamboo-projects.json";
        String projectName = project.getName();
        String projectPath = project.getBasePath();
        String apiUrlFileName = String.format("%s-api.json", projectName);
        String apiUrlFilePath = String.format("%s%s%s",projectPath,"/.idea/bamboo-api/", apiUrlFileName);

        boolean exist = FileUtil.exist(projectsPath);
        List<ProjectInfo> bambooToolConfigs;
        if (exist) {
            FileReader fileReader = new FileReader(projectsPath);
            String projectStr = fileReader.readString();
            bambooToolConfigs = JSONUtil.toList(projectStr, ProjectInfo.class);
        } else {
            bambooToolConfigs = new ArrayList<>();
        }
        ProjectInfo projectInfo = state.getProjectInfo();
        projectInfo.setProjectName(projectName);
        projectInfo.setProjectPath(projectPath);
        projectInfo.setApiUrlFilePath(apiUrlFilePath);
        projectInfo.setApiUrlFileName(apiUrlFileName);
        long count = bambooToolConfigs.stream()
                .filter(e -> projectName.equals(e.getProjectName()))
                .filter(e -> projectPath.equals(e.getProjectPath())).count();
        if (count < 1) {
            bambooToolConfigs.add(projectInfo);
        }

        FileWriter writer = null;
        try {
            String formatStr = JSON.toJSONString(bambooToolConfigs, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat);
            File touch = com.bamboo.tool.util.FileUtil.touch(projectsPath);
            writer = new FileWriter(touch);
            writer.write(formatStr);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProjectManagerListener.super.projectOpened(project);
    }
}