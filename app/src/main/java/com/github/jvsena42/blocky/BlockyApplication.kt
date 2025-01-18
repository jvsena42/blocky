package com.github.jvsena42.blocky

import android.app.Application
import androidx.room.Room
import com.github.jvsena42.blocky.data.datasource.WebSocketDatSourceImpl
import com.github.jvsena42.blocky.data.datasource.WebSocketDataSource
import com.github.jvsena42.blocky.db.BlockDatabase
import com.github.jvsena42.blocky.domain.repository.BlockRepository
import com.github.jvsena42.blocky.domain.repository.BlockRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BlockyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BlockyApplication)
            networkModule
            databaseModule
            repositoryModule
        }
    }
}

val networkModule = module {
    single {
        HttpClient(CIO) {
            install(WebSockets)
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single<WebSocketDataSource> {
        WebSocketDatSourceImpl(get(), get())
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            BlockDatabase::class.java,
            "block_db"
        ).build()
    }

    single { get<BlockDatabase>().blockDao }
}


val repositoryModule = module {
    single<BlockRepository> {
        BlockRepositoryImpl(get(), get())
    }
}