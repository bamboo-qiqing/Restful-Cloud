package com.bamboo.tool.util;


import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.StrUtil;
import com.bamboo.tool.components.api.enums.RequestMethod;
import com.intellij.psi.PsiNameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtil extends StrUtil {


    public static List<String> getAttributes(String attributes, PsiNameValuePair pair) {
        List<String> attributesList = new ArrayList<>();
        if (attributes.contains(StrUtil.COMMA)) {
            char[] chars = {'{', '}', '"','/' };
            StrUtil.replaceChars(attributes, chars, "");
            attributesList = StrUtil.split(attributes, StrUtil.COMMA);
        } else {
            attributes = pair.getValue().getText();
            if (attributes.contains(String.valueOf(CharPool.DOUBLE_QUOTES))) {
                char[] chars = {'"','/' };
                String attribute = StrUtil.replaceChars(attributes, chars, "");
                attributesList.add(attribute);
            } else {
                attributesList.add(attributes);
            }
        }
        return attributesList;
    }

    public static List<String> getRequestMethod(String attributes) {
        List<String> requestMethods = new ArrayList<>();
        if (StringUtil.isNotBlank(attributes)) {
            requestMethods = Arrays.stream(RequestMethod.values()).map(e -> e.getCode()).filter(e -> attributes.contains(e)).collect(Collectors.toList());

        }
        return requestMethods;
    }
}
