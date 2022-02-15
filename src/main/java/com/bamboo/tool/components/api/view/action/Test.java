package com.bamboo.tool.components.api.view.action;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereFoundElementInfo;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereMlService;
import com.intellij.ide.actions.searcheverywhere.SearchRestartReason;
import com.intellij.openapi.project.Project;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Test  extends SearchEverywhereMlService {
    @Override
    public double getMlWeight(@NotNull SearchEverywhereContributor<?> searchEverywhereContributor, @NotNull Object o, int i) {
        System.out.printf("1");
        return 0;
    }

    @Override
    public void notifySearchResultsUpdated() {
        System.out.printf("1");
    }

    @Override
    public void onDialogClose() {
        System.out.printf("1");
    }

    @Override
    public void onItemSelected(@Nullable Project project, @NotNull int[] ints, @NotNull List<?> list, boolean b, @NotNull Function0<? extends List<? extends SearchEverywhereFoundElementInfo>> function0) {
        System.out.printf("1");
    }

    @Override
    public void onSearchFinished(@Nullable Project project, @NotNull Function0<? extends List<? extends SearchEverywhereFoundElementInfo>> function0) {
        System.out.printf("1");
    }

    @Override
    public void onSearchRestart(@Nullable Project project, @NotNull String s, @NotNull SearchRestartReason searchRestartReason, int i, int i1, int i2, @NotNull Function0<? extends List<? extends SearchEverywhereFoundElementInfo>> function0) {
        System.out.printf("1");
    }

    @Override
    public void onSessionStarted(@Nullable Project project) {
        System.out.printf("1");
    }

    @Override
    public boolean shouldOrderByMl(@NotNull String s) {
        System.out.printf("1");
        return false;
    }
}
