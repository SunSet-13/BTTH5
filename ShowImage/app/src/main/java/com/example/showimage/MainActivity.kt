package com.example.showimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    private lateinit var edtUrl: EditText
    private lateinit var btnDownload: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var imgDownloaded: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        edtUrl = findViewById(R.id.edtUrl)
        btnDownload = findViewById(R.id.btnDownload)
        progressBar = findViewById(R.id.progressBar)
        imgDownloaded = findViewById(R.id.imgDownloaded)

        btnDownload.setOnClickListener {
            val url = edtUrl.text.toString().trim()

            if (url.isNotEmpty()) {
                DownloadImageTask().execute(url)
            } else {
                Toast.makeText(this, "Vui lòng nhập URL ảnh!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class DownloadImageTask : AsyncTask<String, Int, Bitmap?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
            imgDownloaded.visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): Bitmap? {
            val imageUrl = params[0]
            var bitmap: Bitmap? = null
            var connection: HttpURLConnection? = null
            var inputStream: InputStream? = null

            try {
                val url = URL(imageUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                // Kiểm tra response code
                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e("DownloadImageTask", "Lỗi HTTP: ${connection.responseCode}")
                    return null
                }

                inputStream = connection.inputStream
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                Log.e("DownloadImageTask", "Lỗi tải ảnh", e)
            } finally {
                inputStream?.close()
                connection?.disconnect()
            }
            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            progressBar.visibility = View.GONE

            if (result != null) {
                imgDownloaded.setImageBitmap(result)
                imgDownloaded.visibility = View.VISIBLE
            } else {
                Toast.makeText(this@MainActivity, "Tải ảnh thất bại!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
