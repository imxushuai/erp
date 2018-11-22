package cn.itcast.erp.entity;
/**
 * 仓库操作记录实体类
 * @author Administrator *
 */
public class Storeoper {

	/** 入库 */
	public static final String TYPE_IN = "0";
	/** 出库 */
	public static final String TYPE_OUT = "1";

    /** 盘盈 */
    public static final String TYPE_INVENTORY_PROFIT = "2";
    /** 盘亏 */
    public static final String TYPE_INVENTORY_LOSS = "3";

    private Long uuid;//编号
    private Long empuuid;//操作员工编号
    private java.util.Date opertime;//操作日期
    private Long storeuuid;//仓库编号
    private Long goodsuuid;//商品编号
    private Long num;//数量
    private String type;//1：入库 2：出库 3：盘盈 4：盘亏
    private String empName;//操作员名称
    private String storeName;//仓库名称
    private String goodsName;//商品名称

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public Long getEmpuuid() {
        return empuuid;
    }

    public void setEmpuuid(Long empuuid) {
        this.empuuid = empuuid;
    }

    public java.util.Date getOpertime() {
        return opertime;
    }

    public void setOpertime(java.util.Date opertime) {
        this.opertime = opertime;
    }

    public Long getStoreuuid() {
        return storeuuid;
    }

    public void setStoreuuid(Long storeuuid) {
        this.storeuuid = storeuuid;
    }

    public Long getGoodsuuid() {
        return goodsuuid;
    }

    public void setGoodsuuid(Long goodsuuid) {
        this.goodsuuid = goodsuuid;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
