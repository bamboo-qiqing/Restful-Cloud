package com.bamboo.tool.db.service;

import cn.hutool.core.collection.CollectionUtil;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.SqlConstant;
import com.bamboo.tool.db.SqliteConfig;
import com.bamboo.tool.util.StringUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Create by GuoQing
 * Date 2022/2/8 14:05
 * Description
 */
public class ApiMethodService {

    @SneakyThrows
    public void createMethodTable() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(SqlConstant.QUERY_METHOD_TABLE);
        if (!rs.next()) {
            rs.close();
            state.executeUpdate(SqlConstant.CREAT_METHOD_SQL);
        }
        conn.close();
    }

    @SneakyThrows
    public void saveMethods(List<ApiClass> allApiList, ProjectInfo projectInfo) {
        this.createMethodTable();
        if (CollectionUtil.isEmpty(allApiList)) {
            return;
        }
        this.deleteMethodsByProjectId(projectInfo.getId());
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        allApiList.stream().forEach(apiClass -> {
            addBatch(projectInfo, state, apiClass);
        });
        state.executeBatch();
    }

    private void addBatch(ProjectInfo projectInfo, Statement state, ApiClass apiClass) {
        apiClass.getMethods().stream().forEach(apiMethod -> {
            String types = StringUtils.join(apiMethod.getTypes(), ',');
            String params = StringUtils.join(apiMethod.getParams(), ',');
            String headers = StringUtils.join(apiMethod.getHeaders(), ',');
            String contentTypes = StringUtils.join(apiMethod.getContentTypes(), ',');
            String methodTypes = StringUtils.join(apiMethod.getMethodTypes(), ',');
            apiMethod.getUrls().stream().forEach(e -> {
                StringBuffer str = new StringBuffer();
                str.append("INSERT INTO bamboo_api_method(project_id, description, method_name, method_type, "
                        + "content_type, header, params, url, model_name, class_name, class_desc,types,service_name,class_path)VALUES(");
                String values = StringUtil.format("{},'{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}','{}','{}','{}');", projectInfo.getId(), apiMethod.getDescription(), apiMethod.getMethodName(), methodTypes, contentTypes, headers, params, e,
                        apiClass.getModuleName(), apiClass.getClassName(),
                        apiClass.getDescription(), types,
                        apiClass.getServiceName(),apiClass.getClassPath());
                str.append(values);
                try {
                    state.addBatch(str.toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        });
    }

    @SneakyThrows
    public void deleteMethodsByProjectId(Integer projectId) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        if (!Objects.isNull(projectId)) {
            state.execute(StringUtil.format("delete from bamboo_api_method WHERE project_id = {}", projectId));
        }
        state.close();
        conn.close();
    }

    @SneakyThrows
    public List<BambooApiMethod> getAllApis() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        ResultSet resultSet = state.executeQuery(StringUtil.format(SqlConstant.OTHER_ALL_API_SQL));
        List<BambooApiMethod> apiMethods = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String projectId = resultSet.getString("project_id");
            String description = resultSet.getString("description");
            String methodName = resultSet.getString("method_name");
            String methodType = resultSet.getString("method_type");
            String contentType = resultSet.getString("content_type");
            String header = resultSet.getString("header");
            String params = resultSet.getString("params");
            String url = resultSet.getString("url");
            String modelName = resultSet.getString("model_name");
            String className = resultSet.getString("class_name");
            String classPath = resultSet.getString("class_path");
            String classDesc = resultSet.getString("class_desc");
            String types = resultSet.getString("types");
            String serviceName = resultSet.getString("service_name");
            String projectName = resultSet.getString("project_name");
            String projectPath = resultSet.getString("project_path");
//            String apiFileName = resultSet.getString("api_file_name");
//            String apiFilePath = resultSet.getString("api_file_path");
            BambooApiMethod bambooApiMethod = new BambooApiMethod();
            bambooApiMethod.setProjectName(projectName);
            bambooApiMethod.setProjectPath(projectPath);
            bambooApiMethod.setId(id);
            bambooApiMethod.setProjectId(projectId);
            bambooApiMethod.setDescription(description);
            bambooApiMethod.setMethodName(methodName);
            bambooApiMethod.setMethodType(methodType);
            bambooApiMethod.setContentType(contentType);
            bambooApiMethod.setHeader(header);
            bambooApiMethod.setParams(params);
            bambooApiMethod.setUrl(url);
            bambooApiMethod.setModelName(modelName);
            bambooApiMethod.setClassName(className);
            bambooApiMethod.setClassDesc(classDesc);
            bambooApiMethod.setTypes(types);
            bambooApiMethod.setServiceName(serviceName);
            bambooApiMethod.setClassPath(classPath);
            apiMethods.add(bambooApiMethod);
        }
        resultSet.close();
        state.close();
        conn.close();

        return apiMethods;
    }
}
