package com.example.tcp_chat

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

private const val PORT_NUMBER = 7777

class Client : AppCompatActivity() {
    private var IP: String? = null
    lateinit var socket: Socket
    private var isSendButtonClicked = false

    lateinit var textView: TextView
    lateinit var inputMessage: EditText

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatting_room)

        val intent = intent
        val nickname = intent.getStringExtra("nickname")
        IP = intent.getStringExtra("ip")

        textView = findViewById(R.id.textView)
        inputMessage = findViewById(R.id.inputMessage)

        val sendButton: Button = findViewById(R.id.sendButton)
        sendButton.setOnClickListener {
            isSendButtonClicked = true
        }

        Thread(Runnable {
            handler.post {
                Toast.makeText(this, "TCP 연결 시도", Toast.LENGTH_LONG).show()
            }
            socket = Socket(IP, PORT_NUMBER)
            handler.post {
                Toast.makeText(this, "TCP 연결 완료", Toast.LENGTH_LONG).show()
            }

            val sender = Sender(socket, nickname)
            val receiver = Receiver(socket)

            sender.start()
            receiver.start()
        }).start()
    }

    inner class Sender(private val socket: Socket, val nickname: String) : Thread() {
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