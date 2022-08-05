package com.bamboo.tool.store;

import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.entity.AnnotationInfoSetting;
import com.bamboo.tool.entity.BambooClass;
import com.bamboo.tool.enums.RequestMethod;
import com.intellij.openapi.project.Project;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/7/25 14:39
 * Description
 */
public interface StoreService {
    /**
     * 获取所有注解信息
     *
     * @return
     */
    List<AnnotationInfoSetting> getAllAnnotation();

    /**
     * 搜索api
     *
     * @param searchChar
     * @param requestMethods
     * @param projectInfos
     * @return
     */
    List<BambooApiMethod> searchApi(String searchChar, List<RequestMethod> requestMethods, List<ProjectInfo> projectInfos, Project project);

    void saveData(List<BambooClass> bambooClasses, Project project);

    List<BambooClass> getOtherProjectBambooClass(Project project);

    /**
     * 获取所有的项目信息
     */
    List<ProjectInfo> getAllProjectInfos();
}
