package com.arminzheng.inflation.datasource;

import com.arminzheng.inflation.constant.DataSourceConst;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;

/**
 * SourceMapper工厂类
 * 负责动态生成SourceMapper接口的实现类
 */
@Slf4j
@Component
public class SourceMapperFactory {

    private final SqlSessionFactory sqlSessionFactory;
    private final SqlFileLoader sqlFileLoader;
    private final MappedStatementContainer mappedStatementContainer;
    private final String namespace;

    public SourceMapperFactory(SqlSessionFactory sqlSessionFactory, SqlFileLoader sqlFileLoader,
            MappedStatementContainer mappedStatementContainer) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.sqlFileLoader = sqlFileLoader;
        this.namespace = DataSourceConst.SOURCE_MAPPER_NAMESPACE;
        this.mappedStatementContainer = mappedStatementContainer;
    }
    
    /**
     * 创建SourceMapper接口的代理实现
     * 
     * @return SourceMapper实例
     */
    public SourceMapper createSourceMapper() {
        // 加载所有SQL文件并创建MappedStatement
        Map<String, String> sqlMap = sqlFileLoader.loadAllSqlFiles();
        for (Map.Entry<String, String> entry : sqlMap.entrySet()) {
            mappedStatementContainer.createMappedStatement(entry.getKey(), entry.getValue());
            log.info("Created MappedStatement for SQL ID: {}", entry.getKey());
        }
        // 创建动态代理
        return (SourceMapper) Proxy.newProxyInstance(
                SourceMapper.class.getClassLoader(),
                new Class<?>[]{SourceMapper.class},
                new SourceMapperInvocationHandler(sqlSessionFactory, namespace, sqlFileLoader)
        );
    }

    /**
     * SourceMapper接口的调用处理器
     */
    private record SourceMapperInvocationHandler(SqlSessionFactory sqlSessionFactory,
                                                 String namespace,
                                                 SqlFileLoader sqlFileLoader) implements
            InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 处理Object类的方法
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            // 处理SourceMapper接口的方法
            if ("query".equals(method.getName())) {
                if (args.length == 1 && args[0] instanceof String) {
                    // 无参数查询
                    return executeQuery((String) args[0], new HashMap<>());
                } else if (args.length == 2 && args[0] instanceof String
                        && args[1] instanceof Map) {
                    // 带参数查询
                    @SuppressWarnings("unchecked")
                    Map<String, Object> params = (Map<String, Object>) args[1];
                    return executeQuery((String) args[0], params);
                }
            }
            throw new UnsupportedOperationException("Method not supported: " + method.getName());
        }

        /**
         * 执行查询
         *
         * @param id     SQL ID
         * @param params 查询参数
         * @return 查询结果
         */
        private List<Map<String, Object>> executeQuery(String id, Map<String, Object> params) {
            // 检查SQL ID是否存在
            if (!sqlFileLoader.containsSql(id)) {
                throw new IllegalArgumentException("SQL ID not found: " + id);
            }
            // 构建完整的statement ID
            String statementId = namespace + "." + id;

            // 执行查询
            try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                return sqlSession.selectList(statementId, params);
            }
        }
    }
}
