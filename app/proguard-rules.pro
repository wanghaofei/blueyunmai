-optimizationpasses 5
#-dontshrink #关闭压缩
-dontoptimize #关闭优化
-dontobfuscate #关闭混淆
-dontpreverify #关闭校验
-ignorewarnings #抑制警告
-useuniqueclassmembernames
-allowaccessmodification
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-verbose
-dontnote
-dontwarn dalvik.**
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#---------------------------------------- 默认保留区 START ----------------------------------------#
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends android.webkit.WebView
-keepnames class * extends android.view.View
-keep class * extends android.app.Fragment {
    public void setUserVisibleHint(boolean);
    public void onHiddenChanged(boolean);
    public void onResume();
    public void onPause();
}
-keep class * extends android.support.v4.app.Fragment {
    public void setUserVisibleHint(boolean);
    public void onHiddenChanged(boolean);
    public void onResume();
    public void onPause();
}
-keep class android.app.Fragment {
    public void setUserVisibleHint(boolean);
    public void onHiddenChanged(boolean);
    public void onResume();
    public void onPause();
}
-keep class android.support.v4.app.Fragment {
    public void setUserVisibleHint(boolean);
    public void onHiddenChanged(boolean);
    public void onResume();
    public void onPause();
}
-keep class android.support.** {*;}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectOutputStream);
    java.lang.Object writeReplace();
    java.lang.Object ReadResolve();
}
-keep class **.R$* {*;}
-keepclassmembers class * {
    void *(**On*Event);
}
#---------------------------------------- 默认保留区 END ----------------------------------------#

#---------------------------------------- 共性的排除项目 START ----------------------------------------#
# 方法名中含有“JNI”字符的，认定是Java Native Interface方法，自动排除
#-keepclasseswithmembers class * {... *JNI*(...);}
# 方法名中含有“JRI”字符的，认定是Java Reflection Interface方法，自动排除
#-keepclasseswithmembernames class * {... *JRI*(...);}
-keep class **JNI* {*;}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep class android.**{*;}
-keep class com.tencent.** {*;}
-keep class com.alipay.** {*;}
-keep class org.json.alipay.** {*;}
-dontwarn android.net.SSLCertificateSocketFactory
#v4
-dontwarn android.support.v4.**
-keep class android.support.v4.** {*;}
-keep interface android.support.v4.app.** {*;}
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v4.app.Fragment
#v13
-dontwarn android.support.v13.**
-keep class android.support.v13.** {*;}
-keep interface android.support.v13.app.** {*;}
-keep public class * extends android.support.v13.**
#js
#-keep public class com.igrs.dlna.activity.webviewActivity.JavaScriptInterface
#-keepclassmembers class com.igrs.dlna.activity.webviewActivity.JavaScriptInterface{
#    void showSource(java.lang.String,java.lang.String);
#}
#-keepclassmembers class com.igrs.dlna.activity.webviewActivity$InJavaScriptLocalObj {
#    public void showSource(java.lang.String,java.lang.String);
#    public void showTitle(java.lang.String);
#}
##gson
#-keep class sun.misc.Unsafe {*;}
-keep class com.google.gson.examples.android.model.** {*;}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#公共平台组件(广告，支付，rms，分享，用户)
-keep class com.zbj.platform.** {*;}
-keep class com.zbj.adver_bundle.** {*;}
-keep class com.zbj.rms_bundle.** {*;}
-keep class com.zhubajie.bundle_share.** {*;}
-keep class com.zhubajie.bundle_user.** {*;}

#主项目
-keep class com.zhubajie.base.** {*;}
-keep class com.zhubajie.bundle_basic.** {*;}
-keep class com.zhubajie.bundle_search.** {*;}
-keep class com.zhubajie.bundle_search_tab.** {*;}
-keep class com.zhubajie.bundle_server.** {*;}
-keep class com.zhubajie.bundle_shop.** {*;}
-keep class com.zhubajie.bundle_server_new.** {*;}
-keep class com.zhubajie.bundle_invoice.** {*;}
-keep class com.zhubajie.bundle_im.** {*;}
-keep class com.zhubajie.bundle_order.** {*;}
-keep class com.zhubajie.bundle_find.** {*;}
-keep class com.zhubajie.bundle_demand_manager.** {*;}
-keep class com.zhubajie.bundle_recruit.** {*;}
-keep class com.zhubajie.bundle_public.** {*;}
-keep class com.zhubajie.bundle_channel.** {*;}
-keep class com.zbj.lite.wxapi.** {*;}
-keep class com.zhubajie.utils.** {*;}
-keep class com.zhubajie.af.proxy.**{*;}
-keep class com.zhubajie.widget.** {*;}
-keep class com.zhubajie.event.** {*;}


