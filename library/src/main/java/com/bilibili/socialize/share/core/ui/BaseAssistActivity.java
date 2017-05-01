package com.bilibili.socialize.share.core.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.bilibili.socialize.share.core.BiliShare;
import com.bilibili.socialize.share.core.BiliShareConfiguration;
import com.bilibili.socialize.share.core.SocializeListeners;
import com.bilibili.socialize.share.core.SocializeMedia;
import com.bilibili.socialize.share.core.error.BiliShareStatusCode;
import com.bilibili.socialize.share.core.error.ShareException;
import com.bilibili.socialize.share.core.handler.AbsShareTransitHandler;
import com.bilibili.socialize.share.core.handler.BaseShareHandler;
import com.bilibili.socialize.share.core.handler.IShareHandler;
import com.bilibili.socialize.share.core.shareparam.BaseShareParam;

/**
 * @author Jungly
 * @email jungly.ik@gmail.com
 * @since 2017/04/28
 */
public abstract class BaseAssistActivity<H extends BaseShareHandler> extends Activity
        implements SocializeListeners.ShareListener {
    public static final String KEY_PARAM = "share_param";
    public static final String KEY_CONFIG = "share_config";
    public static final String KEY_TYPE = "share_type";
    public static final String KEY_CLIENT_NAME = "client_name";

    protected BiliShareConfiguration mShareConfig;
    protected BaseShareParam mShareParam;
    protected String mClientName;
    protected SocializeMedia mSocializeMedia;

    protected H mShareHandler;
    protected boolean mHasGetResult;

    protected abstract String tag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolveParams();
        boolean bingo = true;
        bingo = checkConfigArgs();
        if (bingo) {
            bingo = checkSocializeArgs();
        }
        if (bingo) {
            mShareHandler = resolveHandler(mSocializeMedia, mShareConfig);
            if (mShareHandler == null) {
                bingo = false;
                String msg = String.format("media type is not correct:%s", mSocializeMedia);
                Log.w(tag(), msg);
                finishWithFailResult(msg);
            } else {
                bingo = true;
            }
        }
        if (bingo) {
            bingo = initHandler(savedInstanceState);
        }
        if (bingo) {
            startShare(savedInstanceState);
        }
    }

    protected void resolveParams() {
        Intent intent = getIntent();
        mShareConfig = intent.getParcelableExtra(KEY_CONFIG);
        mShareParam = intent.getParcelableExtra(KEY_PARAM);
        String type = intent.getStringExtra(KEY_TYPE);
        mClientName = intent.getStringExtra(KEY_CLIENT_NAME);
        if (!TextUtils.isEmpty(type)) {
            mSocializeMedia = SocializeMedia.valueOf(type);
        }
    }

    protected boolean checkConfigArgs() {
        if (mShareConfig == null) {
            Log.e(tag(), "null share config");
            finishWithFailResult("null share config");
            return false;
        }
        return true;
    }

    protected boolean checkSocializeArgs() {
        if (mSocializeMedia == null) {
            Log.e(tag(), "null media type");
            finishWithFailResult("null media type");
            return false;
        }
        return true;
    }

    protected abstract H resolveHandler(SocializeMedia media, BiliShareConfiguration shareConfig);

    protected boolean initHandler(Bundle savedInstanceState) {
        try {
            mShareHandler.checkConfig();
            mShareHandler.init();
            Log.d(tag(), "share handler init success");
            mShareHandler.onActivityCreated(this, savedInstanceState, this);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(tag(), String.format("share handler init failed: %s", e.getMessage()));
            finishWithFailResult("share handler init failed");
            return false;
        }
    }

    protected boolean startShare(Bundle savedInstanceState) {
        try {
            if (savedInstanceState == null) {
                if (mShareParam == null) {
                    Log.e(tag(), "null share params");
                    onError(mSocializeMedia, BiliShareStatusCode.ST_CODE_SHARE_ERROR_EXCEPTION,
                            new ShareException("share param error"));
                    return false;
                } else {
                    Log.d(tag(), "call share");
                    mShareHandler.share(mShareParam, this);
                }
            }
        } catch (Exception e) {
            onError(mSocializeMedia, BiliShareStatusCode.ST_CODE_SHARE_ERROR_EXCEPTION, e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected void finishWithSuccessResult() {
        onSuccess(mSocializeMedia, BiliShareStatusCode.ST_CODE_SUCCESSED);
    }

    protected void finishWithFailResult(String msg) {
        onError(mSocializeMedia, BiliShareStatusCode.ST_CODE_ERROR, new ShareException(msg));
    }

    protected void finishWithCancelResult() {
        onCancel(mSocializeMedia);
    }

    @Override
    public void onStart(SocializeMedia type) {
        Log.d(tag(), "on inner share start");
        AbsShareTransitHandler handler = getShareHandler();
        if (handler != null) {
            handler.onStart(type);
        }
    }

    @Override
    public void onProgress(SocializeMedia type, String progressDesc) {
        Log.d(tag(), "on inner share progress");
        AbsShareTransitHandler handler = getShareHandler();
        if (handler != null) {
            handler.onProgress(type, progressDesc);
        }
    }

    @Override
    public void onSuccess(SocializeMedia type, int code) {
        Log.i(tag(), "----->on inner share success<-----");
        mHasGetResult = true;
        AbsShareTransitHandler handler = getShareHandler();
        if (handler != null) {
            handler.onSuccess(type, code);
        }
        finish();
    }

    @Override
    public void onError(SocializeMedia type, int code, Throwable error) {
        Log.i(tag(), "----->on inner share fail<-----");
        mHasGetResult = true;
        AbsShareTransitHandler handler = getShareHandler();
        if (handler != null) {
            handler.onError(type, code, error);
        }
        finish();
    }

    @Override
    public void onCancel(SocializeMedia type) {
        Log.i(tag(), "----->on inner share cancel<-----");
        mHasGetResult = true;
        AbsShareTransitHandler handler = getShareHandler();
        if (handler != null) {
            handler.onCancel(type);
        }
        finish();
    }

    protected AbsShareTransitHandler getShareHandler() {
        if (TextUtils.isEmpty(mClientName)) {
            Log.e(tag(), "null client name");
            return null;
        }

        BiliShare share = BiliShare.get(mClientName);
        IShareHandler handler = share.currentHandler();
        if (handler == null) {
            Log.e(tag(), "null handler");
            return null;
        }
        if (!(handler instanceof AbsShareTransitHandler)) {
            Log.e(tag(), "wrong handler type");
            return null;
        }

        return (AbsShareTransitHandler) handler;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(tag(), "activity onDestroy");
    }

    protected void release() {
        if (mShareHandler != null) {
            mShareHandler.release();
        }
    }

}
