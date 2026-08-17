# Implementation Plan - Notification System & UI Insight Upgrade

Initialize a native notification system and refine the Home screen (Totais!) with smarter expandable cards, including category initials inside colored dots for instant recognition.

## User Review Required

> [!IMPORTANT]
> **Notifications Permission:** On Android 13 (API 33) and above, a permission dialog will appear immediately when opening the app to allow "Pillora Money" to send notifications.
>
> [!NOTE]
> **Category Initials:** I will use the following shorthand: 'E' (Entrada), 'S' (Saída), 'D' (Diário), 'C' (Cartão), 'Ec' (Economia). These will appear inside the colored circles.

## Proposed Changes

### 1. Notification System
* **[NEW] [PilloraNotificationManager.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/notifications/PilloraNotificationManager.kt)**:
    * Create a singleton class to manage notification channels (Alerts, Reminders).
    * Implement helper functions to post test notifications and clear them.
* **[MODIFY] [MainActivity.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/MainActivity.kt)**:
    * Implement the permission request logic using `ActivityResultLauncher`.
    * Trigger two test notifications ("Bem-vindo" and "Dica do dia") after permission is granted.

### 2. Home Screen (Totais!) Expansion
* **[MODIFY] [HomeScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/HomeScreen.kt)**:
    * **Category Dots:** Update the `Box` rendering the colored dots to include a `Text` element with the category initial.
    * **Expandable Custo de Vida:** Add `AnimatedVisibility` and a breakdown of bills, daily expenses, and card usage.
    * **Expandable Diário Médio:** Show a detailed comparison between Planned (Goal) vs. Real, including the "gap" amount and status.
* **[MODIFY] [HomeViewModel.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/viewmodels/HomeViewModel.kt)**:
    * Ensure the `savingsPercentage` calculation correctly uses the `savingsGoal` from the database.

### 3. Savings Integration (FIX)
* **[MODIFY] [HomeViewModel.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/viewmodels/HomeViewModel.kt)**:
    * Fix the flow observation for all categories to ensure "Economia" transactions from any source are accounted for in the Home totals.

## Verification Plan

### Manual Verification
1. **Notifications:** Open the app, grant permission, and check the notification shade for the two test alerts.
2. **Category Dots:** Check the Performance card; verify that circles have letters inside (e.g., 'E', 'S').
3. **Expandables:** Click on "Custo de Vida" and "Diário Médio" to verify they show the detailed breakdown correctly.
4. **Savings Sync:** Add an economy item in the Spreadsheet and verify the "Economizado" percentage on Home updates correctly.
