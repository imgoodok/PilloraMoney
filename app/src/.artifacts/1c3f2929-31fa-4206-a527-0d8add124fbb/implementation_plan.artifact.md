# UI Refinements: Input Filtering, Bottom Bar Alignment, and Login Social Icon

This plan addresses several UI refinements requested by the user, including data validation, layout adjustment, and visual enhancement of the login screen.

## Proposed Changes

### 1. Spreadsheet Input Validation
Ensure the "Valor" field in the transaction dialog only accepts valid numeric input.

#### [MODIFY] [SpreadsheetScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/SpreadsheetScreen.kt)
- Update `onValueChange` for the `value` field in `DayDetailsDialog`.
- Implement a filter to allow only digits and at most one decimal separator (comma or dot).

### 2. Bottom Navigation Bar Alignment
Adjust the position of the icons in the bottom bar to be more vertically centered since the labels were removed.

#### [MODIFY] [PilloraBottomBar.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/components/PilloraBottomBar.kt)
- Adjust the `BottomNavItem` to better position the icons. I will try reducing the height of the `NavigationBar` or adding a small vertical offset to the icons if needed.

### 3. Google Login Icon
Add a Google logo to the "Entrar com Google" button on the login screen.

#### [NEW] [ic_google_logo.xml](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/res/drawable/ic_google_logo.xml)
- Create a vector drawable representing a simplified Google "G" logo.

#### [MODIFY] [LoginScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/LoginScreen.kt)
- Update the `OutlinedButton` for Google login to include the new icon using the `leadingIcon` parameter (or by manually adding an `Icon` composable inside the button).

## Verification Plan

### Automated Tests
- Run `gradle assembleDebug` to ensure all changes are valid.

### Manual Verification
- **Input Filtering**: Open the spreadsheet transaction dialog and try typing non-numeric characters in the "Valor" field.
- **Bottom Bar**: Verify that the icons in the bottom navigation bar are vertically centered and look better without labels.
- **Login Icon**: Check the login screen to ensure the Google icon is visible and correctly aligned within the button.
