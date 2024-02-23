package com.tencent.replay;

import com.tencent.qa.RecordReplayManager;
import com.tencent.qa.framework.AccountChangeCallback;
import org.json.JSONObject;

/**
 * SDK适配层初始化类
 * 配置完IoMock插件接入后，会在application启动里自动调用该类的init方法
 * 请勿修改该类包名和类名！！！
 */
public class ReplayInitManager {

    /**
     * SDK适配层初始化方法，配置完IoMock插件后，会在application启动里自动调用
     * 请在该方法里编写SDK初始化代码
     * 请勿修改该方法名！！！
     */
    public static void init() {
        RecordReplayManager.getInstance()
                // 开启默认replayLog，供回放失败排查问题
                .openDefaultLog(true)
                // 登录QQ账号适配
                .setAccountChangeCallback(new AccountChangeCallback() {

                    @Override
                    public void changeQQAccount(JSONObject jsonObject) {
                        // 先判断账号是否存在，如果存在则先登出
//                        String id = EduFramework.getAccountManager().getUin();
//                        if (!TextUtils.isEmpty(id)) {
//                            LoginMgr.getInstance().logout();
//                        }
                        // 登录
//                        qqLogin(jsonObject);
                    }

                    @Override
                    public boolean isLoginStatus() {
                        // 判断是否登录
                        return false;
                    }

                    @Override
                    public String getQQAppId() {
                        // 返回QQAppId
                        return null;
                    }

                    @Override
                    public void exitAccount() {
                        // 调用登出接口
                    }
                })
                .init();

    }
}
