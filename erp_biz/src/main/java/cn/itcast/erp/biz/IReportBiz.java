package cn.itcast.erp.biz;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表业务接口
 * Author xushuai
 * Description
 */
public interface IReportBiz {

    /**
     * 销售额统计报表(按商品类型)
     *
     * @return java.util.List
     */
    List ordersReport(Date start, Date end);
    
    /**
     * 销售额统计报表(按年份)
     * 
     * @param year 年份
     * @return java.util.List 
     */
    List<Map<String, Object>> trendReport(int year);
}

