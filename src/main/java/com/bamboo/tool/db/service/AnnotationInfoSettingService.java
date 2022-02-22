package com.bamboo.tool.db.service;

import com.bamboo.tool.components.api.entity.*;
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
public class AnnotationInfoSettingService {

    @SneakyThrows
    public List<AnnotationInfoSetting> selectAll() {
        Application application = ApplicationManager.getApplication();
        FrameworkService frameworkService = application.getService(FrameworkService.class);
        Map<Integer, Framework> framework = frameworkService.selectAll().stream().collect(Collectors.toMap(e -> e.getId(), e -> e));

        List<AnnotationMethodScope> methodScopes = application.getService(AnnotationMethodScopeService.class).selectAll();
        Map<Integer, List<AnnotationMethodScope>> methodScopeMap = methodScopes.stream().collect(Collectors.groupingBy(e -> e.getAnnotationId(), Collectors.toList()));

        List<AnnotationParam> params = application.getService(AnnotationParamService.class).selectAll();
        Map<Integer, List<AnnotationParam>> paramMap = params.stream().collect(Collectors.groupingBy(e -> e.getAnnotationInfoId(), Collectors.toList()));

        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        String sql = getQuerySQL();
        ResultSet resultSet = state.executeQuery(sql);
        List<AnnotationInfoSetting> annotationInfoSettings = new ArrayList<>();
        while (resultSet.next()) {
            AnnotationInfoSetting annotationInfoSetting = getAnnotationInfo(resultSet, framework, methodScopeMap, paramMap);
            annotationInfoSettings.add(annotationInfoSetting);
        }
        resultSet.close();
        state.close();
        conn.close();
        return annotationInfoSettings;
    }

    @NotNull
    private String getQuerySQL() {
        StringBuffer str = new StringBuffer();
        str.append("select ");
        str.append("ai.id,");
        str.append("ai.class_path,");
        str.append("ai.class_short_name,");
        str.append("ai.scope,");
        str.append("ai.soa_type,");
        str.append("ai.effect,");
        str.append("ai.framework_id ");
        str.append("from ");
        str.append("annotation_info_setting  ai inner join framework  f on ai.framework_id = f.id and ai.is_delete=0;");
        return str.toString();
    }


    @NotNull
    private AnnotationInfoSetting getAnnotationInfo(ResultSet resultSet,
                                                    Map<Integer, Framework> frameworkMap,
                                                    Map<Integer, List<AnnotationMethodScope>> methodScopeMap,
                                                    Map<Integer, List<AnnotationParam>> paramMap) throws SQLException {
        AnnotationInfoSetting annotationInfoSetting = new AnnotationInfoSetting();
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


        annotationInfoSetting.setId(id);
        annotationInfoSetting.setAnnotationPath(classPath);
        annotationInfoSetting.setAnnotationName(classShortName);
        annotationInfoSetting.setScope(AnnotationScope.getAnnotationScope(scope));
        annotationInfoSetting.setSoaType(soaType);
        annotationInfoSetting.setEffect(effect);
        annotationInfoSetting.setFramework(framework);

        if (annotationMethodScopes != null) {
            annotationInfoSetting.setMethodScopes(annotationMethodScopes);
        }

        if (params != null) {
            annotationInfoSetting.setParams(params);
        }

        return annotationInfoSetting;
    }

}
