package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.fortunexiao.tktx.AsynchUtilKt;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class NetTools {


    /**
     * 没有网络
     */
    public static final int NETWORKTYPE_INVALID = 0;
    /**
     * wap网络
     */
    public static final int NETWORKTYPE_WAP = 1;
    /**
     * 2G网络
     */
    public static final int NETWORKTYPE_2G = 2;
    /**
     * 3G和3G以上网络，或统称为快速网络
     */
    public static final int NETWORKTYPE_3G = 3;
    /**
     * wifi网络
     */
    public static final int NETWORKTYPE_WIFI = 4;
    private static int mNetWorkType;
    private String mOperatorName;
    private static NetTools instance;
    private Context context;
    //private Handler mHandler;

    private NetTools() {
    }


    public static synchronized NetTools getInstance() {
        if (instance == null) {
            instance = new NetTools();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        /*mHandler = new Handler();
        mHandler.post(netRunnable);*/
//        ThreadPool.runUITask(netRunnable);
        AsynchUtilKt.runUIThread(netRunnable);
        context.registerReceiver(mBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void quit() {
        context.unregisterReceiver(mBroadcastReceiver);
    }

    public String getOperatorName() {
        return mOperatorName + "";
    }

    public int getNetWorkType() {
        return mNetWorkType;
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
//                ThreadPool.removeUITask((netRunnable));
                AsynchUtilKt.runUIThread(netRunnable,500);
            }
        }

    };

    private Runnable netRunnable = new Runnable() {

        @Override
        public void run() {
            getCurrentNetWorkType();
            getCurrentOperatorName();
            for(WeakReference<OnNetworkChangedListener> weakReference : mNetworkChangedListeners){
                OnNetworkChangedListener listener = weakReference.get();
                if(listener != null){
                    NetworkType type;
                    switch (mNetWorkType){
                        case NETWORKTYPE_INVALID:
                            type = NetworkType.INVALID;
                            break;
                        case NETWORKTYPE_WAP:
                            type = NetworkType.WAP;
                            break;
                        case NETWORKTYPE_2G:
                            type = NetworkType._2G;
                            break;
                        case NETWORKTYPE_3G:
                            type = NetworkType._3G;
                            break;
                        case NETWORKTYPE_WIFI:
                            type = NetworkType.WIFI;
                            break;
                        default:
                            type = NetworkType.INVALID;
                            break;
                    }
                    listener.onNetChange(type);
                }
            }
        }

    };

    static final String TAG = "NetTools";


    /**
     * * 获取网络状态，wifi,wap,2g,3g. * * @param context 上下文 * @return int 网络状态
     * {@link #NETWORKTYPE_2G},{@link #NETWORKTYPE_3G}, *
     * {@link #NETWORKTYPE_INVALID},{@link #NETWORKTYPE_WAP}*
     * <p>
     * {@link #NETWORKTYPE_WIFI}
     */
    public int getCurrentNetWorkType() {
        ConnectivityManager manager = (ConnectivityManager) context .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            Log.d(TAG,"networkInfo:" + networkInfo);
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {

                int subType = networkInfo.getSubtype();

                Log.d(TAG, "subType:" + subType);


                String proxyHost = android.net.Proxy.getDefaultHost();




                mNetWorkType = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork() ? NETWORKTYPE_3G : NETWORKTYPE_2G) : NETWORKTYPE_WAP;
            }
        } else {
            mNetWorkType = NETWORKTYPE_INVALID;
        }
        return mNetWorkType;
    }

    private boolean isFastMobileNetwork() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }

    }

    /**
     * 获取运营商名字
     */
    private void getCurrentOperatorName() {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telephonyManager.getSimOperator();
        if (operator != null) {
            switch (operator) {
                case "46000":
                case "46002":
                    mOperatorName = "中国移动";
                    break;
                case "46001":
                    mOperatorName = "中国联通";
                    break;
                case "46003":
                    mOperatorName = "中国电信";
                    break;
            }
        }
    }

    public boolean isWifiState(){
        return mNetWorkType == NETWORKTYPE_WIFI;
    }

    public void registerNetworkChangeListener(OnNetworkChangedListener onNetworkChangedListener){
        for(WeakReference<OnNetworkChangedListener> weakReference : mNetworkChangedListeners){
            if(weakReference.get() == onNetworkChangedListener){ // 已存在，返回
                return;
            }
        }
        WeakReference<OnNetworkChangedListener> listener = new WeakReference<>(onNetworkChangedListener);
        mNetworkChangedListeners.add(listener);
    }

    public void unRegNetworkChangeListener(OnNetworkChangedListener onNetworkChangedListener){
        WeakReference<OnNetworkChangedListener> removeItem = null;
        for(WeakReference<OnNetworkChangedListener> weakReference : mNetworkChangedListeners){
            if(weakReference.get() == onNetworkChangedListener){
                removeItem = weakReference;
                break;
            }
        }
        if(removeItem != null){
            mNetworkChangedListeners.remove(removeItem);
        }
    }

    private List<WeakReference<OnNetworkChangedListener>> mNetworkChangedListeners = new ArrayList<>();


    public interface OnNetworkChangedListener {
        void onNetChange(NetworkType type);
    }

    public enum NetworkType {
        INVALID, // 没有网络
        WAP, // wap网络
        _2G, // 2G网络
        _3G, // 3G和3G以上网络，或统称为快速网络
        WIFI // wifi网络
    }

}
