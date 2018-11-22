package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IGoodstypeBiz;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IGoodstypeDao;
import cn.itcast.erp.entity.Dep;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Goods;
import cn.itcast.erp.entity.Goodstype;
import cn.itcast.erp.exception.ErpException;

import java.util.List;

/**
 * 商品分类业务逻辑类
 * @author Administrator
 *
 */
public class GoodstypeBiz extends BaseBiz<Goodstype> implements IGoodstypeBiz {

	private IGoodstypeDao goodstypeDao;
	public void setGoodstypeDao(IGoodstypeDao goodstypeDao) {
		this.goodstypeDao = goodstypeDao;
		super.setBaseDao(this.goodstypeDao);
	}

	private IGoodsDao goodsDao;
	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	@Override
	public void delete(Long uuid) {
		//封装Goodstype和Goods
		Goods goods = new Goods();
		Goodstype goodstype = new Goodstype();
		goodstype.setUuid(uuid);
		goods.setGoodstype(goodstype);
		//查询该商品类型下的商品
		List<Goods> list = goodsDao.getList(goods, null, null);
		//判断该商品类型下是否存在商品
		if(null == list || list.size() < 1) {
			goodstypeDao.delete(uuid);
		}else{
			throw new ErpException("该商品类型下存在商品，不能删除");
		}
	}
}
