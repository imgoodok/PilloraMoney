# PilloraMoney - Notifications & UI Insights Walkthrough

O **PilloraMoney** agora conta com um sistema de notificações nativo e uma interface ainda mais detalhada e intuitiva, facilitando a identificação rápida de cada métrica financeira.

## Novidades e Melhorias

### 1. Sistema de Notificações Android
- **Permissões em Tempo Real:** Ao abrir o app pela primeira vez (no Android 13+), o sistema agora solicita permissão para enviar notificações, garantindo transparência.
- **Notificações de Boas-vindas:** Implementei 2 alertas de teste que aparecem após a permissão:
    1. **Bem-vindo:** Um lembrete motivacional para começar seu planejamento.
    2. **Dica do dia:** Sugestão prática para manter seus gastos sob controle.
- **Arquitetura Pronta:** Criei o `PilloraNotificationManager` que centraliza a criação e remoção de notificações, facilitando a adição de novos alertas programados no futuro.

### 2. Identificação Visual por Iniciais
- **Círculos Inteligentes:** Para uma leitura instantânea, todos os pontos coloridos da Home agora possuem a **letra inicial** da categoria:
    - **E** (Verde): Entrada
    - **S** (Vermelho): Saída
    - **D** (Magenta): Diário
    - **Ec** (Azul): Economia
    - **C** (Laranja): Cartão
- **Consistência:** Essa identificação aparece tanto nos cards fechados quanto nas listas detalhadas (expandidas).

### 3. Dashboard "Totais!" Ultra Detalhado
- **Custo de Vida Expansível:** O card de Custo de Vida agora abre para detalhar seus gastos em Contas/Saídas fixas, Gastos Diários e Cartão de Crédito.
- **Análise do Diário Médio:** Ao expandir a seção de Diário Médio, você verá um comparativo direto:
    - **Média Planejada:** O valor definido na sua calculadora.
    - **Média Real:** O quanto você realmente gastou até o momento.
    - **Diferença:** O valor exato que você está acima ou abaixo da sua meta diária.

## Guia de Notificações para Desenvolvedor
Para criar ou remover notificações programaticamente, use as funções no `PilloraNotificationManager`:
- **Criar:** `PilloraNotificationManager.sendNotification(context, id, "Título", "Mensagem")`
- **Remover:** `PilloraNotificationManager.cancelNotification(context, id)`

---
> [!TIP]
> Use a expansão do **Diário Médio** para ajustar seus hábitos no meio do mês. Se a diferença estiver em vermelho (acima), você sabe que precisa economizar um pouco mais nos próximos dias!

> [!IMPORTANT]
> A barra de economia agora está 100% sincronizada com a sua meta real definida na tela de Detalhes de Economia.
