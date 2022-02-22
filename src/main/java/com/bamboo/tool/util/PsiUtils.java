package com.bamboo.tool.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.bamboo.tool.components.api.entity.AnnotationInfoSetting;
import com.bamboo.tool.components.api.entity.BambooClass;
import com.bamboo.tool.components.api.entity.BambooMethod;
import com.bamboo.tool.components.api.enums.AnnotationScope;

import com.bamboo.tool.components.api.view.component.entity.MethodModel;
import com.bamboo.tool.components.api.view.component.tree.*;

import com.bamboo.tool.config.model.PsiClassCache;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.intellij.configurationStore.schemeManager.SchemeAddOrUpdateEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.AllClassesSearch;
import com.intellij.util.Query;
import nonapi.io.github.classgraph.utils.StringUtils;
import org.apache.commons.collections.IteratorUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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


    @NotNull
    public static List<String> getAnnotationAttributeValues(PsiAnnotationMemberValue value) {

        List<String> values = new ArrayList<>();

        if (value instanceof PsiReferenceExpression) {
            PsiReferenceExpression expression = (PsiReferenceExpression) value;
            String text = expression.getText();
            if (text.contains("RequestMethod.")) {
                text = text.replaceAll("RequestMethod.", "");
            }
            values.add(text);
        } else if (value instanceof PsiLiteralExpression) {
            String text = ((PsiLiteralExpression) value).getValue().toString();
            if (text.contains("RequestMethod.")) {
                text = text.replaceAll("RequestMethod.", "");
            }
            values.add(text);
        } else if (value instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) value).getInitializers();
            for (PsiAnnotationMemberValue initializer : initializers) {
                String text = initializer.getText().replaceAll("\\\"", "");
                if (text.contains("RequestMethod.")) {
                    text = text.replaceAll("RequestMethod.", "");
                }
                values.add(text);
            }
        }

        return values;
    }

