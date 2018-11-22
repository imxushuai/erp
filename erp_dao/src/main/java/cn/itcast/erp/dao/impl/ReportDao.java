package cn.itcast.erp.dao.impl;

import cn.itcast.erp.dao.IReportDao;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ---
 * Author xushuai
 * Description
 */
public class ReportDao extends HibernateDaoSupport implements IReportDao {

    @Override
    public List ordersReport(Date start, Date end) {
        String hql = "SELECT new Map(gt.name as name,SUM(ol.money) as y) " +
                "FROM Goodstype gt, Goods gs, Orderdetail ol, Orders o " +
                "WHERE gs.goodstype = gt AND ol.orders=o AND gs.uuid = ol.goodsuuid AND o.type='2' ";
        List<Date> dateList = new ArrayList<>();
        if (null != start) {
            hql += "AND o.createtime >= ? ";
            dateList.add(start);
        }
        if (null != end) {
            hql += "AND o.createtime <= ? ";
            dateList.add(end);
        }
        hql += "GROUP BY gt.name";
        return getHibernateTemplate().find(hql, dateList.toArray());
    }

    @Override
    public List<Map<String, Object>> trendReport(int year) {
        String hql = "SELECT new Map(MONTH(o.createtime) as name,SUM(ol.money) as y) " +
                "FROM Orders o, Orderdetail ol " +
                "WHERE ol.orders = o " +
                "AND o.type = '2' AND YEAR(o.createtime) = ? " +
                "GROUP BY MONTH(o.createtime)";

        return (List<Map<String, Object>>) getHibernateTemplate().find(hql, year);
    }
}

