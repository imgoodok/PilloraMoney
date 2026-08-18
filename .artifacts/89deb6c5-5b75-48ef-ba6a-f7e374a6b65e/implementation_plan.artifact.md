# Ajustes Finos de UI: TopBars, Menu Inferior e Insets

Este plano visa corrigir o posicionamento das TopBars em telas específicas, reduzir o tamanho do menu inferior e eliminar espaços indesejados entre os componentes.

## User Review Required

> [!NOTE]
> Vou reduzir a altura padrão do menu inferior para deixá-lo mais compacto e ajustar os offsets do botão "+" e dos ícones conforme solicitado.
>
> Para as TopBars, removerei os insets automáticos que as deixavam "baixas" (com muito espaço no topo) para alinhar com o estilo das outras telas.

## Proposed Changes

### 1. Ajuste de Telas (TopBars)

#### [MODIFY] [SettingsScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/SettingsScreen.kt)
- Adicionar `windowInsets = WindowInsets(0.dp)` no `TopAppBar` para remover o preenchimento automático da barra de status que a deixava baixa.

#### [MODIFY] [SubscriptionScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/SubscriptionScreen.kt)
- Adicionar `windowInsets = WindowInsets(0.dp)` no `TopAppBar`.

### 2. Menu Inferior (PilloraBottomBar)

#### [MODIFY] [PilloraBottomBar.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/components/PilloraBottomBar.kt)
- Reduzir a altura da `NavigationBar` para `64.dp`.
- Ajustar o offset do `FloatingActionButton` (botão "+") para `y = (-12).dp` (abaixando-o).
- Ajustar o offset dos ícones (`BottomNavItem`) para `y = (-10).dp` (subindo-os dentro da barra menor).

### 3. Ajuste de Insets e Espaços (PilloraApp)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/MainActivity.kt)
- No `Scaffold` principal do `PilloraApp`, adicionar `contentWindowInsets = WindowInsets(0.dp)` para garantir que o conteúdo fique "rente" ao menu inferior sem a faixa preta.

## Verification Plan

### Manual Verification
1. Abrir a tela de Configurações e Assinatura: Verificar se a TopBar subiu e está alinhada com o topo (respeitando apenas o necessário).
2. Observar a parte inferior de qualquer tela: Garantir que não existe uma faixa preta entre o conteúdo e o menu.
3. Verificar o menu inferior: Confirmar que está mais fino, com o botão "+" mais baixo e os ícones mais altos.
