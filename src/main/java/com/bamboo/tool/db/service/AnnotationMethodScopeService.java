package com.bamboo.tool.db.service;

import com.bamboo.tool.components.api.entity.AnnotationInfo;
import com.bamboo.tool.components.api.entity.AnnotationMethodScope;
import com.bamboo.tool.db.SqliteConfig;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/18 15:55
 * Description
 */
public class AnnotationMethodScopeService {

    @SneakyThrows
    public List<AnnotationMethodScope> selectAll() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuffer str = new StringBuffer();
        str.append("select");
        str.append("ams.*");
        str.append("from");
        str.append("annotation_method_scope ams;");
        ResultSet resultSet = state.executeQuery(str.toString());
        List<AnnotationMethodScope> methodScopes = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String methodScope = resultSet.getString("method_scope");
            int annotationId = resultSet.getInt("annotation_id");
            AnnotationMethodScope annotationInfo = new AnnotationMethodScope();
            annotationInfo.setId(id);
            annotationInfo.setMethodScope(methodScope);
            annotationInfo.setAnnotationId(annotationId);
            methodScopes.add(annotationInfo);
        }
        resultSet.close();
        state.close();
        conn.close();
        return methodScopes;
    }

}
