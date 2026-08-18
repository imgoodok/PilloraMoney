# Correção da Troca de Idioma e Adição do Padrão do Sistema

Este plano visa corrigir o problema de sincronização na troca de idioma e adicionar a opção "Padrão do Sistema", garantindo que o Inglês seja o fallback caso o idioma do sistema não seja suportado.

## User Review Required

> [!IMPORTANT]
> A implementação usará a infraestrutura oficial do Android (`LocaleConfig` e `AppLocalesMetadataHolderService`) para garantir que a troca de idioma funcione corretamente em todas as versões do Android, especialmente no Android 13+.
>
> A opção "Padrão do Sistema" será a nova opção inicial/padrão.

## Proposed Changes

### 1. Configuração de Localidade (Android 13+)

#### [NEW] [locales_config.xml](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/res/xml/locales_config.xml)
Define os idiomas suportados pelo app para que o sistema Android possa gerenciá-los.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/AndroidManifest.xml)
- Registrar o `android:localeConfig`.
- Adicionar o `AppLocalesMetadataHolderService` para persistência automática via AppCompat.

### 2. Recursos de String

#### [MODIFY] [strings.xml (EN)](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/res/values/strings.xml)
#### [MODIFY] [strings.xml (PT)](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/res/values-pt/strings.xml)
Adicionar a string para "Padrão do Sistema".

### 3. Lógica de Aplicação (MainActivity)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/MainActivity.kt)
- Alterar o estado inicial para ler diretamente do `AppCompatDelegate.getApplicationLocales()`.
- Simplificar a lógica de `onLanguageChange` para confiar no `AppCompatDelegate` (removendo a necessidade de `SharedPreferences` manuais para localidade, já que a biblioteca faz isso se configurada no Manifest).
- Adicionar "System" como uma opção válida.

### 4. Interface de Configurações

#### [MODIFY] [SettingsScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/SettingsScreen.kt)
- Incluir a opção "Padrão do Sistema" na lista de idiomas.
- Garantir que a seleção visual (`RadioButton`) reflita corretamente o que está aplicado.

## Verification Plan

### Manual Verification
1. Abrir Configurações.
2. Selecionar "Padrão do Sistema". Verificar se o app segue o idioma do Android.
3. Se o sistema estiver em Francês, o app deve exibir Inglês (fallback).
4. Selecionar "English". Verificar se a interface muda e se o botão "English" fica marcado.
5. Selecionar "Português". Verificar se a interface muda e se o botão "Português" fica marcado.
6. Fechar e reabrir o app; a escolha deve persistir.
