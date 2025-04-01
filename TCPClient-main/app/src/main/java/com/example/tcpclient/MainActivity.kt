package com.example.tcpclient

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.net.Socket
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtMessage = findViewById<EditText>(R.id.edtMessage)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val txtResponse = findViewById<TextView>(R.id.txtResponse)

        btnSend.setOnClickListener {
            val message = edtMessage.text.toString()
            if (message.isNotEmpty()) {
                thread {
                    try {
                        val serverIP = "192.168.78.154" // Thay bằng IP của Server
                        val serverPort = 12345
                        val socket = Socket(serverIP, serverPort)

                        val out = PrintWriter(socket.getOutputStream(), true)
                        val inStream = BufferedReader(InputStreamReader(socket.getInputStream()))

                        out.println(message) // Gửi tin nhắn
                        val response = inStream.readLine() // Nhận phản hồi

                        runOnUiThread {
                            txtResponse.text = "$response"
                        }

                        socket.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            txtResponse.text = "Lỗi kết nối!"
                        }
                    }
                }
            }
        }
    }
}