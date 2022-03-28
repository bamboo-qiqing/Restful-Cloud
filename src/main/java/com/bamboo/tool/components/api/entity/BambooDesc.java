package com.bamboo.tool.components.api.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class BambooDesc {
    private Integer id;
    private String describe;
    private String framewordCode;
    private String associationId;


    public String toSql(String associationId) {
        return "INSERT INTO bamboo_desc (id, describe, frameword_code, association_id) VALUES ('" + UUID.randomUUID() + "', '" + describe + "', '" + framewordCode + "', '" + associationId + "');";
    }
}