#zbjlib
-keep class com.zhubajie.net.** {*;}
-keep class com.zhubajie.secure.** {*;}
-keep class com.zhubajie.config.**{*;}
-keep class com.zhubajie.log.**{*;}
-keep class com.zhubajie.statistics.**{*;}
#点击统计
-keep class com.zhubajie.click.**{*;}
#相册控件
-keep class com.photoselector.** {*;}
-keep class com.polites.** {*;}
-keep class **.R$* {*;}
#sharesdk
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-dontwarn com.mob.**
-dontwarn cn.sharesdk.**
#-dontwarn **.R$*
#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
# tinker混淆规则
-dontwarn com.tencent.tinker.**
-keep class com.tencent.tinker.** { *; }
-keep class android.support.**{*;}

#eventBus
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode {*;}
#-keep enum io.rong.eventbus.ThreadMode {*;}
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#annotation
-keepclassmembers class ** {
    public void on*(android.view.View);
}
#-keep @com.tianpeng.client.tina.annotation.AutoMode class *
#fastjson
-keep class com.alibaba.fastjson.** { *; }
-dontwarn com.alibaba.fastjson.**
#rongim
-keep class io.rong.** {*;}
#-keep class * implements io.rong.imlib.model.MessageContent {*;}
-dontwarn io.rong.**
-dontnote com.xiaomi.**
-dontnote com.google.android.gms.gcm.**
-dontnote io.rong.**
#butterknife
-keep class butterknife.** {*;}
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder {*;}
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#####################视频云的混淆配置#####################


# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-keep class * implements android.os.Parcelable {*;}
-keep class * implements java.io.Serializable {*;}
-keep class * implements java.lang.Runnable {*;}
-keep class * implements java.lang.Cloneable {*;}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    }



-keep class javax.** {*;}
-dontwarn javax.**
-keep class com.youku** {*;}
-dontwarn com.youku**
-keep class com.alibaba** {*;}
-dontwarn com.alibaba**
-keep class com.taobao** {*;}
-dontwarn com.taobao**
-keep class com.ut** {*;}
-dontwarn com.ut**
-keep class com.securityguard** {*;}
-keep class com.luajava** {*;}
-keep class com.intertrust** {*;}
-keep class yunos.media** {*;}
-keep class android.taobao** {*;}
-dontwarn android.taobao**
-keep class com.tmalltv**{*;}
-dontwarn com.tmalltv**
-keep class com.yunos**{*;}
-dontwarn com.yunos**
-keep class mtopsdk.common**{*;}
-dontwarn mtopsdk.common**
-keep class mtopsdk.mtop**{*;}
-dontwarn mtopsdk.mtop**
-keep class mtopsdk.network**{*;}
-dontwarn mtopsdk.network**
-keep class org.android**{*;}
-dontwarn org.android**
-keep class com.uploader** {*;}
-dontwarn com.uploader**
-keep class members.** {*;}
-dontwarn members.**
-keep class org.codehaus** {*;}
-dontwarn org.codehaus**




#talkingdata
-dontwarn com.tendcloud.tenddata.**
-keep class com.tendcloud.** {*;}
-keep public class com.tendcloud.** {  public protected *;}
-keepclassmembers class com.tendcloud.tenddata.**{
public void *(***);
}
#-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}
-keep class dice.** {*; }
-dontwarn dice.**

