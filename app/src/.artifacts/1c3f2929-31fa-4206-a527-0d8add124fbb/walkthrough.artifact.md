# UI Refinements: Login, Inputs and Navigation

Several UI and UX improvements have been implemented based on user feedback.

## Changes Made

### 1. Login Screen: Google Icon
- **New Asset**: Created `ic_google_logo.xml` (standard G logo).
- **Button Update**: The "Entrar com Google" button now features the Google icon next to the text for better visual recognition.

### 2. Spreadsheet: Numeric Input Validation
- **Valor Field**: Updated the input logic in the transaction dialog to accept only numeric values (digits and a single decimal separator).
- **Separator Normalization**: Automatically converts commas to dots to ensure internal numeric consistency while supporting local keyboard input.

### 3. Bottom Navigation: Icon Alignment
- **Vertical Offset**: Applied a small negative vertical offset (`-4.dp`) to the navigation icons. This compensates for the visual gap left by the removal of labels, making the icons appear more centered and balanced within the bar.

## Verification Results

### Automated Tests
- [x] **Build**: Successfully executed `gradle assembleDebug`.

### Manual Verification
- [x] **Login Screen**: Verified the Google icon is visible and correctly colored (tinted to unspecified to keep brand colors).
- [x] **Numeric Input**: Verified that typing letters or multiple dots/commas in the "Valor" field is blocked.
- [x] **Navigation Bar**: The icons now sit slightly higher, providing a more balanced look without the text labels.

> [!NOTE]
> The input filter for "Valor" uses a regex `^\\d*[.,]?\\d*$` which is safe for currency input.
