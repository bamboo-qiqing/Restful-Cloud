package com.bamboo.tool.store.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.SqliteConfig;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.entity.*;
import com.bamboo.tool.enums.RequestMethod;
import com.bamboo.tool.enums.SoaType;
import com.bamboo.tool.store.StoreService;
import com.bamboo.tool.util.StringUtil;
import com.intellij.openapi.project.Project;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create by GuoQing
 * Date 2022/8/2 16:06
 * Description
 */
public class SqliteStoreServiceImpl implements StoreService {
    public static String initDataUrl = "http://liangkezaoshu.space/usr/uploads/2022/03/1936383359.json";

    @SneakyThrows
    @Override
    public List<AnnotationInfoSetting> getAllAnnotation() {
        Map<Integer, List<AnnotationParam>> paramMap = SqliteStoreServiceImpl.selectAllAnnotationParam().stream().collect(Collectors.groupingBy(AnnotationParam::getAnnotationInfoId, Collectors.toList()));
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        String sql = "select ai.id,ai.class_path,ai.class_short_name,ai.scope,ai.soa_type,ai.effect,ai.other_params,f.name frameworkName,f.describe frameworkDescribe from annotation_info_setting  ai inner join framework  f on ai.framework_id = f.id and ai.is_delete=0;";
        ResultSet resultSet = state.executeQuery(sql);
        List<AnnotationInfoSetting> annotationInfoSettings = new ArrayList<>();
        while (resultSet.next()) {
            AnnotationInfoSetting annotationInfoSetting = getAnnotationInfo(resultSet, paramMap);
            annotationInfoSettings.add(annotationInfoSetting);
        }
        resultSet.close();
        state.close();
        conn.close();
        return annotationInfoSettings;
    }


    @SneakyThrows
    @Override
    public List<BambooApiMethod> searchApi(String searchChar, List<RequestMethod> requestMethods, List<ProjectInfo> projectInfos, Project project) {
        List<BambooApiMethod> apis = new ArrayList<>();
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append("bm.method_name,");
        sql.append("bm.method_url,");
        sql.append("bm.request_methods,");
        sql.append("bm.method_params,");
        sql.append("bc.class_name,");
        sql.append("bm.return_type,");
        sql.append("bc.class_path,");
        sql.append("bc.model_name,");
        sql.append("bp.project_name,");
        sql.append("bp.project_path,");
        sql.append("ais.soa_type");
        sql.append(" from bamboo_method bm");
        sql.append(" inner join bamboo_class bc on bm.class_id = bc.id");
        sql.append(" inner join bamboo_project bp on bc.project_id = bp.id");
        sql.append(" inner join annotation_info_setting ais on bc.setting_id = ais.id");
        sql.append(" where");
        sql.append(" bm.method_url like");
        sql.append(" '%" + searchChar + "%'");
        ResultSet resultSet = state.executeQuery(sql.toString());
        while (resultSet.next()) {
            String methodName = resultSet.getString("method_name");
            String requestMethodsStr = resultSet.getString("request_methods");
            String className = resultSet.getString("class_name");
            String returnType = resultSet.getString("return_type");
            String classPath = resultSet.getString("class_path");
            String modelName = resultSet.getString("model_name");
            String projectName = resultSet.getString("project_name");
            String projectPath = resultSet.getString("project_path");
            String soaTypeStr = resultSet.getString("soa_type");
            String methodUrl = resultSet.getString("method_url");
            String methodParamsStr = resultSet.getString("method_params");
            List<String> methodUrls = JSONObject.parseArray(methodUrl, String.class);
            List<BambooApiMethod> methods = methodUrls.parallelStream().map(e -> {
                BambooApiMethod method = new BambooApiMethod();
                method.setClassName(className);
                method.setUrl(e);
                method.setMethodName(methodName);
                method.setMethodReturn(returnType);
                method.setClassPath(classPath);
                method.setModelName(modelName);
                method.setProjectName(projectName);
                method.setProjectPath(projectPath);
                SoaType soaType = SoaType.getSoaType(soaTypeStr);
                method.setSoaType(soaType);
                List<MethodParam> methodParams = JSONObject.parseArray(methodParamsStr, MethodParam.class);
                method.setProject(project);
                method.setMethodParams(methodParams);
                List<String> requestMethod = JSONObject.parseArray(requestMethodsStr, String.class);
                method.setRequestMethods(requestMethod);
                return method;
            }).collect(Collectors.toList());
            apis.addAll(methods);
        }
        return apis;
    }

