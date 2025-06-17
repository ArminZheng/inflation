package com.arminzheng.inflation.influx;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestInflux {

    private InfluxDB influxDB;
    private TestInfluxUtils influx;
    private static final String DATABASE = "history";

    @BeforeEach
    public void before() {
        final String serverURL = "http://127.0.0.1:8086", username = "root", password = "root";
        influxDB = InfluxDBFactory.connect(serverURL, username, password);
        influx = new TestInfluxUtils(influxDB);
    }

    @Test
    void testRetentionPolicy() {
        influx.createDatabaseD(DATABASE);
        influx.use(DATABASE);
        // 168h is 7 days
        influx.execD("create retention policy history_policy on history duration 168h replication 1 shard duration 168h default");
        influx.showRetentionPoliciesD();
        influx.execD("alter retention policy autogen on history default");
        influx.showRetentionPoliciesD();
        influx.execD("drop retention policy history_policy on history");
        influx.showRetentionPoliciesD();
    }

    @Test
    public void testAdvanceQuery() {
        influx.use(DATABASE);
        // select *::field from measurement
        influx.execD("select * from cpu");
        influx.execD("select *::field from cpu");
        // select tag1,tag2,field1 from measurement // at least one field is needed. just tag is not enough
        // like: select * from measurement person_name=~/hello$/
        influx.execD("select * from cpu where temp=~/23/");
        // in: select * from measurement person_name =~/^hello$|^world$/
        influx.execD("select * from cpu where host=~/^server01$/^server02$/");
    }

    @Test
    public void testIIII() {
        println(new Date().getTime());
        println(System.currentTimeMillis());
        println(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        println(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        // 格式化
        LocalDateTime now = LocalDateTime.now();
        println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(now));
        // 解析
        LocalDateTime parse = LocalDateTime.parse("2023-12-31 10:15:30",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        println(parse); // LocalDateTime
        // 带时区处理
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        // println(string)
        println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z").format(zonedDateTime));
        println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS VV").format(zonedDateTime));
    }

    @Test
    public void testMeasurement() {
        TestInfluxUtils influx = new TestInfluxUtils(influxDB);
        influx.showDatabasesD();
        influx.dropDatabaseD(DATABASE);
        influx.createDatabaseD(DATABASE);
        influx.use(DATABASE);
        boolean b = influxDB.databaseExists(DATABASE);
        println("is exist? = " + b);
        Map<String, String> tag1 = Map.of("host", "server01", "region", "us-west");
        Map<String, Object> field1 = Map.of("cpu_usage", 0.64, "temp", 23.1);
        influx.insertD("cpu", tag1, field1);
        Map<String, String> tag2 = Map.of("host", "server02");
        Map<String, Object> field2 = Map.of("cpu_usage", 0.641434055562000000001);
        influx.insertD("cpu", tag2, field2);
        // SELECT * FROM cpu WHERE host='server01'
        influx.selectD("*", "cpu", "host='server01'");
        // SELECT * FROM cpu WHERE time > now() - 1h
        influx.selectD("*", "cpu", "time > now() - 1h");
        // SELECT mean(cpu_usage) FROM cpu GROUP BY time(5m)
        influx.selectD("mean(cpu_usage)", "cpu", "time > now() - 1h");
        // SELECT * FROM cpu ORDER BY time DESC LIMIT 1
        influx.selectD("*", "cpu", null, "time DESC", "LIMIT 1");
        influx.execD("SHOW MEASUREMENTS"); // cpu
        // -- 查看特定measurement的字段
        // SHOW FIELD KEYS FROM cpu
        influx.execD("SHOW FIELD KEYS FROM cpu");  // fieldKey  | fieldType
        // -- 查看标签
        // SHOW TAG KEYS FROM cpu
        influx.execD("SHOW TAG KEYS FROM cpu"); // tagKey
        // -- 查看标签值
        // SHOW TAG VALUES FROM cpu WITH KEY = "host"
        influx.execD("SHOW TAG VALUES FROM cpu WITH KEY = \"host\""); // key | value
        // -- 删除数据 (influxdb 没有单行删除, 但可以按照 tag 进行删除)
        // DELETE FROM cpu WHERE host='server01'
        // influx.execD("DELETE FROM cpu WHERE host='server01'");
    }

    @Test
    public void testII() {
        // final String serverURL = "http://127.0.0.1:8086", username = "root", password = "root";
        // final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);
        QueryResult showDatabases = influxDB.query(new Query("SHOW DATABASES"));
        println(showDatabases);
        influxDB.query(new Query("CREATE DATABASE " + DATABASE));
        influxDB.setDatabase(DATABASE);
        boolean b = influxDB.databaseExists(DATABASE);
        println("is exist? = " + b);
        showDatabases = influxDB.query(new Query("SHOW DATABASES"));
        showDatabases.getResults().forEach(result -> {
            result.getSeries().forEach(series -> {
                series.getValues().forEach(value -> {
                    System.out.println(value);
                });
            });
        });
        QueryResult dropQuery = influxDB.query(new Query("DROP DATABASE " + DATABASE));
        println(dropQuery);
        b = influxDB.databaseExists(DATABASE);
        println("is exist? = " + b);
    }

    @Test
    public void testI() throws InterruptedException {
        // Create an object to handle the communication with InfluxDB.
        // (best practice tip: reuse the 'influxDB' instance when possible)
        final String serverURL = "http://127.0.0.1:8086", username = "root", password = "root";
        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);

        // Create a database...
        // https://docs.influxdata.com/influxdb/v1.7/query_language/database_management/
        String databaseName = DATABASE;
        influxDB.query(new Query("CREATE DATABASE " + databaseName));
        influxDB.setDatabase(databaseName);

        // ... and a retention policy, if necessary.
        // https://docs.influxdata.com/influxdb/v1.7/query_language/database_management/
        String retentionPolicyName = "one_day_only";
        influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicyName
                + " ON " + databaseName + " DURATION 1d REPLICATION 1 DEFAULT"));
        influxDB.setRetentionPolicy(retentionPolicyName);

        // Enable batch writes to get better performance.
        influxDB.enableBatch(
                BatchOptions.DEFAULTS
                        .threadFactory(runnable -> {
                            Thread thread = new Thread(runnable);
                            thread.setDaemon(true);
                            return thread;
                        })
        );

        // Close it if your application is terminating, or you are not using it anymore.
        Runtime.getRuntime().addShutdownHook(new Thread(influxDB::close));

        // Write points to InfluxDB.
        influxDB.write(Point.measurement("h2o_feet")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("location", "santa_monica")
                .addField("level description", "below 3 feet")
                .addField("water_level", 2.064d)
                .build());

        influxDB.write(Point.measurement("h2o_feet")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("location", "coyote_creek")
                .addField("level description", "between 6 and 9 feet")
                .addField("water_level", 8.12d)
                .build());

        // Wait a few seconds in order to let the InfluxDB client
        // write your points asynchronously (note: you can adjust the
        // internal time interval if you need via 'enableBatch' call).
        Thread.sleep(5_000L);

        // Query your data using InfluxQL.
        // https://docs.influxdata.com/influxdb/v1.7/query_language/data_exploration/#the-basic-select-statement
        QueryResult queryResult = influxDB.query(new Query("SELECT * FROM h2o_feet"));

        System.out.println(queryResult);
        // It will print something like:
        // QueryResult [results=[Result [series=[Series [name=h2o_feet, tags=null,
        //      columns=[time, level description, location, water_level],
        //      values=[
        //         [2020-03-22T20:50:12.929Z, below 3 feet, santa_monica, 2.064],
        //         [2020-03-22T20:50:12.929Z, between 6 and 9 feet, coyote_creek, 8.12]
        //      ]]], error=null]], error=null]
    }

    @AfterEach
    public void after() {
        influxDB.close();
    }

    private void println(Object str) {
        System.out.println(str);
    }
}
