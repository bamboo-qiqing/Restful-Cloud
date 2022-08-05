package com.bamboo.tool.util;


import com.bamboo.tool.config.model.PsiClassCache;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.db.service.BambooService;
import com.bamboo.tool.entity.*;
import com.bamboo.tool.enums.AnnotationScope;
import com.bamboo.tool.enums.RequestMethod;
import com.bamboo.tool.view.component.tree.*;
import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.openapi.fileEditor.impl.NonProjectFileWritingAccessProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.PsiImmediateClassType;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PsiUtils {


    public static List<PsiClassCache> getALLPsiClass(Project project, List<AnnotationInfoSetting> annotationInfoSettings) {
        List<AnnotationInfoSetting> infos = annotationInfoSettings.stream().filter(e -> AnnotationScope.CLASS.getCode().equals(e.getScope())).filter(e -> e.getEffect().contains("scann")).collect(Collectors.toList());
        List<PsiClassCache> caches = new ArrayList<>();
        infos.forEach(e -> {
            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(e.getAnnotationName(), project, ProjectScope.getContentScope(project));
            caches.add(new PsiClassCache(e, psiAnnotations));
        });
        return caches;
    }

    public static RootNode convertToRoot(List<BambooApiMethod> apiMethods, List<RequestMethod> requestMethods) {
        boolean isShowDesc = BambooService.selectIsShowDesc();
        final List<DescFramework> descFrameworks = BambooService.selectAllDescFramework();
        Map<String, ModuleNode> moduleNodeMap = new ConcurrentHashMap<>();
        Map<String, ClassNode> classNodeMap = new ConcurrentHashMap<>();
        AtomicInteger apiCount = new AtomicInteger();
        apiMethods.forEach(e -> {
            long count = requestMethods.parallelStream().filter(a -> e.getRequestMethods().contains(a.getCode())).count();
            if (count > 0) {
                apiCount.getAndIncrement();
                String modelName = e.getModelName();
                String className = e.getClassName();

                ModuleNode moduleNode = new ModuleNode(modelName);
                ModuleNode moduleNode1 = moduleNodeMap.putIfAbsent(modelName, moduleNode);
                if (moduleNode1 != null) {
                    moduleNode = moduleNode1;
                }
                final NoteData noteData = new NoteData();
                noteData.setIsShowDesc(isShowDesc);
                noteData.setDescMap(e.getClassDescHashMap());
                noteData.setDescFrameworks(descFrameworks);
                noteData.setName(className);
                noteData.setClassId(e.getClassId());
                ClassNode classNode = new ClassNode(noteData);
                ClassNode classNode1 = classNodeMap.putIfAbsent(modelName + className, classNode);
                if (classNode1 != null) {
                    classNode = classNode1;
                }
                moduleNode.add(classNode);
                e.setIsShowDesc(isShowDesc);
                e.setDescFrameworks(descFrameworks);
                classNode.add(new MethodNode(e));
            }

        });
        RootNode root = new RootNode("apis(" + apiCount.get() + ")");
        moduleNodeMap.values().forEach(e -> root.add(e));
        return root;
    }

    public static RootNode convertOtherToRoot(List<BambooApiMethod> apiMethods, List<RequestMethod> requestMethods) {

        boolean isShowDesc = BambooService.selectIsShowDesc();
        final List<DescFramework> descFrameworks = BambooService.selectAllDescFramework();
        Map<String, ProjectNode> projectNodeMap = new ConcurrentHashMap<>();
        Map<String, ModuleNode> moduleNodeMap = new ConcurrentHashMap<>();

        Map<String, ClassNode> classNodeMap = new ConcurrentHashMap<>();
        AtomicInteger apiCount = new AtomicInteger();
        apiMethods.forEach(e -> {
            long count = requestMethods.parallelStream().filter(a -> e.getRequestMethods().contains(a.getCode())).count();
            if (count > 0) {
                apiCount.getAndIncrement();
                String modelName = e.getModelName();
                String className = e.getClassName();
                String projectName = e.getProjectName();
                ModuleNode moduleNode = new ModuleNode(modelName);
                ModuleNode moduleNode1 = moduleNodeMap.putIfAbsent(modelName, moduleNode);
                if (moduleNode1 != null) {
                    moduleNode = moduleNode1;
                }
                NoteData noteData = new NoteData();
                noteData.setIsShowDesc(isShowDesc);
                noteData.setDescMap(e.getClassDescHashMap());
                noteData.setDescFrameworks(descFrameworks);
                noteData.setName(className);
                noteData.setClassId(e.getClassId());
                ClassNode classNode = new ClassNode(noteData);
                ClassNode classNode1 = classNodeMap.putIfAbsent(modelName + className, classNode);
                if (classNode1 != null) {
                    classNode = classNode1;
                }
                moduleNode.add(classNode);
                e.setIsShowDesc(isShowDesc);
                e.setDescFrameworks(descFrameworks);
                classNode.add(new MethodNode(e));

                ProjectNode projectNode = new ProjectNode(projectName);
                ProjectNode projectNode1 = projectNodeMap.putIfAbsent(projectName, projectNode);
                if (projectNode1 != null) {
                    projectNode = projectNode1;
                }
                projectNode.add(moduleNode);
            }

        });
        RootNode root = new RootNode("apis(" + apiCount.get() + ")");
        projectNodeMap.values().forEach(e -> root.add(e));
        return root;
    }


    public static void convertHistoryToRoot(DefaultMutableTreeNode root, List<BambooApiMethod> apiMethods) {
        boolean isShowDesc = BambooService.selectIsShowDesc();
        final List<DescFramework> descFrameworks = BambooService.selectAllDescFramework();
        Map<String, List<BambooApiMethod>> projectGroup = apiMethods.stream().collect(Collectors.groupingBy(e -> e.getProjectName(), Collectors.toList()));

        projectGroup.forEach((projectName, value) -> {
            ProjectNode projectNode = new ProjectNode(projectName);
            value.stream().sorted(Comparator.comparingInt(BambooApiMethod::getQueryCount)).map(e -> {
                e.setIsShowDesc(isShowDesc);
                e.setDescFrameworks(descFrameworks);
                MethodNode methodNode = new MethodNode(e);
                return methodNode;
            }).forEach(e -> projectNode.add(e));
            root.add(projectNode);
        });
    }


    public static void openFile(@NotNull VirtualFile file, @NotNull Project project, String methodName, String methodReturn, List<MethodParam> methodParams) {
        if (file == null) {
            return;
        }

        if (project == null) {
            return;
        }

        NonProjectFileWritingAccessProvider.allowWriting(Collections.singletonList(file));
        PsiFile psiFile = PsiUtil.getPsiFile(project, file);
        if (psiFile instanceof PsiJavaFile) {
            PsiClass childClass = ((PsiJavaFileImpl) psiFile).findChildByClass(PsiClass.class);
            if (childClass != null) {
                PsiElement psiElement = Arrays.stream(childClass.getMethods()).filter(e -> e.getName().equals(methodName)).filter(e -> {
                    if (!e.getReturnType().getCanonicalText().equals(methodReturn)) {
                        return false;
                    }
                    List<String> paramTypePaths = methodParams.parallelStream().map(type -> type.getParamType()).collect(Collectors.toList());
                    if (methodParams != null) {
                        PsiParameterList parameters = e.getParameterList();
                        long count = Arrays.stream(parameters.getParameters()).filter(a -> !paramTypePaths.contains(a.getType().getPresentableText())).count();
                        return count == 0;
                    }
                    return false;
                }).map(PsiElement::getNavigationElement).findFirst().get();
                Navigatable navigatable = (Navigatable) psiElement;
                navigatable.navigate(true);
            } else {
                PsiNavigationSupport.getInstance().createNavigatable(project, file, -1).navigate(true);
            }
        } else {
            PsiNavigationSupport.getInstance().createNavigatable(project, file, -1).navigate(true);
        }
    }

    public static List<JavaFieldInfo> getClassField(PsiType psiType) {
        List<JavaFieldInfo> fields = new ArrayList<>();
        if (psiType instanceof PsiClassReferenceType) {
            PsiClassReferenceType psiClassReferenceType = (PsiClassReferenceType) psiType;
            PsiClass psiClass = psiClassReferenceType.resolve();
            if (!Objects.isNull(psiClass)) {
                PsiField[] allFields = psiClass.getAllFields();
                for (PsiField field : allFields) {
                    if (hasStaticModifier(field.getModifierList()) || hasFinalModifier(field.getModifierList())) {
                        // 如果字段是static或final则跳过
                        continue;
                    }
                    JavaFieldInfo fieldInfo = new JavaFieldInfo();
                    fieldInfo.setFieldName(field.getName());
                    fieldInfo.setJavaDocComment(JavaDocComment.buildJavaDocComment(field.getDocComment()));
                    PsiType type = field.getType();
                    if (type instanceof PsiClassReferenceType) {
                        PsiClassReferenceType classReferenceType = (PsiClassReferenceType) type;
                        final PsiClass aClass = classReferenceType.resolve();
                        if (!Objects.isNull(aClass)) {
                            List<JavaFieldInfo> classField = PsiUtils.getClassField(type);
                            fieldInfo.setChildren(classField);
                        }
                        fieldInfo.setFieldType(fieldInfo.getFieldType());
                    } else if (type instanceof PsiPrimitiveType) {
                        PsiPrimitiveType primitiveType = (PsiPrimitiveType) type;
                        fieldInfo.setFieldType(primitiveType.getName());
                    } else if (type instanceof PsiImmediateClassType) {
                        PsiImmediateClassType immediateClassType = (PsiImmediateClassType) type;
                        final String name = immediateClassType.getName();
                    } else {
                        System.out.printf("");
                    }
                    fields.add(fieldInfo);
                }
            }
        }
        return fields;
    }

    /**
     * 是否是静态(static)方法
     *
     * @param target PsiModifierList的实现类
     * @return bool
     * @see PsiUtils#hasModifier(PsiModifierList, String)
     */
    public static boolean hasStaticModifier(@Nullable PsiModifierList target) {
        return PsiUtils.hasModifier(target, PsiModifier.STATIC);
    }

    /**
     * 是否是最终(final)方法
     *
     * @param target PsiModifierList的实现类
     * @return bool
     * @see PsiUtils#hasModifier(PsiModifierList, String)
     */
    public static boolean hasFinalModifier(@Nullable PsiModifierList target) {
        return PsiUtils.hasModifier(target, PsiModifier.FINAL);
    }

    /**
     * 是否具有指定修饰符
     *
     * @param target PsiModifierList的实现类
     * @return bool
     */
    public static boolean hasModifier(@Nullable PsiModifierList target, @PsiModifier.ModifierConstant @NotNull String modifier) {
        if (target == null) {
            return false;
        }
        // 是否具有修饰符属性
        return target.hasModifierProperty(modifier);
    }

}
