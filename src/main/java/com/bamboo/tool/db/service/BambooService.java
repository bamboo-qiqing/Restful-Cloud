package com.bamboo.tool.db.service;


import b.C.S;
import cn.hutool.core.collection.CollectionUtil;
import com.bamboo.tool.components.api.entity.BambooAnnotationInfo;
import com.bamboo.tool.components.api.entity.BambooClass;
import com.bamboo.tool.components.api.entity.BambooMethod;
import com.bamboo.tool.components.api.entity.SqliteMaster;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.SqliteConfig;
import com.bamboo.tool.util.StringUtil;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by GuoQing
 * Date 2022/2/25 13:27
 * Description
 */
public class BambooService {

    @SneakyThrows
    public void initTable() {
        final List<SqliteMaster> tables = querTables();
        final Map<String, SqliteMaster> masterMap = tables.stream().collect(Collectors.toMap(SqliteMaster::getName, e -> e));
        final List<String> exeSql = new ArrayList<>();
        if (masterMap.get("bamboo_class") == null) {
            //初始化表
            final StringBuffer initClassTable = new StringBuffer();
            initClassTable.append("create table bamboo_class(");
            initClassTable.append("id    text not null  constraint bamboo_class_pk  primary key,");
            initClassTable.append("class_name  text,");
            initClassTable.append("model_name  text,");
            initClassTable.append("description text,");
            initClassTable.append("class_path  text,");
            initClassTable.append("project_id  text");
            initClassTable.append(");");
            exeSql.add(initClassTable.toString());
            //初始化索引
            exeSql.add("create unique index bamboo_class_id_uindex on bamboo_class (id);");
        }
        if (masterMap.get("bamboo_method") == null) {
            final StringBuffer initApiMethodTable = new StringBuffer();
            initApiMethodTable.append("create table bamboo_method(");
            initApiMethodTable.append("id  text not null constraint bamboo_api_method_pk  primary key,");
            initApiMethodTable.append("project_id  text not null,");
            initApiMethodTable.append("method_name text,");
            initApiMethodTable.append("class_id  text,");
            initApiMethodTable.append("description  text");

            initApiMethodTable.append(");");
            exeSql.add(initApiMethodTable.toString());
            //初始化索引
            exeSql.add("create unique index bamboo_method_id_uindex on bamboo_method (id);");
        }
        if (masterMap.get("bamboo_annotation_info") == null) {
            final StringBuffer initAnnotationInfoTable = new StringBuffer();
            initAnnotationInfoTable.append("create table bamboo_annotation_info(");
            initAnnotationInfoTable.append("id   text not null  constraint bamboo_class_annotation_pk primary key,");
            initAnnotationInfoTable.append("settinf_id  text,");
            initAnnotationInfoTable.append("value    text,");
            initAnnotationInfoTable.append("associated_id   text,");
            initAnnotationInfoTable.append("project_id   text");
            initAnnotationInfoTable.append(");");
            exeSql.add(initAnnotationInfoTable.toString());

            //初始化索引
            exeSql.add("create unique index bamboo_class_annotation_id_uindex on bamboo_annotation_info (id);");


        }
        if (masterMap.get("annotation_param_setting") == null) {
            final StringBuffer initParamSettingTable = new StringBuffer();
            initParamSettingTable.append("create table annotation_param_setting(");
            initParamSettingTable.append("id  integer not null constraint annotation_param_pk primary key autoincrement,");
            initParamSettingTable.append("name        text,");
            initParamSettingTable.append("type        text,");
            initParamSettingTable.append("describe    text,");
            initParamSettingTable.append("annotation_info_setting_id integer");
            initParamSettingTable.append(");");
            exeSql.add(initParamSettingTable.toString());
            //初始化索引
            exeSql.add("create unique index annotation_param_id_uindex on annotation_param_setting (id);");

        }
        if (masterMap.get("annotation_method_scope") == null) {
            final StringBuffer initMethodScopeTable = new StringBuffer();
            initMethodScopeTable.append("create table annotation_method_scope(");
            initMethodScopeTable.append("id   integer not null constraint annotation_method_scope_pk primary key autoincrement,");
            initMethodScopeTable.append("annotation_id integer,");
            initMethodScopeTable.append(" method_scope  text");
            initMethodScopeTable.append(");");
            exeSql.add(initMethodScopeTable.toString());
            //初始化索引
            exeSql.add("create unique index annotation_method_scope_id_uindex  on annotation_method_scope (id);");

        }
        if (masterMap.get("annotation_info_setting") == null) {
            final StringBuffer initMethodScopeTable = new StringBuffer();
            initMethodScopeTable.append(" create table annotation_info_setting(");
            initMethodScopeTable.append("id     integer not null constraint annotation_info_pk primary key autoincrement,");
            initMethodScopeTable.append("class_path       text,");
            initMethodScopeTable.append("class_short_name text,");
            initMethodScopeTable.append("framework_id  integer,");
            initMethodScopeTable.append("scope   text,");
            initMethodScopeTable.append("soa_type  text,");
            initMethodScopeTable.append("effect  text,");
            initMethodScopeTable.append("is_delete integer");
            initMethodScopeTable.append(");");
            exeSql.add(initMethodScopeTable.toString());
            //初始化索引
            exeSql.add("create unique index annotation_info_id_uindex on annotation_info_setting (id);");
            //初始化数据
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (1, 'org.springframework.web.bind.annotation.RestController', 'RestController', 1, 'Class', 'service', 'scann', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (2, 'org.springframework.stereotype.Controller', 'Controller', 1, 'Class', 'service', 'scann', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (3, 'org.springframework.web.bind.annotation.RequestMapping', 'RequestMapping', 1, 'Class', 'service', 'attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (4, 'org.springframework.web.bind.annotation.GetMapping', 'GetMapping', 1, 'Method', 'service', 'scann,attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (5, 'org.springframework.web.bind.annotation.PostMapping', 'PostMapping', 1, 'Method', 'service', 'scann,attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (6, 'org.springframework.web.bind.annotation.DeleteMapping', 'DeleteMapping', 1, 'Method', 'service', 'scann,attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (7, 'org.springframework.web.bind.annotation.PutMapping', 'PutMapping', 1, 'Method', 'service', 'scann,attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (8, 'org.springframework.web.bind.annotation.PatchMapping', 'PatchMapping', 1, 'Method', 'service', 'scann,attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (9, 'com.odianyun.soa.annotation.SoaMethodRegister', 'SoaMethodRegister', 2, 'Method', 'service', 'scann,attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (10, 'com.odianyun.soa.client.annotation.SoaServiceClient', 'SoaServiceClient', 2, 'Class', 'client', 'scann,attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (11, 'com.odianyun.soa.annotation.SoaServiceRegister', 'SoaServiceRegister', 2, 'Class', 'service', 'scann,attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (12, 'org.springframework.web.bind.annotation.RequestMapping', 'RequestMapping', 1, 'Method', 'service', 'scann,attribute', 0);");

        }
        if (masterMap.get("bamboo_api_setting") == null) {

        }
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        exeSql.stream().forEach(e -> {
            try {
                state.addBatch(e);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        state.executeBatch();
        state.close();
        conn.close();
    }

    private List<SqliteMaster> querTables() throws SQLException {
        StringBuffer str = new StringBuffer();
        str.append("select * from ");
        str.append("sqlite_master  where " + "type='table' AND name in ( " + "'bamboo_api_project'," + "'framework'," + "'bamboo_class'," + "'bamboo_api_setting'," + "'bamboo_method'," + "'bamboo_annotation_info'," + "'annotation_param_setting'," + "'annotation_method_scope'," + "'annotation_info_setting'" + ");");
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        ResultSet resultSet = state.executeQuery(str.toString());
        List<SqliteMaster> tableInfos = new ArrayList<>();
        while (resultSet.next()) {
            SqliteMaster sqliteMaster = new SqliteMaster();
            sqliteMaster.setType(resultSet.getString("type"));
            sqliteMaster.setName(resultSet.getString("name"));
            sqliteMaster.setTblName(resultSet.getString("tbl_name"));
            sqliteMaster.setRootpage(resultSet.getInt("rootpage"));
            sqliteMaster.setSql(resultSet.getString("sql"));
            tableInfos.add(sqliteMaster);
        }
        resultSet.close();
        state.close();
        conn.close();
        return tableInfos;
    }


    @SneakyThrows
    public void saveClass(List<BambooClass> allClasses, ProjectInfo projectInfo) {
        if (CollectionUtil.isEmpty(allClasses)) {
            return;
        }
        this.deleteClasssByProjectId(projectInfo.getId());
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        final List<String> sqls = this.addBatchSql(projectInfo, allClasses);
        sqls.stream().forEach(e -> {
            try {
                state.addBatch(e);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        state.executeBatch();

    }

    @SneakyThrows
    public void deleteClasssByProjectId(Integer projectId) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        if (!Objects.isNull(projectId)) {
            state.execute(StringUtil.format("delete from bamboo_class WHERE project_id = {}", projectId));
            state.execute(StringUtil.format("delete from bamboo_method WHERE project_id = {}", projectId));
        }
        state.close();
        conn.close();
    }

    @SneakyThrows
    private List<String> addBatchSql(ProjectInfo projectInfo, List<BambooClass> allClasses) {
        final List<String> sqls = new ArrayList<>();
        allClasses.stream().forEach(bambooClass -> {
            bambooClass.setId(UUID.randomUUID().toString());
            StringBuffer sql = new StringBuffer();
            sql.append("INSERT INTO bamboo_class (");
            sql.append("id,class_name, model_name, description, class_path, project_id");
            sql.append(") VALUES (");
            sql.append("'" + bambooClass.getId() + "',");
            sql.append("'" + bambooClass.getClassName() + "',");
            sql.append("'" + bambooClass.getModuleName() + "',");
            sql.append("'" + bambooClass.getDescription() + "',");
            sql.append("'" + bambooClass.getClassPath() + "',");
            sql.append("'" + projectInfo.getProjectId() + "'");
            sql.append(");");
            sqls.add(sql.toString());
            final List<BambooAnnotationInfo> annotations = bambooClass.getAnnotations();
            if(CollectionUtil.isNotEmpty(annotations)){
                annotations.forEach(annotation->{
                    annotation.setId(UUID.randomUUID().toString());
                    StringBuffer annotationSql = new StringBuffer();
                    annotationSql.append(" INSERT INTO bamboo_annotation_info (");
                    annotationSql.append("id, settinf_id, value, associated_id,project_id) ");
                    annotationSql.append(") VALUES (");
                    annotationSql.append("'" + annotation.getId() + "',");
                    annotationSql.append("'" + annotation.getAnnotationInfoSetting().getId() + "',");
                    annotationSql.append("'" + annotation.getValue() + "',");
                    annotationSql.append("'" + bambooClass.getId() + "',");
                    annotationSql.append("'" + projectInfo.getId() + "'");
                    annotationSql.append(");");
                    sqls.add(annotationSql.toString());
                });
            }
            final List<BambooMethod> methods = bambooClass.getMethods();
            if (CollectionUtil.isNotEmpty(methods)) {
                methods.forEach(method -> {
                    method.setId(UUID.randomUUID().toString());
                    StringBuffer sqlMethod = new StringBuffer();
                    sql.append("insert into bamboo_method (id, project_id, method_name, class_id) VALUES(");
                    sql.append("'" + method.getId() + "',");
                    sql.append("'" + projectInfo.getProjectId() + "',");
                    sql.append("'" + method.getMethodName() + "',");
                    sql.append("'" + method.getDescription() + "',");
                    sql.append(");");
                    sqls.add(sqlMethod.toString());
                });
            }
        });
        return sqls;
    }
}
