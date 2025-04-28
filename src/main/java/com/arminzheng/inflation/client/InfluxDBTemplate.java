package com.arminzheng.inflation.client;

import com.arminzheng.inflation.config.InfluxDBProperties;
import com.arminzheng.inflation.util.BeanUtils;
import com.arminzheng.inflation.util.DescriptorCache;
import jakarta.annotation.PreDestroy;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
public class InfluxDBTemplate {

    private final InfluxDBProperties influxDBProperties;
    private InfluxDB influxDB;

    public InfluxDBTemplate(InfluxDBProperties influxDBProperties) {
        this.influxDBProperties = influxDBProperties;
        getInfluxDB();
    }

    private void getInfluxDB() {
        if (influxDB != null) {
            return;
        }
        final String serverURL = influxDBProperties.getServerURL();
        final String username = influxDBProperties.getUsername();
        final String password = influxDBProperties.getPassword();
        this.influxDB = InfluxDBFactory.connect(serverURL, username, password);

        use(influxDBProperties.getDatabase());
        if (!ObjectUtils.isEmpty(influxDBProperties.getRetention())) {
            useRetention(influxDBProperties.getRetention());
        }
    }

    @PreDestroy
    public void destroy() {
        if (influxDB != null) {
            influxDB.close();
        }
    }

    public void useRetention(String retention) {
        influxDB.setRetentionPolicy(retention);
    }

    public QueryResult exec(String sql) {
        QueryResult result = influxDB.query(new Query(sql));
        return result;
    }

    public void use(String databaseName) {
        influxDB.setDatabase(databaseName);
    }

    public QueryResult createDatabase(String databaseName) {
        return exec("CREATE DATABASE " + databaseName);
    }

    public void insert(Point... point) {
        // 1000 条数据
        BatchPoints points = BatchPoints.builder().points(point).build();
        influxDB.write(points);
    }

    public void insert(final String measurement, final Map<String, String> tag,
            final Map<String, Object> fields) {
        Builder builder = Point.measurement(measurement);
        if (tag != null) {
            builder.tag(tag);
        }
        if (fields != null) {
            builder.fields(fields);
        }
        // builder.time(new Date().getTime(), TimeUnit.MILLISECONDS);
        // builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        // no needed. auto generate time tag
        insert(builder.build());
    }

    private <T> List<T> handleQueryResult(QueryResult result, Class<T> clz) {
        List<T> res = new ArrayList<>();
        for (Result r : result.getResults()) {
            if (r.getError() != null) {
                throw new RuntimeException(r.getError());
            }
            if (r.getSeries() == null) {
                continue;
            }
            List<Series> series = r.getSeries();
            for (Series ser : series) {
                List<String> columns = ser.getColumns();
                List<List<Object>> valueList = ser.getValues();
                try {
                    T t = clz.getDeclaredConstructor().newInstance();
                    DescriptorCache descriptorCache = BeanUtils.extractClass(clz);
                    Map<String, PropertyDescriptor> descriptorMap = descriptorCache.getPropertyDescriptorMap();
                    for (List<Object> objects : valueList) {
                        for (int k = 0; k < columns.size(); k++) {
                            String col = columns.get(k);
                            Object val = objects.get(k);
                            PropertyDescriptor propertyDescriptor = descriptorMap.get(col);
                            if (propertyDescriptor != null) {
                                propertyDescriptor.getWriteMethod().invoke(t, val);
                            }
                        }
                    }
                } catch (InstantiationException | IllegalAccessException |
                         InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return res;
    }

    public QueryResult select(String select, String measurement, String where, String order,
            String last) {
        // 1. Add parameter validation
        if (measurement == null || measurement.trim().isEmpty()) {
            throw new IllegalArgumentException("Measurement cannot be null or empty");
        }
        if (select == null || select.trim().isEmpty()) {
            select = "*";
        }
        // 2. Use StringBuilder for efficient string construction
        StringBuilder query = new StringBuilder("SELECT ")
                .append(select.trim())
                .append(" FROM ")
                .append(measurement.trim());
        // 3. Add WHERE clause if present
        if (where != null && !where.trim().isEmpty()) {
            query.append(" WHERE ").append(where.trim());
        }
        // 4. Add ORDER BY clause if present
        if (order != null && !order.trim().isEmpty()) {
            query.append(" ORDER BY ").append(order.trim());
        }
        // 5. last
        if (last != null && !last.trim().isEmpty()) {
            query.append(" ").append(last.trim());
        }
        // 5. Execute the query
        return exec(query.toString());
    }

    public QueryResult dropDatabase(String databaseName) {
        return exec("DROP DATABASE " + databaseName);
    }

    public QueryResult showDatabases() {
        return exec("SHOW DATABASES");
    }

    public QueryResult showRetentionPolicies() {
        return exec("SHOW RETENTION POLICIES");
    }

}
