package com.farid.myai

import android.app.Application
import androidx.room.Room
import com.farid.myai.database.AppDatabase
import com.farid.myai.network.Api
import com.farid.myai.repositories.Repository
import com.farid.myai.repositories.RepositoryImpl
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyAiApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(module {
                single {
                    Retrofit.Builder()
                        .baseUrl("https://api.openai.com/v1/chat/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                single {
                    val retrofit: Retrofit = get()
                    retrofit.create(Api::class.java)
                }
                single {
                    Room.databaseBuilder(
                        this@MyAiApp,
                        AppDatabase::class.java,
                        "dbchatgpt"
                    ).build()
                }
                single {
                    val api: Api = get()
                    val database: AppDatabase = get()

                    RepositoryImpl(api = api, dao = database.answerDao())
                } bind Repository::class
            })
        }
    }
}