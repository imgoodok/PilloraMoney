# Walkthrough - Implementação de Multi-usuário (Isolamento de Dados)

O sistema foi atualizado para garantir que todas as informações financeiras sejam separadas por conta de usuário. Agora, ao trocar de conta no Firebase, o aplicativo exibirá apenas os dados pertencentes ao usuário logado.

## Alterações Realizadas

### 1. Modelos de Dados (Entities)
Todas as tabelas do banco de dados Room agora possuem um campo `userId: String`:
- **Transaction**: Atrelada ao UID do usuário.
- **FinancialGoal**: A chave primária agora é o `userId`, garantindo uma meta por conta.
- **MonthlyBalance**: Chave composta por `userId` e `monthKey`.
- **CalculatorItem**: Atrelada ao UID do usuário.

### 2. Camada de Acesso a Dados (DAOs)
Todos os DAOs foram atualizados para incluir o parâmetro `userId` em suas consultas:
- Filtragem obrigatória em `SELECT`, `UPDATE` e `DELETE`.
- Isso garante que um usuário nunca veja os dados de outro, mesmo que as informações estejam no mesmo banco de dados local.

### 3. Repositórios e Lógica de Negócio
- **TransactionRepository**: Agora injeta o `AuthRepository` e atrela automaticamente o `userId` em transações de repetição e projeções da calculadora.
- **NotificationWorker**: O worker agora identifica qual usuário está logado no momento do disparo para buscar a meta de economia e o histórico correto.

### 4. Interface e ViewModels
Todos os ViewModels foram atualizados para obter o `userId` atual via `AuthRepository` e passá-lo para os DAOs:
- `HomeViewModel`
- `SpreadsheetViewModel`
- `BalanceHorizonViewModel`
- `SavingsViewModel`
- `CalculatorViewModel`
- `AddTransactionViewModel`

### 5. Banco de Dados
- Versão do banco incrementada para **4**.
- Ativada a política `fallbackToDestructiveMigration()`, que recriará o banco na nova estrutura.

## Como Validar

1.  **Limpeza**: Ao rodar o app, os dados antigos serão apagados devido à mudança de versão do banco.
2.  **Teste de Isolamento**:
    - Logue com uma conta (ex: E-mail A) e adicione transações.
    - Faça logout e logue com outra conta (ex: Google ou E-mail B).
    - Verifique se a tela está vazia (sem os dados da conta A).
    - Adicione dados na conta B e volte para a conta A para confirmar que os dados originais permanecem lá.

> [!CAUTION]
> Os dados locais que não estavam atrelados a nenhum usuário foram removidos nesta atualização para permitir a nova arquitetura.
