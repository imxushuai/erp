package cn.itcast.erp.action;

import cn.itcast.erp.biz.IBaseBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.util.ViewUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用Action类
 * @author Administrator
 *
 * @param <T>
 */
public class BaseAction<T> {

	private IBaseBiz<T> baseBiz;

	public void setBaseBiz(IBaseBiz<T> baseBiz) {
		this.baseBiz = baseBiz;
	}

	//属性驱动:条件查询
	private T t1;
	private T t2;
	private Object param;
	public T getT2() {
		return t2;
	}
	public void setT2(T t2) {
		this.t2 = t2;
	}
	public Object getParam() {
		return param;
	}
	public void setParam(Object param) {
		this.param = param;
	}
	public T getT1() {
		return t1;
	}
	public void setT1(T t1) {
		this.t1 = t1;
	}

	private int page;//页码
	private int rows;//每页的记录数

	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	/**
	 * 条件查询
	 */
	public void list(){
		List<T> list = baseBiz.getList(t1,t2,param);
		//把部门列表转JSON字符串
		String listString = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
		ViewUtils.write(listString);
	}

	public void listByPage(){
		//System.out.println("页码：" + page + " 记录数:" + rows);
		int firstResult = (page -1) * rows;
		List<T> list = baseBiz.getListByPage(t1,t2,param,firstResult, rows);
		long total = baseBiz.getCount(t1,t2,param);
		//{total: total, rows:[]}
		Map<String, Object> mapData = new HashMap<String, Object>();
		mapData.put("total", total);
		mapData.put("rows", list);
		//把部门列表转JSON字符串
		//DisableCircularReferenceDetect禁用循环引用保护
		String listString = JSON.toJSONString(mapData, SerializerFeature.DisableCircularReferenceDetect);
		ViewUtils.write(listString);
	}

	/**新增，修改*/
	private T t;
	public T getT() {
		return t;
	}
	public void setT(T t) {
		this.t = t;
	}
	/**
	 * 新增
	 */
	public void add(){
		//{"success":true,"message":""}
		//返回前端的JSON数据
		Map<String, Object> rtn = new HashMap<String, Object>();
		try {
			baseBiz.add(t);
			rtn.put("success",true);
			rtn.put("message","新增成功");
		} catch (Exception e) {
			e.printStackTrace();
			rtn.put("success",false);
			rtn.put("message","新增失败");
		}
		ViewUtils.write(JSON.toJSONString(rtn));
	}

	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * 删除
	 */
	public void delete(){

		try {
			baseBiz.delete(id);
			ajaxReturn(true, "删除成功");
		} catch (Exception e) {
			if(e instanceof ErpException) {
				ajaxReturn(false, e.getMessage());
			}else{
				ajaxReturn(false, "删除失败");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通过编辑查询对象
	 */
	public void get(){
		T t = baseBiz.get(id);
		String jsonString = JSON.toJSONStringWithDateFormat(t,"yyyy-MM-dd");
		//System.out.println("转换前：" + jsonString);
		//{"name":"管理员组","tele":"000011","uuid":1}
		String jsonStringAfter = mapData(jsonString, "t");
		//System.out.println("转换后：" + jsonStringAfter);
		ViewUtils.write(jsonStringAfter);
	}

	/**
	 * 修改
	 */
	public void update(){
		try {
			baseBiz.update(t);
			ajaxReturn(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			ajaxReturn(false, "修改失败");
		}
	}

	/**
	 * //{"name":"管理员组","tele":"000011","uuid":1}
	 * @param jsonString JSON数据字符串
	 * @param prefix 要加上的前缀
	 * @return  {"t.name":"管理员组","t.tele":"000011","t.uuid":1}
	 */
	public String mapData(String jsonString, String prefix){
		Map<String, Object> map = JSON.parseObject(jsonString);

		//存储key加上前缀后的值
		Map<String, Object> dataMap = new HashMap<String, Object>();
		//给每key值加上前缀
		for(String key : map.keySet()){
			if(map.get(key) instanceof Map){
				//key值进行拼接
				Map<String,Object> m2 = (Map<String,Object>)map.get(key);
				for(String key2 : m2.keySet()){
					dataMap.put(prefix + "." + key + "." + key2, m2.get(key2));
				}
			}else{
				dataMap.put(prefix + "." + key, map.get(key));
			}
		}
		return JSON.toJSONString(dataMap);
	}

	/**
	 * 返回前端操作结果
	 * @param success
	 * @param message
	 */
	public void ajaxReturn(boolean success, String message){
		//返回前端的JSON数据
		Map<String, Object> rtn = new HashMap<String, Object>();
		rtn.put("success",success);
		rtn.put("message",message);
		ViewUtils.write(JSON.toJSONString(rtn));
	}


	/**
	 * 获取登陆的用户信息
	 * @return Emp 当前登录的用户
	 */
	public Emp getLoginUser(){
		//从shiro操作主题中获取当前登录用户
		Subject subject = SecurityUtils.getSubject();
		Emp emp = (Emp) subject.getPrincipal();
		return emp;
	}

}
