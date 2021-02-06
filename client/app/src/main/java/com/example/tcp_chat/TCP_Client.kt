package com.example.tcp_chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class TCP_Client : AppCompatActivity() {
    private val handler = Handler()

    lateinit var textView: TextView
    lateinit var inputMessage: EditText

    lateinit var client: ClientThread.Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatting_room)

        val intent = intent
        val nickname = intent.getStringExtra("nickname")

        textView = findViewById(R.id.textView)
        inputMessage = findViewById(R.id.inputMessage)
        val sendButton: Button = findViewById(R.id.sendButton)

        val clientThread = ClientThread(nickname)
        clientThread.start()

        sendButton.setOnClickListener {
            val msg = inputMessage.editableText.toString()
            if (msg != "") {
                client.sender.send(msg)
            }
            inputMessage.setText("")
        }
    }

    inner class ClientThread(var nickname: String?) : Thread() {
        override fun run() {
            try {
                val socket = Socket(
                    resources.getString(R.string.ip_address),
                    resources.getInteger(R.integer.port_number)
                )

                client = Client(nickname, socket)
                client.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        inner class Client(private var nickname: String?, private var s: Socket) : Thread() {
            lateinit var sender: Sender
            lateinit var receiver: Receiver

            override fun run() {
                super.run()

                sender = Sender(PrintWriter(s.getOutputStream()))
                receiver = Receiver(BufferedReader(InputStreamReader(s.getInputStream())))

                sender.start()
                sender.send(nickname!!)
                receiver.start()
            }

            inner class Sender(private var pw: PrintWriter) : Thread() {
                private var isButtonClick = false

                override fun run() {
                    super.run()

                    while (true) {
                        while (!isButtonClick) {}
                    }
                }

                fun send(msg: String) {
                    isButtonClick = true
                    Thread {
                        pw.println(msg)
                        pw.flush()
                    }.start()
                    isButtonClick = false
                }
            }

            inner class Receiver(private var br: BufferedReader) : Thread() {
                override fun run() {
                    super.run()

                    while (true) {
                        val msg = br.readLine()

                        if (msg != null) {
                            handler.post {
                                textView.append("$msg\n")
                            }
                        }
                    }
                }
            }
        }
    }
}