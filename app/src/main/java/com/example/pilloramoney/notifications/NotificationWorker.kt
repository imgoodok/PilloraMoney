package com.example.pilloramoney.notifications

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pilloramoney.data.local.GoalDao
import com.example.pilloramoney.data.local.TransactionDao
import com.example.pilloramoney.data.repository.AuthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlin.random.Random

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao,
    private val authRepository: AuthRepository
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "NotificationWorker"
    }

    override suspend fun doWork(): Result {
        val user = authRepository.currentUser
        val userId = user?.uid
        
        Log.d(TAG, "Worker acionado. Tipo: ${inputData.getString("type")}. User ID: $userId")

        if (userId == null) {
            Log.w(TAG, "Usuário não logado. Abortando worker.")
            return Result.success() // Não disparar se não houver usuário
        }
        
        return try {
            val notificationType = inputData.getString("type") ?: "reminder"
            Log.d(TAG, "Tipo de notificação: $notificationType")
            
            when (notificationType) {
                "reminder" -> sendReminderNotification()
                "savings" -> sendSavingsNotification(userId)
                else -> {
                    Log.d(TAG, "Tipo desconhecido: $notificationType")
                    Result.retry()
                }
            }
            
            Log.d(TAG, "Notificação enviada com sucesso")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao enviar notificação", e)
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun sendReminderNotification() {
        val messages = listOf(
            "Não esqueça de lançar seus gastos do dia!" to "Clique aqui para atualizar sua projeção",
            "Já lançou seus gastos de hoje?" to "Vamos manter seu orçamento em dia",
            "Hora de registrar suas transações" to "Mantenha seu controle financeiro em dia"
        )
        
        val (title, message) = messages.random()
        
        Log.d(TAG, "Enviando notificação de lançamento: $title")
        
        PilloraNotificationManager.sendNotificationWithDeepLink(
            context = applicationContext,
            id = 101,
            title = title,
            message = message,
            deepLink = "pillora://spreadsheet"
        )
    }

    private suspend fun sendSavingsNotification(userId: String) {
        try {
            val goal = goalDao.getSavingsGoal(userId).first()
            
            if (goal != null && goal.targetValue > 0.0) {
                val savingsHistory = transactionDao.getAllSavings(userId).first()
                val totalSaved = savingsHistory.sumOf { it.value }
                val amountNeeded = goal.targetValue - totalSaved
                
                if (amountNeeded > 0) {
                    val formattedAmount = String.format("%.2f", amountNeeded).replace(".", ",")
                    val formattedTarget = String.format("%.2f", goal.targetValue).replace(".", ",")
                    
                    Log.d(TAG, "Enviando notificação de economia: $formattedAmount / $formattedTarget")
                    
                    PilloraNotificationManager.sendNotificationWithDeepLink(
                        context = applicationContext,
                        id = 102,
                        title = "Meta de Economia",
                        message = "Economize mais R$$formattedAmount para sua meta de R$$formattedTarget. Faça um lançamento ->",
                        deepLink = "pillora://savings"
                    )
                } else {
                    Log.d(TAG, "Meta já atingida, notificação não será enviada")
                }
            } else {
                Log.d(TAG, "Nenhuma meta configurada, notificação de economia não será enviada")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao enviar notificação de economia", e)
            e.printStackTrace()
        }
    }
}

