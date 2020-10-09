package com.example.tcp_chat

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.DataInputStream
import java.io.DataOutput
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.collections.HashMap

private const val PORT_NUMBER = 7777

class Server : AppCompatActivity() {
    lateinit var serverSocket: ServerSocket
    lateinit var socket: Socket
    lateinit var clients: HashMap<String, DataOutputStream>
    private var isSendButtonClicked = false

    lateinit var textView: TextView
    lateinit var inputMessage: EditText

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatting_room)

        textView = findViewById(R.id.textView)
        inputMessage = findViewById(R.id.inputMessage)

        val sendButton: Button = findViewById(R.id.sendButton)
        sendButton.setOnClickListener {
            isSendButtonClicked = true
        }

        Thread(Runnable {
            clients = HashMap()
            Collections.synchronizedMap(clients)
            serverSocket = ServerSocket(PORT_NUMBER)
            handler.post {
                Toast.makeText(
                    this,
                    "서버 소켓 생성",
                    Toast.LENGTH_LONG
                ).show()
            }
            while (true) {
                socket = serverSocket.accept()

                val clientHost = socket.localAddress
                val clientPort = socket.port
                handler.post {
                    Toast.makeText(
                        this,
                        "클라이언트 접속 \n호스트: $clientHost \n포트: $clientPort",
                        Toast.LENGTH_LONG
                    ).show()
                }

                val sender = Sender(socket)
                val receiver = Receiver(socket)
                sender.start()
                receiver.start()
            }
        }).start()
    }

    inner class Sender(private val socket: Socket) : Thread() {
        override fun run() {
            while (true) {
                while (!isSendButtonClicked) {}

                val msg = inputMessage.editableText.toString()
                sendMessage(msg)
                handler.post {
                    textView.append("$msg\n")
                }

                isSendButtonClicked = false
            }
        }
    }

    inner class Receiver(private val socket: Socket) : Thread() {
        private val dis: DataInputStream? = DataInputStream(socket.getInputStream())
        private val dos: DataOutputStream? = DataOutputStream(socket.getOutputStream())
        private val nickname = dis?.readUTF()

        init {
            clients[nickname!!] = dos!!
            handler.post {
                Toast.makeText(
                    applicationContext,
                    "nickname: $nickname",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun run() {
            while (true) {
                val msg = dis?.readUTF()
                sendMessage(msg!!)
                handler.post {
                    textView.append("$msg\n")
                }
            }
        }
    }

    private fun sendMessage(msg: String) {
        val iterator = clients.keys.iterator()

        while (iterator.hasNext()) {
            val key = iterator.next()
            val dos = clients[key]
            dos?.writeUTF(msg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        socket.close()
        serverSocket.close()
    }
}