-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep class * extends java.lang.annotation.Annotation
-keep class vi.com.** {*;}
-keep class okhttp3.** {*;}
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-keep class java.nio.** {*;}
-dontwarn java.nio.**
-keep class org.jios.elemt.widget.** {*;}
-dontwarn org.jios.elemt.widget.**
#-keep class org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#-keep class org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#HotFix
-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}
-dontwarn com.tendcloud.tenddata.**
-dontwarn com.qiniu.android.http.**
-dontwarn com.alipay.**
-dontwarn com.tencent.smtt.**
#-keep class * extends com.thejoyrun.router.RouterInitializer {*;}
#getui
-dontwarn com.igexin.**
-keep class com.igexin.** {*;}
-keep class org.json.** {*;}
#聚安全加密组件
-keep class com.taobao.securityjni.**{*;}
-keep class com.taobao.wireless.security.**{*;}
-keep class com.ut.secbody.**{*;}
-keep class com.taobao.dp.**{*;}
-keep class com.alibaba.wireless.security.**{*;}
#风控指纹
-keep public class cn.com.bsfit.**{*;}
-keepclassmembers class * extends android.webkit.WebChromeClient{
    public void openFileChooser(...);
}
-keep class de.greenrobot.event.** {*;}
-keep class com.zbj.finance.counter.** {*;}
#Glide
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
#-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule
#-keep public class * implements com.bumptech.glide.module.GlideModule
#清除日志
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int d(...);
    public static int w(...);
    public static int v(...);
    public static int i(...);
}
#grow
-keep class com.growingio.android.sdk.** {
    *;
}
-dontwarn com.growingio.android.sdk.**
-keep class com.github.mzule.activityrouter.router.** {*;}
-keep class com.networkbench.** {*;}
-dontwarn com.networkbench.**
#-keepnames class * extends okhttp3.internal.ws.WebSocketWriter

