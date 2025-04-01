package com.example.compassapp

import android.hardware.*
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// MainActivity kế thừa AppCompatActivity và implement SensorEventListener để lắng nghe dữ liệu cảm biến
class MainActivity : AppCompatActivity(), SensorEventListener {

    // SensorManager dùng để quản lý các cảm biến trên thiết bị
    private lateinit var sensorManager: SensorManager

    // Cảm biến gia tốc (để xác định hướng thiết bị so với trọng lực)
    private var accelerometer: Sensor? = null

    // Cảm biến từ trường (để xác định hướng từ trường Trái Đất)
    private var magnetometer: Sensor? = null

    // ImageView hiển thị hình ảnh la bàn
    private lateinit var compassImage: ImageView

    // TextView hiển thị góc phương hướng của la bàn
    private lateinit var angleText: TextView

    // Mảng lưu dữ liệu gia tốc kế và từ kế
    private val gravity = FloatArray(3) // Lưu dữ liệu từ cảm biến gia tốc
    private val geomagnetic = FloatArray(3) // Lưu dữ liệu từ cảm biến từ trường

    // Biến lưu giá trị góc phương hướng (azimuth)
    private var azimuth = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ View từ layout XML
        compassImage = findViewById(R.id.compassImage)
        angleText = findViewById(R.id.angleText)

        // Lấy dịch vụ SensorManager từ hệ thống
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Lấy cảm biến gia tốc
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Lấy cảm biến từ trường
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onResume() {
        super.onResume()

        // Đăng ký lắng nghe sự kiện từ cảm biến gia tốc nếu có
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        // Đăng ký lắng nghe sự kiện từ cảm biến từ trường nếu có
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()

        // Hủy đăng ký lắng nghe cảm biến khi Activity tạm dừng để tiết kiệm tài nguyên
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return // Kiểm tra nếu event là null thì không làm gì cả

        // Cập nhật dữ liệu từ cảm biến
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                // Lưu giá trị của cảm biến gia tốc vào mảng gravity
                System.arraycopy(event.values, 0, gravity, 0, event.values.size)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                // Lưu giá trị của cảm biến từ trường vào mảng geomagnetic
                System.arraycopy(event.values, 0, geomagnetic, 0, event.values.size)
            }
        }

        // Tạo ma trận quay và mảng định hướng
        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)

        // Nếu có thể tính toán ma trận quay từ dữ liệu cảm biến
        if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {

            // Tính toán góc phương hướng từ ma trận quay
            SensorManager.getOrientation(rotationMatrix, orientation)

            // Chuyển đổi góc phương hướng từ radian sang độ
            val newAzimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()

            // Đảm bảo giá trị azimuth luôn dương (0° - 360°)
            azimuth = (newAzimuth + 360) % 360

            // Xoay hình ảnh la bàn theo hướng azimuth
            compassImage.rotation = -azimuth

            // Cập nhật TextView hiển thị góc quay
            angleText.text = "Angle: ${azimuth.toInt()}°"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Phương thức này không bắt buộc nhưng có thể được sử dụng để kiểm tra độ chính xác của cảm biến
    }
}
