package com.example.take_picure_by_camera_practice

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.take_picure_by_camera_practice.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val get_img_requestCode = 1001
    private val Permissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )
    private lateinit var currentPhotoPath: String
    private var img_Uri: Uri? = null
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        //사진가져오기
        binding.GetPictureByGallery.setOnClickListener {
            val get_image = Intent(Intent.ACTION_PICK)
            get_image.setType("image/*")
            startActivityForResult(get_image, get_img_requestCode)
        }
        //사진 찍기
        binding.TakePictureGtn.setOnClickListener {
            Take_Picture()
        }
    }
    private fun Permission_Check(){
        var Rejected_Permission = ArrayList<String>()
        for(permission in Permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                Rejected_Permission.add(permission)
            }
        }
        if(Rejected_Permission.isNotEmpty()){
            val array = arrayOfNulls<String>(Rejected_Permission.size)
            ActivityCompat.requestPermissions(this, Rejected_Permission.toArray(array), REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (grantResults.isEmpty()){
                    for ((i, permission) in  permissions.withIndex()){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            Log.i("TAG", "${permission}을 거절하셨습니다.")
                        }
                    }
                }
            }
        }
    }
    private fun Take_Picture(){
       Permission_Check()
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try{
                    createImageFile()
                }catch (e: Exception){
                    null
                }
                photoFile?.also {
                    val photoUri: Uri = FileProvider.getUriForFile(this, "com.example.take_picure_by_camera_practice.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
    private fun createImageFile(): File{
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile("JPEG_${timestamp}_", ".jpeg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == get_img_requestCode && resultCode == RESULT_OK){
            img_Uri = data?.data
            Picasso.get().load(img_Uri).into(binding.ImgView)
        }else if(requestCode == REQUEST_IMAGE_CAPTURE){
                if(resultCode == RESULT_OK){
                    val bitmap : Bitmap
                    val file = File(currentPhotoPath)
                    if(Build.VERSION.SDK_INT < 28){
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
                        binding.ImgView.setImageBitmap(bitmap)
                    }else{
                        val decode = ImageDecoder.createSource(
                            this.contentResolver, Uri.fromFile(file)
                        )
                        bitmap = ImageDecoder.decodeBitmap(decode)
                        binding.ImgView.setImageBitmap(bitmap)
                    }
                    savePhoto(bitmap)
                }
        }
    }

    private fun savePhoto(bitmap: Bitmap) {
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/"
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "$timestamp.jpeg"
        val folder = File(folderPath)
        if(!folder.isDirectory){
            folder.mkdir()
        }
        val out = FileOutputStream(folderPath + fileName)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        Toast.makeText(this@MainActivity, "사진 저장 완료.", Toast.LENGTH_SHORT).show()

    }
}
