// pages/binding-phone/binding-phone.js

var http = require("../../utils/http.js");
var config = require("../../utils/config.js");

Page({ 

  /**
   * 页面的初始数据
   */
  data: {
    phonenum:'',
    code:'',
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },

  getCodeNumber:function(){
    if (this.data.phonenum == "" ) {
      wx.showToast({
        title: '请输入手机号',
        icon: "none"
      })
      return;
    }
    var params = {
      url: "/member-service/p/sms/send",
      method: "POST",
      data: {
        phonenum: this.data.phonenum,
        code: this.data.code
      },
      callBack: (res) => {
        wx.showToast({
          title: res,
          icon: "none"
        });

        this.setData({
          phonenum: this.data.phonenum,
          code: this.data.code
        });
      }
    };
    http.request(params);
  },
  // 保存手机号的方法
  saveCodeNumber:function(){
    if (this.data.phonenum == "" ) {
      wx.showToast({
        title: '请输入手机号',
        icon: "none"
      })
      return;
    }

    if (this.data.code == "" ) {
      wx.showToast({
        title: '请输入验证码',
        icon: "none"
      })
      return;
    }

    var params = {
      url: "/member-service/p/sms/savePhone",
      method: "POST",
      data: {
        phonenum: this.data.phonenum,
        code: this.data.code
      },
      callBack: (res) => {
// 如果调用保存的方法成功 返回到 个人主页
  wx.showToast({
    title: res,
    icon: "none"
  });
  wx.navigateBack({
    delta: 1
  })

      }
    };
    http.request(params);
  },

  onPhoneInput:function(e){
    this.setData({
      phonenum: e.detail.value
    });
  },
  onCodeInput: function (e) {
    this.setData({
      code: e.detail.value
    });
  }
})
