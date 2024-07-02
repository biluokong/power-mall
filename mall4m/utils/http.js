var config = require("config.js");

//统一的网络请求方法
function request(params, isGetToken) {   
  // 全局变量
  var globalData = getApp().globalData;
  // 如果正在进行登陆，就将非登陆请求放在队列中等待登陆完毕后进行调用
  if (!isGetToken && globalData.isLanding) {   
    globalData.requestQueue.push(params);
    return;  
  }

  // 这是微信内置的发请求的组件 
  wx.request({
    url: config.domain + params.url, //接口请求地址
    data: params.data,  
    header: { 
      // 'content-type': params.method == "GET" ? 'application/x-www-form-urlencoded' : 'application/json;charset=utf-8',
      'Authorization': params.login ? '' : wx.getStorageSync('token'),
      'loginType':'memberLogin'   
    },  
 
    method: params.method == undefined ? "POST" : params.method,
    dataType: 'json',
    responseType: params.responseType == undefined ? 'text' : params.responseType,
    success: function(res) {         
      console.log(res); 
if(res.data.code == -1){   
  console.log(1212312313231);
  wx.showToast({
    title: res.data.msg, 
    icon: "none" 
  });
}else if (res.statusCode == 200) {  
      if(res.data.accessToken){
        params.callBack(res.data);
      }else{
        params.callBack(res.data.data);
      } 
      } else if (res.statusCode == 500) {
        wx.showToast({
          title: "服务器出了点小差", 
          icon: "none"
        });
      } else if (res.data.code == 401) {
        // 添加到请求队列
        globalData.requestQueue.push(params);
        // 是否正在登陆
        if (!globalData.isLanding) {
          globalData.isLanding = true
          //重新获取token,再次请求接口
          getToken();
        }
      } else if (res.statusCode == 400) {
        wx.showToast({
          title: res.data,
          icon: "none" 
        })

      } else {
        //如果有定义了params.errCallBack，则调用 params.errCallBack(res.data)
        if (params.errCallBack) {
          params.errCallBack(res);
        }
      }
      if (!globalData.isLanding) {
        wx.hideLoading();
      }
    },
    fail: function(err) {
      wx.hideLoading();
      wx.showToast({
        title: "服务器出了点小差",
        icon: "none"
      });
    }
  })
}
 
//通过code获取token,并保存到缓存
var getToken = function() {     
  wx.login({ // 1.微信小程序内置有一个login方法 拿到res.code
    success: res => {    
      // 发送 res.code 到后台换取 用户唯一 openId, sessionKey, unionId
      request({   
        login: true,    
        url: '/auth-server/doLogin?username=' + res.code +'&password=WECHAT', 
        // data: {         
        //   username: res.code,      
        //   password: 'WECHAT'     
        // },  
        callBack: result => {  
          // 获取更新用户信息   
            // updateUserInfo();  
          if (!result.accessToken) {  
              //如果没有access_token 
            wx.showModal({ 
              showCancel: false, 
              title: '提示',
              content: '未获取到身份信息，请重新登录'
            })
            getToken();
            wx.setStorageSync('token', '');  
          } else { 
            wx.setStorageSync('token', 'bearer ' + result.accessToken); //把token存入缓存，请求接口数据时要用
          }
          var globalData = getApp().globalData;
          globalData.isLanding = false;
          while (globalData.requestQueue.length) {
            request(globalData.requestQueue.pop());
          }
        }
      }, true)

    }
  })
}

// 更新用户头像昵称  wx.getUserInfo 获取到当前用户信息 调用你的接口把用户信息存起来
function updateUserInfo(userInfo) {   
  console.log(JSON.stringify(userInfo)+"----------")
      request({
        url: "/member-service/p/user/setUserInfo",
        method: "PUT", 
        data: {  
          pic: userInfo.avatarUrl,  
          nickName: userInfo.nickName,  
          sex:userInfo.gender 
        },
        // data:  JSON.stringify(userInfo)
  })
}

//获取购物车商品数量
function getCartCount() { 
  var params = {
    url: "/cart-service/p/shopCart/prodCount",
    method: "GET",
    data: {},
    callBack: function(res) { 
      if (res > 0) {
        wx.setTabBarBadge({
          index: 2,
          text: res + "",
        })
        var app = getApp();
        app.globalData.totalCartCount = res;
      } else {
        wx.removeTabBarBadge({
          index: 2
        })
        var app = getApp(); 
        app.globalData.totalCartCount = 0;
      }
    }
  };
  request(params);
}


exports.getToken = getToken;
exports.request = request;
exports.getCartCount = getCartCount;
exports.updateUserInfo = updateUserInfo;
