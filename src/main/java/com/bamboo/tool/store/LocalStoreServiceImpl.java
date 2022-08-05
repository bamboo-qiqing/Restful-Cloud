package com.bamboo.tool.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.entity.AnnotationInfoSetting;
import com.bamboo.tool.entity.BambooClass;
import com.bamboo.tool.enums.RequestMethod;
import com.bamboo.tool.enums.SoaType;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by GuoQing
 * Date 2022/7/25 14:42
 * Description
 */
public class LocalStoreServiceImpl implements StoreService {
    @Override
    public List<AnnotationInfoSetting> getAllAnnotation() {
        return BambooToolComponent.getInstance().getState().getAnnotations();
    }

    @Override
    public List<BambooApiMethod> searchApi(String searchChar, List<RequestMethod> requestMethods, List<ProjectInfo> projectInfos, Project project) {
        List<BambooApiMethod> apis = new ArrayList<>();
        projectInfos.parallelStream().forEach(projectInfo -> {
            String path = projectInfo.getProjectPath() + "\\.idea\\restfulCloudApis.json";
            boolean exist = FileUtil.exist(path);
            if (exist) {
                JSONArray objects = JSONUtil.readJSONArray(new File(projectInfo.getProjectPath() + "\\.idea\\restfulCloudApis.json"), CharsetUtil.CHARSET_UTF_8);
                List<BambooClass> bambooClasses = JSONUtil.toList(objects, BambooClass.class);
                bambooClasses.parallelStream().forEach(bambooClass -> {
                    bambooClass.getMethods().parallelStream().forEach(method -> {
                        List<BambooApiMethod> list = method.getMethodUrl().stream().filter(e -> e.contains(searchChar)).map(e -> {
                            BambooApiMethod bambooApiMethod = new BambooApiMethod();
                            bambooApiMethod.setUrl(e);
                            bambooApiMethod.setProject(project);
                            bambooApiMethod.setMethodName(method.getMethodName());
                            bambooApiMethod.setClassPath(bambooClass.getClassPath());
                            bambooApiMethod.setProjectName(projectInfo.getProjectName());
                            bambooApiMethod.setMethodReturn(method.getReturnType().toString());
                            bambooApiMethod.setModelName(bambooClass.getModuleName());
                            bambooApiMethod.setProjectPath(projectInfo.getProjectPath());
                            bambooApiMethod.setClassName(bambooClass.getClassName());
                            AnnotationInfoSetting setting = bambooClass.getSetting();
                            String soaTypeStr = setting.getSoaType();
                            SoaType soaType = SoaType.getSoaType(soaTypeStr);
                            bambooApiMethod.setSoaType(soaType);
                            return bambooApiMethod;
                        }).collect(Collectors.toList());
                        apis.addAll(list);
                    });
                });
            }
        });
        return apis;
    }

    @Override
    public void saveData(List<BambooClass> bambooClasses, Project project) {
        String content = JSONUtil.toJsonStr(bambooClasses);
        String path = project.getBasePath() + "\\.idea\\restfulCloudApis.json";
        boolean exist = FileUtil.exist(path);
        if (exist) {
            FileUtil.del(path);
        }
        FileUtil.writeUtf8String(content, path);
    }

    @Override
    public List<BambooClass> getOtherProjectBambooClass(Project project) {
        List<BambooClass> bambooClasses = new ArrayList<>();
        List<ProjectInfo> projectInfos = BambooToolComponent.getInstance().getState().getProjectInfos();
        List<ProjectInfo> list = projectInfos.stream().filter(e -> e.getProjectPath().equals(project.getBasePath())).collect(Collectors.toList());
        list.parallelStream().forEach(projectInfo -> {
            String path = projectInfo.getProjectPath() + "\\.idea\\restfulCloudApis.json";
            boolean exist = FileUtil.exist(path);
            if (exist) {
                JSONArray objects = JSONUtil.readJSONArray(new File(projectInfo.getProjectPath() + "\\.idea\\restfulCloudApis.json"), CharsetUtil.CHARSET_UTF_8);
                List<BambooClass> bambooClassList = JSONUtil.toList(objects, BambooClass.class);
                bambooClasses.addAll(bambooClassList);
            }
        });
        return bambooClasses;
    }

    @Override
    public List<ProjectInfo> getAllProjectInfos() {
        return BambooToolComponent.getInstance().getState().getProjectInfos();
    }
}
