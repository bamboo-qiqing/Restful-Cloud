package com.bamboo.tool.entity;

import lombok.Data;

import java.util.List;

@Data
public class TableInit {
    private String tableName;
    private String creatSql;
    private List<String> index;
    private List<String> datas;
}
