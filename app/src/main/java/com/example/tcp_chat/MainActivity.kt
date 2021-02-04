package com.example.tcp_chat

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.Formatter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputNickname: EditText = findViewById(R.id.inputNickname)
        val enterButton: Button = findViewById(R.id.enterButton)
        enterButton.setOnClickListener {
            when (val nickname = inputNickname.editableText.toString()) {
                "" -> {
                    Toast.makeText(this, "닉네임을 다시 입력하세요.", Toast.LENGTH_LONG).show()
                }
                else -> {
                    val intent = Intent(this, TCP_Client::class.java)
                    intent.putExtra("nickname", nickname)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getIpAddress(): String {
        val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        val ip = info.ipAddress
        val ipAddress = Formatter.formatIpAddress(ip)

        return ipAddress
    }
}