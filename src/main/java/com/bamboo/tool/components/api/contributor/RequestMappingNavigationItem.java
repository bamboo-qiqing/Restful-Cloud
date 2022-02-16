package com.bamboo.tool.components.api.contributor;

import com.intellij.ide.lightEdit.LightEdit;
import com.intellij.ide.lightEdit.LightEditFeatureUsagesUtil;
import com.intellij.ide.lightEdit.LightEditService;
import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.lang.jvm.JvmMethod;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.fileEditor.impl.NonProjectFileWritingAccessProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiClassUtil;
import com.intellij.psi.util.PsiUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Create by GuoQing
 * Date 2022/2/15 11:15
 * Description
 */
@Data
public class RequestMappingNavigationItem implements NavigationItem {
    private Navigatable navigationElement;
    private PsiElement psiElement;
    private String urlPath;
    private String className;
    private String classPath;
    private String requestMethod;
    private String types;
    private String modelName;
    private String projectName;
    private String methodName;
    private Project project;

    public RequestMappingNavigationItem() {

    }

    @Override
    public @Nullable
    @NlsSafe String getName() {
        return this.requestMethod + " " + this.urlPath;
    }

    @Override
    public @Nullable
    ItemPresentation getPresentation() {

        return new RequestMappingItemPresentation(this);
    }

    @Override
    public void navigate(boolean requestFocus) {
        VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(classPath);

        if (file != null && file.isValid()) {
            openFile(file, project, methodName);
        }
        if (navigationElement != null) {
            navigationElement.navigate(requestFocus);
        }
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
                PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                PsiClass[] classes = javaFile.getClasses();
                if (classes.length > 0) {
                    PsiClass aClass = classes[0];
                    PsiElement psiElement = Arrays.stream(aClass.getMethods()).filter(e -> e.getName().equals(methodName)).map(e -> e.getNavigationElement()).findFirst().get();
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
