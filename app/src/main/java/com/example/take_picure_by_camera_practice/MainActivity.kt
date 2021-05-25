package com.example.take_picure_by_camera_practice

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.ImageReader
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.take_picure_by_camera_practice.databinding.ActivityMainBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.StringBuilder
import java.security.Permission
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val get_img_requestCode = 1001
    private lateinit var currentPhotoPath: String
    private var img_Uri: Uri? = null
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        setPermission()

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
    private fun Take_Picture(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try{
                    createImageFile()
                }catch (e: Exception){
                    null
                }
                photoFile?.also {
                    val photoUri: Uri = FileProvider.getUriForFile(this, "com.gmail.moontae0317.memory.fileprovider", it)
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

    private fun setPermission() {
        val permisiion = object : PermissionListener{
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionGranted() {
                Toast.makeText(this@MainActivity, "권한이 설정되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permisiion)
            .setRationaleMessage("카메라 사용을 위해 권한을 허용해 주세요")
            .setDeniedMessage("거부되셨습니다.")
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
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
