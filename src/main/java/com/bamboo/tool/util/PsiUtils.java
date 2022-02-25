package com.bamboo.tool.util;

import b.C.S;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.bamboo.tool.components.api.entity.*;
import com.bamboo.tool.components.api.enums.AnnotationScope;
import com.bamboo.tool.components.api.enums.RequestMethod;
import com.bamboo.tool.components.api.view.component.entity.MethodModel;
import com.bamboo.tool.components.api.view.component.tree.*;
import com.bamboo.tool.config.model.PsiClassCache;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.AllClassesSearch;
import com.intellij.util.Query;
import org.apache.commons.collections.IteratorUtils;

import javax.swing.tree.DefaultMutableTreeNode;
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

    public static List<BambooClass> convertToRoot(DefaultMutableTreeNode root, Map<String, List<BambooClass>> dataMap) {
        List<BambooClass> list = new ArrayList<>();
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
                    if ("org.springframework.web.bind.annotation.RequestMapping".equals(path) && ("value".equals(name) || "null".equals(name))) {
                        final String url = CharSequenceUtil.addPrefixIfNot(StringUtil.lowerFirst(e.getValue()), "/");
                        classUrls.add(url);
                    }
                });
                List<BambooMethod> bambooMethodList = bambooClass.getMethods();
                bambooMethodList.forEach(bambooMethod -> {
                    List<BambooApiModel> methodModels = new ArrayList<>();
                    classUrls.forEach(classUrl -> {
                        if (isSoaServiceClient.get()) {
                            BambooApiModel methodModel = new BambooApiModel();
                            methodModel.setUrl(classUrl + CharSequenceUtil.addPrefixIfNot(bambooMethod.getMethodName(), "/"));
                            methodModel.setDesc(bambooMethod.getDescription());
                            methodModel.setPsiMethod(bambooMethod.getPsiMethod());
                            methodModels.add(methodModel);
                        } else if (isSoaServiceRegister.get()) {
                            BambooApiModel methodModel = new BambooApiModel();
                            methodModel.setUrl(classUrl + CharSequenceUtil.addPrefixIfNot(bambooMethod.getMethodName(), "/"));
                            methodModel.setDesc(bambooMethod.getDescription());
                            methodModel.setPsiMethod(bambooMethod.getPsiMethod());
                            methodModels.add(methodModel);
                        } else {
                            bambooMethod.getAnnotationInfos().forEach(bambooAnnotationInfo -> {
                                BambooApiModel methodModel = new BambooApiModel();
                                methodModel.setDesc(bambooMethod.getDescription());
                                methodModel.setPsiMethod(bambooMethod.getPsiMethod());
                                final AnnotationInfoSetting annotationInfoSetting = bambooAnnotationInfo.getAnnotationInfoSetting();
                                String path = annotationInfoSetting.getAnnotationPath();
                                final String name = bambooAnnotationInfo.getParam().getName();
                                if ("org.springframework.web.bind.annotation.RequestMapping".equals(path)) {
                                    if ("value".equals(name) || "null".equals(name) || "path".equals(name)) {
                                        methodModel.setUrl(classUrl + CharSequenceUtil.addPrefixIfNot(bambooAnnotationInfo.getValue(), "/"));
                                    }
                                    if ("method".equals(name)) {
                                        if (StringUtil.isEmpty(methodModel.getRequestType())) {
                                            methodModel.setRequestType(bambooAnnotationInfo.getValue());
                                        } else {
                                            if ("org.springframework.web.bind.annotation.PostMapping".equals(path)) {
                                                methodModel.setRequestType(methodModel.getRequestType() + "," + bambooAnnotationInfo.getValue());
                                            }
                                        }
                                    } else {
                                        if (StringUtil.isEmpty(methodModel.getRequestType()) || !methodModel.getRequestType().contains(RequestMethod.POST.getCode())) {
                                            methodModel.setRequestType(RequestMethod.POST.getCode());
                                        }
                                    }
                                }
                            });
                        }
                        if (CollectionUtil.isNotEmpty(methodModels)) {
                            bambooMethod.getApis().addAll(methodModels);
                            methodModels.parallelStream().forEach(e -> {
                                if (StringUtil.isEmpty(e.getRequestType())) {
                                    e.setRequestType(RequestMethod.ALL.getCode());
                                }
                                classNode.add(new MethodNode(e));
                            });
                        }
                    });

                });
                list.add(bambooClass);
                moduleNode.add(classNode);
            }
            moduleNodeList.add(moduleNode);
        }
        moduleNodeList.sort(Comparator.comparing(ModuleNode::toString));
        moduleNodeList.forEach(root::add);
        return list;
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
                    BambooApiModel apiModel = new BambooApiModel();
                    apiModel.setUrl(method.getUrl());
                    apiModel.setDesc(method.getDescription());
                    apiModel.setRequestType(method.getMethodType());
                    MethodNode methodNode = new MethodNode(apiModel);
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


}
