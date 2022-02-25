package com.bamboo.tool.db.service;

import com.bamboo.tool.components.api.entity.SqliteMaster;
import com.bamboo.tool.db.SqliteConfig;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/25 13:27
 * Description
 */
public class BambooService {

    @SneakyThrows
    public void initTable() {
        StringBuffer str = new StringBuffer();
        str.append("select * from");
        str.append("sqlite_master  where "
                + "type='table' AND name in ( "
                + "bamboo_api_project,"
                + "framework,"
                + "bamboo_class,"
                + "bamboo_api_setting,"
                + "bamboo_api_method,"
                + "bamboo_annotation_info,"
                + "annotation_param_setting,"
                + "annotation_method_scope,"
                + "annotation_info_setting"
                + ");");
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        ResultSet resultSet = state.executeQuery(str.toString());
        List<SqliteMaster> tableInfos = new ArrayList<>();
        while (resultSet.next()){
            SqliteMaster sqliteMaster = new SqliteMaster();
            sqliteMaster.setType(resultSet.getString("type"));
            sqliteMaster.setName(resultSet.getString("name"));
            sqliteMaster.setTblName(resultSet.getString("tbl_name"));
            sqliteMaster.setRootpage(resultSet.getInt("rootpage"));
            sqliteMaster.setSql(resultSet.getString("sql"));
            tableInfos.add(sqliteMaster);
        }
        resultSet.close();
    }

    private void initTableStructure() {

    }
}
