# Implementation Plan - Advanced Dashboard & Calculator Integration

Enhance the Dashboard with professional metrics and integrate the Daily Calculator directly into the financial projection with bulk management capabilities.

## User Review Required

> [!IMPORTANT]
> **Calculator Sync:** When you "Apply to Projection", the app will create a daily recurring transaction named **"Gasto Diário (Calculadora)"**. If you apply it again, it will **overwrite** (delete and recreate) the previous ones to ensure no duplicates.
>
> [!NOTE]
> **Dashboard Metrics:**
> - **Performance:** Month Balance (Income - Expenses - Savings).
> - **Cost of Living:** Sum of all non-income and non-saving transactions.
> - **Economizado:** Total categorized as "Economia".

## Proposed Changes

### Data & Logic Layer
* **[MODIFY] [TransactionDao.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/local/TransactionDao.kt)**:
    * Add `@Query("DELETE FROM transactions WHERE description = :desc AND type = :type")` to clear previous calculator entries.
* **[MODIFY] [TransactionRepository.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/repository/TransactionRepository.kt)**:
    * Add `applyCalculatorValueToProjection(value: Double)`: Clears existing "Gasto Diário (Calculadora)" entries and generates new ones for 10 years.
    * Add `clearCalculatorProjection()`: Removes all calculator-driven entries.

### ViewModels
* **[MODIFY] [CalculatorViewModel.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/viewmodels/CalculatorViewModel.kt)**:
    * Add `applyToProjection()` and `clearProjection()` methods using the repository.
* **[MODIFY] [HomeViewModel.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/viewmodels/HomeViewModel.kt)**:
    * Update `HomeUiState` to include `costOfLiving`, `savingsPercentage`, and `realDailyAverage`.

### UI Screens
* **[MODIFY] [CalculatorScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/CalculatorScreen.kt)**:
    * Add "Aplicar à Projeção" button (Primary action).
    * Add "Remover da Projeção" button (Secondary action).
* **[MODIFY] [HomeScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/HomeScreen.kt)**:
    * Redesign to match the "Cálculos do mês" layout from the screenshot.
    * Use progress bars for "Economizado".
    * Add status text (e.g., "Faltou dinheiro" if balance < 0).

## Verification Plan

### Manual Verification
1. Go to **Calculadora**, add some items.
2. Click **Aplicar à Projeção**.
3. Verify in **Projeção** (Planilha) that every day has a "Gasto Diário (Calculadora)" entry.
4. Go back to **Calculadora**, click **Remover da Projeção**.
5. Verify they are gone from the Spreadsheet.
6. Open **Dashboard** and check if "Performance" and "Custo de Vida" match your monthly totals.
