# Implementation Plan - Theming, Totais! & Final UX Polishing

Finalize the transition to a mobile-native experience with a dedicated Light Theme, refined Home screen (Totais!) insights, and drawer improvements.

## User Review Required

> [!IMPORTANT]
> **Theme Management:** I will implement a Theme repository to persist your choice (Light/Dark/System). This will be managed in the new "Configurações" screen.
>
> [!NOTE]
> **Performance Calculation:** "Diário Médio" logic will be refined to reflect real spending (Current Month Expenses / Days Passed) vs. your Goal.

## Proposed Changes

### 1. Theming (Light & Dark Support)
* **[MODIFY] [Color.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/theme/Color.kt)**: Define a clean light palette (Background: `#F3F4F6`, Surfaces: `#FFFFFF`).
* **[MODIFY] [Theme.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/theme/Theme.kt)**:
    * Add `LightColorScheme`.
    * Implement a global state or repository to toggle between schemes.
* **[NEW] [SettingsScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/SettingsScreen.kt)**: Screen with theme selectors.

### 2. Home (Totais!) Refinements
* **[MODIFY] [HomeScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/HomeScreen.kt)**:
    * **Rename:** Header now says "Totais!".
    * **Performance Card:** Fix missing green dot for Income. Add "Cartão" to expanded details.
    * **Custo de Vida Card:** Implement expandable details (Bills, Daily, Card).
    * **Diário Médio Card:** Expand to show detailed comparison (Real vs. Planned vs. Gap).
    * **Drawer Button:** Move to the **top-left** (as requested).
* **[MODIFY] [HomeViewModel.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/viewmodels/HomeViewModel.kt)**:
    * Implement month selection logic (`nextMonth()`, `previousMonth()`).
    * Refine daily average calculation.

### 3. Drawer & UI Cleanup
* **[MODIFY] [PilloraDrawer.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/components/PilloraDrawer.kt)**:
    * Reduce drawer width (set `maxWidth` to ~280dp).
    * Link the "Configurações" button.
* **[MODIFY] [SavingsDetailScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/SavingsDetailScreen.kt)** & **[CalculatorScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/CalculatorScreen.kt)**:
    * Fix TopBar overlap/positioning.

## Verification Plan

### Manual Verification
1. **Theming:** Toggle between Light and Dark in Settings. Verify all screens adapt.
2. **Totais! Logic:** Change the month on Home. Verify all expanded cards update their values accordingly.
3. **Drawer:** Verify the narrower width and top-left trigger.
4. **Calculations:** Verify "Diário Médio" expansion shows correct math.
