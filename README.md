本商城系统分为后台管理系统和微信小程序两个终端，后台管理系统主要是对商城业务的支撑，核心功能包括系统管理、产品管理、类目管理、商品属性管理、商品分组标签管理、公告管理、轮播图管理、地址管理。微信小程序端的核心功能包括首页展示、商品分类展示、商品详情页面、购物车页面。

后台管理系统前端使用Vue、ElementUI和移动端使用微信小程序完成页面的展示，后端使用Spring Cloud Alibaba分布式框架进行整合开发，同时使用Redis进行缓存管理、Spring Security进行安全管理。

当前只实现了视频中讲解的功能。

- 前端项目：`mall4v`
- 后端项目：`power-all`
- 小程序端：`mall4m`



**node.js版本：16.15.0**，可以使用nvm工具切换到该版本后运行，否则可能会运行失败。

~~~bash
# 如果没有下载nodejs-16.15.0
nvm install 16.15.0
nvm use 16.15.0
# 设置淘宝npm源
npm config set registry https://registry.npmmirror.com/
# 下载依赖和运行项目（需要在mall4v文件夹下）
npm install
npm run dev
~~~



gitee地址：https://gitee.com/biluoer/project

