# Plano de Multi-usuário (Isolamento de Dados)

Este plano detalha as alterações necessárias para atrelar todos os dados do aplicativo (transações, metas, projeções e cálculos) à conta do usuário logado no Firebase. Isso garante que, ao trocar de conta, as informações exibidas sejam exclusivas daquele usuário.

## User Review Required

> [!IMPORTANT]
> A estrutura do banco de dados local será alterada. Para evitar problemas de migração complexos em fase de desenvolvimento, o aplicativo usará `fallbackToDestructiveMigration()`. **Isso significa que os dados locais atuais serão apagados uma única vez após esta atualização.**

## Proposed Changes

### 1. Modelos de Dados (Entities)

Adicionar o campo `userId: String` em todas as entidades para permitir a filtragem por proprietário.

#### [MODIFY] [Transaction.kt](file:///C:/Users/USUARIO/Desktop/GitHub%20Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/model/Transaction.kt)
- Adicionar `val userId: String`.

#### [MODIFY] [FinancialGoal.kt](file:///C:/Users/USUARIO/Desktop/GitHub%20Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/model/FinancialGoal.kt)
- Adicionar `val userId: String`.
- Alterar chave primária ou lógica de ID para suportar uma meta por usuário.

#### [MODIFY] [MonthlyBalance.kt](file:///C:/Users/USUARIO/Desktop/GitHub%20Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/model/MonthlyBalance.kt)
- Adicionar `val userId: String`.
- Atualizar a chave primária para ser composta (`userId` + `monthKey`).

#### [MODIFY] [CalculatorItem.kt](file:///C:/Users/USUARIO/Desktop/GitHub%20Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/model/CalculatorItem.kt)
- Adicionar `val userId: String`.

### 2. Camada de Acesso a Dados (DAOs)

Atualizar todas as consultas SQL para incluir a cláusula `WHERE userId = :userId`.

#### [MODIFY] [TransactionDao.kt](file:///C:/Users/USUARIO/Desktop/GitHub%20Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/local/TransactionDao.kt)
#### [MODIFY] [GoalDao.kt](file:///C:/Users/USUARIO/Desktop/GitHub%20Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/local/GoalDao.kt)
#### [MODIFY] [CalculatorDao.kt](file:///C:/Users/USUARIO/Desktop/GitHub%20Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/local/CalculatorDao.kt)
#### [MODIFY] [MonthlyBalanceDao.kt](file:///C:/Users/USUARIO/Desktop/GitHub%20Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/local/MonthlyBalanceDao.kt)

### 3. Repositórios e ViewModels

#### [MODIFY] [TransactionRepository.kt](file:///C:/Users/USUARIO/Desktop/GitHub%20Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/repository/TransactionRepository.kt)
- Injetar `AuthRepository` para obter o `uid` atual e passá-lo para o DAO.

#### [MODIFY] [AppDatabase.kt](file:///C:/Users/USUARIO/Desktop/GitHub%20Repository/PilloraMoney/app/src/main/java/com/example/pilloramoney/data/local/AppDatabase.kt)
- Incrementar a versão do banco de dados e adicionar `fallbackToDestructiveMigration()`.

#### [MODIFY] ViewModels (Home, Spreadsheet, etc.)
- Atualizar as chamadas para os DAOs/Repositories para incluir o `userId` do usuário logado.

## Verification Plan

### Manual Verification
1. Logar com a **Conta A**, adicionar algumas transações e uma meta.
2. Fazer logout e logar com a **Conta B**.
3. Verificar se a lista de transações e metas está vazia (isolamento verificado).
4. Adicionar dados na **Conta B**, deslogar e voltar para a **Conta A**.
5. Verificar se os dados da **Conta A** reaparecem intactos.
