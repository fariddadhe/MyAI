package com.farid.myai.network

import com.farid.myai.models.Answer
import com.farid.myai.models.Question
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

const val APIKEY = "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"

interface Api {

    @POST("completions")
    @Headers("Authorization: Bearer $APIKEY", "Content-Type: application/json")
    suspend fun askQuestion(
        @Body question: Question
    ): Response<Answer>
}