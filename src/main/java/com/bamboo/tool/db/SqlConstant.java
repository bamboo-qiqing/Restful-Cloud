package com.bamboo.tool.db;

import com.bamboo.tool.util.StringUtil;

/**
 * Create by GuoQing
 * Date 2022/1/29 18:17
 * Description
 */
public class SqlConstant {

    public static final String CREAT_PROJECT_SQL = "create table bamboo_api_project ( id integer not null constraint bamboo_api_project_pk primary key autoincrement, project_name text, project_path text, api_file_path text, api_file_name text );";

    public static final String CREAT_METHOD_SQL = "create table bamboo_api_method ( id integer not null constraint bamboo_api_method_pk primary key autoincrement, project_id integer not null, description text, method_name text, method_type text,"
            + " content_type text, header text, params text, url text, model_name text, class_name text, class_desc text,types text,service_name text,class_path text);";

    public static final String SQLITE_MASTER_QUERY = "SELECT  * FROM sqlite_master WHERE type='table' AND name = '{}'";


    public static final String PROJECT_TABLE_NAME = "bamboo_api_project";

    public static final String METHOD_TABLE_NAME = "bamboo_api_method";


    public static final String QUERY_PROJECT_TABLE = StringUtil.format(SQLITE_MASTER_QUERY, PROJECT_TABLE_NAME);

    public static final String QUERY_METHOD_TABLE = StringUtil.format(SQLITE_MASTER_QUERY, METHOD_TABLE_NAME);

    public static final String OTHER_ALL_API_SQL = "select bam.id, bam.project_id, bam.description, bam.method_name, bam.method_type, bam.content_type, bam.header, bam.params, bam.url, bam.model_name, bam.class_name, bam.class_desc, bam.types, bam.service_name, bap.project_name, bap.project_path, bap.api_file_name, bap.api_file_path, bap.id as project_id from bamboo_api_method bam inner join bamboo_api_project bap on bap.id = bam.project_id";
}
