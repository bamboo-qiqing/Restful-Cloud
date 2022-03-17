package com.bamboo.tool.util;


import com.bamboo.tool.components.api.entity.AnnotationInfoSetting;
import com.bamboo.tool.components.api.enums.AnnotationScope;
import com.bamboo.tool.components.api.view.component.tree.ClassNode;
import com.bamboo.tool.components.api.view.component.tree.MethodNode;
import com.bamboo.tool.components.api.view.component.tree.ModuleNode;
import com.bamboo.tool.components.api.view.component.tree.ProjectModel;
import com.bamboo.tool.config.model.PsiClassCache;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.intellij.openapi.externalSystem.view.ProjectNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.AllClassesSearch;
import com.intellij.util.Query;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PsiUtils {


    public static final List<PsiClassCache> getALLPsiClass(Project project, List<AnnotationInfoSetting> annotationInfoSettings) {
        final List<AnnotationInfoSetting> infos = annotationInfoSettings.stream().filter(e -> AnnotationScope.CLASS.equals(e.getScope())).filter(e -> e.getEffect().contains("scann")).collect(Collectors.toList());
        final Map<String, AnnotationInfoSetting> infoMap = infos.stream().collect(Collectors.toMap(e -> e.getAnnotationPath(), e -> e));
        Query<PsiClass> query = AllClassesSearch.search(ProjectScope.getContentScope(project), project);
        final List<PsiClassCache> caches = new ArrayList<>();
        query.allowParallelProcessing().forEachAsync(e -> {
            final PsiAnnotation[] annotations = e.getAnnotations();
            Arrays.stream(annotations).forEach(annotation -> {
                final AnnotationInfoSetting annotationInfoSetting = infoMap.get(annotation.getQualifiedName());
                if (annotationInfoSetting != null) {
                    caches.add(new PsiClassCache(annotationInfoSetting, e));
                }
            });
            return true;
        });
        return caches;
    }

    public static void convertToRoot(DefaultMutableTreeNode root, List<BambooApiMethod> apiMethods) {
        Map<String, ModuleNode> moduleNodeMap = new ConcurrentHashMap<>();
        Map<String, ClassNode> classNodeMap = new ConcurrentHashMap<>();
        apiMethods.stream().forEach(e -> {
            final String modelName = e.getModelName();
            final String className = e.getClassName();
            ModuleNode moduleNode = new ModuleNode(modelName);
            final ModuleNode moduleNode1 = moduleNodeMap.putIfAbsent(modelName, moduleNode);
            if (moduleNode1 != null) {
                moduleNode = moduleNode1;
            }
            ClassNode classNode = new ClassNode(className);
            final ClassNode classNode1 = classNodeMap.putIfAbsent(modelName + className, classNode);
            if (classNode1 != null) {
                classNode = classNode1;
            }
            moduleNode.add(classNode);
            classNode.add(new MethodNode(e));
        });
        moduleNodeMap.values().stream().forEach(e -> root.add(e));
    }

    public static void convertOtherToRoot(DefaultMutableTreeNode root, List<BambooApiMethod> apiMethods) {

        Map<String, Map<String, Map<String, List<BambooApiMethod>>>> methodGroup = apiMethods.parallelStream().collect(Collectors.groupingBy(e -> e.getProjectName(), Collectors.groupingBy(e -> e.getModelName(), Collectors.groupingBy(e -> e.getClassName(), Collectors.toList()))));

        methodGroup.forEach((key, value) -> {
                    ModuleNode projectModel = new ModuleNode(key);
                    value.forEach((modelKey, modelValue) -> {
                        ModuleNode model = new ModuleNode(modelKey);
                        modelValue.forEach((classKey, classValue) -> {
                            ClassNode classNode = new ClassNode(classKey);
                            classValue.parallelStream().forEach(e -> classNode.add(new MethodNode(e)));
                            model.add(classNode);
                        });
                        projectModel.add(model);
                    });
                    root.add(projectModel);
                }
        );

    }
}
