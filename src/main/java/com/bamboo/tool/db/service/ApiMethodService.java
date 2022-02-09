package com.bamboo.tool.db.service;

import cn.hutool.core.collection.CollectionUtil;
import com.bamboo.tool.components.api.entity.ApiClass;
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
                str.append("INSERT INTO bamboo_api_method(project_id, description, method_name, method_type, " +
                        "content_type, header, params, url, model_name, class_name, class_desc,types,service_name)VALUES(");
                String values = StringUtil.format("{},'{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}','{}','{}');",
                        projectInfo.getId(), apiMethod.getDescription(),
                        apiMethod.getMethodName(), methodTypes,
                        contentTypes,
                        headers,
                        params, e,
                        apiClass.getModuleName(), apiClass.getClassName(),
                        apiClass.getDescription(),
                        types,apiClass.getServiceName());
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

}
