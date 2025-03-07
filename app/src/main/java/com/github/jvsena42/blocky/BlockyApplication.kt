package com.github.jvsena42.blocky

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.github.jvsena42.blocky.data.datasource.WebSocketDatSourceImpl
import com.github.jvsena42.blocky.data.datasource.WebSocketDataSource
import com.github.jvsena42.blocky.db.BlockDatabase
import com.github.jvsena42.blocky.domain.repository.BlockRepository
import com.github.jvsena42.blocky.domain.repository.BlockRepositoryImpl
import com.github.jvsena42.blocky.domain.util.NetworkStatusTracker
import com.github.jvsena42.blocky.presentation.screen.home.HomeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class BlockyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BlockyApplication)
            modules(
                listOf(
                    viewmodelModule,
                    networkModule,
                    databaseModule,
                    repositoryModule,
                    utilModule
                )
            )
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
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single<WebSocketDataSource> {
        WebSocketDatSourceImpl(get(), get())
    }

    single<ConnectivityManager> {
        androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
        BlockRepositoryImpl(webSocketDataSource = get(), blockDao = get())
    }
}

val viewmodelModule = module {
    viewModel<HomeViewModel> {
        HomeViewModel(
            blockRepository = get(),
            networkStatusTracker = get()
        )
    }
}

val utilModule = module {
    single<NetworkStatusTracker> { NetworkStatusTracker(get()) }
}