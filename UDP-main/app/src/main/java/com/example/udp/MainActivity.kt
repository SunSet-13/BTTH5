package com.example.udp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var edtMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var txtReceived: TextView

    companion object {
        const val SERVER_PORT = 9876 // Cổng UDP để nhận tin nhắn
        const val SERVER_IP = "192.168.78.154" // Thay bằng địa chỉ IP của thiết bị nhận
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtMessage = findViewById(R.id.edtMessage)
        btnSend = findViewById(R.id.btnSend)
        txtReceived = findViewById(R.id.txtReceived)

        // Bắt đầu lắng nghe tin nhắn
        thread { receiveMessage() }

        // Xử lý sự kiện gửi tin nhắn
        btnSend.setOnClickListener {
            val message = edtMessage.text.toString()
            if (message.isNotEmpty()) {
                thread { sendMessage(message) }
            }
        }
    }

    // Gửi tin nhắn qua UDP
    private fun sendMessage(message: String) {
        try {
            val socket = DatagramSocket()
            val serverAddr = InetAddress.getByName(SERVER_IP)
            val buffer = message.toByteArray()
            val packet = DatagramPacket(buffer, buffer.size, serverAddr, SERVER_PORT)

            socket.send(packet)
            socket.close()

            runOnUiThread {
                txtReceived.text = "Nhận được tin nhắn: $message" // Hiển thị tin nhắn gửi trên UI
            }

            println("Gửi tin nhắn: $message đến $SERVER_IP:$SERVER_PORT") // Debug log
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // Nhận tin nhắn từ UDP
    private fun receiveMessage() {
        try {
            val socket = DatagramSocket(SERVER_PORT)
            val buffer = ByteArray(1024)

            while (true) {
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)
                val receivedMessage = String(packet.data, 0, packet.length)

                println("Nhận tin nhắn: $receivedMessage") // Debug log

                runOnUiThread {
                    txtReceived.text = "Người khác: $receivedMessage"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
