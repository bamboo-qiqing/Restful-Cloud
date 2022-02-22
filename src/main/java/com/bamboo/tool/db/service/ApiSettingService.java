package com.bamboo.tool.db.service;

import com.bamboo.tool.components.api.entity.BambooApiSetting;
import com.bamboo.tool.db.SqliteConfig;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ApiSettingService {

    @SneakyThrows
    public List<BambooApiSetting> selectAll() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuffer str = new StringBuffer();
        str.append("select ");
        str.append("bas.*");
        str.append("from ");
        str.append("bamboo_api_setting bas;");
        ResultSet resultSet = state.executeQuery(str.toString());
        List<BambooApiSetting> apiSettings = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            int annotationInfoSettingId = resultSet.getInt("annotation_info_setting_id");
            String classUrlMode = resultSet.getString("class_url_mode");
            String classUrlModeValue = resultSet.getString("class_url_mode_value");
            String methodUrlMode = resultSet.getString("method_url_mode");
            String methodUrlModeValue = resultSet.getString("method_url_mode_value");

            BambooApiSetting apiSetting = new BambooApiSetting();
            apiSetting.setId(id);
            apiSetting.setAnnotationInfoSettingId(annotationInfoSettingId);
            apiSetting.setClassUrlMode(classUrlMode);
            apiSetting.setClassUrlModeValue(classUrlModeValue);
            apiSetting.setMethodUrlMode(methodUrlMode);
            apiSetting.setMethodUrlModeValue(methodUrlModeValue);
            apiSettings.add(apiSetting);
        }
        resultSet.close();
        state.close();
        conn.close();
        return apiSettings;
    }
}
