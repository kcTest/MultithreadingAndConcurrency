package com.zkc.petStore.goods;

import com.zkc.util.RandomUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class Goods implements IGoods {
	
	/**
	 * protected 子类直接使用
	 */
	protected float price;
	protected int id;
	protected String goodName;
	protected int amount;
	protected IGoods.Type goodType;
	
	private static int goodNo;
	
	protected Goods() {
		this.id = ++goodNo;
		this.amount = 0;
		this.price = 0;
		this.goodName = "未知商品";
	}
	
	public static IGoods produceOne() {
		Type type = Type.randType();
		return produceByType(type);
	}
	
	private static IGoods produceByType(Type type) {
		switch (type) {
			case PET:
				return new GoodsPet();
			case CLOTHES:
				return new GoodsClothes();
			case FOOD:
				return new GoodsFood();
			default:
				return new Goods();
		}
	}
	
	@Override
	public String toString() {
		return String.format("商品{ID=%s,名称=%s,价格=%.2f}", getId(), getName(), getPrice());
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void setPrice(float price) {
		this.price = price;
	}
	
	@Override
	public float getPrice() {
		return price;
	}
	
	@Override
	public String getName() {
		return this.goodName;
	}
	
	@Override
	public int getAmount() {
		return this.amount;
	}
	
	@Override
	public Type getType() {
		return this.goodType;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		
		Goods goods = (Goods) o;
		return id == goods.id;
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
	
	@Override
	public int compareTo(IGoods o) {
		if (o == null) {
			throw new NullPointerException("Comparing object is null");
		}
		return this.id - o.getId();
	}
	
	
	private static class GoodsPet extends Goods {
		private static final AtomicInteger PET_NO = new AtomicInteger(0);
		
		public GoodsPet() {
			super();
			this.goodType = Type.PET;
			this.goodName = "宠物" + PET_NO.incrementAndGet();
			price = RandomUtil.randomInRange(1000, 10000);
			amount = RandomUtil.random(5);
		}
	}
	
	private static class GoodsClothes extends Goods {
		private static final AtomicInteger CLOTHES_NO = new AtomicInteger(0);
		
		public GoodsClothes() {
			super();
			this.goodType = Type.CLOTHES;
			this.goodName = "宠物衣服" + CLOTHES_NO.incrementAndGet();
			price = RandomUtil.randomInRange(50, 100);
			amount = RandomUtil.random(5);
		}
	}
	
	private static class GoodsFood extends Goods {
		private static final AtomicInteger FOOD_NO = new AtomicInteger(0);
		
		public GoodsFood() {
			super();
			this.goodType = Type.CLOTHES;
			this.goodName = "宠物粮食" + FOOD_NO.incrementAndGet();
			price = RandomUtil.randomInRange(40, 120);
			amount = RandomUtil.random(5);
		}
	}
}
