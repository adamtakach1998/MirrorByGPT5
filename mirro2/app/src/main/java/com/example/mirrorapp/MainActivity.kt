
package com.example.mirrorapp

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    companion object { const val CAPTURE_REQUEST_CODE = 1001 }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val startBtn = findViewById<Button>(R.id.startBtn)
        val mpm = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        startBtn.setOnClickListener {
            val password = passwordInput.text.toString().trim()
            if (password.isEmpty()) { Toast.makeText(this, "Enter a password", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val req = mpm.createScreenCaptureIntent()
            req.putExtra("sessionPassword", password)
            startActivityForResult(req, CAPTURE_REQUEST_CODE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val svc = Intent(this, ScreenStreamService::class.java)
            svc.putExtra("resultCode", resultCode)
            svc.putExtra("data", data)
            svc.putExtra("sessionPassword", intent.getStringExtra("sessionPassword") ?: "")
            startForegroundService(svc)
        } else { Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show() }
    }
}
