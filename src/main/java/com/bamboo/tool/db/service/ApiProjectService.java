package com.bamboo.tool.db.service;

import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.SqlConstant;
import com.bamboo.tool.db.SqliteConfig;
import com.bamboo.tool.util.StringUtil;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/8 11:29
 * Description
 */
public class ApiProjectService {

    @SneakyThrows
    public ProjectInfo queryProject(String projectId) {
        this.createProject();
        String sql = StringUtil.format("SELECT * FROM bamboo_api_project where project_id='{}'; ", projectId);
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(sql);
        List<ProjectInfo> projectInfos = new ArrayList<>();
        while (rs.next()) {
            ProjectInfo projectInfo = new ProjectInfo();
            String name = rs.getString("project_name");
            int id = rs.getInt("id");
            String path = rs.getString("project_path");
            projectInfo.setProjectId(rs.getString("project_id"));
            projectInfo.setProjectPath(path);
            projectInfo.setProjectName(name);
            projectInfo.setId(id);
            projectInfos.add(projectInfo);
        }
        rs.close();
        conn.close();
        return projectInfos.size() > 0 ? projectInfos.get(0) : null;
    }

    @SneakyThrows
    public void createProject() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(SqlConstant.QUERY_PROJECT_TABLE);
        if (!rs.next()) {
            rs.close();
            state.executeUpdate(SqlConstant.CREAT_PROJECT_SQL);
        }
        conn.close();
    }

    @SneakyThrows
    public ProjectInfo saveProject(ProjectInfo projectInfo) {
        ProjectInfo project = this.queryProject(projectInfo.getProjectId());
        if (project == null) {
            StringBuffer str = new StringBuffer();
            str.append("INSERT INTO bamboo_api_project (project_name, project_path,project_id) VALUES (");
            str.append(StringUtil.format("'{}',", projectInfo.getProjectName()));
            str.append(StringUtil.format("'{}',", projectInfo.getProjectPath()));
            str.append(StringUtil.format("'{}')", projectInfo.getProjectId()));
            Connection conn = SqliteConfig.getConnection();
            Statement state = conn.createStatement();
            state.executeUpdate(str.toString());
            conn.close();
            project = this.queryProject(projectInfo.getProjectId());
        }
        return project;
    }

}
