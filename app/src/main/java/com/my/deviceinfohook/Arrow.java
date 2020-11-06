package com.my.deviceinfohook;

import android.content.ContentResolver;
import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;

import java.net.InetAddress;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Arrow implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        // 不是需要 Hook 的包直接返回
        if (!loadPackageParam.packageName.equals("com.my.device_info_android"))
            return;

        XposedBridge.log("app包名：" + loadPackageParam.packageName);

        /**
         * 拦截系统方法 篡改IMEI设备号
         * */
        XposedBridge.hookAllMethods(TelephonyManager.class, "getImei",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        XposedBridge.log("imei：" + param.getResult());
                        param.setResult("9999999999999999");
                    }
                });

        /**
         * 拦截系统方法 篡改MEID设备号
         * */
        XposedBridge.hookAllMethods(TelephonyManager.class, "getMeid",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        XposedBridge.log("MEID ：" + param.getResult());
                        param.setResult("288888888888");
                    }
                });

        /**
         * 拦截系统方法 篡改Android ID
         * */
        XposedHelpers.findAndHookMethod("android.provider.Settings$Secure", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader,
                "getString", // 被Hook函数的名称
                ContentResolver.class,
                String.class,
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult("66666666666666666");
                    }
                });

        /**
         * 拦截系统方法 获取序列号
         * */
        XposedHelpers.findAndHookMethod("android.os.SystemProperties", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader,
                "get", // 被Hook函数的名称
                String.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("SerialNumber ：" + param.getResult());
                        param.setResult("555555555555555555");
                    }
                });

        //拦截流量上网IP地址
        XposedHelpers.findAndHookMethod(InetAddress.class, "getHostAddress",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("流量 IP地址：" + param.getResult());
                        param.setResult("99.99.99.99");
                    }
                });


        //拦截WiFi上网IP地址
        XposedHelpers.findAndHookMethod(WifiInfo.class, "getIpAddress",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("WIFI IP地址：" + param.getResult());
                        // 分割字符串
                        String[] str = "88.88.88.88".split("\\.");
                        // 定义一个字符串，用来存储反转后的IP地址
                        String ipAdress = "";
                        // for循环控制IP地址反转
                        for (int i = 3; i >= 0; i--) {
                            ipAdress = ipAdress + str[i] + ".";
                        }
                        // 去除最后一位的"."
                        ipAdress = ipAdress.substring(0, ipAdress.length() - 1);
                        // 返回新的整形IP地址
                        param.setResult((int) ipToLong(ipAdress));
                    }
                });

    }

    public static long ipToLong(String strIp) {
        long[] ip = new long[4];
        // 先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整形
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] >> 8) + ip[3];
    }
}
