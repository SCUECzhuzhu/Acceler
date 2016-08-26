package com.example.demo3_2; // ¿ª»úÆô¶¯

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "This demo3_2's Boot Complete", Toast.LENGTH_LONG).show();
	}
	
}
