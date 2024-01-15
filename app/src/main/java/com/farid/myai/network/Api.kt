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