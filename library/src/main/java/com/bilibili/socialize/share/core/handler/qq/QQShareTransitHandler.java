package com.bilibili.socialize.share.core.handler.qq;

import android.app.Activity;

import com.bilibili.socialize.share.core.BiliShareConfiguration;
import com.bilibili.socialize.share.core.SocializeMedia;
import com.bilibili.socialize.share.core.handler.AbsShareTransitHandler;
import com.bilibili.socialize.share.core.shareparam.BaseShareParam;
import com.bilibili.socialize.share.core.ui.QQAssistActivity;

/**
 * @author Jungly
 * @email jungly.ik@gmail.com
 * @since 2017/04/28
 */
public class QQShareTransitHandler extends AbsShareTransitHandler {
    private static final String TAG = "BShare.qq.transit";

    private SocializeMedia mTypeName;
    private String mClientName;

    public QQShareTransitHandler(Activity context, BiliShareConfiguration configuration, SocializeMedia type, String clientName) {
        super(context, configuration);
        mTypeName = type;
        mClientName = clientName;
    }

    @Override
    protected void onJumpToAssist(Activity act, BaseShareParam params) {
        QQAssistActivity.start(act, params, mShareConfiguration, mTypeName, mClientName);
    }

    @Override
    public SocializeMedia getShareMedia() {
        return mTypeName;
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
