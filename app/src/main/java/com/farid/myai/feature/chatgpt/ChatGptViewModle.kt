package com.farid.myai.feature.chatgpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farid.myai.database.AnswerEntity
import com.farid.myai.database.AppDatabase
import com.farid.myai.models.BaseModel
import com.farid.myai.models.Message
import com.farid.myai.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatGptViewModle : ViewModel(), KoinComponent{

    private val database: AppDatabase by inject()
    private val repository: Repository by inject()

    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val messages = _messages.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getMessage().collect { data ->
                _messages.update { data }
            }
        }
    }

    fun askQuestion(question: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                database.answerDao().addAnswer(
                    answerEntity = AnswerEntity(
                        role = "user",
                        content = question
                    )
                )
            }

            _loading.update { true }
            repository.askQuestion(
                prevQuestion = messages.value,
                question = question
            ).also { baseModel ->
                _loading.update { false }
                when(baseModel) {
                    is BaseModel.Success -> {
                        withContext(Dispatchers.IO) {
                            database.answerDao().addAnswer(answerEntity = AnswerEntity(
                                role = "assistant",
                                content = baseModel.data.choices.first().message.content
                            ))
                        }
                    }

                    is BaseModel.Error -> {
                        println("error: ${baseModel.error}")
                        withContext(Dispatchers.IO) {
                            database.answerDao().addAnswer(answerEntity = AnswerEntity(
                                role = "error",
                                content = baseModel.error
                            ))
                        }
                    }

                    is BaseModel.Loading -> {

                    }
                }
            }
        }
    }
}



