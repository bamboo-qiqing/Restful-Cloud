package com.bamboo.tool.db.service;


import cn.hutool.core.collection.CollectionUtil;
import com.bamboo.tool.components.api.entity.*;
import com.bamboo.tool.components.api.enums.AnnotationScope;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.SqliteConfig;
import com.bamboo.tool.util.StringUtil;
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
            initAnnotationInfoTable.append("setting_id  text,");
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
    public static void saveClass(List<BambooClass> allClasses, ProjectInfo projectInfo) {
        if (CollectionUtil.isEmpty(allClasses)) {
            return;
        }
        BambooService.deleteClasssByProjectId(projectInfo.getProjectId());
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
            if (CollectionUtil.isNotEmpty(annotations)) {
                annotations.forEach(annotation -> {
                    annotation.setId(UUID.randomUUID().toString());
                    StringBuffer annotationSql = new StringBuffer();
                    annotationSql.append("INSERT INTO bamboo_annotation_info(id, setting_id, value, associated_id,project_id)");
                    annotationSql.append("VALUES (");
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
                    sqlMethod.append("insert into bamboo_method (id, project_id, method_name, class_id,description) VALUES(");
                    sqlMethod.append("'" + method.getId() + "',");
                    sqlMethod.append("'" + projectInfo.getProjectId() + "',");
                    sqlMethod.append("'" + method.getMethodName() + "',");
                    sqlMethod.append("'" + bambooClass.getId() + "',");
                    sqlMethod.append("'" + method.getDescription() + "'");
                    sqlMethod.append(");");
                    sqls.add(sqlMethod.toString());
                    final List<BambooAnnotationInfo> annotationInfos = method.getAnnotationInfos();
                    if (CollectionUtil.isNotEmpty(annotationInfos)) {
                        annotationInfos.forEach(annotation -> {
                            annotation.setId(UUID.randomUUID().toString());
                            StringBuffer annotationSql = new StringBuffer();
                            annotationSql.append("INSERT INTO bamboo_annotation_info(id, setting_id, value, associated_id,project_id)");
                            annotationSql.append("VALUES (");
                            annotationSql.append("'" + annotation.getId() + "',");
                            annotationSql.append("'" + annotation.getAnnotationInfoSetting().getId() + "',");
                            annotationSql.append("'" + annotation.getValue() + "',");
                            annotationSql.append("'" + method.getId() + "',");
                            annotationSql.append("'" + projectInfo.getId() + "'");
                            annotationSql.append(");");
                            sqls.add(annotationSql.toString());
                        });
                    }
                });
            }
        });
        return sqls;
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
    public static List<BambooClass> selectAllClass() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuffer str = new StringBuffer();
        str.append("select ");
        str.append("*");
        str.append("from ");
        str.append("bamboo_class;");
        ResultSet resultSet = state.executeQuery(str.toString());
        List<BambooClass> bambooClasses = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String className = resultSet.getString("class_name");
            String modelName = resultSet.getString("model_name");
            String description = resultSet.getString("description");
            String classPath = resultSet.getString("class_path");
            String projectId = resultSet.getString("project_id");
            BambooClass bambooClass = new BambooClass();
            bambooClass.setId(id);
            bambooClass.setClassName(className);
            bambooClass.setModuleName(modelName);
            bambooClass.setDescription(description);
            bambooClass.setClassPath(classPath);
            bambooClass.setProjectId(projectId);
            bambooClasses.add(bambooClass);
        }
        resultSet.close();
        state.close();
        conn.close();
        return bambooClasses;
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
    public static List<BambooMethod> selectAllMethod() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuffer str = new StringBuffer();
        str.append("select ");
        str.append("*");
        str.append("from ");
        str.append("bamboo_method;");
        ResultSet resultSet = state.executeQuery(str.toString());
        List<BambooMethod> methods = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String projectId = resultSet.getString("project_id");
            String methodName = resultSet.getString("method_name");
            String classId = resultSet.getString("class_id");
            String description = resultSet.getString("description");
            BambooMethod method = new BambooMethod();
            method.setId(id);
            method.setProjectId(projectId);
            method.setMethodName(methodName);
            method.setClassId(classId);
            method.setDescription(description);
            methods.add(method);
        }
        resultSet.close();
        state.close();
        conn.close();
        return methods;
    }

    @SneakyThrows
    public static List<BambooAnnotationInfo> selectAllAnnotationInfo() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuffer str = new StringBuffer();
        str.append("select ");
        str.append("*");
        str.append("from ");
        str.append("bamboo_annotation_info;");
        ResultSet resultSet = state.executeQuery(str.toString());
        List<BambooAnnotationInfo> annotationInfos = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String settingId = resultSet.getString("setting_id");
            String value = resultSet.getString("value");
            String associatedId = resultSet.getString("associated_id");
            String projectId = resultSet.getString("project_id");
            BambooAnnotationInfo annotationInfo = new BambooAnnotationInfo();
            annotationInfo.setId(id);
            annotationInfo.setSettingId(settingId);
            annotationInfo.setValue(value);
            annotationInfo.setAssociatedId(associatedId);
            annotationInfo.setProjectId(projectId);
            annotationInfos.add(annotationInfo);
        }
        resultSet.close();
        state.close();
        conn.close();
        return annotationInfos;
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
        Map<Integer, Framework> framework = BambooService.selectAllFramework().stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
        Map<Integer, List<AnnotationMethodScope>> methodScopeMap = BambooService.selectAllAnnotationMethodScope().stream().collect(Collectors.groupingBy(e -> e.getAnnotationId(), Collectors.toList()));
        Map<Integer, List<AnnotationParam>> paramMap = BambooService.selectAllAnnotationParam().stream().collect(Collectors.groupingBy(e -> e.getAnnotationInfoId(), Collectors.toList()));

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
    private static AnnotationInfoSetting getAnnotationInfo(ResultSet resultSet, Map<Integer, Framework> frameworkMap, Map<Integer, List<AnnotationMethodScope>> methodScopeMap, Map<Integer, List<AnnotationParam>> paramMap) throws SQLException {
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

    public static void main(String[] args) throws SQLException {
        List<BambooAnnotationInfo> bambooAnnotationInfos = BambooService.selectAllAnnotationInfo();
        List<BambooClass> classList = BambooService.selectAllClass();
        List<BambooMethod> bambooMethods = BambooService.selectAllMethod();
//        String sql = "select bc.project_id as projectId, bc.model_name as modelName, bc.class_name as className, bc.class_path as className, bc.description as classDescription, bamim.value classAnnotationValue, aism.soa_type as classAnnotationSoaType, aism.class_path as classAnnotationPath, aism.class_short_name as classAnnotationShortName, f.name as classAnnotationFrameworkName, bm.method_name as methodName, bm.description as methodDescription, bamic.value from bamboo_method bm left join bamboo_annotation_info bamim on bamim.associated_id = bm.id inner join annotation_info_setting aism on aism.id = bamim.setting_id inner join framework f on aism.framework_id = f.id inner join bamboo_class bc on bc.id = bm.class_id left join bamboo_annotation_info bamic on bamic.associated_id = bc.id; ";
//        Connection conn = SqliteConfig.getConnection();
//        Statement state = conn.createStatement();
//        ResultSet resultSet = state.executeQuery(sql);
//        List<BambooModel> list = new ArrayList<>();
//        while (resultSet.next()) {
//            String projectId = resultSet.getString("projectId");
//            String modelName = resultSet.getString("modelName");
//            String className = resultSet.getString("className");
//            String classDescription = resultSet.getString("classDescription");
//            String classAnnotationValue = resultSet.getString("classAnnotationValue");
//            String classAnnotationSoaType = resultSet.getString("classAnnotationSoaType");
//            String classAnnotationPath = resultSet.getString("classAnnotationPath");
//            String classAnnotationShortName = resultSet.getString("classAnnotationShortName");
//            String classAnnotationFrameworkName = resultSet.getString("classAnnotationFrameworkName");
//            String methodName = resultSet.getString("methodName");
//            String methodDescription = resultSet.getString("methodDescription");
//            BambooModel apiModel = new BambooModel(projectId, modelName, className, classDescription, classAnnotationValue, classAnnotationSoaType, classAnnotationPath, classAnnotationShortName, classAnnotationFrameworkName, methodName, methodDescription);
//            list.add(apiModel);
//        }
//        System.out.printf(list.toString());
//        System.out.printf("1");
//        System.out.printf("1");

    }
}
