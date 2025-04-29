package com.voyah.ai.basecar.phone.manager;

import android.content.ComponentName;

/**
 * @author:lcy
 * @data:2024/4/13
 **/
public interface InnerServiceConnection {
    void onServiceConnected(ComponentName var1, boolean var2);

    void onServiceDisconnected(ComponentName var1);

    void onBindingDied(ComponentName var1);

    void onNullBinding(ComponentName var1);
}
