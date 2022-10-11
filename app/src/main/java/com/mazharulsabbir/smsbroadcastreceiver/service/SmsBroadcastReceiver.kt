package com.mazharulsabbir.smsbroadcastreceiver.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class SmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive: ${intent?.action}")

        if (intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            val msgFromIntent = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            msgFromIntent.forEachIndexed { index, smsMessage ->
                Log.i(TAG, "onReceive: $index")

                Log.i(TAG, "Address: ${smsMessage.originatingAddress}")
                Log.i(TAG, "Body: ${smsMessage.displayMessageBody}")

                val serviceIntent = Intent(context, SmsUploadService::class.java)
                serviceIntent.let {
                    it.putExtra("address", smsMessage.originatingAddress)
                    it.putExtra("body", smsMessage.displayMessageBody)
                }
                context?.startForegroundService(serviceIntent)
            }
        }
    }

    companion object {
        private const val TAG = "SmsBroadcastReceiver"
    }
}