# Implementation Plan - PilloraMoney App

Create a modern, native Android app for financial management with a localized database, advanced spreadsheet-like view, and a daily expense calculator.

## User Review Required

> [!IMPORTANT]
> The central circular button (3rd space in the bottom bar) will be implemented as an "Action" button. Should it open a quick entry dialog or have a specific primary function?
>
> [!NOTE]
> For the "Spreadsheet" (Planilha) view, I will use a custom grid layout to ensure it feels like a spreadsheet while being mobile-friendly.

## Proposed Changes

### Project Configuration & Structure
* Update dependencies in `libs.versions.toml` and `app/build.gradle.kts` to include:
    * **Jetpack Navigation (Compose)**: For screen routing.
    * **Room Database**: For local persistence.
    * **Hilt (Dagger)**: For Dependency Injection.
    * **Kotlin Serialization**: For type-safe navigation.
    * **Material Icons Extended**: For better icons in the menus.
* Establish package structure:
    * `com.example.pilloramoney.data`: Room entities, DAOs, and repository.
    * `com.example.pilloramoney.di`: Hilt modules.
    * `com.example.pilloramoney.ui.screens`: Home, Planilha, Calculator.
    * `com.example.pilloramoney.ui.components`: Custom BottomBar, SideMenu, Table.
    * `com.example.pilloramoney.ui.viewmodels`: ViewModels for each screen.
    * `com.example.pilloramoney.navigation`: NavHost and Route definitions.

---

### Data Layer (Room Database)
* **[NEW] [Transaction.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/model/Transaction.kt)**: Entity for entries (Entrada, Saída, etc.).
* **[NEW] [CalculatorItem.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/model/CalculatorItem.kt)**: Entity for the daily expense calculator (weekly/monthly/daily).
* **[NEW] [AppDatabase.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/local/AppDatabase.kt)**: Room database setup.

---

### UI Components
* **[NEW] [PilloraBottomBar.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/components/PilloraBottomBar.kt)**: Custom bottom navigation with 5 slots and a central elevated FAB.
* **[NEW] [PilloraDrawer.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/components/PilloraDrawer.kt)**: Navigation drawer with user profile placeholder and navigation links.

---

### Screens
* **[NEW] [HomeScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/HomeScreen.kt)**: Dashboard with balance summaries and basic charts.
* **[NEW] [SpreadsheetScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/SpreadsheetScreen.kt)**:
    * Month navigation.
    * Smart spreadsheet with columns: Dia, Entrada, Saida, Diario, Cartão, Economia, Saldo.
    * Dynamic coloring based on balance.
* **[NEW] [CalculatorScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/CalculatorScreen.kt)**: Interface to add recurring expenses and calculate the daily average.

---

### Navigation
* **[MODIFY] [MainActivity.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/MainActivity.kt)**: Initialize Navigation and set up the main Scaffold.

## Verification Plan

### Automated Tests
* Room DAO tests for transaction persistence and recurring logic.
* Viewmodel unit tests for calculator logic.

### Manual Verification
* Deploy to emulator/device.
* Verify Navigation Drawer and Bottom Bar integration.
* Test spreadsheet month switching and data entry.
* Validate color coding for balances.
