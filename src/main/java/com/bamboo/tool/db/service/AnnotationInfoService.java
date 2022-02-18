package com.bamboo.tool.db.service;

import b.C.C;
import com.bamboo.tool.components.api.entity.AnnotationInfo;
import com.bamboo.tool.components.api.entity.AnnotationMethodScope;
import com.bamboo.tool.components.api.entity.AnnotationParam;
import com.bamboo.tool.components.api.entity.Framework;
import com.bamboo.tool.components.api.enums.AnnotationScope;
import com.bamboo.tool.db.SqliteConfig;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create by GuoQing
 * Date 2022/2/18 11:15
 * Description
 */
public class AnnotationInfoService {

    @SneakyThrows
    public List<AnnotationInfo> selectAll() {
        Application application = ApplicationManager.getApplication();
        FrameworkService frameworkService = application.getService(FrameworkService.class);
        Map<Integer, Framework> framework = frameworkService.selectAll().stream().collect(Collectors.toMap(e -> e.getId(), e -> e));

        AnnotationMethodScopeService methodScopeService = application.getService(AnnotationMethodScopeService.class);
        Map<Integer, List<AnnotationMethodScope>> methodScopeMap = methodScopeService.selectAll().stream().collect(Collectors.groupingBy(e -> e.getAnnotationId(), Collectors.toList()));

        AnnotationParamService paramService = application.getService(AnnotationParamService.class);
        Map<Integer, List<AnnotationParam>> paramMap = paramService.selectAll().stream().collect(Collectors.groupingBy(e -> e.getAnnotationInfoId(), Collectors.toList()));
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        String sql = getQuerySQL();
        ResultSet resultSet = state.executeQuery(sql);
        List<AnnotationInfo> annotationInfos = new ArrayList<>();
        while (resultSet.next()) {
            AnnotationInfo annotationInfo = getAnnotationInfo(resultSet, framework, methodScopeMap,paramMap);
            annotationInfos.add(annotationInfo);
        }
        resultSet.close();
        state.close();
        conn.close();
        return annotationInfos;
    }

    @NotNull
    private String getQuerySQL() {
        StringBuffer str = new StringBuffer();
        str.append("select");
        str.append("ai.id,");
        str.append("ai.class_path,");
        str.append("ai.class_short_name,");
        str.append("ai.scope,");
        str.append("ai.soa_type,");
        str.append("ai.effect,");
        str.append("ai.framework_id");
        str.append("from");
        str.append("annotation_info ai inner join framework f on ai.framework_id = f.id;");
        return str.toString();
    }

    @NotNull
    private String getQueryScannClassSQL() {
        StringBuffer str = new StringBuffer();
        str.append("select");
        str.append("ai.id,");
        str.append("ai.class_path,");
        str.append("ai.class_short_name,");
        str.append("ai.scope,");
        str.append("ai.soa_type,");
        str.append("ai.effect,");
        str.append("ai.framework_id,");
        str.append("f.name as framework_name,");
        str.append("f.describe as framework_describe,");
        str.append("from");
        str.append("annotation_info ai inner join framework f on ai.framework_id = f.id");
        str.append("where ai.scope='Class' and ai.effect like '%scann%' ;");
        return str.toString();
    }

    @NotNull
    private AnnotationInfo getAnnotationInfo(ResultSet resultSet, Map<Integer, Framework> frameworkMap,
                                             Map<Integer, List<AnnotationMethodScope>> methodScopeMap,
                                             Map<Integer, List<AnnotationParam>> paramMap) throws SQLException {
        AnnotationInfo annotationInfo = new AnnotationInfo();
        int id = resultSet.getInt("id");
        String classPath = resultSet.getString("class_path");
        String classShortName = resultSet.getString("class_short_name");
        String scope = resultSet.getString("scope");
        String soaType = resultSet.getString("soa_type");
        String effect = resultSet.getString("effect");
        int frameworkId = resultSet.getInt("framework_id");
        Framework framework = frameworkMap.get(Integer.valueOf(frameworkId));
        List<AnnotationMethodScope> annotationMethodScopes = methodScopeMap.get(Integer.valueOf(id));
        List<AnnotationParam> params = paramMap.get(Integer.valueOf(id));
        annotationInfo.setId(id);
        annotationInfo.setAnnotationPath(classPath);
        annotationInfo.setAnnotationName(classShortName);
        annotationInfo.setScope(AnnotationScope.getAnnotationScope(scope));
        annotationInfo.setSoaType(soaType);
        annotationInfo.setEffect(effect);
        annotationInfo.setFramework(framework);
        annotationInfo.setMethodScopes(annotationMethodScopes);
        annotationInfo.setParams(params);
        return annotationInfo;
    }

    @SneakyThrows
    public List<AnnotationInfo> selectScannClassAll() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        String sql = getQueryScannClassSQL();
        ResultSet resultSet = state.executeQuery(sql);
        List<AnnotationInfo> annotationInfos = new ArrayList<>();
        while (resultSet.next()) {
            AnnotationInfo annotationInfo = new AnnotationInfo();
            ;
            int id = resultSet.getInt("id");
            String classPath = resultSet.getString("class_path");
            String classShortName = resultSet.getString("class_short_name");
            String scope = resultSet.getString("scope");
            String soaType = resultSet.getString("soa_type");
            String effect = resultSet.getString("effect");


            int frameworkId = resultSet.getInt("framework_id");
            String frameworkName = resultSet.getString("framework_name");
            String frameworkDescribe = resultSet.getString("framework_describe");

            annotationInfo.setId(id);
            annotationInfo.setAnnotationPath(classPath);
            annotationInfo.setAnnotationName(classShortName);
            annotationInfo.setScope(AnnotationScope.getAnnotationScope(scope));
            annotationInfo.setSoaType(soaType);
            annotationInfo.setEffect(effect);

            annotationInfos.add(annotationInfo);
        }
        resultSet.close();
        state.close();
        conn.close();
        return annotationInfos;
    }
}