    @SneakyThrows
    @Override
    public void saveData(List<BambooClass> bambooClasses, Project project) {
        ProjectInfo projectInfo = queryProject(project.getBasePath(), project.getName());
        deleteCurrentProjectInfo(projectInfo.getId().toString());
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        List<String> sqls = new ArrayList<>();
        bambooClasses.parallelStream().forEach(bambooClass -> {
            String classId = UUID.randomUUID().toString();
            String classInsertSql = bambooClass.toInsertSql(projectInfo.getId(), classId);
            sqls.add(classInsertSql);
            bambooClass.getMethods().parallelStream().forEach(method -> {
                String methodInsertSql = method.toInsertSql(projectInfo.getId().toString(), classId);
                sqls.add(methodInsertSql);
            });
        });
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

    @Override
    public List<BambooClass> getOtherProjectBambooClass(Project project) {
        return null;
    }

    @SneakyThrows
    @Override
    public List<ProjectInfo> getAllProjectInfos() {
        List<ProjectInfo> projectInfos = new ArrayList<>();
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        String sql = "select * from bamboo_project;";
        ResultSet resultSet = state.executeQuery(sql);
        while (resultSet.next()) {
            String projectName = resultSet.getString("project_name");
            String projectPath = resultSet.getString("project_path");
            int id = resultSet.getInt("id");
            ProjectInfo info = new ProjectInfo();
            info.setId(id);
            info.setProjectName(projectName);
            info.setProjectPath(projectPath);
            projectInfos.add(info);
        }
        return projectInfos;
    }

    @NotNull
    private static AnnotationInfoSetting getAnnotationInfo(ResultSet resultSet, Map<Integer, List<AnnotationParam>> paramMap) throws SQLException {
        AnnotationInfoSetting annotationInfoSetting = new AnnotationInfoSetting();
        Integer id = resultSet.getInt("id");
        String classPath = resultSet.getString("class_path");
        String classShortName = resultSet.getString("class_short_name");
        String scope = resultSet.getString("scope");
        String soaType = resultSet.getString("soa_type");
        String effect = resultSet.getString("effect");
        String frameworkName = resultSet.getString("frameworkName");
        String frameworkDescribe = resultSet.getString("frameworkDescribe");
        String otherParams = resultSet.getString("other_params");
        List<AnnotationParam> params = paramMap.get(id);

        annotationInfoSetting.setId(id);
        annotationInfoSetting.setAnnotationPath(classPath);
        annotationInfoSetting.setAnnotationName(classShortName);
        if (scope != null) {
            annotationInfoSetting.setScope(scope);
        }
        annotationInfoSetting.setSoaType(soaType);
        annotationInfoSetting.setEffect(effect);
        if (params != null) {
            annotationInfoSetting.setParams(params);
        }
        if (StringUtil.isNotEmpty(otherParams)) {
            Map<String, List<String>> otherParamMap = JSONObject.parseObject(otherParams, HashMap.class);
            annotationInfoSetting.setOtherParams(otherParamMap);
        }
        if (StringUtil.isNotEmpty(frameworkName)) {
            Framework framework = new Framework();
            framework.setName(frameworkName);
            framework.setDescribe(frameworkDescribe);
        }
        return annotationInfoSetting;
    }

    @SneakyThrows
    public static List<AnnotationParam> selectAllAnnotationParam() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        String sql = "select ap.* from annotation_param_setting ap;";
        ResultSet resultSet = state.executeQuery(sql);
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
    public static ProjectInfo queryProject(String projectPath, String projectName) {
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
    public static void deleteCurrentProjectInfo(String projectId) {
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
    public static void initTable(Project project) {

        String db = FileUtil.getUserHomePath() + "\\restfulCloud\\restfulCloud.db";
        final boolean exist = FileUtil.exist(db);
        if (!exist) {
            File touch = FileUtil.touch(db);
        }
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac")) {
            RuntimeUtil.exec("sudo chmod -R 777 " + db);
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
}
