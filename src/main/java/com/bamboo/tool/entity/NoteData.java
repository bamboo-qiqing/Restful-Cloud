package com.bamboo.tool.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NoteData {
    private String name;
    private Map<String, String> descMap;
    private Boolean isShowDesc;
    private List<DescFramework> descFrameworks;
    private String classId;
    private String methodId;
}
