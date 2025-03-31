package com.example.counthours

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    // Khai báo biến UI
    private lateinit var timeTextView: TextView // Hiển thị thời gian
    private lateinit var startButton: Button    // Nút bắt đầu
    private lateinit var stopButton: Button     // Nút dừng

    // Biến đếm số giây đã trôi qua
    private var seconds = 0

    // Trạng thái chạy của bộ đếm
    private var running = false

    // Handler để cập nhật giao diện từ luồng phụ
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Gán giao diện từ layout XML

        // Ánh xạ view từ layout XML vào biến trong code
        timeTextView = findViewById(R.id.timeTextView)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        // Xử lý sự kiện khi nhấn nút "Bắt đầu"
        startButton.setOnClickListener {
            running = true  // Đặt trạng thái chạy
            startButton.visibility = View.GONE  // Ẩn nút "Bắt đầu"
            stopButton.visibility = View.VISIBLE  // Hiển thị nút "Dừng"
            startTimer()  // Gọi hàm bắt đầu bộ đếm thời gian
        }

        // Xử lý sự kiện khi nhấn nút "Dừng"
        stopButton.setOnClickListener {
            running = false  // Đặt trạng thái dừng
            startButton.visibility = View.VISIBLE  // Hiển thị nút "Bắt đầu"
            stopButton.visibility = View.GONE  // Ẩn nút "Dừng"
        }
    }

    // Hàm chạy bộ đếm thời gian
    private fun startTimer() {
        Thread {
            while (running) { // Chạy khi biến running = true
                Thread.sleep(1000) // Chờ 1 giây
                seconds++  // Tăng biến đếm lên 1
                handler.post {
                    // Cập nhật giao diện người dùng từ luồng chính
                    timeTextView.text = "Thời gian: $seconds giây"
                }
            }
        }.start() // Bắt đầu luồng mới
    }
}
