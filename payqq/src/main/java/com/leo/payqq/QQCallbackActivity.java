package com.leo.payqq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mobileqq.openpay.api.IOpenApiListener;
import com.tencent.mobileqq.openpay.data.base.BaseResponse;
import com.tencent.mobileqq.openpay.data.pay.PayResponse;

/**
 * Created by LEO
 * on 2019/1/23
 * QQ支付模板代码，请勿更改
 */
public class QQCallbackActivity extends Activity implements IOpenApiListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (QQPay.getInstance() != null) {
            QQPay.getInstance().getQQApi().handleIntent(getIntent(), this);
        } else {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (QQPay.getInstance() != null) {
            QQPay.getInstance().getQQApi().handleIntent(getIntent(), this);
        }
    }


    @Override
    public void onOpenResponse(BaseResponse response) {
        if (null != response && response instanceof PayResponse) {
            PayResponse payResponse = (PayResponse) response;
            if (QQPay.getInstance() != null) {
                if (payResponse.retMsg != null) {
                    Log.i("qqpay", "retMsg=" + payResponse.retMsg);
                }
                QQPay.getInstance().onResp(response.retCode);
                finish();
            }
        }
    }

}
