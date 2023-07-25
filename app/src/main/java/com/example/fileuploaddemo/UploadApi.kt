package com.example.fileuploaddemo

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadApi {
    @Multipart
    @POST("upload")
    suspend fun uploadImg(
        @Part image : MultipartBody.Part
    ): Imageresponce
}