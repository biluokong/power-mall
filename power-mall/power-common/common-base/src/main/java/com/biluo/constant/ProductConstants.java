package com.biluo.constant;

/**
 * 产品业务模块常量类
 */
public interface ProductConstants {
	/**
	 * key前缀
	 */
	String PRODUCT_KEY_PREFIX = "mall:product";

	/**
	 * 商品分类缓存key
	 */
	String ALL_CATEGORY_KEY = "'categories'";

	/**
	 * 一级类目缓存key
	 */
	String FIRST_CATEGORY_KEY = "'firstCategories'";

	/**
	 * 状态正常的商品分组标签缓存key
	 */
	String ALL_NORMAL_TAG_KEY = "'tags'";

	/**
	 * 商品规格名集合缓存key
	 */
	String ALL_SPEC_NAME_KEY = "'specs'";

	/**
	 * 小程序商品分组标签缓存key
	 */
	String WX_PROD_TAG = "'wxProds'";

	/**
	 * 小程序一级类目缓存key
	 */
	String WX_FIRST_CATEGORY = "'wxFirstCategories'";
}
