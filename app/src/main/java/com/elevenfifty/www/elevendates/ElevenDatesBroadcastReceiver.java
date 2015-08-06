package com.elevenfifty.www.elevendates;

import android.content.Context;
import android.content.Intent;

import com.elevenfifty.www.elevendates.Models.ChatMessageArrived;
import com.parse.ParsePushBroadcastReceiver;

import de.greenrobot.event.EventBus;

/**
 * Created by kathy on 7/16/2015.
 */
public class ElevenDatesBroadcastReceiver extends ParsePushBroadcastReceiver {
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new ChatMessageArrived());

        super.onPushReceive(context, intent);

    }
}
