package com.example.take_picure_by_camera_practice

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.take_picure_by_camera_practice.databinding.ActivityMainBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.squareup.picasso.Picasso
import java.security.Permission

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_CAMERA_PERMISSION = 1
    private val get_img_requestCode = 1001
    private val Perimisiions = arrayListOf<String>("android.perimisiion.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE")
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
        }
    }
    private fun Cheack_Permisiion(){

    }
}