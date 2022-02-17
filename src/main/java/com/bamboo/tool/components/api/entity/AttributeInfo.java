package com.bamboo.tool.components.api.entity;

import com.bamboo.tool.components.api.enums.PsiAnnotationMemberType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/16 11:10
 * Description
 */
@Data
public class AttributeInfo {
    private PsiAnnotationMemberType type;
    private String name;
    private List<String> values=new ArrayList<>();
}
