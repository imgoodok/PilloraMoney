# PilloraMoney - Walkthrough

O app **PilloraMoney** foi implementado com sucesso seguindo as melhores práticas de desenvolvimento Android nativo.

## Funcionalidades Implementadas

### 1. Estrutura e Navegação
- **Modal Navigation Drawer:** Menu lateral estilizado com cabeçalho e links para as seções principais.
- **Custom Bottom Bar:** Menu inferior com 5 espaços, incluindo um botão central (FAB) elevado para ações rápidas.
- **Type-safe Navigation:** Uso de Kotlin Serialization para navegação entre telas.

### 2. Planilha Inteligente (`SpreadsheetScreen`)
- **Navegação Mensal:** Seletores de mês no topo para alternar entre períodos.
- **Tabela Responsiva:** Grid horizontalmente rolável com colunas para *Entrada, Saída, Diário, Cartão, Economia e Saldo*.
- **Interação Dinâmica:** Clique em qualquer célula de valor para abrir o diálogo de inserção rápida.
- **Cores Inteligentes:** O saldo muda de cor conforme o valor (de vermelho forte para verde forte).
- **Recorrência:** Opção de replicar gastos/entradas para meses futuros.

### 3. Calculadora de Gastos Diários (`CalculatorScreen`)
- **Conversão Automática:** Lista de gastos semanais ou mensais convertidos automaticamente em uma média diária.
- **Gestão de Itens:** Adição e exclusão de itens de custo fixo.

### 4. Dashboard Home (`HomeScreen`)
- **Resumo Financeiro:** Cards com saldo total, entradas e saídas do mês atual.
- **Interface Moderna:** Design limpo utilizando Material Design 3.

## Detalhes Técnicos
- **Banco de Dados Local:** Implementado com **Room** para persistência Offline.
- **Injeção de Dependência:** Utilizado **Hilt** para gerenciar ViewModels e DAOs.
- **UI:** Construída 100% em **Jetpack Compose**.
- **Versão mínima do SDK:** 24 (Android 7.0).
- **Compilação:** Atualizado para **SDK 37** para compatibilidade com as bibliotecas mais recentes.

## Como testar
1. Abra a tela de **Planilha**.
2. Clique em uma célula (ex: Saída) para adicionar um gasto.
3. Observe o **Saldo** mudar de cor automaticamente.
4. Vá para a **Calculadora** e adicione seus custos fixos para ver sua média diária.
5. Confira o resumo no **Dashboard** da Home.

---
> [!TIP]
> No futuro, a integração com Firebase permitirá sincronizar esses dados na nuvem automaticamente.

> [!NOTE]
> O botão central circular na barra inferior está pronto para receber uma ação de atalho rápido (como um "Quick Add" global).
