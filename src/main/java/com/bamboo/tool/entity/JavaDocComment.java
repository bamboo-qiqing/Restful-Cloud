package com.bamboo.tool.entity;

import com.bamboo.tool.util.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import lombok.Data;
import org.apache.tools.ant.taskdefs.Java;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Create by GuoQing
 * Date 2022/8/3 17:44
 * Description
 */
@Data
public class JavaDocComment {

    private Map<String, List<JavaDocTag>> tags;

    public static JavaDocComment buildJavaDocComment(PsiDocComment psiDocComment) {
        if (Objects.isNull(psiDocComment)) {
            return null;
        }
        JavaDocComment javaDocComment = new JavaDocComment();
        PsiDocTag[] tags = psiDocComment.getTags();
        if (tags.length > 0) {
            Map<String, List<JavaDocTag>> listMap = Arrays.stream(tags).map(e -> {
                JavaDocTag javaDocTag = new JavaDocTag();
                javaDocTag.setName(e.getName());
                PsiElement[] dataElements = e.getDataElements();
                StringBuilder values = new StringBuilder();
                for (PsiElement psiElement : dataElements) {
                    if (StringUtil.isNotEmpty(psiElement.getText())) {
                        values.append(psiElement.getText().trim());
                    }
                }
                javaDocTag.setValue(values.toString());
                return javaDocTag;
            }).filter(e -> StringUtil.isNotEmpty(e.getValue())).collect(Collectors.groupingBy(e -> e.getName(), Collectors.toList()));
            javaDocComment.setTags(listMap);
        }
        return javaDocComment;
    }
}
