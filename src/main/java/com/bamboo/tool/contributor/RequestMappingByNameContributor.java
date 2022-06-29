package com.bamboo.tool.contributor;


import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
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
        String userData = project.getUserData(ChooseByNamePopup.CURRENT_SEARCH_PATTERN);
        navigationItems = BambooService.getAllApi(null, null,project,userData,false,null);
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
