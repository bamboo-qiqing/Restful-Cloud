package com.bamboo.tool.components.api.frameworkType;

import com.bamboo.tool.components.api.entity.ApiMethod;
import com.bamboo.tool.components.api.enums.ClassAnnotationTypeEnum;
import com.bamboo.tool.components.api.enums.FrameworkTypeEnum;

import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/1/21 14:28
 * Description 框架基础类
 */
public abstract class FrameworkBase {

    /**
     * 获取框架类型
     *
     * @return 框架类型枚举
     */
    public abstract FrameworkTypeEnum getFrameworkType();


    public  abstract ApiMethod getApiMethod();

    /**
     * 获取框架所有需要查找的注解枚举
     *
     * @return 注解枚举集合
     */
    public List<ClassAnnotationTypeEnum> getCLassType() {
        return ClassAnnotationTypeEnum.getClassAnnotationTypes(this.getFrameworkType());
    }

}
