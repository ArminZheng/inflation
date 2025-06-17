package com.arminzheng.datacenter.constant;

import com.arminzheng.datacenter.datasource.SourceMapper;

public final class DataSourceConst {


    public static final String SOURCE_MAPPER_NAMESPACE = "com.arminzheng.datacenter.datasource.SourceMapper";

    private DataSourceConst() {
        System.out.println(" get class name: " + SourceMapper.class.getName());
    }
}
