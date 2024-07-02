// pages/search-prod-show/search-prod-show.js

var http = require('../../utils/http.js');
 var WxParse = require('../../wxParse/wxParse.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    sts: 0,
    showType:2,
    searchProdList:[],
    sprodName:"",
    current:1
  },

  changeShowType:function(){
    var showType = this.data.showType;
    if (showType==1){
      showType=2;
    }else{
      showType = 1;
    }
    this.setData({
      showType: showType
    });
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log(111);
    console.log(options);
    let tName = options.prodName;
    this.setData({
      sprodName: tName
      
    });

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  //输入商品获取数据
  getSearchContent: function (e) {
    console.log(222);
    console.log(e);
    this.setData({
      sprodName: e.detail.value
    });
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    console.log(333);
    this.toLoadData();
  },

//请求商品接口
  toLoadData:function(){
    var ths = this;
    //热门搜索
    var params = {
      url: "/search-service/search/searchProdPage",
      method: "GET",
      data: {
        current: this.data.current,
        size: 10,
        prodName: this.data.sprodName,
        sort: this.data.sts
      },
      callBack: function (res) {
        // res.records.forEach(element => {
        //   let myName = element.prodName;
        //   WxParse.wxParse('prodName', 'html', myName, ths,5);
        // }); 
        let help_list = res.records;
        for (let i = 0; i < help_list.length; i++) {
          WxParse.wxParse('infodetail' + i, 'html', help_list[i].prodName, ths);
          if (i === help_list.length - 1) {
            WxParse.wxParseTemArray("infodetailArr",'infodetail', help_list.length, ths)
          }
        }
        console.log(ths.data.infodetailArr);
        ths.setData({
          searchProdList: help_list,
        });
      },
    };
    http.request(params);
  },

//当前搜索页二次搜索商品
  toSearchConfirm:function(){
    this.toLoadData();
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
    this.data.current = this.data.current-1;
    this.toLoadData();

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    this.data.current = this.data.current+1;
    this.toLoadData();
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },

    /**
   * 状态点击事件
   */
  onStsTap: function(e) {
    var sts = e.currentTarget.dataset.sts;
    this.setData({
      sts: sts
    });
    this.toLoadData();
  },

  toProdPage: function (e) {
    var prodid = e.currentTarget.dataset.prodid;
    wx.navigateTo({
      url: '/pages/prod/prod?prodid=' + prodid,
    })
  },
})
