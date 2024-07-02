package com.biluo.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 业务响应状态码枚举
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum BusinessEnum {
	OPERATION_SUCCESS(0, "操作成功"),
	OPERATION_FALL(-1, "操作失败"),
	SERVER_INNER_ERROR(9999, "服务器内部错误"),
	UN_AUTHORIZATION(401, "未授权"),
	ACCESS_DENY_FAIL(403, "权限不足"),

	ADD_SYS_USER_FAIL(-1, "新增管理员失败"),
	MODIFY_SYS_USER_FAIL(-1, "修改管理员失败"),
	REMOVE_SYS_USER_FAIL(-1, "删除管理员失败"),

	ADD_SYS_ROLE_FAIL(-1, "新增角色失败"),
	MODIFY_SYS_ROLE_FAIL(-1, "修改角色失败"),
	REMOVE_SYS_ROLE_FAIL(-1, "删除角色失败"),

	ADD_SYS_MENU_FAIL(-1, "新增菜单失败"),
	MODIFY_SYS_MENU_FAIL(-1, "修改菜单失败"),
	REMOVE_SYS_MENU_FAIL(-1, "删除菜单失败"),
	REMOVE_SYS_MENU_BY_CHILD_FAIL(-1, "当前菜单节点包含子节点集合，不可删除"),

	ADD_PRODUCT_CATEGORY_FAIL(-1, "新增商品分类失败"),
	MODIFY_PRODUCT_CATEGORY_FAIL(-1, "修改商品分类失败"),
	MODIFY_PRODUCT_CATEGORY_BY_CHILD_FAIL(-1, "当前类目包含子类目，不可修改"),
	REMOVE_PRODUCT_CATEGORY_FAIL(-1, "删除商品分类失败"),
	REMOVE_PRODUCT_CATEGORY_BY_CHILD_FAIL(-1, "当前类目包含子类目，不可删除"),

	ADD_PRODUCT_TAG_FAIL(-1, "新增商品分组失败"),
	MODIFY_PRODUCT_TAG_FAIL(-1, "修改商品分组失败"),
	REMOVE_PRODUCT_TAG_FAIL(-1, "删除商品分组失败"),

	ADD_PRODUCT_SPEC_FAIL(-1, "新增商品规格失败"),
	MODIFY_PRODUCT_SPEC_FAIL(-1, "修改商品规格失败"),
	REMOVE_PRODUCT_SPEC_FAIL(-1, "删除商品规格失败"),

	MODIFY_PRODUCT_COMMENT_FAIL(-1, "回复和审核评论失败"),

	ADD_PRODUCT_FAIL(-1, "新增商品失败"),
	MODIFY_PRODUCT_FAIL(-1, "修改商品失败"),
	REMOVE_PRODUCT_FAIL(-1, "删除商品失败"),

	ADD_NOTIcE_FAIL(-1, "新增公告失败"),
	MODIFY_NOTICE_FAIL(-1, "修改公告失败"),
	REMOVE_NOTICE_FAIL(-1, "删除公告失败"),

	QUERY_FEIGN_PRODUCT_FAIL(-1, "Feign查询商品失败"),
	ADD_INDEX_IMG_FAIL(-1, "新增轮播图失败"),
	MODIFY_INDEX_IMG_FAIL(-1, "修改轮播图失败"),
	REMOVE_INDEX_IMG_FAIL(-1, "删除轮播图失败"),

	MODIFY_MEMBER_STATUS_FAIL(-1, "修改会员状态失败"),
	REMOVE_MEMBER_FAIL(-1, "删除会员失败"),

	QUERY_FEIGN_ADDRESS_FAIL(-1, "Feign查询收货地址失败"),
	QUERY_FEIGN_MEMBER_NICKNAME_FAIL(-1, "Feign获取会员昵称失败"),

	MODIFY_WX_USER_INFO_FAIL(-1, "更新微信用户名和头像失败"),
	BINDING_PHONE_FAIL(-1, "绑定手机号码失败"),
	PHONE_CODE_ERROR(-1, "手机验证码错误"),

	QUERY_ORDER_FAIL(-1, "查询订单信息失败"),
	MODIFY_ORDER_STATUS_FAIL(-1, "修改订单状态失败"),
	REMOVE_ORDER_FAIL(-1, "删除订单失败"),

	QUERY_FEIGN_PROD_LIST_FAIL(-1, "Feign获取商品集合失败"),
	ADD_MEMBER_ADDR_FAIL(-1, "新增收货地址失败"),
	MODIFY_MEMBER_ADDR_FAIL(-1, "修改收货地址失败"),
	REMOVE_MEMBER_ADDR_FAIL(-1, "删除收货地址失败"),
	MODIFY_MEMBER_DEFAULT_ADDR_FAIL(-1, "修改默认收货地址失败"),

	QUERY_FEIGN_PROD_TAG_LIST_FAIL(-1, "Feign获取商品与分组标签的关系失败"),
	QUERY_FEIGN_CATEGORY_LIST_FAIL(-1, "Feign获取分类信息集合失败"),
	QUERY_FEIGN_MEMBER_LIST_FAIL(-1, "Feign获取会员信息列表失败"),

	MODIFY_CART_NUM_FAIL(-1, "修改购物车商品数量失败"),
	REMOVE_CART_PROD_FAIL(-1, "移除购物车商品失败"),
	QUERY_FEIGN_SKU_LIST_FAIL(-1, "Feign获取sku信息集合失败"),
	QUERY_FEIGN_MEMBER_DEFAULT_ADDR_FAIL(-1, "Feign获取会员默认地址失败"),
	QUERY_FEIGN_SKU_FAIL(-1, "Feign获取sku信息失败"),
	QUERY_FEIGN_BASKET_LIST_FAIL(-1, "Feign获取购物车数据集合失败"),
	MODIFY_FEIGN_STOCK_FAIL(-1, "Feign修改库存失败"),

	MODIFY_PROP_STOCK_FAIL(-1, "修改商品库存失败"),
	MODIFY_SKU_STOCK_FAIL(-1, "修改sku库存失败"),
	ADD_ORDER_ITEM_FAIL(-1, "添加订单商品条目数据失败"),
	ADD_ORDER_FAIL(-1, "添加订单数据失败");
	private int code;
	private String msg;
}
