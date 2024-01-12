package com.farid.myai.repositories

import com.farid.myai.models.Answer
import com.farid.myai.models.BaseModel
import com.farid.myai.models.Message
import com.farid.myai.models.Question
import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun askQuestion(prevQuestion: List<Message>, question: String): BaseModel<Answer>

    suspend fun getMessage(): Flow<List<Message>>

    suspend fun addAnswer(answer: Message)
}