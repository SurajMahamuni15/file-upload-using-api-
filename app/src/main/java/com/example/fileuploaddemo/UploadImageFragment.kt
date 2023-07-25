package com.example.fileuploaddemo

import android.net.Uri
import android.os.Bundle
import android.service.carrier.CarrierMessagingService.SendMultipartSmsResult
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.fileuploaddemo.Constants.BASE_URL
import com.example.fileuploaddemo.databinding.FragmentUploadImageBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.annotations.Contract
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import kotlin.contracts.contract


class UploadImageFragment : Fragment() {
    private var _binding: FragmentUploadImageBinding? = null
    private val binding get() = _binding!!
    private lateinit var image: Uri
    private lateinit var retroInstance: UploadApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initilizeRetro()
        initializeListener()

    }

    private fun initilizeRetro() {
        retroInstance = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build().create(UploadApi::class.java)
    }

    private var contract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        image = it!!
        binding.image.setImageURI(it)
    }

    private fun initializeListener() {
        binding.selectImage.setOnClickListener { contract.launch("image/*") }
        binding.uploadImage.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {
        val fileDir = requireContext().filesDir
        val file = File(fileDir, "image.png")
        val inputStream = requireContext().contentResolver.openInputStream(image)
        val fileOutputStream = FileOutputStream(file)
        inputStream?.copyTo(fileOutputStream)

        val requestFile = RequestBody.create(
            MediaType.parse("image/jpg"),
            file
        )

        val part = MultipartBody.Part.createFormData("profile", file.name, requestFile)
        lifecycleScope.launch(Dispatchers.IO) {
            val responce = retroInstance.uploadImg(part)

            Toast.makeText(requireContext(), "file successfully uploaded!", Toast.LENGTH_SHORT)
                .show()
            Log.e("responce", responce.toString())
        }

    }


}