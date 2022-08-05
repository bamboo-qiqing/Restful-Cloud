package com.bamboo.tool.contributor;


import com.bamboo.tool.config.BambooApisComponent;
import com.bamboo.tool.config.BambooToolComponent;
import com.bamboo.tool.config.model.ProjectInfo;
import com.bamboo.tool.configurable.BambooApiFilterConfiguration;
import com.bamboo.tool.configurable.ProjectFileterConfiguration;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.enums.RequestMethod;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RequestMappingByNameContributor implements ChooseByNameContributor {


    private List<BambooApiMethod> navigationItems = new ArrayList<>();

    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        // 输入的参数
        String userData = project.getUserData(ChooseByNamePopup.CURRENT_SEARCH_PATTERN);
         //已经选中的requestMethod
        BambooApiFilterConfiguration service = project.getService(BambooApiFilterConfiguration.class);
        List<RequestMethod> selectRequestMethods = ContainerUtil.filter(List.of(RequestMethod.values()), service::isFileTypeVisible);
        //已经选中的项目
        ProjectFileterConfiguration projectFileterConfiguration = project.getService(ProjectFileterConfiguration.class);
        List<ProjectInfo> projectInfos = BambooToolComponent.getInstance().getState().getProjectInfos();
        List<ProjectInfo> selectProjectInfos = ContainerUtil.filter(projectInfos, projectFileterConfiguration::isFileTypeVisible);
        //搜索apis集合
        navigationItems = BambooApisComponent.getStoreService().searchApi(userData, selectRequestMethods, selectProjectInfos, project);
        String[] strings = navigationItems.parallelStream().map(BambooApiMethod::getName).distinct().toArray(String[]::new);
        return strings;
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        BambooApiMethod[] requestMappingItems = navigationItems.parallelStream().filter(q -> q.getName().equals(name)).toArray(BambooApiMethod[]::new);
        return requestMappingItems;
    }

}
