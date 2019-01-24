package com.leo.payqq;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mobileqq.openpay.api.IOpenApi;
import com.tencent.mobileqq.openpay.api.OpenApiFactory;
import com.tencent.mobileqq.openpay.constants.OpenConstants;
import com.tencent.mobileqq.openpay.data.pay.PayApi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LEO
 * on 2017/7/18.
 * QQ支付
 */

public class QQPay {
    public static final int NO_OR_LOW_QQ = 1;   //未安装QQ或QQ版本过低
    public static final int ERROR_PAY_PARAM = 2;  //支付参数错误
    public static final int ERROR_PAY = 3;  //支付失败
    private final IOpenApi openApi;
    private static QQPay qqPay;

    public interface QQPayResultCallBack {
        void onSuccess(); //支付成功

        void onError(int error_code);   //支付失败

        void onCancel();    //支付取消
    }

    private QQPayResultCallBack mCallback;

    public static void init(Context context, String qq_appid) {
        if (qqPay == null) {
            qqPay = new QQPay(context, qq_appid);
        }
    }

    public static QQPay getInstance() {
        return qqPay;
    }

    public IOpenApi getQQApi() {
        return openApi;
    }

    private QQPay(Context context, @NonNull String qq_appid) {
        openApi = OpenApiFactory.getInstance(context, qq_appid);
    }

    /*
     * 封装支付Bean
     * */
    public PayApi getPayApi(@NonNull String appId, @NonNull String serialNumber, @NonNull String callbackScheme, String mPayParam) {
        try {
            if (TextUtils.isEmpty(mPayParam))
                return null;
            JSONObject param = new JSONObject(mPayParam);
            if (TextUtils.isEmpty(param.optString("tokenId"))
                    || TextUtils.isEmpty(param.optString("nonce"))
                    || TextUtils.isEmpty(param.optString("bargainorId"))
                    || TextUtils.isEmpty(param.optString("sig"))) {
                return null;
            }
            PayApi api = new PayApi();
            api.appId = appId; // 在http://open.qq.com注册的AppId,参与支付签名，签名关键字key为appId
            api.serialNumber = serialNumber; // 支付序号,用于标识此次支付
            api.callbackScheme = callbackScheme; // QQ钱包支付结果回调给urlscheme为callbackScheme的activity.，参看后续的“支付回调结果处理”
            api.tokenId = param.optString("tokenId"); // QQ钱包支付生成的token_id
            api.pubAcc = param.optString("pubAcc");
            api.pubAccHint = "";
            api.nonce = param.optString("nonce"); // 随机字段串，每次支付时都要不一样.参与支付签名，签名关键字key为nonce
            api.timeStamp = System.currentTimeMillis() / 1000; // 时间戳，为1970年1月1日00:00到请求发起时间的秒数
            api.bargainorId = param.optString("bargainorId"); // 商户号.参与支付签名，签名关键字key为bargainorId
            api.sig = param.optString("sig"); // 商户Server下发的数字签名，生成的签名串，参看“数字签名”
            api.sigType = "HMAC-SHA1"; // 签名时，使用的加密方式，默认为"HMAC-SHA1"
            return api;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发起QQ支付
     */
    public void doPay(PayApi api, @NonNull QQPayResultCallBack callback) {
        mCallback = callback;
        if (null == openApi) {
            Log.e("QQPay", "should invoke init(context, qq_appid) first");
            return;
        }

        if (!isSupportQQPay()) {
            if (mCallback != null) {
                mCallback.onError(NO_OR_LOW_QQ);
            }
            return;
        }

        if (null != api && api.checkParams()) {
            openApi.execApi(api);
        } else {
            if (mCallback != null)
                mCallback.onError(ERROR_PAY_PARAM);
        }
    }

    //支付回调响应
    public void onResp(int error_code) {
        if (mCallback == null) {
            return;
        }
        if (error_code == 0) {//成功
            mCallback.onSuccess();
        } else {//失败
            mCallback.onError(ERROR_PAY);
        }
        mCallback = null;
    }

    /*
     * 是否支持QQ支付
     * */
    public boolean isSupportQQPay() {
        return null != openApi && openApi.isMobileQQSupportApi(OpenConstants.API_NAME_PAY);
    }
}
