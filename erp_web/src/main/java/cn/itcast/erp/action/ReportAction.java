package cn.itcast.erp.action;

import cn.itcast.erp.biz.IReportBiz;
import cn.itcast.erp.util.ViewUtils;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表 Action
 * Author xushuai
 * Description
 */
public class ReportAction {

    private IReportBiz reportBiz;

    /** 开始日期 */
    private Date startDate;
    /** 截止日期 */
    private Date endDate;

    /** 年份 */
    private int year;

    /**
     * 生成销售额统计(按商品类型)
     */
    public void ordersReport() {
        List list = reportBiz.ordersReport(startDate, endDate);
        ViewUtils.write(JSON.toJSONString(list));
    }

    /**
     * 生成销售额统计(按年份)
     */
    public void trendReport() {
        List<Map<String, Object>> list = reportBiz.trendReport(year);
        ViewUtils.write(JSON.toJSONString(list));
    }



    /** 以下为 get、set 方法 */
    public void setReportBiz(IReportBiz reportBiz) {
        this.reportBiz = reportBiz;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public IReportBiz getReportBiz() {
        return reportBiz;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}