//    @NotNull
//    public static void buildValuePair(BambooMethod bambooMethod, PsiAnnotation annotation, BambooClass BambooClass, String method) {
//        if (StringUtil.isNotEmpty(method)) {
//            bambooMethod.getMethodTypes().add(method);
//        }
//        PsiNameValuePair[] pairs = annotation.getParameterList().getAttributes();
//        if (pairs == null && pairs.length < 1) {
//            return;
//        }
//        for (PsiNameValuePair pair : pairs) {
//            String attributeName = pair.getName();
//            // url
//            if (Objects.equals(attributeName, "value") || Objects.equals(attributeName, "path") || StringUtil.isBlank(attributeName)) {
//                PsiAnnotationMemberUtil.getAnnotationAttributeValue(pair);
//                List<String> values = PsiUtils.getAnnotationAttributeValues(pair.getValue());
//                bambooMethod.getMethodUrls().addAll(values);
//            }
//            // method
//            if (Objects.equals(attributeName, "method")) {
//                if (StringUtil.isBlank(method)) {
//                    List<String> values = PsiUtils.getAnnotationAttributeValues(pair.getValue());
//                    bambooMethod.getMethodTypes().addAll(values);
//                }
//            }
//
//            //ContentType
//            if (Objects.equals(attributeName, "produces") || Objects.equals(attributeName, "consumes")) {
//                List<String> values = PsiUtils.getAnnotationAttributeValues(pair.getValue());
//                bambooMethod.getContentTypes().addAll(values);
//            }
//            //headers
//            if (Objects.equals(attributeName, "headers")) {
//                List<String> values = PsiUtils.getAnnotationAttributeValues(pair.getValue());
//                bambooMethod.getHeaders().addAll(values);
//            }
//        }
//        if (CollectionUtil.isEmpty(bambooMethod.getMethodTypes())) {
//            bambooMethod.getMethodTypes().add(RequestMethod.ALL.getCode());
//        }
//
//    }

    public static void convertToRoot(DefaultMutableTreeNode root, Map<String, List<BambooClass>> dataMap) {
        List<ModuleNode> moduleNodeList = new ArrayList<>();
        for (Map.Entry<String, List<BambooClass>> moduleEntry : dataMap.entrySet()) {
            String moduleName = moduleEntry.getKey();
            ModuleNode moduleNode = new ModuleNode(moduleName);
            List<BambooClass> classList = moduleEntry.getValue();
            for (BambooClass bambooClass : classList) {
                final ClassNode classNode = new ClassNode(bambooClass.getClassName());
                AtomicReference<Boolean> isSoaServiceClient = new AtomicReference<>(false);
                AtomicReference<Boolean> isSoaServiceRegister = new AtomicReference<>(false);

                List<String> classUrls = new ArrayList<>();
                bambooClass.getAnnotations().parallelStream().forEach(e -> {
                    final AnnotationInfoSetting annotationInfoSetting = e.getAnnotationInfoSetting();
                    String path = annotationInfoSetting.getAnnotationPath();
                    final String name = e.getParam().getName();
                    if ("com.odianyun.soa.annotation.SoaServiceRegister".equals(path) && "interfaceClass".equals(name)) {
                        isSoaServiceRegister.set(true);
                        final String[] split = e.getValue().split("\\.");
                        final String url = CharSequenceUtil.addPrefixIfNot(StringUtil.lowerFirst(split[split.length - 1]), "/");
                        classUrls.add(url);
                    }
                    if ("com.odianyun.soa.client.annotation.SoaServiceClient".equals(path) && "interfaceName".equals(name)) {
                        final String[] split = e.getValue().split("\\.");
                        final String url = CharSequenceUtil.addPrefixIfNot(StringUtil.lowerFirst(split[split.length - 1]), "/");
                        isSoaServiceClient.set(true);
                        classUrls.add(url);
                    }
                    if ("org.springframework.web.bind.annotation.RequestMapping".equals(path) && "value".equals(name)) {
                        final String url = CharSequenceUtil.addPrefixIfNot(StringUtil.lowerFirst(e.getValue()), "/");
                        classUrls.add(url);
                    }
                });
                List<BambooMethod> bambooMethodList = bambooClass.getMethods();
                bambooMethodList.forEach(bambooMethod -> {
                    classUrls.forEach(classUrl -> {
                        if (isSoaServiceClient.get()) {
                            MethodModel methodModel = new MethodModel();
                            methodModel.setUrl(classUrl + CharSequenceUtil.addPrefixIfNot(bambooMethod.getMethodName(), "/"));
                            methodModel.setDesc(bambooMethod.getDescription());
//                            methodModel.setMethodType(StringUtils.join(bambooMethod.getMethodTypes(), ','));
                            methodModel.setPsiMethod(bambooMethod.getPsiMethod());
                            classNode.add(new MethodNode(methodModel));
                        } else if (isSoaServiceRegister.get()) {
                            MethodModel methodModel = new MethodModel();
                            methodModel.setUrl(classUrl + CharSequenceUtil.addPrefixIfNot(bambooMethod.getMethodName(), "/"));
                            methodModel.setDesc(bambooMethod.getDescription());
//                            methodModel.setMethodType(StringUtils.join(bambooMethod.getMethodTypes(), ','));
                            methodModel.setPsiMethod(bambooMethod.getPsiMethod());
                            classNode.add(new MethodNode(methodModel));
                        } else {
                            bambooMethod.getAnnotationInfos().forEach(bambooAnnotationInfo -> {
                                final AnnotationInfoSetting annotationInfoSetting = bambooAnnotationInfo.getAnnotationInfoSetting();
                                String path = annotationInfoSetting.getAnnotationPath();
                                final String name = bambooAnnotationInfo.getParam().getName();
                                if ("org.springframework.web.bind.annotation.RequestMapping".equals(path) && ("value".equals(name)||"null".equals(name)||"path".equals(name))) {
                                    MethodModel methodModel = new MethodModel();
                                    methodModel.setUrl(classUrl + CharSequenceUtil.addPrefixIfNot(bambooAnnotationInfo.getValue(), "/"));
                                    methodModel.setDesc(bambooMethod.getDescription());
                                    // methodModel.setMethodType(StringUtils.join(bambooMethod.getMethodTypes(), ','));
                                    methodModel.setPsiMethod(bambooMethod.getPsiMethod());
                                    classNode.add(new MethodNode(methodModel));
                                }
                            });
                        }
                    });

                });
                moduleNode.add(classNode);
            }
            moduleNodeList.add(moduleNode);
        }
        moduleNodeList.sort(Comparator.comparing(ModuleNode::toString));
        moduleNodeList.forEach(root::add);
    }

    public static void convertToOtherApi(DefaultMutableTreeNode root, Map<String, Map<String, List<BambooApiMethod>>> otherApis) {
        List<ProjectModel> projectModels = new ArrayList<>();

        for (Map.Entry<String, Map<String, List<BambooApiMethod>>> moduleEntry : otherApis.entrySet()) {
            String projectName = moduleEntry.getKey().split("_")[0];
            Map<String, List<BambooApiMethod>> modes = moduleEntry.getValue();
            ProjectModel projectModel = new ProjectModel(projectName);
            for (Map.Entry<String, List<BambooApiMethod>> entry : modes.entrySet()) {
                String modelName = entry.getKey();
                List<BambooApiMethod> apiMethods = entry.getValue();
                ModuleNode moduleNode = new ModuleNode(modelName);
                for (BambooApiMethod method : apiMethods) {
                    MethodModel methodModel = new MethodModel();
                    methodModel.setUrl(method.getUrl());
                    methodModel.setDesc(method.getDescription());
                    methodModel.setMethodType(method.getMethodType());
                    MethodNode methodNode = new MethodNode(methodModel);
                    moduleNode.add(methodNode);
                }
                projectModel.add(moduleNode);
            }
            projectModels.add(projectModel);
        }
        projectModels.forEach(e -> {
            root.add(e);
        });
    }

    public static Map<String, List<BambooClass>> convertToMap(List<BambooClass> BambooClasses) {
        Map<String, List<BambooClass>> dataMap = BambooClasses.parallelStream().collect(Collectors.groupingBy(BambooClass::getModuleName));
        return dataMap;
    }


    private static void sortChildNode(BaseNode rootNode) {
        ArrayList<BaseNode> nodeList = (ArrayList<BaseNode>) IteratorUtils.toList(rootNode.children().asIterator());
        nodeList.sort((n1, n2) -> {
            String prefix1 = n1 instanceof ClassNode ? "999999" : "000000";
            String prefix2 = n2 instanceof ClassNode ? "999999" : "000000";
            String name1 = n1.toString();
            String name2 = n2.toString();
            return (prefix1 + name1).compareTo(prefix2 + name2);
        });
        rootNode.removeAllChildren();
        nodeList.forEach(rootNode::add);
    }

    @NotNull
    private static PackageNode findChild(@NotNull PackageNode node, @NotNull String name) {
        Enumeration<TreeNode> children = node.children();
        while (children.hasMoreElements()) {
            TreeNode child = children.nextElement();
            if (!(child instanceof PackageNode)) {
                continue;
            }
            PackageNode packageNode = (PackageNode) child;
            if (name.equals(packageNode.getSource())) {
                return packageNode;
            }
        }
        PackageNode packageNode = new PackageNode(name);
        node.add(packageNode);
        return packageNode;
    }

    private static List<PackageNode> findChildren(@NotNull PackageNode node) {
        List<PackageNode> children = new ArrayList<>();
        Enumeration<TreeNode> enumeration = node.children();
        while (enumeration.hasMoreElements()) {
            TreeNode ele = enumeration.nextElement();
            if (ele instanceof PackageNode) {
                sortChildNode((BaseNode) ele);
                children.add((PackageNode) ele);
            }
        }
        return children;
    }
}
