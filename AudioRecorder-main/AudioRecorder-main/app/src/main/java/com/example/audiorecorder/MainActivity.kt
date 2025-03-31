package com.example.audiorecorder

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    // Khai báo các thành phần giao diện và biến cần thiết
    private lateinit var startRecordButton: Button
    private lateinit var stopRecordButton: Button
    private lateinit var playRecordButton: Button
    private lateinit var recordListView: ListView
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var fileName: String = ""
    private val records = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ các thành phần giao diện
        startRecordButton = findViewById(R.id.startRecordButton)
        stopRecordButton = findViewById(R.id.stopRecordButton)
        playRecordButton = findViewById(R.id.playRecordButton)
        recordListView = findViewById(R.id.recordListView)

        // Khởi tạo adapter để hiển thị danh sách file ghi âm
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, records)
        recordListView.adapter = adapter

        // Thiết lập sự kiện cho các nút bấm
        startRecordButton.setOnClickListener { startRecording() }
        stopRecordButton.setOnClickListener { stopRecording() }
        playRecordButton.setOnClickListener { playRecording() }
        recordListView.setOnItemClickListener { _, _, position, _ ->
            playSelectedRecording(records[position])
        }

        // Kiểm tra quyền trước khi thực hiện ghi âm
        checkPermissions()
        // Tải danh sách các file ghi âm đã lưu
        loadRecordings()
    }

    // Hàm kiểm tra quyền RECORD_AUDIO và WRITE_EXTERNAL_STORAGE
    private fun checkPermissions() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val granted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!granted) {
            ActivityCompat.requestPermissions(this, permissions, 100)
        }
    }

    // Hàm bắt đầu ghi âm
    private fun startRecording() {
        // Tạo tên file ghi âm theo thời gian hiện tại
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "record_$timeStamp.3gp")
        fileName = file.absolutePath

        // Cấu hình MediaRecorder để ghi âm
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            prepare()
            start()
        }

        // Ẩn nút bắt đầu và hiển thị nút dừng
        startRecordButton.visibility = Button.GONE
        stopRecordButton.visibility = Button.VISIBLE
        Toast.makeText(this, "Đang ghi âm...", Toast.LENGTH_SHORT).show()
    }

    // Hàm dừng ghi âm
    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null

        // Lưu file ghi âm vào MediaStore
        saveRecordingToMediaStore(fileName)

        // Hiển thị lại nút bắt đầu, ẩn nút dừng và hiển thị nút phát
        startRecordButton.visibility = Button.VISIBLE
        stopRecordButton.visibility = Button.GONE
        playRecordButton.visibility = Button.VISIBLE

        Toast.makeText(this, "Ghi âm đã lưu!", Toast.LENGTH_SHORT).show()
        loadRecordings()
    }

    // Hàm lưu file ghi âm vào MediaStore để hiển thị trong thư viện nhạc
    private fun saveRecordingToMediaStore(filePath: String) {
        val fileName = File(filePath).name
        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp")
            put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings")
            put(MediaStore.Audio.Media.IS_PENDING, 1) // Đánh dấu file đang được ghi
        }

        val uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                File(filePath).inputStream().copyTo(outputStream) // Sao chép file vào MediaStore
            }
            values.clear()
            values.put(MediaStore.Audio.Media.IS_PENDING, 0) // Đánh dấu file đã xong
            contentResolver.update(it, values, null, null)
        }
    }

    // Hàm phát lại file ghi âm mới nhất
    private fun playRecording() {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(fileName)
            prepare()
            start()
        }
        Toast.makeText(this, "Đang phát lại...", Toast.LENGTH_SHORT).show()
    }

    // Hàm phát file ghi âm được chọn từ danh sách
    private fun playSelectedRecording(filePath: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }
        Toast.makeText(this, "Đang phát: $filePath", Toast.LENGTH_SHORT).show()
    }

    // Hàm tải danh sách các file ghi âm đã lưu
    private fun loadRecordings() {
        records.clear()
        val musicDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        musicDir?.listFiles()?.forEach {
            if (it.extension == "3gp") {
                records.add(it.absolutePath)
            }
        }
        adapter.notifyDataSetChanged()
    }
}
