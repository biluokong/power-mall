// pages/submit-order/submit-order.js
var http = require("../../utils/http.js");
var qrCode = require("../../utils/weapp-qrcode");

Page({

    /**
     * 页面的初始数据
     */
    data: {
        popupShow: false,
        couponSts: 1,
        couponList: [], 
        // 订单入口 0购物车 1立即购买
        orderEntry: "0", 
        userAddr: null,
        orderItems: [],
        coupon: {
            totalLength: 0,
            canUseCoupons: [],
            noCanUseCoupons: []
        },
        actualTotal: 0,
        total: 0,
        totalCount: 0,
        transfee: 0,
        reduceAmount: 0,
        remark: "",
        couponIds: [],
        qrCodeImg: false,
        myOrderItem: {} // 这是我自定义的数据
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.setData({
            orderEntry: options.orderEntry,
        });
    },

    //加载订单数据
    loadOrderData: function () {
        var addrId = 0;
        if (this.data.userAddr != null) {
            addrId = this.data.userAddr.addrId;
        }
        wx.showLoading({
            mask: true  
        });  
       var orderItem = wx.getStorageSync("orderItem");
       if(orderItem){   
           var myOrderItem =  JSON.parse(orderItem);
           myOrderItem.prodCount = myOrderItem.basketCount;
       } 
        var params = { 
            url: "/order-service/p/myOrder/confirm",
            method: "POST",
            data: {    
                // addrId: addrId,
                orderItem: this.data.orderEntry === "1" ? myOrderItem : undefined,
                basketIds: this.data.orderEntry === "0" ? JSON.parse(wx.getStorageSync("basketIds")) : undefined,
                couponIds: this.data.couponIds,
                userChangeCoupon: 1
            },
            callBack: res => { 
                // 这个res需要在提交订单的时候带到后台去
                wx.hideLoading();
                let orderItems = [];
                res.shopCartOrders.forEach(shopa=>{ 
                shopa.shopOrderItems.forEach(itemDiscount=>{
                    orderItems = orderItems.concat(itemDiscount)
                })
             })
                console.log(orderItems);
                // 把确认的对象存起来
                this.data.myOrderItem = res;

                this.setData({
                    orderItems: orderItems,
                    actualTotal: res.actualTotal, 
                    total: res.total,
                    totalCount: res.totalCount,
                    userAddr: res.memberAddr,
                    transfee: res.transfee,
                    shopReduce: res.shopReduce,
                });
            },
            errCallBack: res => {  
                wx.hideLoading(); 
                this.chooseCouponErrHandle(res)
            }
        };
        http.request(params);

    },

    /**
     * 优惠券选择出错处理方法
     */
    chooseCouponErrHandle(res) {
        // 优惠券不能共用处理方法
        if (res.statusCode == 601) {
            wx.showToast({
                title: res.data,
                icon: "none",
                duration: 3000,
                success: res => {
                    this.setData({
                        couponIds: []
                    })
                }
            }) 
            setTimeout(() => { 
                this.loadOrderData();
            }, 2500) 
        }
    },

    /**
     * 提交订单
     */
    toPay: function () {
        if (!this.data.userAddr) {
            wx.showToast({
                title: '请选择地址',
                icon: "none"
            })
            return;
        } 

        this.submitOrder();
    }, 


    submitOrder: function () { 
        wx.showLoading({
            mask: true
        });
        // 设置买家留言
        this.data.myOrderItem.remarks = this.data.remark;
        // 设置入口
        this.data.myOrderItem.orderEntry = this.data.orderEntry;
        var params = {
            url: "/order-service/p/myOrder/submit",
            method: "POST", 
            data: this.data.myOrderItem, 
            callBack: res => {   
                wx.requestPayment({
		"timeStamp": res.timeStamp,
		"nonceStr": res.nonceStr,
		"package": res.package,
		"signType": res.signType,
		"paySign": res.paySign,
		"success":function(res){
            
        },
		"fail":function(res){},
		"complete":function(res){}
	}
)
                
            //     wx.hideLoading();   
            //     // 做一个分割 
            //  let realOrderNum =    res.split(":")[1];
            //     this.calPay(realOrderNum); 
            } 
        };   
        http.request(params);   
    },

    /**
     * 唤起微信支付   
     */ 
    calPay: function (orderNumbers) {
        var ths = this;
        wx.showLoading({
            mask: true
        }); 
        var params = {      
            url: "/order-service/p/myOrder/pay", 
            method: "POST",
            data: {
                payType: 1,
                orderNumber: orderNumbers
            },
            callBack: (res) => {

                // wx.requestPayment(res){

                // }
                //让二维码展示
                ths.setData({          
                    qrCodeImg: true
                })
                qrCode({
                    width: 200,
                    height: 200,
                    canvasId: 'myQrcode',
                    text: res,
                })
                this.queryOrder(orderNumbers);
                wx.hideLoading();


            },
        };
        http.request(params);

    },

    queryOrder: function (orderNumbers) {
        //定时任务查询订单是否已经支付完成
        var timerName = setInterval(function () {
            //循环代码
            var orderQuery = { 
                url: "/order-service/p/myOrder/query",
                method: "GET",  
                data: {
                    orderSn: orderNumbers 
                },
                callBack: (res) => {  
                    console.log(res); 
                    if (res) {
                        clearInterval(timerName);
                        //成功 跳到支付成功页面
                        wx.navigateTo({
                            url: '/pages/pay-result/pay-result?sts=1&orderNumbers=' + orderNumbers + "&orderType=0"
                        })
                    }
                    wx.hideLoading();
                },
            };
            http.request(orderQuery);
        }, 2000)


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
        var pages = getCurrentPages();
        var currPage = pages[pages.length - 1];
        if (currPage.data.selAddress == "yes") {
            this.setData({ //将携带的参数赋值
                userAddr: currPage.data.item
            });
        }
        //获取订单数据
        this.loadOrderData();
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

    changeCouponSts: function (e) {
        this.setData({
            couponSts: e.currentTarget.dataset.sts
        });
    },

    showCouponPopup: function () {
        this.setData({
            popupShow: true
        });
    },

    closePopup: function () {
        this.setData({
            popupShow: false
        });
    },

    /**
     * 去地址页面
     */
    toAddrListPage: function () {
        wx.navigateTo({
            url: '/pages/delivery-address/delivery-address?order=0',
        })
    },
    /**
     * 确定选择好的优惠券
     */
    choosedCoupon: function () {
        this.loadOrderData();
        this.setData({
            popupShow: false
        });
    },

    /**
     * 优惠券子组件发过来
     */
    checkCoupon: function (e) {
        var ths = this;
        let index = ths.data.couponIds.indexOf(e.detail.couponId);
        if (index === -1) {
            ths.data.couponIds.push(e.detail.couponId)
        } else {
            ths.data.couponIds.splice(index, 1)
        }
    },

    onRemarkInput:function(e){
        this.setData({
            remark: e.detail.value
        })



    }
})
