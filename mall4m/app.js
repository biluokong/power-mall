//app.js
var http = require("utils/http.js");
// 这个app.js 是整个微信小程序的入口
App({
  // 加载小程序的入口生命周期
  onLaunch: function () {
    // 一进来就去拿token
    http.getToken();
    wx.getSetting({
      success(res) { 
        console.log(res);
        if (!res.authSetting['scope.userInfo']) {
          wx.navigateTo({
            url: '/pages/login/login',
          })
        }
      }
    })
  },
  globalData: {
    // 定义全局请求队列
    requestQueue: [],
    // 是否正在进行登陆
    isLanding: true,
    // 购物车商品数量
    totalCartCount: 0
  }
})