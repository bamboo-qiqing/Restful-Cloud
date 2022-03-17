package com.bamboo.tool.db;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;

import java.sql.*;

/**
 * Create by GuoQing
 * Date 2022/1/29 16:46
 * Description
 */
public class SqliteConfig {

    @SneakyThrows
    public static Connection getConnection() {

        Class.forName("org.sqlite.JDBC");
        String db = FileUtil.getUserHomePath() + "\\bambooTool\\bambooApi.db";
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db);
        return conn;
    }


}
