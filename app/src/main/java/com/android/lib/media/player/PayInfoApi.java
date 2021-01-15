package com.android.lib.media.player;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

/**
 * @author Alan
 * 时 间：2020/12/3
 * 简 述：<功能简述>
 */
public class PayInfoApi {


    /**
     * money : 50.00
     * pay : {"package":"Sign=WXPay","appid":"wx5c5cb988245a65b5","sign":"57BDCC2421D74AEF1DC69FF6D1BF5F4C","orderCode":"20201203000000004","prepayid":"wx031815400327885c5354f2e98424970000","partnerid":"1499518132","noncestr":"ss0yqs56k7da6y3ouwoqdsmnulpqk80p","timestamp":"1606990540"}
     * description : success
     * result_code : 200
     */

    private String money;
    private PayBean pay;
    private String description;
    @SerializedName("result_code")
    private int resultCode;

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public PayBean getPay() {
        return pay;
    }

    public void setPay(PayBean pay) {
        this.pay = pay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public static class PayBean {
        /**
         * package : Sign=WXPay
         * appid : wx5c5cb988245a65b5
         * sign : 57BDCC2421D74AEF1DC69FF6D1BF5F4C
         * orderCode : 20201203000000004
         * prepayid : wx031815400327885c5354f2e98424970000
         * partnerid : 1499518132
         * noncestr : ss0yqs56k7da6y3ouwoqdsmnulpqk80p
         * timestamp : 1606990540
         */

        @SerializedName("package")
        private String packageX;
        private String appid;
        private String sign;
        private String orderCode;
        private String prepayid;
        private String partnerid;
        private String noncestr;
        private String timestamp;

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getOrderCode() {
            return orderCode;
        }

        public void setOrderCode(String orderCode) {
            this.orderCode = orderCode;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }

    public void pay(Context context) {
        if (pay != null) {
            /*IWXAPI api = WXAPIFactory.createWXAPI(context, "wx5c5cb988245a65b5");
            if (!api.isWXAppInstalled()) {
                ToastManager.getInstance().showToast(context, "还没有安装微信，请先安装微信客户端");
            }else if (api.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
                ToastManager.getInstance().showToast(context, "微信版本过低，请先升级微信客户端");
            }else {
                ConfigManager.getInstance(context).setOrderCode(pay.orderCode);
                PayReq req = new PayReq();
                req.appId = pay.appid;
                req.partnerId = pay.partnerid;
                req.prepayId = pay.prepayid;
                req.nonceStr = pay.noncestr;
                req.timeStamp = pay.timestamp;
                req.packageValue = pay.packageX;
                req.sign = pay.sign;
                req.extData = "app data"; // optional
                api.sendReq(req);
            }*/
        }
    }
}
