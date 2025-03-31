package com.example.videoview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private lateinit var urlInput: EditText
    private lateinit var btnLoadUrl: Button
    private lateinit var btnPickVideo: Button
    private lateinit var mediaController: MediaController

    companion object {
        private const val PICK_VIDEO_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.videoView)
        urlInput = findViewById(R.id.urlInput)
        btnLoadUrl = findViewById(R.id.btnLoadUrl)
        btnPickVideo = findViewById(R.id.btnPickVideo)

        // Tạo MediaController và gán vào VideoView
        mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Xử lý phát video từ URL
        btnLoadUrl.setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isNotEmpty()) {
                val videoUri = Uri.parse(url)
                playVideo(videoUri)
            } else {
                Toast.makeText(this, "Vui lòng nhập URL", Toast.LENGTH_SHORT).show()
            }
        }

        // Chọn video từ bộ nhớ thiết bị
        btnPickVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_VIDEO_REQUEST)
        }
    }

    // Nhận kết quả khi chọn video từ MediaStore
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedVideoUri = data.data
            if (selectedVideoUri != null) {
                playVideo(selectedVideoUri)
            }
        }
    }

    // Phát video
    private fun playVideo(videoUri: Uri) {
        videoView.setVideoURI(videoUri)
        videoView.requestFocus()
        videoView.start()
    }
}
