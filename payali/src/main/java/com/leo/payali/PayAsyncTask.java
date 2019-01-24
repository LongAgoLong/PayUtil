package com.leo.payali;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

public class PayAsyncTask extends AsyncTask<String, Void, PayResult> {
    private Context context;
    protected OnPayListener mOnPayListener;

    public PayAsyncTask(Context context) {
        this.context = context;
    }

    public void setOnAsynListener(OnPayListener onPayListener) {
        this.mOnPayListener = onPayListener;
    }

    @Override
    protected PayResult doInBackground(String... strings) {
        String signStr = strings[0];
        // 构造PayTask 对象
        PayTask alipay = new PayTask((Activity) context);
        // 调用支付接口，获取支付结果
        Map<String, String> result = alipay.payV2(signStr, true);
        return new PayResult(result);
    }

    @Override
    protected void onPostExecute(PayResult result) {
        if (null != mOnPayListener) {
            mOnPayListener.onPayResult(result);
        }
    }
}
