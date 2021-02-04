package com.example.tcp_chat

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.net.Socket

class TCP_Client : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var inputMessage: EditText

    lateinit var socket: Socket
    private var isSendButtonClicked = false

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatting_room)

        val intent = intent
        val nickname = intent.getStringExtra("nickname")

        textView = findViewById(R.id.textView)
        inputMessage = findViewById(R.id.inputMessage)

        val sendButton: Button = findViewById(R.id.sendButton)

        Toast.makeText(this, "$nickname : TCP 연결 시도", Toast.LENGTH_LONG).show()

        socket = Socket(resources.getString(R.string.ip_address), resources.getInteger(R.integer.port_number))

        Toast.makeText(this, "$nickname : TCP 연결 완료", Toast.LENGTH_LONG).show()

        val client = Client(socket)
        client.start()

        sendButton.setOnClickListener {
            client.sender.isButtonClick = true
        }
    }

    inner class Sender(private val socket: Socket, private val nickname: String) : Thread() {
        private val dos: DataOutputStream? = DataOutputStream(socket.getOutputStream())

        init {
            dos?.writeUTF(nickname)
        }

        override fun run() {
            while (true) {
                while (!isSendButtonClicked) {}

                val msg = inputMessage.editableText.toString()
                dos?.writeUTF("[$nickname] $msg")
                dos?.flush()

                isSendButtonClicked = false
            }
        }
    }

    inner class Receiver(private val socket: Socket) : Thread() {
        private val dis: DataInputStream? = DataInputStream(socket.getInputStream())

        override fun run() {
            while (true) {
                val msg = dis?.readUTF()
                handler.post {
                    textView.append("$msg\n")
                }
                Thread.sleep(1000)
            }
        }
    }
}