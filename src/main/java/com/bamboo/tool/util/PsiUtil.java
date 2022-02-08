package com.bamboo.tool.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.bamboo.tool.components.api.entity.ApiClass;
import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.ClassAnnotationType;
import com.bamboo.tool.components.api.enums.RequestMethod;
import com.bamboo.tool.components.api.factory.ClassAnnotationProcess;
import com.bamboo.tool.components.api.factory.FrameworkExecute;
import com.bamboo.tool.components.api.view.component.tree.*;
import com.bamboo.tool.config.model.PsiClassCache;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.searches.AllClassesSearch;
import com.intellij.util.Query;
import org.apache.commons.collections.IteratorUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.stream.Collectors;

public class PsiUtil {


    public static final List<PsiClassCache> getALLPsiClass(Project project) {
        Query<PsiClass> query = AllClassesSearch.search(ProjectScope.getContentScope(project), project);
        List<PsiClassCache> psiClassCaches = new ArrayList<>();
        query.allowParallelProcessing().forEachAsync(psiClass -> {
            PsiAnnotation[] annotations = psiClass.getAnnotations();
            List<PsiClassCache.ClassAnnotationProcessCache> processList = Arrays.stream(annotations).parallel().filter(e -> !Objects.isNull(FrameworkExecute.classAnnotationProcessMap.get(e.getNameReferenceElement().getReferenceName()))).map(e -> {
                ClassAnnotationProcess classAnnotationProcess = FrameworkExecute.classAnnotationProcessMap.get(e.getNameReferenceElement().getReferenceName());
                PsiClassCache.ClassAnnotationProcessCache cache = new PsiClassCache.ClassAnnotationProcessCache();
                cache.setPsiAnnotation(e);
                cache.setClassAnnotationProcesses(classAnnotationProcess);
                return cache;
            }).collect(Collectors.toList());

            if (processList.size() > 0) {
                psiClassCaches.add(new PsiClassCache(psiClass, processList));
            }
            return true;
        });
        return psiClassCaches;
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

    @NotNull
    public static void buildValuePair(ApiMethod apiMethod, PsiAnnotation annotation, ApiClass apiClass, String method) {

        PsiNameValuePair[] pairs = annotation.getParameterList().getAttributes();
        if (pairs == null && pairs.length < 1) {
            return;
        }
        for (PsiNameValuePair pair : pairs) {
            String attributeName = pair.getName();
            // url
            if (Objects.equals(attributeName, "value") || Objects.equals(attributeName, "path") || StringUtil.isBlank(attributeName)) {
                List<String> values = PsiUtil.getAnnotationAttributeValues(pair.getValue());
                apiMethod.getMethodUrls().addAll(values);
            }
            // method
            if (Objects.equals(attributeName, "method")) {
                if (StringUtil.isBlank(method)) {
                    List<String> values = PsiUtil.getAnnotationAttributeValues(pair.getValue());
                    apiMethod.getMethodTypes().addAll(values);
                } else {
                    apiMethod.getMethodTypes().add(method);
                }
            }

            //ContentType
            if (Objects.equals(attributeName, "produces") || Objects.equals(attributeName, "consumes")) {
                List<String> values = PsiUtil.getAnnotationAttributeValues(pair.getValue());
                apiMethod.getContentTypes().addAll(values);
            }
            //headers
            if (Objects.equals(attributeName, "headers")) {
                List<String> values = PsiUtil.getAnnotationAttributeValues(pair.getValue());
                apiMethod.getHeaders().addAll(values);
            }
        }
        if (CollectionUtil.isEmpty(apiMethod.getMethodTypes())) {
            apiMethod.getMethodTypes().add(RequestMethod.ALL.getCode());
        }
        if (CollectionUtil.isNotEmpty(apiMethod.getMethodUrls())) {
            List<String> classUrls = apiMethod.getMethodUrls().parallelStream().map(e -> CharSequenceUtil.addPrefixIfNot(e, "/")).collect(Collectors.toList());
            apiMethod.setMethodUrls(classUrls);

            apiClass.getClassUrls().parallelStream().forEach(e -> {
                List<String> urls = apiMethod.getMethodUrls().parallelStream().map(a -> e + a).collect(Collectors.toList());
                apiMethod.getUrls().addAll(urls);
            });
        }

    }

    public static void convertToRoot(DefaultMutableTreeNode root, Map<String, List<ApiClass>> dataMap) {
        List<ModuleNode> moduleNodeList = new ArrayList<>();
        for (Map.Entry<String, List<ApiClass>> moduleEntry : dataMap.entrySet()) {
            String moduleName = moduleEntry.getKey();
            ModuleNode moduleNode = new ModuleNode(moduleName);
            List<ApiClass> classList = moduleEntry.getValue();

            for (ApiClass apiClass : classList) {
                List<ApiMethod> apiMethodList = apiClass.getMethods();
                apiMethodList.forEach(apiMethod -> {
                    apiMethod.getUrls().forEach(e -> {
                        apiMethod.setUrlStr(e);
                        moduleNode.add(new MethodNode(apiMethod));
                    });
                });
            }
            moduleNodeList.add(moduleNode);
        }
        moduleNodeList.sort(Comparator.comparing(ModuleNode::toString));
        moduleNodeList.forEach(root::add);
    }

    public static Map<String, List<ApiClass>> convertToMap(List<ApiClass> apiClasses) {
        Map<String, List<ApiClass>> dataMap = apiClasses.parallelStream().collect(Collectors.groupingBy(ApiClass::getModuleName));
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
