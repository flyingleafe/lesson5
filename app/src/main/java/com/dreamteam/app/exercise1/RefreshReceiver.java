package com.dreamteam.app.exercise1;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by flyingleafe on 10.11.14.
 */
public class RefreshReceiver extends ResultReceiver {
    Receiver mReceiver;

    public RefreshReceiver(Handler handler, Receiver mReceiver) {
        super(handler);
        this.mReceiver = mReceiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
