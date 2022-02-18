package com.bamboo.tool.db.service;

import com.bamboo.tool.components.api.entity.Framework;
import com.bamboo.tool.db.SqliteConfig;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuoQing
 * Date 2022/2/18 16:49
 * Description
 */
public class FrameworkService {
    @SneakyThrows
    public List<Framework> selectAll() {
        Connection conn = SqliteConfig.getConnection();
        Statement state = conn.createStatement();
        StringBuffer str = new StringBuffer();
        str.append("select");
        str.append("f.*");
        str.append("from");
        str.append("framework f;");
        ResultSet resultSet = state.executeQuery(str.toString());
        List<Framework> frameworks = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String describe = resultSet.getString("describe");

            Framework framework = new Framework();
            framework.setId(id);
            framework.setName(name);
            framework.setDescribe(describe);
            frameworks.add(framework);
        }
        resultSet.close();
        state.close();
        conn.close();
        return frameworks;
    }
}
