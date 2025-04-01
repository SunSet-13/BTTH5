package com.example.accelerometerapp

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

// MainActivity kế thừa AppCompatActivity và implement SensorEventListener để lắng nghe sự kiện từ cảm biến
class MainActivity : AppCompatActivity(), SensorEventListener {

    // Khai báo SensorManager để quản lý các cảm biến trên thiết bị
    private lateinit var sensorManager: SensorManager

    // Khai báo biến cảm biến gia tốc (Accelerometer)
    private var accelerometer: Sensor? = null

    // TextView để hiển thị giá trị gia tốc
    private lateinit var tvAccelerometer: TextView

    // ImageView để đại diện cho một quả bóng di chuyển theo cảm biến
    private lateinit var ball: ImageView

    // Biến lưu trữ vị trí hiện tại của bóng
    private var xPos = 0f
    private var yPos = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ (findViewById) các View từ layout
        tvAccelerometer = findViewById(R.id.tv_accelerometer)
        ball = findViewById(R.id.ball)

        // Lấy dịch vụ SensorManager để truy cập các cảm biến
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Lấy cảm biến gia tốc từ hệ thống
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        // Đăng ký lắng nghe cảm biến khi Activity được hiển thị
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        // Hủy đăng ký lắng nghe cảm biến khi Activity tạm dừng
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // Lấy giá trị gia tốc trên 3 trục x, y, z từ cảm biến
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            // Hiển thị giá trị gia tốc lên TextView
            tvAccelerometer.text = "Gia tốc: x=$x, y=$y, z=$z"

            // Cập nhật vị trí của ImageView (bóng) dựa vào gia tốc
            xPos -= x * 5  // Điều chỉnh vị trí theo trục X
            yPos += y * 5  // Điều chỉnh vị trí theo trục Y

            // Áp dụng vị trí mới vào ImageView
            ball.translationX = xPos
            ball.translationY = yPos
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Không sử dụng phương thức này, nhưng phải override vì nó thuộc SensorEventListener
    }
}
