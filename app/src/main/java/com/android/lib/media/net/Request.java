package com.android.lib.media.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import com.android.lib.media.config.LibMedia;
import com.android.lib.media.utils.LibMediaUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weici on 2021/1/14.
 * Describe:
 */
public class Request {

    private OnCallBackListListener mListener;
    private final Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            mListener.callBack((Result) message.obj);
            return false;
        }
    });

    public void execute(Context context, final ParamsBody body, final OnCallBackListListener listener) {
        Result result = new Result();
        if (!LibMediaUtils.isNetworkAvailable(context)) {
            result.code = -2;//无网络错误码
            if (listener != null) listener.callBack(result);
            return;
        }
        Executor.AsyncRun(new Runnable() {
            @Override
            public void run() {
                String s = LibMedia.config.net(body.URL, body.params);
                LibMediaUtils.i("result:" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    result.text = s;
                    result.code = json.optInt("result_code");
                    body.parse(result, json);
                } catch (JSONException e) {
                    result.code = 0;
                }
                if (listener != null) {
                    mListener = listener;
                    Message message = new Message();

                    message.obj = result;
                    handler.sendMessage(message);
                }
            }
        });
    }

    public interface OnCallBackListListener{
        void callBack(Result result);
    }
}
