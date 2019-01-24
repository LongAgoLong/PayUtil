package com.leo.payali;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

public final class AliPay {
    /**
     * 已经封装好异步，结果通过接口回调
     *
     * @param context
     * @param signStr
     * @param onPayListener
     */
    public static void doPay(Context context, @NonNull String signStr, @NonNull OnPayListener onPayListener) {
        PayAsyncTask payAsyncTask = new PayAsyncTask(context);
        payAsyncTask.setOnAsynListener(onPayListener);
        payAsyncTask.execute(signStr);
    }

    /**
     * 同步操作，异步需要自行处理，可用于配合rxjava使用
     *
     * @param context
     * @param signStr
     * @return
     */
    public static PayResult doPaySync(Context context, @NonNull String signStr) {
        // 构造PayTask 对象
        PayTask alipay = new PayTask((Activity) context);
        // 调用支付接口，获取支付结果
        Map<String, String> result = alipay.payV2(signStr, true);
        return new PayResult(result);
    }
}
