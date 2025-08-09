
package com.example.mirrorapp

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.webkit.*
import java.security.MessageDigest

class ScreenStreamService : Service() {
    private var webView: WebView? = null
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val password = intent?.getStringExtra("sessionPassword") ?: ""
        val sessionId = sha256(password)
        createNotification()
        webView = WebView(this)
        webView?.settings?.javaScriptEnabled = true
        webView?.settings?.mediaPlaybackRequiresUserGesture = false
        webView?.settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView?.webViewClient = WebViewClient()
        webView?.loadUrl("file:///android_asset/sender.html")
        webView?.postDelayed({ webView?.evaluateJavascript("startSender('$sessionId')", null) }, 1500)
        return START_NOT_STICKY
    }
    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("mirror", "Screen Mirror", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notif: Notification = Notification.Builder(this, "mirror")
            .setContentTitle("Screen Mirroring Active")
            .setContentText("Your screen is being shared")
            .setSmallIcon(android.R.drawable.ic_media_play).build()
        startForeground(1, notif)
    }
    override fun onDestroy() { webView?.destroy(); webView = null; super.onDestroy() }
}
