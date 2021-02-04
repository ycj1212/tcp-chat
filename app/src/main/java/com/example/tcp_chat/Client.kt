package com.example.tcp_chat

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class Client(private var s: Socket) : Thread() {
    lateinit var sender: Sender
    lateinit var receiver: Receiver

    class Sender(private var pw: PrintWriter) : Thread() {
        var isButtonClick = false

        override fun run() {
            super.run()

            while (true) {
                while (!isButtonClick) {}

                isButtonClick = false
                pw.println()
                pw.flush()
            }
        }
    }

    class Receiver(private var br: BufferedReader) : Thread() {
        override fun run() {
            super.run()

            while (true) {
                val line = br.readLine()

                if (line != null) {

                }
            }
        }
    }

    override fun run() {
        super.run()

        sender = Sender(PrintWriter(s.getOutputStream()))
        receiver = Receiver(BufferedReader(InputStreamReader(s.getInputStream())))

        sender.start()
        receiver.start()
    }
}