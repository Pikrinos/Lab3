package com.example.selfi

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import com.example.selfi.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        Log.d(MainActivity::class.simpleName,"Permission result: $it")
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        Log.d(MainActivity::class.simpleName,"Picture: $it")
        if(it){
            binding.imageView.setImageURI(mySelfie)
        }
    }

    private lateinit var binding : ActivityMainBinding

    private var mySelfie: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.take.setOnClickListener {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
            permissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            getTmpFileUri().let {
                mySelfie = it
                takePictureLauncher.launch(it)
            }
        }

        binding.send.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf("hodovychenko.labs@gmail.com"))
            i.putExtra(Intent.EXTRA_SUBJECT,"КПП АИ-193 Яновский С.Ю.")
            i.type = "image/png"
            i.putExtra(Intent.EXTRA_STREAM, mySelfie)
            startActivity(i)
        }
    }
    private fun getTmpFileUri(): Uri {
        val image = File.createTempFile("image", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", image)
    }
}