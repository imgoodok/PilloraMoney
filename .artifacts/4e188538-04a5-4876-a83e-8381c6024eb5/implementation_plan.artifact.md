# Implementation Plan - Firestore Serialization & Robust Sync

Fix the issues where Calculator categories and Financial Goals are not being saved to Firestore, and ensure that edits/deletions are correctly propagated.

## User Review Required

> [!IMPORTANT]
> I will add default values to all properties in the data models (`Transaction`, `CalculatorItem`, `FinancialGoal`, `MonthlyBalance`). This ensures that Firebase Firestore can correctly serialize and deserialize these objects using its reflection-based `toObject` and `set` methods.

## Proposed Changes

### Data Models (Firestore Compatibility)

#### [MODIFY] [Transaction.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/model/Transaction.kt)
- Ensure all properties have default values (already done for some, but will verify all).

#### [MODIFY] [CalculatorItem.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/model/CalculatorItem.kt)
- Add default values for `userId`, `name`, `value`, and `frequency`.

#### [MODIFY] [FinancialGoal.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/model/FinancialGoal.kt)
- Add default value for `userId`.

#### [MODIFY] [MonthlyBalance.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/model/MonthlyBalance.kt)
- Add default values for `userId` and `monthKey`.

### Repositories (Robust Sync)

#### [MODIFY] [CalculatorRepository.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/repository/CalculatorRepository.kt)
#### [MODIFY] [GoalRepository.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/repository/GoalRepository.kt)
#### [MODIFY] [MonthlyBalanceRepository.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/repository/MonthlyBalanceRepository.kt)
#### [MODIFY] [TransactionRepository.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/repository/TransactionRepository.kt)
- Use `.set(item).await()` or `batch.commit().await()` to ensure operations complete and errors are traceable.
- In `TransactionRepository`, ensure the `category` field in the projection transactions reflects the source if necessary (though for calculator it's usually "Geral").

### Sync Logic Enhancement

#### [MODIFY] [SyncRepository.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/repository/SyncRepository.kt)
- Ensure the batch sync correctly handles the objects with the new default values.

## Verification Plan

### Automated Tests
- Verify that objects can be converted to Firestore-compatible Maps.

### Manual Verification
1. **Calculator Sync:** Add a category in the calculator, verify it appears in Firestore under `users/{uid}/calculator_items/{syncId}`.
2. **Goal Sync:** Update the savings goal, verify it updates in Firestore under `users/{uid}/goals/current_goal`.
3. **Edit Reflection:** Verify that updating a balance or transaction reflects the change in the cloud.
