package com.bilibili.socialize.share.core.handler.wx;

import android.app.Activity;

import com.bilibili.socialize.share.core.BiliShareConfiguration;
import com.bilibili.socialize.share.core.SocializeMedia;
import com.bilibili.socialize.share.core.handler.AbsShareTransitHandler;
import com.bilibili.socialize.share.core.shareparam.BaseShareParam;
import com.bilibili.socialize.share.core.ui.WxAssistActivity;

/**
 * @author Jungly
 * @email jungly.ik@gmail.com
 * @since 2017/04/27
 */
public class WxShareTransitHandler extends AbsShareTransitHandler {
    private static final String TAG = "BShare.wx.transit";
    private SocializeMedia mTypeName;
    private String mClientName;

    public WxShareTransitHandler(Activity context, BiliShareConfiguration configuration, SocializeMedia type, String clientName) {
        super(context, configuration);
        mTypeName = type;
        mClientName = clientName;
    }

    @Override
    protected void onJumpToAssist(Activity context, BaseShareParam params) {
        WxAssistActivity.start(context, params, mShareConfiguration, mTypeName, mClientName);
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
