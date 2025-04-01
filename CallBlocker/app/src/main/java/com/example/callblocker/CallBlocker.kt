package com.example.callblocker

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat

class CallBlocker : BroadcastReceiver() {

    private val blockedNumbers = setOf("0984546458", "0384719169")

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            Log.d("CallBlocker", "Trạng thái cuộc gọi: $state, Số gọi đến: $incomingNumber")

            if (state == TelephonyManager.EXTRA_STATE_RINGING && incomingNumber in blockedNumbers) {
                Log.d("CallBlocker", "Chặn cuộc gọi từ: $incomingNumber")
                rejectCall(context)
            }
        }
    }

    private fun rejectCall(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // Chỉ chạy trên Android 9+
            try {
                val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                    telecomManager.endCall()
                    Log.d("CallBlocker", "Đã từ chối cuộc gọi thành công")
                } else {
                    Log.e("CallBlocker", "Không có quyền ANSWER_PHONE_CALLS")
                }
            } catch (e: Exception) {
                Log.e("CallBlocker", "Lỗi khi từ chối cuộc gọi", e)
            }
        } else {
            Log.e("CallBlocker", "Không thể từ chối cuộc gọi trên Android < 9")
        }
    }
}
