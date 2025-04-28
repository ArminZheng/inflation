package com.arminzheng.inflation.influx;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.concurrent.NotThreadSafe;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

@NotThreadSafe
public class TestInfluxUtils {

    private final InfluxDB influxDB;

    public TestInfluxUtils(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }

    public QueryResult exec(String sql) {
        QueryResult result = influxDB.query(new Query(sql));
        return result;
    }

    public void execD(String sql) {
        QueryResult result = exec(sql);
        display(result);
    }

    public void use(String databaseName) {
        influxDB.setDatabase(databaseName);
    }

    public QueryResult createDatabase(String databaseName) {
        return exec("CREATE DATABASE " + databaseName);
    }

    public void createDatabaseD(String databaseName) {
        QueryResult database = createDatabase(databaseName);
        display(database);
    }

    @Deprecated
    public QueryResult insertWrong(String retentionPolicy, String measurementTag, String field) {
        // query not support INSERT statement
        return exec("INSERT " + (retentionPolicy == null ? "" : retentionPolicy) + " "
                + measurementTag + " " + field);
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

    public void insertD(final String measurement, final Map<String, String> tag,
            final Map<String, Object> fields) {
        insert(measurement, tag, fields);
        selectD(measurement);
    }

    private void display(QueryResult result) {
        result.getResults().forEach(r -> {
            if (r.getSeries() != null) {
                r.getSeries().forEach(series -> {
                    // 计算每列的最大宽度
                    List<Integer> columnWidths = new ArrayList<>();
                    List<String> columns = series.getColumns();
                    // 初始化宽度为列名长度
                    for (String col : columns) {
                        columnWidths.add(col.length());
                    }
                    // 检查所有值的长度
                    for (List<Object> values : series.getValues()) {
                        for (int i = 0; i < values.size(); i++) {
                            String value = formatValue(values.get(i));
                            columnWidths.set(i, Math.max(columnWidths.get(i), value.length()));
                        }
                    }
                    // 创建分隔线
                    String separator = createSeparator(columnWidths);
                    // 打印表头
                    System.out.println(separator);
                    printRow(columns, columnWidths);
                    System.out.println(separator);
                    // 打印数据行
                    for (List<Object> values : series.getValues()) {
                        List<String> formattedValues = values.stream()
                                .map(this::formatValue)
                                .collect(Collectors.toList());
                        printRow(formattedValues, columnWidths);
                    }
                    System.out.println(separator);
                });
            }
            if (r.getError() != null) {
                System.err.println("Error: " + r.getError());
            }
        });
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

    public void selectD(String measurement) {
        selectD(null, measurement, null);
    }

    public void selectD(String select, String measurement, String where) {
        selectD(select, measurement, where, null, null);
    }

    public void selectD(String select, String measurement, String where, String order,
            String last) {
        QueryResult result = select(select, measurement, where, order, last);
        display(result);
    }

    public QueryResult dropDatabase(String databaseName) {
        return exec("DROP DATABASE " + databaseName);
    }

    public void dropDatabaseD(String databaseName) {
        QueryResult result = dropDatabase(databaseName);
        display(result);
    }

    public QueryResult showDatabases() {
        return exec("SHOW DATABASES");
    }

    public void showDatabasesD() {
        QueryResult result = showDatabases();
        display(result);
    }

    public QueryResult showRetentionPolicies() {
        return exec("SHOW RETENTION POLICIES");
    }

    public void showRetentionPoliciesD() {
        QueryResult result = showRetentionPolicies();
        display(result);
    }

    // 格式化值的辅助方法
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Instant || value.toString().contains("T")) {
            // 格式化时间
            String format;
            try {
                ZonedDateTime time = Instant.parse(value.toString()).atZone(ZoneId.systemDefault());
                format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(time);
            } catch (Exception e) {
                format = value.toString();
            }
            return format;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    // 创建分隔线
    private String createSeparator(List<Integer> widths) {
        return "+-" + widths.stream()
                .map(w -> "-".repeat(w))
                .collect(Collectors.joining("-+-")) + "-+";
    }

    // 打印数据行
    private void printRow(List<?> values, List<Integer> widths) {
        StringBuilder row = new StringBuilder("| ");
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i).toString();
            row.append(String.format("%-" + widths.get(i) + "s", value));
            row.append(" | ");
        }
        System.out.println(row);
    }

}
