# PayUtil
支付封装工具
包括支付宝、微信支付、QQ支付(可拆分单独依赖)
# 依赖支付宝
implementation 'com.github.LongAgoLong.PayUtil:payali:1.0'

在AndroidManifest.xml中配置：
        <activity
            android:name="com.alipay.sdk.app.H5PayActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:exported="false"
            android:screenOrientation="behind"
            android:windowSoftInputMode="adjustResize|stateHidden" />
        <activity
            android:name="com.alipay.sdk.app.H5AuthActivity"
            android:configChanges="orientation|keyboardHidden|navigation"
            android:exported="false"
            android:screenOrientation="behind"
            android:windowSoftInputMode="adjustResize|stateHidden" />
# 依赖微信
implementation 'com.github.LongAgoLong.PayUtil:paywechat:1.0'

在AndroidManifest.xml中配置：
        <activity
            android:name="com.leo.paywechat.WXPayEntryActivity"
            android:exported="true"
            android:launchMode="singleTop" />
# 依赖QQ
implementation 'com.github.LongAgoLong.PayUtil:payqq:1.0'

在AndroidManifest.xml中配置：
        <activity
            android:name="com.leo.payqq.QQCallbackActivity"
            android:exported="true"
            android:launchMode="singleTop">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.BROWSABLE" />
                <category android:name="android.intent.category.DEFAULT" />
                <!--scheme需要更改【格式为qwallet+appid】-->
                <data android:scheme="qwallet1101335990" />
            </intent-filter>
        </activity>
