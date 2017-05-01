package com.bilibili.socialize.share.core.handler;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.bilibili.socialize.share.core.BiliShareConfiguration;
import com.bilibili.socialize.share.core.SocializeListeners;
import com.bilibili.socialize.share.core.SocializeMedia;
import com.bilibili.socialize.share.core.shareparam.BaseShareParam;

/**
 * @author Jungly
 * @email jungly.ik@gmail.com
 * @since 2017/04/28
 */
public abstract class AbsShareTransitHandler extends AbsShareHandler {

    public AbsShareTransitHandler(Activity context, BiliShareConfiguration configuration) {
        super(context, configuration);
    }

    @Override
    public final void share(final BaseShareParam params, final SocializeListeners.ShareListener listener) throws Exception {
        super.share(params, listener);
        final Context context = getContext();
        mImageHelper.saveBitmapToExternalIfNeed(params);
        mImageHelper.copyImageToCacheFileDirIfNeed(params);
        mImageHelper.downloadImageIfNeed(params, new Runnable() {
            @Override
            public void run() {
                Log.d(tag(), "start intent to assist act");
                onJumpToAssist((Activity) context, params);
            }
        });
    }

    protected abstract void onJumpToAssist(Activity act, BaseShareParam params);

    @Override
    protected final boolean isNeedActivityContext() {
        return true;
    }

    protected abstract String tag();

    public void onStart(SocializeMedia type) {
        Log.d(tag(), "on share start");
        SocializeListeners.ShareListener listener = getShareListener();
        if (listener == null) {
            return;
        }
        listener.onStart(type);
    }

    public void onProgress(SocializeMedia type, String progressDesc) {
        Log.d(tag(), "on share progress");
        SocializeListeners.ShareListener listener = getShareListener();
        if (listener == null) {
            return;
        }
        listener.onProgress(type, progressDesc);
    }

    public void onSuccess(SocializeMedia type, int code) {
        Log.d(tag(), "on share success");
        SocializeListeners.ShareListener listener = getShareListener();
        if (listener == null) {
            return;
        }
        listener.onSuccess(type, code);
    }

    public void onError(SocializeMedia type, int code, Throwable error) {
        Log.d(tag(), "on share failed");
        SocializeListeners.ShareListener listener = getShareListener();
        if (listener == null) {
            return;
        }
        listener.onError(type, code, error);
    }

    public void onCancel(SocializeMedia type) {
        Log.d(tag(), "on share cancel");
        SocializeListeners.ShareListener listener = getShareListener();
        if (listener == null) {
            return;
        }
        listener.onCancel(type);
    }

}
