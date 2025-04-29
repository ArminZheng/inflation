package com.arminzheng.inflation.constant;

import com.arminzheng.inflation.datasource.SourceMapper;

public final class DataSourceConst {


    public static final String SOURCE_MAPPER_NAMESPACE = "com.arminzheng.inflation.datasource.SourceMapper";

    private DataSourceConst() {
        System.out.println(" get class name: " + SourceMapper.class.getName());
    }
}
