package com.bamboo.tool.util;

import java.util.ResourceBundle;

public class I18nUtil {
    public static final String I18N_PATH = "language.restfulCloudMsg.rc";

    public static String getString(String key) {
        return ResourceBundle.getBundle(I18N_PATH).getString(key);
    }
}
