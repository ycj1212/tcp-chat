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
    lateinit var inputNickname: EditText
    lateinit var ipAddress: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputNickname = findViewById(R.id.inputNickname)
        ipAddress = findViewById(R.id.ipAddress)

        val enterButton: Button = findViewById(R.id.enterButton)
        enterButton.setOnClickListener {
            when (val nickname = inputNickname.editableText.toString()) {
                "" -> {
                    Toast.makeText(this, "닉네임 재입력", Toast.LENGTH_LONG).show()
                }
                "서버" -> {
                    Toast.makeText(this, "서버 접속", Toast.LENGTH_LONG).show()
                    val intentServer = Intent(this, Server::class.java)
                    startActivity(intentServer)
                }
                else -> {
                    val ip = ipAddress.editableText.toString()
                    Toast.makeText(this, "클라이언트 $nickname 접속", Toast.LENGTH_LONG).show()
                    val intentClient = Intent(this, Client::class.java)
                    intentClient.putExtra("nickname", nickname)
                    intentClient.putExtra("ip", ip)
                    startActivity(intentClient)
                }
            }
        }


        val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        val ip = info.ipAddress
        val ipAddress = Formatter.formatIpAddress(ip)

        Toast.makeText(this, ipAddress, Toast.LENGTH_LONG).show()
    }
}