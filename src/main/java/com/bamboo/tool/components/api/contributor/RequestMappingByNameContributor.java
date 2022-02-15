package com.bamboo.tool.components.api.contributor;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RequestMappingByNameContributor implements ChooseByNameContributor {
    @Override
    public String @NotNull [] getNames(Project project, boolean b) {
        return new String[]{"你好"};
    }

    @Override
    public NavigationItem @NotNull [] getItemsByName(String s, String s1, Project project, boolean b) {
        return new NavigationItem[0];
    }
}
