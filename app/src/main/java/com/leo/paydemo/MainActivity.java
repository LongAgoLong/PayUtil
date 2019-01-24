package com.leo.paydemo;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.leo.payali.AliPay;
import com.leo.payali.OnPayListener;
import com.leo.payali.PayResult;
import com.leo.payqq.QQPay;
import com.leo.paywechat.WXPay;
import com.tencent.mobileqq.openpay.data.pay.PayApi;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends RxAppCompatActivity implements View.OnClickListener {
    private Context context;
    private Button mAliPayBtn;
    private Button mWechatPayBtn;
    private Button mQqPayBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initView();
    }

    private void initView() {
        mAliPayBtn = findViewById(R.id.aliPayBtn);
        mAliPayBtn.setOnClickListener(this);
        mWechatPayBtn = findViewById(R.id.wechatPayBtn);
        mWechatPayBtn.setOnClickListener(this);
        mQqPayBtn = findViewById(R.id.qqPayBtn);
        mQqPayBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aliPayBtn:
                // 第一种方式：同步方式-配合rxjava诸如之类使用
                Observable.create((ObservableOnSubscribe<PayResult>) observableEmitter -> {
                    PayResult payResult = AliPay.doPaySync(context, "『加密后的字符串』");
                    observableEmitter.onNext(payResult);
                    observableEmitter.onComplete();
                }).compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(payResult -> {
                            String resultStatus = payResult.getResultStatus();
                            if (TextUtils.equals(resultStatus, "9000")) {
                                // 支付成功
                            } else if (TextUtils.equals(resultStatus, "8000")) {
                                // 支付结果因为支付渠道原因或者系统原因还在等待确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            } else {
                                // 支付失败，包括用户主动取消支付，或者系统返回的错误
                            }
                        }, throwable -> {

                        });
                // 第二种方式：封装好的异步
                AliPay.doPay(context, "『加密后字符串』", new OnPayListener() {
                    @Override
                    public void onPayResult(PayResult result) {
                        String resultStatus = result.getResultStatus();
                        if (TextUtils.equals(resultStatus, "9000")) {
                            // 支付成功
                        } else if (TextUtils.equals(resultStatus, "8000")) {
                            // 支付结果因为支付渠道原因或者系统原因还在等待确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        } else {
                            // 支付失败，包括用户主动取消支付，或者系统返回的错误
                        }
                    }
                });
                break;
            case R.id.wechatPayBtn:
                // 自行替换参数
                WXPay.init(context.getApplicationContext(), "『自己的appid』");      //要在支付前调用
                WXPay.getInstance().doPay("『加密后字符串』", new WXPay.WXPayResultCallBack() {
                    @Override
                    public void onSuccess() {
                        // 支付成功
                    }

                    @Override
                    public void onError(int error_code) {
                        switch (error_code) {
                            case WXPay.NO_OR_LOW_WX:
                                Toast.makeText(context, "没有安装微信", Toast.LENGTH_SHORT).show();
                                break;
                            case WXPay.ERROR_PAY_PARAM:
                                Toast.makeText(context, "参数错误", Toast.LENGTH_SHORT).show();
                                break;
                            case WXPay.ERROR_PAY:
                                Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(context, "取消支付", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.qqPayBtn:
                // 自行替换参数
                QQPay.init(context, "『自己的appid』");      //替换为自己的appid,要在支付前调用
                PayApi payApi = QQPay.getInstance().getPayApi("『自己的appid』",
                        "『订单号』", "『同清单中配置的QQ_SCHEME』",
                        "『加密后字符串』");
                QQPay.getInstance().doPay(payApi, new QQPay.QQPayResultCallBack() {
                    @Override
                    public void onSuccess() {
                        // 支付成功
                    }

                    @Override
                    public void onError(int error_code) {
                        switch (error_code) {
                            case QQPay.NO_OR_LOW_QQ:
                                Toast.makeText(context, "没安装QQ", Toast.LENGTH_SHORT).show();
                                break;
                            case QQPay.ERROR_PAY_PARAM:
                                Toast.makeText(context, "参数错误", Toast.LENGTH_SHORT).show();
                                break;
                            case QQPay.ERROR_PAY:
                                Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(context, "取消支付", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}
