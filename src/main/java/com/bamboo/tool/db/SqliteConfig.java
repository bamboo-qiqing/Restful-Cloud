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

    public static ResultSet exeQuerySql(String sql) {
        try {
            Class.forName("org.sqlite.JDBC");
            String db = FileUtil.getUserHomePath() + "\\bambooTool\\bambooApi.db";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db);
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery(sql); //查询数据
            return rs;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean exeQueryUpdateSql(String sql) {
        try {
            Class.forName("org.sqlite.JDBC");
            String db = FileUtil.getUserHomePath() + "\\bambooTool\\bambooApi.db";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db);
            Statement state = conn.createStatement();
            int i = state.executeUpdate(sql);
            conn.close();
            return i > 0 ? true : false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
