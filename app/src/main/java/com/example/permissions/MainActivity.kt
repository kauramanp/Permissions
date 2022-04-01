package com.example.permissions

import android.Manifest
import android.R.attr.data
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.permissions.databinding.ActivityMainBinding
import java.security.Permissions


class MainActivity : AppCompatActivity() {
    private  val TAG = "MainActivity"
    lateinit var binding : ActivityMainBinding
     var permissionsArray:ArrayList<Permissions> = ArrayList()
    var showPermissionDialog: Boolean = false
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Log.e(TAG, "permission granted")
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.e(TAG, "permission declined")
                var alertDialog = AlertDialog.Builder(this)
                alertDialog.apply {
                    setTitle("Permission required")
                    setMessage("Permission required to run the app")
                    setCancelable(false)
                    setPositiveButton("Ok"){_,_-> requestPermission()}
                }
                alertDialog.show()

            }
        }
    val mulRequestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            for(its in it){
                Log.e(TAG, "key ${its.key} value ${its.value}")
                if(its.value){
                    Log.e(TAG, "permission granted")
                }else {
                    showPermissionDialog = true
                }
            }
            if(showPermissionDialog){
                requestPermission()
            }
        }

    val getImage =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            Log.e(TAG, "it uri ${it.path}")
            binding.ivImage.setImageURI(it)
        }

    private fun requestPermission() {
        Log.e(TAG," request permission")
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetImage.setOnClickListener {
            getImage.launch("image/*")
        }
        binding.btnSinglePermission.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    Log.e(TAG, "permission granted when")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)->{
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
        binding.btnMultiplePermission.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                        this,
                Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED-> {
                    // You can use the API that requires the permission.
                    Log.e(TAG, "permission granted when")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)->{
                    Log.e(TAG, " rationale")
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    mulRequestPermissionLauncher.launch(
                       arrayOf( Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA))
                }
            }
        }




    }
}