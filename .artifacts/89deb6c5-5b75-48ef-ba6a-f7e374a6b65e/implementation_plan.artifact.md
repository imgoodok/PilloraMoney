# Plano de Correção Visual: Fade da Planilha e Setas da Home

Este plano detalha as correções para tornar o efeito de "fade" na planilha realmente visível e garantir que as setas de expansão na Home estejam perfeitas, além de estabilizar o layout geral.

## User Review Required

> [!IMPORTANT]
> Vou usar uma técnica de gradiente com cores sólidas (Branco/Preto) e opacidade alta para garantir que o fade seja notado.
>
> Vou reverter ajustes de insets que causaram o desaparecimento da planilha e as TopBars desalinhadas.

## Proposed Changes

### 1. Tela de Planilha (Efeito Fade)

#### [MODIFY] [SpreadsheetScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/SpreadsheetScreen.kt)
- **Fade de Alto Contraste**: Usar `Color.White.copy(alpha = 0.6f)` no modo Dark e `Color.Black.copy(alpha = 0.4f)` no modo Light.
- **Largura do Fade**: Definir como `50.dp` para ser bem perceptível.
- **Z-Index**: Garantir que o fade seja desenhado *após* o conteúdo do cabeçalho.

### 2. Tela Home (Setas de Expansão)

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/screens/HomeScreen.kt)
- Garantir que o ícone `ExpandMore` esteja presente e alinhado corretamente à direita ou ao lado do título em todos os cards que possuem `AnimatedVisibility`.

### 3. Estabilização de Layout (Insets)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/MainActivity.kt)
- Remover `contentWindowInsets = WindowInsets(0.dp)` que está causando o corte das TopBars.
- Definir a cor do `Scaffold` como `MaterialTheme.colorScheme.background` para eliminar qualquer faixa preta residual.

#### [MODIFY] [PilloraBottomBar.kt](file:///C:/Users/USUARIO/Desktop/GitHub Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/ui/components/PilloraBottomBar.kt)
- Ajustar altura para **72dp** e remover deslocamentos manuais dos ícones para que o indicador (pill) fique centralizado.

## Verification Plan

### Manual Verification
1. **Planilha**: Abrir a tela e ver se as bordas do cabeçalho rolável têm uma "névoa" clara/escura bem visível.
2. **Home**: Verificar se as setas aparecem em Performance, Custo de Vida e Diário Médio.
3. **Layout**: Garantir que o título "Configurações" e "Assinatura" não estejam colados no topo do celular.
