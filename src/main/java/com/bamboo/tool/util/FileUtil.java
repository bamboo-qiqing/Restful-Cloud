package com.bamboo.tool.util;

import cn.hutool.core.io.file.FileReader;

import java.io.File;

public class FileUtil extends cn.hutool.core.io.FileUtil {


    public static String getFileReaderString(String filePath) {
        boolean exist = FileUtil.exist(filePath);
        File file = null;
        if (!exist) {
            file = FileUtil.touch(filePath);
        } else {
            file = FileUtil.file(filePath);
        }
        FileReader fileReader = FileReader.create(file);
        return fileReader.readString();
    }
}
