package com.bamboo.tool.components.api.contributor;


import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RequestMappingByNameContributor implements ChooseByNameContributor {


    private List<BambooApiMethod> navigationItems = new ArrayList<>();

    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        navigationItems = BambooService.getAllApi(null, null,project);
        String[] strings = navigationItems.stream().map(BambooApiMethod::getName).distinct().toArray(String[]::new);
        return strings;
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        BambooApiMethod[] requestMappingItems = navigationItems.stream().filter(q -> q.getName().equals(name)).toArray(BambooApiMethod[]::new);
        return requestMappingItems;
    }

}
