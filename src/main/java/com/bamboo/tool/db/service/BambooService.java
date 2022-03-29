package com.bamboo.tool.db.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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

    public static String initDataUrl = "http://liangkezaoshu.space/usr/uploads/2022/03/1936383359.json";

    @SneakyThrows
    public static void initTable() {
        final List<SqliteMaster> tables = querTables();
        final Map<String, SqliteMaster> masterMap = tables.stream().collect(Collectors.toMap(SqliteMaster::getName, e -> e));
        final List<String> exeSql = new ArrayList<>();

        final String tableSetting = HttpUtil.get(initDataUrl);
        final JSONArray objects = JSONUtil.parseArray(tableSetting);
        final List<TableInit> inits = Arrays.stream(objects.toArray()).map(e -> (JSONObject) e).map(e -> JSONUtil.toBean(e, TableInit.class)).collect(Collectors.toList());
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
    }

    private static List<SqliteMaster> querTables() throws SQLException {
        StringBuffer str = new StringBuffer();
        str.append("select * from sqlite_master  where  type='table';");
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
            state.addBatch(StringUtil.format("delete from bamboo_method_return_type where method_id in (select id from bamboo_method where project_id='{}');", projectId));
            state.addBatch(StringUtil.format("delete from bamboo_method_param where method_id in (select id from bamboo_method where project_id='{}');", projectId));
            state.addBatch(StringUtil.format("delete from bamboo_desc where association_id in (select id from bamboo_method where project_id = '{}');", projectId));
            state.addBatch(StringUtil.format("delete from bamboo_desc where association_id in (select id from bamboo_class where project_id = '{}');", projectId));
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
            bambooClass.getDescs().stream().map(e -> e.toSql(bambooClass.getId())).forEach(e -> sqls.add(e));

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
                    sqls.add(method.getReturnType().toSql(method.getId()));
                    List<String> methodParamsSql = method.getMethodParams().parallelStream().map(e -> e.toSql(method.getId())).collect(Collectors.toList());
                    method.getDescs().stream().map(e -> e.toSql(method.getId())).forEach(e -> sqls.add(e));
                    sqls.addAll(methodParamsSql);
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
    public static Map<String, List<MethodParam>> getApi(String methodId) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        String sql = "select bmrt.return_type, bmp.param_type, bmp.param_type_path, bmp.param_name, bmp.param_index from bamboo_api ba inner join bamboo_method_return_type bmrt on bmrt.method_id = ba.method_id left join bamboo_method_param bmp on bmp.method_id = ba.method_id where ba.method_id='" + methodId + "' order by bmp.method_id, bmp.param_index ";
        final ResultSet resultSet = state.executeQuery(sql);
        Map<String, List<MethodParam>> methodParamMap = new HashMap<>();
        while (resultSet.next()) {
            String returnType = resultSet.getString("return_type");
            String paramType = resultSet.getString("param_type");
            String paramTypePath = resultSet.getString("param_type_path");
            String paramName = resultSet.getString("param_name");
            int paramIndex = resultSet.getInt("param_index");
            final MethodParam methodParam = new MethodParam();
            methodParam.setParamIndex(paramIndex);
            methodParam.setParamName(paramName);
            methodParam.setParamType(paramType);
            methodParam.setParamTypePath(paramTypePath);
            List<MethodParam> methodParams = methodParamMap.get(returnType);
            if (CollectionUtil.isEmpty(methodParams)) {
                methodParams = new ArrayList<>();
                if (StringUtil.isNotBlank(paramType)) {
                    methodParams.add(methodParam);
                }
                methodParamMap.put(returnType, methodParams);
            } else {
                if (StringUtil.isNotBlank(paramType)) {
                    methodParams.add(methodParam);
                }
            }
        }
        return methodParamMap;
    }

    @SneakyThrows
    public static List<BambooApiMethod> getAllApi(String projectId, String notProjectId, Project project) {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuilder str = new StringBuilder();
        str.append("select ba.url,");
        str.append("ba.request_methods requestMethods,");
        str.append("ba.method_id  methodId,");
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
        str.append("f.name             frameworkName,");
        str.append("iif(ifnull(bd.frameword_code,'')=='',null,json_group_object(bd.frameword_code, bd.describe)) as classDescribe,");
        str.append("iif(ifnull(bdm.frameword_code,'')=='',null,json_group_object(bdm.frameword_code, bdm.describe)) as methodDescribe");
        str.append(" from bamboo_api ba");
        str.append(" inner join bamboo_method bm on ba.method_id = bm.id");
        str.append(" inner join bamboo_class bc on bm.class_id = bc.id");
        str.append(" inner join bamboo_project bap on bc.project_id = bap.id ");
        str.append(" inner join annotation_info_setting ais on bc.setting_id = ais.id");
        str.append(" inner join framework f on f.id = ais.framework_id");
        str.append(" left join bamboo_desc bd on bc.id = bd.association_id");
        str.append(" left join bamboo_desc bdm on bm.id = bdm.association_id");

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
        str.append("group by ba.url, ba.request_methods, ba.method_id, bm.method_name, bm.description, bc.class_name, bc.class_path, bc.description, bc.model_name, bc.description, bap.project_name, bap.id, ais.soa_type, f.name");
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
            String methodId = resultSet.getString("methodId");
            String classDescribe = resultSet.getString("classDescribe");
            String methodDescribe = resultSet.getString("methodDescribe");
            api.setUrl(url);
            api.setRequestMethods(requestMethods);
            api.setMethodName(methodName);
            api.setModelName(modelName);
            api.setClassName(className);
            api.setClassPath(classPath);
            api.setProjectName(projectName);
            api.setSoaType(soaType);
            api.setFrameworkName(frameworkName);
            api.setProject(project);
            api.setMethodId(methodId);
            api.getMethodDescHashMap().put("javadoc", methodDesc);
            api.getClassDescHashMap().put("javadoc", classDesc);
            if (StringUtil.isNotBlank(classDescribe)) {
                HashMap otherDesc = JSONUtil.toBean(classDescribe, HashMap.class);
                api.getClassDescHashMap().putAll(otherDesc);
            }
            if (StringUtil.isNotBlank(methodDescribe)) {
                HashMap otherDesc = JSONUtil.toBean(methodDescribe, HashMap.class);
                api.getMethodDescHashMap().putAll(otherDesc);
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

    public static BambooDict selectOneDictByCode(String code) {
        final List<BambooDict> dicts = BambooService.selectAllDictByCode(code);
        if (CollectionUtil.isNotEmpty(dicts)) {
            return dicts.get(0);
        } else {
            return null;
        }
    }

    public static boolean selectIsShowDesc() {
        final BambooDict isShowDesc = BambooService.selectOneDictByCode("isShowDesc");
        if (isShowDesc == null) {
            return true;
        } else {
            return isShowDesc.getValue().equals("true");
        }
    }
}
