package com.example.pilloramoney

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PilloraApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(if (::workerFactory.isInitialized) workerFactory else DelegatingWorkerFactory())
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()
        // O WorkManager será inicializado automaticamente na primeira chamada a getInstance() 
        // usando a configuração fornecida pelo Configuration.Provider acima.
    }
}
