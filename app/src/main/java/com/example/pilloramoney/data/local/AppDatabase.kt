package com.example.pilloramoney.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pilloramoney.data.model.CalculatorItem
import com.example.pilloramoney.data.model.Transaction

@Database(entities = [Transaction::class, CalculatorItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun calculatorDao(): CalculatorDao
}
