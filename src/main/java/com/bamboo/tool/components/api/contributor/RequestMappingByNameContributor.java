package com.bamboo.tool.components.api.contributor;


import com.bamboo.tool.db.service.BambooService;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMappingByNameContributor implements ChooseByNameContributor {


    private List<RequestMappingNavigationItem> navigationItems = new ArrayList<>();

    @Override
    public String[] getNames(Project project, boolean b) {
        System.out.printf("2222222222222");
//        navigationItems = BambooService.getAllApis().stream().parallel().map(e -> {
//            RequestMappingNavigationItem item = new RequestMappingNavigationItem();
//            item.setRequestMethod(e.getMethodType());
//            item.setClassName(e.getClassName());
//            item.setClassPath(e.getClassPath());
//            item.setUrlPath(e.getUrl());
//            item.setTypes(e.getTypes());
//            item.setModelName(e.getModelName());
//            item.setProjectName(e.getProjectName());
//            item.setMethodName(e.getMethodName());
//            item.setProject(project);
//            return item;
//        }).collect(Collectors.toList());
        String[] strings = navigationItems.stream().map(RequestMappingNavigationItem::getName).distinct().toArray(String[]::new);
        return strings;
    }

    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        System.out.printf("3333");
        RequestMappingNavigationItem[] requestMappingItems = navigationItems.stream().filter(q -> q.getName().equals(name)).toArray(RequestMappingNavigationItem[]::new);
        return requestMappingItems;
    }

}
