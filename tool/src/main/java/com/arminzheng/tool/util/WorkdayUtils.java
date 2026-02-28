package com.arminzheng.tool.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 节假日/工作日 工具类 (节假日信息应在前一年进行维护)
 */
@Slf4j
public class WorkdayUtils {
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    static Map<String, List<String>> holiday = new HashMap<>();       // 假期
    static Map<String, List<String>> extraWorkDay = new HashMap<>();  // 调休日
    static Map<String, String> calenderMap = new HashMap<>();         // 所有日期

    /**
     * 判断是否是节假日
     *
     * @param time 日期参数 格式 'yyyy-MM-dd', 不传参默认当前日期
     * @return 0上班 1周末 2节假日
     */
    public static String isWorkingDayBaseDB(String... time) {
        String calcTime;
        if (time == null || time.length == 0 || time[0] == null || time[0].isEmpty()) {
            calcTime = LocalDate.now().format(formatter);
        }
        else {
            calcTime = time[0];
        }
        if (calenderMap.isEmpty()) {
            synchronized (WorkdayUtils.class) {
                if (calenderMap.isEmpty()) {
                    // dummy code 应该调整为 api 方式调用, 而不是额外加 orm 依赖
                    GeneralDAO dao = new GeneralDAO();
                    String sql = "select date_type,fiscal_date from T_WORKDAY t where fiscal_year = ?";
                    List<Map> bySql = dao.findBySql(sql, new Object[]{calcTime.substring(0, 4)});
                    Map<String, String> result = bySql.stream().collect(Collectors.toMap(map -> String.valueOf(map.get("fiscal_date")), map -> String.valueOf(map.get("date_type"))));
                    if (result.isEmpty()) {
                        throw new RuntimeException("获取当前年度日历失败！");
                    }
                    else {
                        calenderMap.putAll(result);
                    }
                }
            }
        }
        return calenderMap.get(calcTime);
    }

    @Component("generalDAO")
    public static class GeneralDAO {
        /**
         * <pre>
         * create table T_WORKDAY (
         *   ID NUMBER(6)    not null,
         *   FISCAL_YEAR NUMBER(4)    not null,
         *   FISCAL_DATE  VARCHAR2(20),
         *   DATE_MONTH  VARCHAR2(20) not null,
         *   DATE_DAY    VARCHAR2(2)  not null,
         *   WEEK        NUMBER,
         *   DATE_TYPE   NUMBER,
         *   LAST_VER    NUMBER(18)
         * )</pre>
         */
        public List findBySql(final String sql, final Object[] params) {
            return null;
        }
    }
}
