package com.example.pilloramoney.di

import android.content.Context
import androidx.room.Room
import com.example.pilloramoney.data.local.AppDatabase
import com.example.pilloramoney.data.local.CalculatorDao
import com.example.pilloramoney.data.local.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pillora_money_db"
        ).build()
    }

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCalculatorDao(db: AppDatabase): CalculatorDao = db.calculatorDao()
}
