package com.bamboo.tool.db.service;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.BambooToolConfig;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.SqlConstant;
import com.bamboo.tool.db.SqliteConfig;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.entity.*;
import com.bamboo.tool.enums.AnnotationScope;
import com.bamboo.tool.enums.RequestMethod;
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

    public static String initDataUrl = "http://liangkezaoshu.space/usr/uploads/2022/03/1936383359.json";

    @SneakyThrows
    public static void initTable(Project project) {
        String db = FileUtil.getUserHomePath() + "\\bambooTool\\bambooApi.db";
        final boolean exist = FileUtil.exist(db);
        if (!exist) {
            FileUtil.touch(db);
        }
        ProjectInfo projectInfo = null;

        ProjectInfo currentProject = BambooService.queryProject(project.getBasePath(), project.getName());
        if (currentProject != null) {
            projectInfo = currentProject;
        } else {
            projectInfo = new ProjectInfo();
            projectInfo.setProjectPath(project.getBasePath());
            projectInfo.setProjectName(project.getName());
            BambooService.saveProject(projectInfo);
        }
        List<SqliteMaster> tables = querTables();
        Map<String, SqliteMaster> masterMap = tables.stream().collect(Collectors.toMap(SqliteMaster::getName, e -> e));
        List<String> exeSql = new ArrayList<>();

        String tableSetting = HttpUtil.get(initDataUrl);
        JSONArray objects = JSONUtil.parseArray(tableSetting);
        List<TableInit> inits = Arrays.stream(objects.toArray()).map(e -> (cn.hutool.json.JSONObject) e).map(e -> JSONUtil.toBean(e, TableInit.class)).collect(Collectors.toList());
        inits.stream().filter(e -> masterMap.get(e.getTableName()) == null).forEach(e -> {
            exeSql.add(e.getCreatSql());
            if (CollectionUtil.isNotEmpty(e.getIndex())) {
                exeSql.addAll(e.getIndex());
            }
            if (CollectionUtil.isNotEmpty(e.getDatas())) {
                exeSql.addAll(e.getDatas());
            }
        });
        if (CollectionUtil.isNotEmpty(exeSql)) {
            Connection conn = SqliteConfig.getConnection();
            Statement state = conn.createStatement();
            for (String e : exeSql) {
                try {
                    state.addBatch(e);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            state.executeBatch();
            state.close();
            conn.close();
        }
    }

    private static List<SqliteMaster> querTables() throws SQLException {

        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        ResultSet resultSet = state.executeQuery("select * from sqlite_master  where  type='table';");
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
    public static ProjectInfo saveProject(ProjectInfo projectInfo) {
        ProjectInfo project = BambooService.queryProject(projectInfo.getProjectPath(), projectInfo.getProjectName());
        if (project == null) {
            StringBuffer str = new StringBuffer();
            str.append("INSERT INTO bamboo_project (project_name, project_path) VALUES (");
            str.append(StringUtil.format("'{}',", projectInfo.getProjectName()));
            str.append(StringUtil.format("'{}')", projectInfo.getProjectPath()));
            Connection conn = SqliteConfig.getConnection();
            Statement state = conn.createStatement();
            state.executeUpdate(str.toString());
            conn.close();
            project = BambooService.queryProject(projectInfo.getProjectPath(), projectInfo.getProjectName());
        }
        return project;
    }

    @SneakyThrows
    public static void saveClass(List<BambooClass> allClasses, ProjectInfo projectInfo) {
        if (CollectionUtil.isEmpty(allClasses)) {
            return;
        }
        BambooService.deleteClasssByProjectId(projectInfo.getId().toString());
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        List<String> sqls = BambooService.addBatchSql(projectInfo, allClasses);
        sqls.forEach(e -> {
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
            state.addBatch(StringUtil.format("delete from bamboo_api WHERE project_id = '{}';", projectId));
            state.executeBatch();
        }
        state.close();
        conn.close();
    }

    @SneakyThrows
    private static List<String> addBatchSql(ProjectInfo projectInfo, List<BambooClass> allClasses) {
        String projectId = projectInfo.getId().toString();
        Map<String, BambooClass> classMap = BambooService.queryClassByProjectId(projectId);
        Map<String, BambooMethod> methodMap = BambooService.queryMethodByProjectId(projectId);
        List<String> sqls = new ArrayList<>();
        allClasses.forEach(bambooClass -> {
            String Id = Base64.encode(bambooClass.getClassPath(), CharsetUtil.CHARSET_UTF_8);
            BambooClass oldClass = classMap.get(Id);
            bambooClass.setId(Id);
            if (!Objects.isNull(oldClass)) {

                if (bambooClass.getSettingId() != null && !oldClass.getSettingId().equals(bambooClass.getSettingId())) {
                    oldClass.setSettingId(bambooClass.getSettingId());
                    oldClass.setIsExist(true);
                }
                String description = oldClass.getDescription();
                String descs = JSONObject.toJSONString(bambooClass.getDescs());
                if (!description.equals(descs)) {
                    oldClass.setIsExist(true);
                    oldClass.setDescription(descs);
                }
                if (oldClass.getIsExist()) {
                    sqls.add(oldClass.toUpdateSql());
                }
            } else if (Objects.isNull(oldClass)) {
                sqls.add(bambooClass.toInsertSql(projectInfo.getId()));
            }

            List<BambooMethod> methods = bambooClass.getMethods();
            if (CollectionUtil.isNotEmpty(methods)) {

                methods.forEach(method -> {
                    String returnType = JSONObject.toJSONString(method.getReturnType());
                    List<MethodParam> methodParams = method.getMethodParams().stream().sorted(Comparator.comparing(MethodParam::getParamIndex)).collect(Collectors.toList());
                    List<String> paramTypePaths = methodParams.stream().map(e -> e.getParamTypePath()).collect(Collectors.toList());
                    String methodParamsStr = JSONObject.toJSONString(methodParams);
                    String descs = JSONObject.toJSONString(method.getDescs());
                    String methodId = Base64.encode(method.getReturnType().getReturnTypePath() + paramTypePaths + method.getMethodName() + bambooClass.getClassUrl(), CharsetUtil.CHARSET_UTF_8);

                    BambooMethod bambooMethod = methodMap.get(methodId);
                    method.setId(methodId);
                    method.setClassId(bambooClass.getId());
                    method.setProjectId(projectId);
                    method.setReturnTypeStr(returnType);
                    method.setMethodParamsStr(methodParamsStr);
                    if (Objects.isNull(bambooMethod)) {
                        method.setDescription(descs);
                        String methodInsertSql = method.toInsertSql(projectId);
                        sqls.add(methodInsertSql);
                    } else {
                        if (!descs.equals(bambooMethod.getDescription())) {
                            bambooMethod.setDescription(descs);
                            sqls.add(bambooMethod.toUpdateSql());
                            bambooMethod.setIsExist(true);
                        }

                    }

                    List<String> classUrls = bambooClass.getClassUrl();
                    if (CollectionUtil.isNotEmpty(classUrls)) {
                        classUrls.parallelStream().forEach(e -> buildApiSql(sqls, bambooClass, method, e, projectInfo));
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
                StringBuilder api = new StringBuilder();
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
                List<String> requestMethods = method.getRequestMethods();
                if (CollectionUtil.isEmpty(requestMethods)) {
                    requestMethods.add(RequestMethod.ALL.getCode());
                }

                api.append("insert into bamboo_api (id, method_id, url, request_methods,project_id,consumes,params,headers,produces) VALUES(");
                api.append("'").append(UUID.randomUUID()).append("',");
                api.append("'").append(method.getId()).append("',");
                api.append("'").append(poolUrl).append(classUrl).append(StringUtil.addPrefixIfNot(methodUrl, "/")).append("',");
                api.append("'").append(requestMethods).append("',");
                api.append("'").append(projectInfo.getId()).append("',");
                api.append("'").append(method.getConsumes()).append("',");
                api.append("'").append(method.getParams()).append("',");
                api.append("'").append(method.getHeaders()).append("',");
                api.append("'").append(method.getProduces()).append("'");
                api.append(");");
                sqls.add(api.toString());
            });
        }
    }

    @SneakyThrows
    public static List<AnnotationParam> selectAllAnnotationParam() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        String str = "select ap.* from annotation_param_setting ap;";
        ResultSet resultSet = state.executeQuery(str);
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
        String str = "select f.* from framework f;";
        ResultSet resultSet = state.executeQuery(str);
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
        String str = "select ams.* from annotation_method_scope ams;";
        ResultSet resultSet = state.executeQuery(str);
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
        return "select ai.id,ai.class_path,ai.class_short_name,ai.scope,ai.soa_type,ai.effect,ai.framework_id from annotation_info_setting  ai inner join framework  f on ai.framework_id = f.id and ai.is_delete=0;";
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
    public static Map<String, List<MethodParam>> getApi(String methodId) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        String sql = "select bm.return_type,bm.method_params from bamboo_api ba  inner join bamboo_method bm on bm.id = ba.method_id where ba.method_id='" + methodId + "'";
        ResultSet resultSet = state.executeQuery(sql);
        Map<String, List<MethodParam>> methodParamMap = new HashMap<>();
        while (resultSet.next()) {
            String returnType = resultSet.getString("return_type");
            String methodParam = resultSet.getString("method_params");
            List<MethodParam> methodParams = JSONObject.parseArray(methodParam, MethodParam.class);
            MethodReturnType methodReturnType = JSONObject.parseObject(returnType, MethodReturnType.class);
            methodParamMap.put(methodReturnType.getReturnType(), methodParams);
        }
        return methodParamMap;
    }

    @SneakyThrows
    public static List<BambooApiMethod> getAllApi(String projectId, String notProjectId, Project project, String queryCondition, Boolean isHistory) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuilder str = new StringBuilder();
        str.append("select ba.url,");
        str.append("ba.request_methods requestMethods,");
        str.append("ba.method_id  methodId,");
        str.append("bm.method_name     methodName,");
        str.append("bm.description     methodDesc,");
        str.append("bm.remark          methodRename,");
        str.append("bm.return_type     methodReturnType,");
        str.append("bm.query_count     queryCount,");
        str.append("bc.class_name      className,");
        str.append("bc.id     classId,");
        str.append("bc.class_path      classPath,");
        str.append("bc.description     classDesc,");
        str.append("bc.model_name      modelName,");
        str.append("bc.description     classDesc,");
        str.append("bc.remark          classRename,");
        str.append("bap.project_name   projectName,");
        str.append("bap.project_path   projectPath,");
        str.append("bap.id     projectId,");
        str.append("ais.soa_type       soaType,");
        str.append("f.name             frameworkName");
        str.append(" from bamboo_api ba");
        str.append(" inner join bamboo_method bm on ba.method_id = bm.id");
        str.append(" inner join bamboo_class bc on bm.class_id = bc.id");
        str.append(" inner join bamboo_project bap on bc.project_id = bap.id ");
        str.append(" inner join annotation_info_setting ais on bc.setting_id = ais.id");
        str.append(" inner join framework f on f.id = ais.framework_id");
        str.append(" where 1=1");

        if (StringUtil.isNotEmpty(projectId)) {
            str.append("  and ba.project_id =");
            str.append("'");
            str.append(projectId);
            str.append("'");
        }
        if (isHistory) {
            str.append("  and bm.query_count > 0");

        }
        if (StringUtil.isNotEmpty(notProjectId)) {
            str.append(" and ba.project_id !=");
            str.append("'");
            str.append(notProjectId);
            str.append("'");
        }
        if (StringUtil.isNotBlank(queryCondition)) {
            str.append(" and ba.url like '%" + queryCondition + "%'");
        }
        str.append(" group by ba.url, ba.request_methods, ba.method_id, bm.method_name, bm.description, bc.class_name, bc.class_path, bc.description, bc.model_name, bc.description, bap.project_name, bap.id, ais.soa_type, f.name");
        str.append(";");
        List<BambooApiMethod> apis = new ArrayList<>();
        ResultSet resultSet = state.executeQuery(str.toString());
        while (resultSet.next()) {
            BambooApiMethod api = new BambooApiMethod();
            String url = resultSet.getString("url");
            String requestMethods = resultSet.getString("requestMethods");
            String methodName = resultSet.getString("methodName");
            String methodReturn = resultSet.getString("methodReturnType");
            String methodDesc = resultSet.getString("methodDesc");
            String methodRename = resultSet.getString("methodRename");
            String className = resultSet.getString("className");
            String classPath = resultSet.getString("classPath");
            String classRename = resultSet.getString("classRename");
            String modelName = resultSet.getString("modelName");
            String projectName = resultSet.getString("projectName");
            String projectPath = resultSet.getString("projectPath");
            String soaType = resultSet.getString("soaType");
            String frameworkName = resultSet.getString("frameworkName");
            String methodId = resultSet.getString("methodId");
            String classDescribe = resultSet.getString("classDesc");
            String classId = resultSet.getString("classId");
            Integer queryCount = resultSet.getInt("queryCount");
            api.setUrl(url);
            api.setRequestMethods(requestMethods);
            api.setMethodName(methodName);
            api.setMethodReturn(methodReturn);
            api.setModelName(modelName);
            api.setClassName(className);
            api.setClassPath(classPath);
            api.setProjectName(projectName);
            api.setSoaType(soaType);
            api.setFrameworkName(frameworkName);
            api.setProject(project);
            api.setMethodId(methodId);
            api.setProjectPath(projectPath);
            api.setQueryCount(queryCount);
            api.setClassId(classId);
            if (StringUtil.isNotBlank(classDescribe)) {
                List<BambooDesc> classDescribes = JSONObject.parseArray(classDescribe, BambooDesc.class);
                Map<String, String> methodDescHashMap = classDescribes.stream().collect(Collectors.toMap(BambooDesc::getFramewordCode, BambooDesc::getDescribe, (key1, key2) -> key2));
                api.getClassDescHashMap().putAll(methodDescHashMap);
                if(StringUtil.isNotBlank(classRename)){
                    api.getClassDescHashMap().put("custom",classRename);
                }
            }
            if (StringUtil.isNotBlank(methodDesc)) {
                List<BambooDesc> bambooDescs = JSONObject.parseArray(methodDesc, BambooDesc.class);
                Map<String, String> methodDescHashMap = bambooDescs.stream().collect(Collectors.toMap(BambooDesc::getFramewordCode, BambooDesc::getDescribe, (key1, key2) -> key2));
                api.getMethodDescHashMap().putAll(methodDescHashMap);
                if(StringUtil.isNotBlank(methodRename)){
                    api.getMethodDescHashMap().put("custom",methodRename);
                }
            }
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
    public static List<DescFramework> selectAllDescFramework() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        String sql = "select * from bamboo_desc_framework order by sequence; ";

        ResultSet resultSet = state.executeQuery(sql);
        List<DescFramework> descFrameworks = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String framework = resultSet.getString("framework");
            String frameworkCode = resultSet.getString("framework_code");
            String describe = resultSet.getString("describe");
            int sequence = resultSet.getInt("sequence");
            DescFramework descFramework = new DescFramework();
            descFramework.setId(id);
            descFramework.setDescribe(describe);
            descFramework.setFramework(framework);
            descFramework.setFrameworkCode(frameworkCode);
            descFramework.setSequence(sequence);
            descFrameworks.add(descFramework);
        }
        resultSet.close();
        state.close();
        conn.close();
        return descFrameworks;
    }

    @SneakyThrows
    public static List<BambooDict> selectAllDictByCode(String code) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        String sql = "select *  from bamboo_dict where key='" + code + "'; ";

        ResultSet resultSet = state.executeQuery(sql);
        List<BambooDict> dicts = new ArrayList<>();
        while (resultSet.next()) {

            String id = resultSet.getString("id");
            String key = resultSet.getString("key");
            String value = resultSet.getString("value");
            String description = resultSet.getString("description");
            BambooDict bambooDict = new BambooDict();
            bambooDict.setId(id);
            bambooDict.setKey(key);
            bambooDict.setDescription(description);
            bambooDict.setValue(value);
            dicts.add(bambooDict);
        }
        resultSet.close();
        state.close();
        conn.close();
        return dicts;
    }

    @SneakyThrows
    public static void updateDescFrameworkSequence(List<DescFramework> descFrameworks) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        for (int i = 0; i < descFrameworks.size(); i++) {
            state.addBatch(" UPDATE bamboo_desc_framework SET sequence = " + i + " WHERE id = '" + descFrameworks.get(i).getId() + "';");
        }
        state.executeBatch();
        state.close();
        conn.close();
    }


    public static BambooDict selectOneDictByCode(String code) {
        List<BambooDict> dicts = BambooService.selectAllDictByCode(code);
        if (CollectionUtil.isNotEmpty(dicts)) {
            return dicts.get(0);
        } else {
            return null;
        }
    }

    public static boolean selectIsShowDesc() {
        BambooDict isShowDesc = BambooService.selectOneDictByCode("isShowDesc");
        if (isShowDesc == null) {
            return true;
        } else {
            return isShowDesc.getValue().equals("true");
        }
    }

    @SneakyThrows
    public static void updateIsShowDesc(Boolean isShowDesc) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        state.executeUpdate("UPDATE bamboo_dict SET  value = '" + isShowDesc + "' WHERE key = 'isShowDesc';");
        state.close();
        conn.close();
    }

    @SneakyThrows
    public static Map<String, BambooClass> queryClassByProjectId(String projectId) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        ResultSet resultSet = state.executeQuery("select * from bamboo_class where project_id='" + projectId + "'");
        Map<String, BambooClass> map = new HashMap<>();
        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String className = resultSet.getString("class_name");
            String modelName = resultSet.getString("model_name");
            String description = resultSet.getString("description");
            String classPath = resultSet.getString("class_path");
            BambooClass bambooClass = new BambooClass();
            bambooClass.setId(id);
            bambooClass.setClassPath(classPath);
            bambooClass.setClassName(className);
            bambooClass.setModuleName(modelName);
            bambooClass.setDescription(description);
            bambooClass.setProjectId(projectId);
            map.put(id, bambooClass);
        }
        return map;
    }

    @SneakyThrows
    public static Map<String, BambooMethod> queryMethodByProjectId(String projectId) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        ResultSet resultSet = state.executeQuery("select * from bamboo_method where project_id='" + projectId + "'");
        Map<String, BambooMethod> map = new HashMap<>();
        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String methodName = resultSet.getString("method_name");
            String classId = resultSet.getString("class_id");
            String description = resultSet.getString("description");
            String methodParams = resultSet.getString("method_params");
            String returnType = resultSet.getString("return_type");

            BambooMethod bambooMethod = new BambooMethod();
            bambooMethod.setId(id);
            bambooMethod.setMethodName(methodName);
            bambooMethod.setClassId(classId);
            bambooMethod.setDescription(description);
            MethodReturnType methodReturnType = JSONObject.parseObject(returnType, MethodReturnType.class);
            bambooMethod.setReturnType(methodReturnType);
            if (StringUtil.isNotBlank(methodParams)) {
                List<MethodParam> methodParamsList = JSONObject.parseArray(methodParams, MethodParam.class);
                bambooMethod.setMethodParams(methodParamsList);
            }

            map.put(id, bambooMethod);
        }
        return map;
    }


    @SneakyThrows
    public static void saveMethodCount(String methodId, Integer queryCount) {

        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        state.executeUpdate("UPDATE bamboo_method SET query_count = '" + (queryCount + 1) + "' WHERE id = '" + methodId + "';");
        state.close();
        conn.close();
    }

    @SneakyThrows
    public static void saveMethodRemark(String remark, String methodId) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        state.executeUpdate("UPDATE bamboo_method SET remark = '" + remark + "' WHERE id = '" + methodId + "';");
        state.close();
        conn.close();
    }

    @SneakyThrows
    public static void saveClassRemark(String remark, String classId) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        state.executeUpdate("UPDATE bamboo_class SET remark = '" + remark + "' WHERE id = '" + classId + "';");
        state.close();
        conn.close();
    }


}
