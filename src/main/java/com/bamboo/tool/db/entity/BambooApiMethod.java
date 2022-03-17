package com.bamboo.tool.db.entity;

import com.bamboo.tool.components.api.contributor.RequestMappingItemPresentation;
import com.intellij.ide.lightEdit.LightEdit;
import com.intellij.ide.lightEdit.LightEditFeatureUsagesUtil;
import com.intellij.ide.lightEdit.LightEditService;
import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.impl.NonProjectFileWritingAccessProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.util.PsiUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;

/**
 * Create by GuoQing
 * Date 2022/2/14 11:50
 * Description
 */
@Data
public class BambooApiMethod implements NavigationItem {
    private Navigatable navigationElement;
    private String url;
    private String requestMethods;
    private String methodName;
    private String methodDesc;
    private String className;
    private String classPath;
    private String classDesc;
    private String modelName;
    private String projectName;
    private String soaType;
    private String frameworkName;
    private Project project;
    private PsiMethod PsiMethod;

    @Override
    public @Nullable
    @NlsSafe String getName() {
        return this.requestMethods + " " + this.url;
    }

    @Override
    public @Nullable
    ItemPresentation getPresentation() {

        return new RequestMappingItemPresentation(this);
    }

    @Override
    public void navigate(boolean requestFocus) {
        VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(classPath);

        ApplicationManager.getApplication().runReadAction(() -> {
            if (file != null && file.isValid()) {
                openFile(file, project, methodName);
            }
            if (navigationElement != null) {
                navigationElement.navigate(requestFocus);
            }
        });
    }

    @Override
    public boolean canNavigate() {
        return this.navigationElement != null;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    public static void openFile(@NotNull VirtualFile file, @NotNull Project project, String methodName) {
        if (file == null) {
            return;
        }

        if (project == null) {
            return;
        }

        NonProjectFileWritingAccessProvider.allowWriting(Collections.singletonList(file));
        if (LightEdit.owns(project)) {
            LightEditService.getInstance().openFile(file);
            LightEditFeatureUsagesUtil.logFileOpen(project, LightEditFeatureUsagesUtil.OpenPlace.LightEditOpenAction);
        } else {
            PsiFile psiFile = PsiUtil.getPsiFile(project, file);
            if (psiFile instanceof PsiJavaFile) {
                final PsiClass childClass = ((PsiJavaFileImpl) psiFile).findChildByClass(PsiClass.class);
                if (childClass != null) {
                    PsiElement psiElement = Arrays.stream(childClass.getMethods()).filter(e -> e.getName().equals(methodName)).map(e -> e.getNavigationElement()).findFirst().get();
                    Navigatable navigatable = (Navigatable) psiElement;
                    navigatable.navigate(true);
                } else {
                    PsiNavigationSupport.getInstance().createNavigatable(project, file, -1).navigate(true);
                }
            } else {
                PsiNavigationSupport.getInstance().createNavigatable(project, file, -1).navigate(true);
            }

        }

    }
}
