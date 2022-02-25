package com.bamboo.tool.components.api.entity;

import b.C.S;
import lombok.Data;

/**
 * Create by GuoQing
 * Date 2022/2/25 13:41
 * Description
 */
@Data
public class SqliteMaster {
    private String type;
    private String name;
    private String tblName;
    private Integer rootpage;
    private String sql;
}
