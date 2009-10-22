package net.ccghe.emocha.receivers;

import net.ccghe.emocha.services.GpsService;
import net.ccghe.emocha.services.ServerService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService( new Intent(context , ServerService.class ) );
        context.startService( new Intent(context , GpsService.class ) );
    }

}
