package com.mazharulsabbir.smsbroadcastreceiver.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mazharulsabbir.smsbroadcastreceiver.R
import com.mazharulsabbir.smsbroadcastreceiver.data.retrofit.RetrofitBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SmsUploadService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: created()")

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SMS Receiver")
            .setContentText("SMS Receiver is running")
            .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: Intent Data: ${intent?.extras}")
        makeApiCall()

        return super.onStartCommand(intent, flags, startId)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun makeApiCall() {
        Log.i(TAG, "makeApiCall: Called!")
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitBuilder.getApiService().getPosts()
                Log.i(TAG, "rawJSON: ${response.body()}")
            } catch (e: Exception) {
                Log.e(TAG, "rawJSON: ", e)
            }

            /*stop the service running foreground.*/
            stopSelf()
        }
        Log.i(TAG, "makeApiCall: Released!")
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.i(TAG, "onBind: called!")
        return null
    }

    override fun stopService(name: Intent?): Boolean {
        Log.i(TAG, "stopService: stopping.........")
        return super.stopService(name)
    }

    companion object {
        private const val TAG = "SmsUploadService"
        const val CHANNEL_ID = "SmsUploadServiceChannel_001"
        const val NOTIFICATION_ID = 127
        // https://www.tutlane.com/tutorial/android/android-progress-notification-with-examples
    }
}
