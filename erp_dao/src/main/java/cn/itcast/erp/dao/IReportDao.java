package cn.itcast.erp.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 相关统计报表Dao
 * Author xushuai
 * Description
 */
public interface IReportDao {

    /**
     * 获取销售额报表需要的数据
     *
     * @param start 开始日期
     * @param end   截止日期
     * @return java.util.List
     */
    List ordersReport(Date start, Date end);

    /**
     * 获取某年销售额数据
     *
     * @param year 年份
     * @return java.util.List
     */
    List<Map<String, Object>> trendReport(int year);
}
