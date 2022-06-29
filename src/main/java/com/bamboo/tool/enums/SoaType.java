package com.bamboo.tool.enums;

import icons.PluginIcons;
import lombok.Getter;

import javax.swing.*;

/**
 * Create by GuoQing
 * Date 2022/2/16 14:18
 * Description
 */
@Getter
public enum SoaType {
    SERVICE("service", "服务端", PluginIcons.SERVICE),
    CLIENT("client", "客户端", PluginIcons.CLIENT),
    SOA_SERVICE("soa_service", "SOA服务端", PluginIcons.SOA_SERVICE),
    SOA_CLIENT("soa_client", "SOA客户端", PluginIcons.SOA_CLIENT),
    ;
    private final String code;
    private final String desc;
    private final Icon icon;

    SoaType(String code, String desc, Icon icon) {

        this.code = code;
        this.desc = desc;
        this.icon = icon;
    }

    public static SoaType getSoaType(String code) {
        for (SoaType value : SoaType.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
