package com.example.take_picure_by_camera_practice

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.take_picure_by_camera_practice.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_CAMERA_PERMISSION = 1001
    private val get_img_requestCode = 1001
    private val Perimisiions = arrayListOf<String>("android.perimisiion.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE")
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

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == get_img_requestCode && resultCode == RESULT_OK){
            img_Uri = data?.data
            Picasso.get().load(img_Uri).into(binding.ImgView)
        }
    }

    //사진 이미지뷰에 출력
    private fun Set_Picture(){

    }
    private fun Cheack_Permisiion(){

    }
}