-keep class com.baidu.** {*;}
-keep class com.baidu.speech.**{*;}
-keep class com.baidu.mapapi.**{*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.mapapi.**
-dontwarn com.baidu.**

#登录SDK
-dontwarn com.zbj.sdk.login.**
-keep class com.zbj.sdk.login.**{*;}
#登录SDK 极验验证
-dontwarn com.geetest.sdk.**
-keep class com.geetest.sdk.** { *; }

-dontwarn com.zbj.lite.bean.**
-keep class com.zbj.lite.bean.** { public *; }

-dontwarn com.zbj.lite.request.**
-keep class com.zbj.lite.request.** { public *; }

-dontwarn com.zbj.lite.response.**
-keep class com.zbj.lite.response.** { public *; }

-dontwarn com.android.hydra.doraemon.**
-keep class com.android.hydra.doraemon.** { public *; }

-dontwarn com.readystatesoftware.systembartint.**
-keep class com.readystatesoftware.systembartint.** { public *; }


-dontwarn com.zbj.lite.order.**
-keep class com.zbj.lite.order.** { *; }

-dontwarn kotlin.**
-dontwarn kotlinx.**

#BaseQuickAdapter
-keep class com.chad.library.adapter.** {
*;
}
#-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
#-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
#-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
#     <init>(...);
#}

#数据类
-keep class **.model.**{*;}
-keep class **.manager.**{*;}

# 保留实体类和成员不被混淆
#-keep public class * extends com.tianpeng.client.tina.model.TinaBaseRequest {
#    public void set*(***);
#    public *** get*();
#    public *** is*();
#}

#钱包
-keep public class com.zbj.finance.wallet.** { public *;}

#--------------------------------------------------------------------------

# Addidional for x5.sdk classes for apps

-keep class com.tencent.smtt.export.external.**{
    *;
}

#-keep class com.tencent.tbs.video.interfaces.IUserStateChangedListener {
#	*;
#}
#
#-keep class com.tencent.smtt.sdk.CacheManager {
#	public *;
#}
#
#-keep class com.tencent.smtt.sdk.CookieManager {
#	public *;
#}
#
#-keep class com.tencent.smtt.sdk.WebHistoryItem {
#	public *;
#}
#
#-keep class com.tencent.smtt.sdk.WebViewDatabase {
#	public *;
#}
#
#-keep class com.tencent.smtt.sdk.WebBackForwardList {
#	public *;
#}

#-keep public class com.tencent.smtt.sdk.WebView {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebView$HitTestResult {
#	public static final <fields>;
#	public java.lang.String getExtra();
#	public int getType();
#}
#
#-keep public class com.tencent.smtt.sdk.WebView$WebViewTransport {
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebView$PictureListener {
#	public <fields>;
#	public <methods>;
#}


-keepattributes InnerClasses

-keep public enum com.tencent.smtt.sdk.WebSettings$** {
    *;
}

-keep public enum com.tencent.smtt.sdk.QbSdk$** {
    *;
}

#-keep public class com.tencent.smtt.sdk.WebSettings {
#    public *;
#}
#
#
#-keepattributes Signature
#-keep public class com.tencent.smtt.sdk.ValueCallback {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebViewClient {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.DownloadListener {
#	public <fields>;
#	public <methods>;
#}

#-keep public class com.tencent.smtt.sdk.WebChromeClient {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebChromeClient$FileChooserParams {
#	public <fields>;
#	public <methods>;
#}
#
#-keep class com.tencent.smtt.sdk.SystemWebChromeClient{
#	public *;
#}
# 1. extension interfaces should be apparent
-keep public class com.tencent.smtt.export.external.extension.interfaces.* {
	public protected *;
}

# 2. interfaces should be apparent
-keep public class com.tencent.smtt.export.external.interfaces.* {
	public protected *;
}

#-keep public class com.tencent.smtt.sdk.WebViewCallbackClient {
#	public protected *;
#}
#
#-keep public class com.tencent.smtt.sdk.WebStorage$QuotaUpdater {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.WebIconDatabase {
#	public <fields>;
#	public <methods>;
#}

#-keep public class com.tencent.smtt.sdk.WebStorage {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.DownloadListener {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.QbSdk {
#	public <fields>;
#	public <methods>;
#}

#-keep public class com.tencent.smtt.sdk.QbSdk$PreInitCallback {
#	public <fields>;
#	public <methods>;
#}
#-keep public class com.tencent.smtt.sdk.CookieSyncManager {
#	public <fields>;
#	public <methods>;
#}

-keep public class com.tencent.smtt.sdk.Tbs* {
	public <fields>;
	public <methods>;
}

#-keep public class com.tencent.smtt.utils.LogFileUtils {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.utils.TbsLog {
#	public <fields>;
#	public <methods>;
#}
#
#-keep public class com.tencent.smtt.utils.TbsLogClient {
#	public <fields>;
#	public <methods>;
#}

#-keep public class com.tencent.smtt.sdk.CookieSyncManager {
#	public <fields>;
#	public <methods>;
#}
#
## Added for game demos
#-keep public class com.tencent.smtt.sdk.TBSGamePlayer {
#	public <fields>;
#	public <methods>;
#}

-keep public class com.tencent.smtt.sdk.TBSGamePlayerClient* {
	public <fields>;
	public <methods>;
}

#-keep public class com.tencent.smtt.sdk.TBSGamePlayerClientExtension {
#	public <fields>;
#	public <methods>;
#}

-keep public class com.tencent.smtt.sdk.TBSGamePlayerService* {
	public <fields>;
	public <methods>;
}

#-keep public class com.tencent.smtt.utils.Apn {
#	public <fields>;
#	public <methods>;
#}
-keep class com.tencent.smtt.** {
	*;
}
# end


#-keep public class com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension {
#	public <fields>;
#	public <methods>;
#}
#
#-keep class MTT.ThirdAppInfoNew {
#	*;
#}
#
#-keep class com.tencent.mtt.MttTraceEvent {
#	*;
#}

# Game related
-keep public class com.tencent.smtt.gamesdk.* {
	public protected *;
}

#-keep public class com.tencent.smtt.sdk.TBSGameBooter {
#        public <fields>;
#        public <methods>;
#}
#
#-keep public class com.tencent.smtt.sdk.TBSGameBaseActivity {
#	public protected *;
#}
#
#-keep public class com.tencent.smtt.sdk.TBSGameBaseActivityProxy {
#	public protected *;
#}
#
#-keep public class com.tencent.smtt.gamesdk.internal.TBSGameServiceClient {
#	public *;
#}

#-keep class com.alipay.android.app.IAlixPay{*;}
#-keep class com.alipay.android.app.IAlixPay$Stub{*;}
#-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
#-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
#-keep class com.alipay.sdk.app.PayTask{ public *;}
#-keep class com.alipay.sdk.app.AuthTask{ public *;}
#-keep class com.alipay.sdk.app.H5PayCallback {
#    <fields>;
#    <methods>;
#}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

#动态权限申请
-dontwarn me.weyye.hipermission.**
-keep class me.weyye.hipermission.**{*;}
#---------------------------------------------------------------------------
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#mipush
-dontwarn com.xiaomi.mipush.sdk.**
-keep public class com.xiaomi.mipush.sdk.* {*;}
#-keep class com.zbj.push.xiaomi.BuyerMessageReceiver {*;}

#hwpush
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.android.hms.agent.**{*;}
-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

-keep class com.bun.miitmdid.core.** {*;}
-keep class com.openapplus.** {*;}
#map
-keep class mapsdkvi.com.** {*;}
#MSA
# sdk
-keep class com.bun.miitmdid.** { *; }
-keep interface com.bun.supplier.** { *; }

# asus
-keep class com.asus.msa.SupplementaryDID.** { *; }
-keep class com.asus.msa.sdid.** { *; }
# freeme
-keep class com.android.creator.** { *; }
-keep class com.android.msasdk.** { *; }
# huawei
-keep class com.huawei.hms.ads.** { *; }
-keep interface com.huawei.hms.ads.** {*; }
# lenovo
-keep class com.zui.deviceidservice.** { *; }
-keep class com.zui.opendeviceidlibrary.** { *; }
# meizu
-keep class com.meizu.flyme.openidsdk.** { *; }
# nubia
#-keep class com.bun.miitmdid.provider.nubia.NubiaIdentityImpl { *; }
# oppo
-keep class com.heytap.openid.** { *; }
# samsung
-keep class com.samsung.android.deviceidservice.** { *; }
# vivo
-keep class com.vivo.identifier.** { *; }
# xiaomi
#-keep class com.bun.miitmdid.provider.xiaomi.IdentifierManager { *; }
# zte
-keep class com.bun.lib.** { *; }
# coolpad
-keep class com.coolpad.deviceidsupport.** { *; }


-dontwarn com.vivo.push.**
-keep class com.vivo.push.**{*;  }
-keep class com.vivo.vms.**{*; }
#-keep class com.zhubajie.bundle_basic.push.BuyerVivoPushReceiver{*;}

-keep class tv.danmaku.ijk.media.player.** {*;}
#-keep class tv.danmaku.ijk.media.player.IjkMediaPlayer{*;}
#-keep class tv.danmaku.ijk.media.player.ffmpeg.FFmpegApi{*;}
-keep public class com.megvii.**{*;}

#-keep class com.zhubajie.bundle_basic.push.BuyerMiPushReceiver {*;}
-dontwarn com.xiaomi.push.**


-keep class XI.CA.XI.**{*;}

-keep class XI.K0.XI.**{*;}

-keep class XI.XI.K0.**{*;}

-keep class XI.xo.XI.XI.**{*;}

-keep class com.asus.msa.SupplementaryDID.**{*;}

-keep class com.asus.msa.sdid.**{*;}

-keep class com.bun.lib.**{*;}

-keep class com.bun.miitmdid.**{*;}

-keep class com.huawei.hms.ads.identifier.**{*;}

-keep class com.samsung.android.deviceidservice.**{*;}

-keep class com.zui.opendeviceidlibrary.**{*;}

-keep class org.json.**{*;}

#-keep public class com.netease.nis.sdkwrapper.Utils {public <methods>;}

# 联通取号、认证混淆
 -dontwarn com.unicom.xiaowo.**
 -keep class com.unicom.xiaowo.**{*;}

# 移动混淆
 -dontwarn com.cmic.sso.sdk.**
 -keep class com.cmic.sso.sdk.**{*;}

# 电信混淆
 -dontwarn cn.com.chinatelecom.account.**
 -keep class cn.com.chinatelecom.account.**{*;}

# 一键登录
 -dontwarn cc.lkme.linkaccount.**
 -keep class cc.lkme.linkaccount.**{*;}

-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

-keep class org.libpag.** {*;}
-keep class androidx.exifinterface.** {*;}