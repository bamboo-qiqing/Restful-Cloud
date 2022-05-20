package com.bamboo.tool.util;

import com.bamboo.tool.enums.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by GuoQing
 * Date 2022/3/15 14:34
 * Description
 */
public class RequestMethodUtil {

    public static Map<String, String> REQUEST_METHOD = new HashMap<String, String>();

    static {
        REQUEST_METHOD.put("org.springframework.web.bind.annotation.PostMapping", RequestMethod.POST.getCode());
        REQUEST_METHOD.put("org.springframework.web.bind.annotation.GetMapping", RequestMethod.GET.getCode());
        REQUEST_METHOD.put("org.springframework.web.bind.annotation.PatchMapping", RequestMethod.PATCH.getCode());
        REQUEST_METHOD.put("org.springframework.web.bind.annotation.DeleteMapping", RequestMethod.DELETE.getCode());
        REQUEST_METHOD.put("org.springframework.web.bind.annotation.PutMapping", RequestMethod.PUT.getCode());
    }
}
