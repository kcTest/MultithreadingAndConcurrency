package com.zkc.petStore.goods;

import com.zkc.util.RandomUtil;

public interface IGoods extends Comparable<IGoods> {
	
	enum Type {
		/**
		 * 商品类型
		 */
		PET,
		FOOD,
		CLOTHES;
		
		/**
		 * 随机获取一种类型的商品
		 */
		public static Type randType() {
			int length = values().length;
			int typeNo = RandomUtil.random(length) - 1;
			return values()[typeNo];
		}
	}
	
	/**
	 * 设置商品ID
	 */
	void setId(int id);
	
	/**
	 * 获取商品ID
	 *
	 * @return ID
	 */
	int getId();
	
	/**
	 * 获取商品价格
	 */
	void setPrice(float price);
	
	/**
	 * 获取商品价格
	 *
	 * @return 商品价格
	 */
	float getPrice();
	
	/**
	 * 获取商品名称
	 *
	 * @return 商品名称
	 */
	String getName();
	
	/**
	 * 获取商品数量
	 *
	 * @return 商品数量
	 */
	int getAmount();
	
	/**
	 * 获取商品类型
	 *
	 * @return 类型
	 */
	Type getType();
}
