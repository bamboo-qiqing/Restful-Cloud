package com.bamboo.tool.db.service;


import cn.hutool.core.collection.CollectionUtil;
import com.bamboo.tool.components.api.entity.*;
import com.bamboo.tool.components.api.enums.AnnotationScope;
import com.bamboo.tool.components.api.enums.RequestMethod;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.SqlConstant;
import com.bamboo.tool.db.SqliteConfig;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.util.StringUtil;
import com.intellij.openapi.project.Project;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

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
    public static void initTable() {
        final List<SqliteMaster> tables = querTables();
        final Map<String, SqliteMaster> masterMap = tables.stream().collect(Collectors.toMap(SqliteMaster::getName, e -> e));
        final List<String> exeSql = new ArrayList<>();


        if (masterMap.get("bamboo_project") == null) {
            final StringBuffer initProjectTable = new StringBuffer();
            initProjectTable.append("create table bamboo_project(");
            initProjectTable.append(" id integer not null   constraint bamboo_project_pk   primary key autoincrement,");
            initProjectTable.append("project_name  text,");
            initProjectTable.append("project_path  text");
            initProjectTable.append(");");
            exeSql.add(initProjectTable.toString());
            exeSql.add("create unique index bamboo_project_id_uindex on bamboo_project (id);");
        }
        if (masterMap.get("bamboo_class") == null) {

            final StringBuffer initClassTable = new StringBuffer();
            initClassTable.append("create table bamboo_class(");
            initClassTable.append("id    text not null  constraint bamboo_class_pk  primary key,");
            initClassTable.append("class_name  text,");
            initClassTable.append("model_name  text,");
            initClassTable.append("description text,");
            initClassTable.append("class_path  text,");
            initClassTable.append("project_id  text,");
            initClassTable.append("setting_id  text");

            initClassTable.append(");");
            exeSql.add(initClassTable.toString());

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

            exeSql.add("create unique index bamboo_method_id_uindex on bamboo_method (id);");
        }


        if (masterMap.get("bamboo_api") == null) {
            final StringBuffer initAnnotationInfoTable = new StringBuffer();
            initAnnotationInfoTable.append("create table bamboo_api(");
            initAnnotationInfoTable.append("id  text not null constraint bamboo_api_pk primary key,");
            initAnnotationInfoTable.append("method_id  text,");
            initAnnotationInfoTable.append("url    text,");
            initAnnotationInfoTable.append("request_methods   text,");
            initAnnotationInfoTable.append("project_id   text,");
            initAnnotationInfoTable.append("params   text,");
            initAnnotationInfoTable.append("headers   text,");
            initAnnotationInfoTable.append("consumes   text,");
            initAnnotationInfoTable.append("produces   text");
            initAnnotationInfoTable.append(");");
            exeSql.add(initAnnotationInfoTable.toString());
            exeSql.add("create unique index bamboo_api_id_uindex on bamboo_api (id);");
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

            exeSql.add("create unique index annotation_param_id_uindex on annotation_param_setting (id);");

            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (1, 'value', 'classUrl', null, 3);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (2, 'name', 'poolUrl', null, 10);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (3, 'interfaceName', 'classUrl', null, 10);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (4, 'interfaceClass', 'classUrl', null, 11);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (5, 'value', 'methodUrl', null, 12);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (6, 'null', 'methodUrl', null, 12);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (7, 'null', 'classUrl', null, 3);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (8, 'method', 'requestMethod', null, 12);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (9, 'null', 'methodUrl', null, 5);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (10, 'value', 'methodUrl', null, 5);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (11, 'path', 'methodUrl', null, 5);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (12, 'consumes', 'consumes', null, 12);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (13, 'params', 'params', null, 12);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (14, 'headers', 'headers', null, 12);");
            exeSql.add("INSERT INTO annotation_param_setting (id, name, type, describe, annotation_info_setting_id) VALUES (15, 'produces', 'produces', null, 12);");

        }
        if (masterMap.get("annotation_method_scope") == null) {
            final StringBuffer initMethodScopeTable = new StringBuffer();
            initMethodScopeTable.append("create table annotation_method_scope(");
            initMethodScopeTable.append("id   integer not null constraint annotation_method_scope_pk primary key autoincrement,");
            initMethodScopeTable.append("annotation_id integer,");
            initMethodScopeTable.append(" method_scope  text");
            initMethodScopeTable.append(");");
            exeSql.add(initMethodScopeTable.toString());

            exeSql.add("create unique index annotation_method_scope_id_uindex  on annotation_method_scope (id);");

            exeSql.add("INSERT INTO annotation_method_scope (id, annotation_id, method_scope) VALUES (1, 11, 'public');");
            exeSql.add("INSERT INTO annotation_method_scope (id, annotation_id, method_scope) VALUES (2, 10, 'public');");
            exeSql.add("INSERT INTO annotation_method_scope (id, annotation_id, method_scope) VALUES (3, 1, 'annotation');");
            exeSql.add("INSERT INTO annotation_method_scope (id, annotation_id, method_scope) VALUES (4, 2, 'annotation');");
            exeSql.add("INSERT INTO annotation_method_scope (id, annotation_id, method_scope) VALUES (5, 5, 'annotation');");
        }
        if (masterMap.get("framework") == null) {
            final StringBuffer initMethodScopeTable = new StringBuffer();
            initMethodScopeTable.append("create table framework(");
            initMethodScopeTable.append("id       integer not null  constraint framework_pk  primary key autoincrement,");
            initMethodScopeTable.append(" name     text,");
            initMethodScopeTable.append(" describe text");
            initMethodScopeTable.append(");");
            exeSql.add(initMethodScopeTable.toString());

            exeSql.add("create unique index framework_id_uindex  on framework (id);");
            exeSql.add("INSERT INTO framework (id, name, describe) VALUES (1, 'Spring', 'Spring');");
            exeSql.add("INSERT INTO framework (id, name, describe) VALUES (2, 'o_dian_yun', 'o_dian_yun');");
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

            exeSql.add("create unique index annotation_info_id_uindex on annotation_info_setting (id);");

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
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (14, 'org.springframework.web.bind.annotation.PostMapping', 'PostMapping', 1, 'Method', 'service', 'attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (15, 'org.springframework.web.bind.annotation.GetMapping', 'GetMapping', 1, 'Method', 'service', 'attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (16, 'org.springframework.web.bind.annotation.PatchMapping', 'PatchMapping', 1, 'Method', 'service', 'attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (17, 'org.springframework.web.bind.annotation.DeleteMapping', 'DeleteMapping', 1, 'Method', 'service', 'attribute', 0);");
            exeSql.add("INSERT INTO annotation_info_setting (id, class_path, class_short_name, framework_id, scope, soa_type, effect, is_delete) VALUES (18, 'org.springframework.web.bind.annotation.PutMapping', 'PutMapping', 1, 'Method', 'service', 'attribute', 0);");

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

    private static List<SqliteMaster> querTables() throws SQLException {
        StringBuffer str = new StringBuffer();
        str.append("select * from ");
        str.append("sqlite_master  where " + "type='table' AND name in ( ");
        str.append("'bamboo_project',");
        str.append("'framework',");
        str.append("'bamboo_class',");
        str.append("'bamboo_method',");
        str.append("'bamboo_api',");
        str.append("'annotation_param_setting',");
        str.append("'annotation_method_scope',");
        str.append("'annotation_info_setting'");
        str.append(");");
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
    public static void saveClass(List<BambooClass> allClasses, ProjectInfo projectInfo) {
        if (CollectionUtil.isEmpty(allClasses)) {
            return;
        }
        BambooService.deleteClasssByProjectId(projectInfo.getId().toString());
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        final List<String> sqls = BambooService.addBatchSql(projectInfo, allClasses);
        sqls.stream().forEach(e -> {
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

    @SneakyThrows
    public static void deleteClasssByProjectId(String projectId) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        if (!Objects.isNull(projectId)) {
            state.addBatch(StringUtil.format("delete from bamboo_class WHERE project_id = '{}';", projectId));
            state.addBatch(StringUtil.format("delete from bamboo_method WHERE project_id = '{}';", projectId));
            state.addBatch(StringUtil.format("delete from bamboo_api WHERE project_id = '{}';", projectId));
            state.executeBatch();
        }
        state.close();
        conn.close();
    }

    @SneakyThrows
    private static List<String> addBatchSql(ProjectInfo projectInfo, List<BambooClass> allClasses) {
        final List<String> sqls = new ArrayList<>();
        allClasses.stream().forEach(bambooClass -> {
            bambooClass.setId(UUID.randomUUID().toString());
            StringBuffer sql = new StringBuffer();
            sql.append("INSERT INTO bamboo_class (id,class_name, model_name, description, class_path, project_id,setting_id) VALUES (");
            sql.append("'").append(bambooClass.getId()).append("',");
            sql.append("'").append(bambooClass.getClassName()).append("',");
            sql.append("'").append(bambooClass.getModuleName()).append("',");
            sql.append("'").append(bambooClass.getDescription()).append("',");
            sql.append("'").append(bambooClass.getClassPath()).append("',");
            sql.append("'").append(projectInfo.getId().toString()).append("',");
            sql.append("'").append(bambooClass.getSetting().getId()).append("'");
            sql.append(");");
            sqls.add(sql.toString());
            final List<BambooMethod> methods = bambooClass.getMethods();
            if (CollectionUtil.isNotEmpty(methods)) {
                methods.forEach(method -> {
                    method.setId(UUID.randomUUID().toString());
                    StringBuffer sqlMethod = new StringBuffer();
                    sqlMethod.append("insert into bamboo_method (id, project_id, method_name, class_id,description) VALUES(");
                    sqlMethod.append("'").append(method.getId()).append("',");
                    sqlMethod.append("'").append(projectInfo.getId()).append("',");
                    sqlMethod.append("'").append(method.getMethodName()).append("',");
                    sqlMethod.append("'").append(bambooClass.getId()).append("',");
                    sqlMethod.append("'").append(method.getDescription()).append("'");
                    sqlMethod.append(");");
                    sqls.add(sqlMethod.toString());
                    final List<String> classUrls = bambooClass.getClassUrl();
                    if (CollectionUtil.isNotEmpty(classUrls)) {
                        classUrls.parallelStream().forEach(e -> {
                            buildApiSql(sqls, bambooClass, method, e, projectInfo);
                        });
                    } else {
                        buildApiSql(sqls, bambooClass, method, "", projectInfo);
                    }
                });
            }
        });
        return sqls;
    }

    private static void buildApiSql(List<String> sqls, BambooClass bambooClass, BambooMethod method, String e, ProjectInfo projectInfo) {
        List<String> methodUrls = method.getMethodUrl();
        if (CollectionUtil.isNotEmpty(methodUrls)) {
            methodUrls.parallelStream().forEach(methodUrl -> {
                StringBuffer api = new StringBuffer();
                String poolUrl = bambooClass.getPoolUrl();
                String classUrl = e;
                if (StringUtil.isEmpty(poolUrl) || "/".equals(poolUrl)) {
                    poolUrl = "";
                } else {
                    poolUrl = StringUtil.addPrefixIfNot(poolUrl, "/");
                }
                if (StringUtil.isEmpty(classUrl) || "/".equals(classUrl)) {
                    classUrl = "";
                } else {
                    classUrl = StringUtil.addPrefixIfNot(classUrl, "/");
                }
                final List<String> requestMethods = method.getRequestMethods();
                if (CollectionUtil.isEmpty(requestMethods)) {
                    requestMethods.add(RequestMethod.ALL.getCode());
                }

                api.append("insert into bamboo_api (id, method_id, url, request_methods,project_id,consumes,params,headers,produces) VALUES(");
                api.append("'" + UUID.randomUUID() + "',");
                api.append("'" + method.getId() + "',");
                api.append("'" + poolUrl + classUrl + StringUtil.addPrefixIfNot(methodUrl, "/") + "',");
                api.append("'" + requestMethods + "',");
                api.append("'" + projectInfo.getId() + "',");
                api.append("'" + method.getConsumes() + "',");
                api.append("'" + method.getParams() + "',");
                api.append("'" + method.getHeaders() + "',");
                api.append("'" + method.getProduces() + "'");
                api.append(");");
                sqls.add(api.toString());
            });
        }
    }

    @SneakyThrows
    public static List<AnnotationParam> selectAllAnnotationParam() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuffer str = new StringBuffer();
        str.append("select ");
        str.append("ap.*");
        str.append("from ");
        str.append("annotation_param_setting ap;");
        ResultSet resultSet = state.executeQuery(str.toString());
        List<AnnotationParam> params = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String type = resultSet.getString("type");
            String describe = resultSet.getString("describe");
            int annotationInfoId = resultSet.getInt("annotation_info_setting_id");
            AnnotationParam annotationInfo = new AnnotationParam();
            annotationInfo.setId(id);
            annotationInfo.setName(name);
            annotationInfo.setType(type);
            annotationInfo.setDescribe(describe);
            annotationInfo.setAnnotationInfoId(annotationInfoId);
            params.add(annotationInfo);
        }
        resultSet.close();
        state.close();
        conn.close();
        return params;
    }


    @SneakyThrows
    public static List<Framework> selectAllFramework() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuffer str = new StringBuffer();
        str.append("select ");
        str.append("f.*");
        str.append("from ");
        str.append("framework f;");
        ResultSet resultSet = state.executeQuery(str.toString());
        List<Framework> frameworks = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String describe = resultSet.getString("describe");

            Framework framework = new Framework();
            framework.setId(id);
            framework.setName(name);
            framework.setDescribe(describe);
            frameworks.add(framework);
        }
        resultSet.close();
        state.close();
        conn.close();
        return frameworks;
    }


    @SneakyThrows
    public static List<AnnotationMethodScope> selectAllAnnotationMethodScope() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuffer str = new StringBuffer();
        str.append("select ");
        str.append("ams.* ");
        str.append("from ");
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

    @SneakyThrows
    public static List<AnnotationInfoSetting> selectAllAnnotationInfoSetting() {
        Map<Integer, Framework> framework = BambooService.selectAllFramework().stream().collect(Collectors.toMap(Framework::getId, e -> e));
        Map<Integer, List<AnnotationMethodScope>> methodScopeMap = BambooService.selectAllAnnotationMethodScope().stream().collect(Collectors.groupingBy(AnnotationMethodScope::getAnnotationId, Collectors.toList()));
        Map<Integer, List<AnnotationParam>> paramMap = BambooService.selectAllAnnotationParam().stream().collect(Collectors.groupingBy(AnnotationParam::getAnnotationInfoId, Collectors.toList()));

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
    private static String getQuerySQL() {
        StringBuilder str = new StringBuilder();
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
    private static AnnotationInfoSetting getAnnotationInfo(ResultSet resultSet, Map<Integer, Framework> frameworkMap, Map<Integer, List<AnnotationMethodScope>> methodScopeMap, Map<Integer, List<AnnotationParam>> paramMap) throws SQLException {
        AnnotationInfoSetting annotationInfoSetting = new AnnotationInfoSetting();
        int id = resultSet.getInt("id");
        String classPath = resultSet.getString("class_path");
        String classShortName = resultSet.getString("class_short_name");
        String scope = resultSet.getString("scope");
        String soaType = resultSet.getString("soa_type");
        String effect = resultSet.getString("effect");
        int frameworkId = resultSet.getInt("framework_id");
        Framework framework = frameworkMap.get(frameworkId);
        List<AnnotationMethodScope> annotationMethodScopes = methodScopeMap.get(id);
        List<AnnotationParam> params = paramMap.get(id);


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

    @SneakyThrows
    public static List<BambooApiMethod> getAllApi(String projectId, String notProjectId, Project project) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuilder str = new StringBuilder();
        str.append("select ba.url,");
        str.append("ba.request_methods requestMethods,");
        str.append("bm.method_name     methodName,");
        str.append("bm.description     methodDesc,");
        str.append("bc.class_name      className,");
        str.append("bc.class_path      classPath,");
        str.append("bc.description     classDesc,");
        str.append("bc.model_name      modelName,");
        str.append("bc.description     classDesc,");
        str.append("bap.project_name   projectName,");
        str.append("bap.id     projectId,");
        str.append("ais.soa_type       soaType,");
        str.append("f.name             frameworkName");
        str.append(" from bamboo_api ba");
        str.append(" inner join bamboo_method bm on ba.method_id = bm.id");
        str.append(" inner join bamboo_class bc on bm.class_id = bc.id");
        str.append(" inner join bamboo_project bap on bc.project_id = bap.id ");
        str.append(" inner join annotation_info_setting ais on bc.setting_id = ais.id");
        str.append(" inner join framework f on f.id = ais.framework_id");
        if (StringUtil.isNotEmpty(projectId)) {
            str.append(" where ba.project_id =");
            str.append("'");
            str.append(projectId);
            str.append("'");
        }
        if (StringUtil.isNotEmpty(notProjectId)) {
            str.append(" where ba.project_id !=");
            str.append("'");
            str.append(notProjectId);
            str.append("'");
        }
        str.append(";");
        List<BambooApiMethod> apis = new ArrayList<>();
        final ResultSet resultSet = state.executeQuery(str.toString());
        while (resultSet.next()) {
            final BambooApiMethod api = new BambooApiMethod();
            String url = resultSet.getString("url");
            String requestMethods = resultSet.getString("requestMethods");
            String methodName = resultSet.getString("methodName");
            String methodDesc = resultSet.getString("methodDesc");
            String className = resultSet.getString("className");
            String classPath = resultSet.getString("classPath");
            String classDesc = resultSet.getString("classDesc");
            String modelName = resultSet.getString("modelName");
            String projectName = resultSet.getString("projectName");
            String soaType = resultSet.getString("soaType");
            String frameworkName = resultSet.getString("frameworkName");
            api.setUrl(url);
            api.setRequestMethods(requestMethods);
            api.setMethodName(methodName);
            api.setModelName(modelName);
            api.setMethodDesc(methodDesc);
            api.setClassName(className);
            api.setClassPath(classPath);
            api.setProjectName(projectName);
            api.setSoaType(soaType);
            api.setFrameworkName(frameworkName);
            api.setProject(project);
            api.setClassDesc(classDesc);
            apis.add(api);
        }
        resultSet.close();
        state.close();
        conn.close();
        return apis;
    }

    @SneakyThrows
    public static ProjectInfo queryProject(String projectPath, String projectName) {
        BambooService.createProject();
        String sql = StringUtil.format("SELECT * FROM bamboo_project where project_path='{}' and project_name='{}' ; ", projectPath, projectName);
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(sql);
        List<ProjectInfo> projectInfos = new ArrayList<>();
        while (rs.next()) {
            ProjectInfo projectInfo = new ProjectInfo();
            String name = rs.getString("project_name");
            int id = rs.getInt("id");
            String path = rs.getString("project_path");
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
    public static void createProject() {
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
    public static ProjectInfo saveProject(ProjectInfo projectInfo) {
        ProjectInfo project = BambooService.queryProject(projectInfo.getProjectPath(), projectInfo.getProjectName());
        if (project == null) {
            StringBuffer str = new StringBuffer();
            str.append("INSERT INTO bamboo_project (project_name, project_path) VALUES (");
            str.append(StringUtil.format("'{}',", projectInfo.getProjectName()));
            str.append(StringUtil.format("'{}'", projectInfo.getProjectPath()));
            str.append(");");
            Connection conn = SqliteConfig.getConnection();
            Statement state = conn.createStatement();
            state.executeUpdate(str.toString());
            conn.close();
            project = BambooService.queryProject(projectInfo.getProjectPath(), projectInfo.getProjectName());
        }
        return project;
    }

}
