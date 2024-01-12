package com.farid.myai.repositories

import com.farid.myai.database.AnswerDao
import com.farid.myai.database.AnswerEntity
import com.farid.myai.models.Answer
import com.farid.myai.models.BaseModel
import com.farid.myai.models.Message
import com.farid.myai.models.Question
import com.farid.myai.network.Api
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepositoryImpl (private val api: Api, private val dao: AnswerDao) : Repository {
    override suspend fun askQuestion(
        prevQuestion: List<Message>,
        question: String
    ): BaseModel<Answer> {
        try {
            api.askQuestion(
                question = Question(
                    message = prevQuestion + Message(
                        role = "user",
                        content = question
                    )
                )
            ).also { response ->
                return if(response.isSuccessful) {
                    BaseModel.Success(data = response.body()!!)
                } else {
                    BaseModel.Error(response.errorBody()?.string().toString())
                }
            }
        }catch (e: Exception) {
            return BaseModel.Error(e.message.toString())
        }
    }

    override suspend fun getMessage(): Flow<List<Message>> {
        return dao.getAnswer().map { value ->
            value.map { entity ->
                Message(role = entity.role, content = entity.content)
            }
        }
    }

    override suspend fun addAnswer(answer: Message) {
        dao.addAnswer(AnswerEntity(role = answer.role, content = answer.content))
    }

}