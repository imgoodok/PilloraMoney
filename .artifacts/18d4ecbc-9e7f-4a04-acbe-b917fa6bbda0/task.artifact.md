# Tasks - Advanced Balance Horizon & UI Refinement

- [ ] Spreadsheet (Projeção) Refinement
    - [ ] Remove "Saldo Inicial" from top bar
    - [ ] Correct balance math: `In - (Out + Daily + Savings)`
    - [ ] Improve formatting for thousands separator and column width
    - [ ] Refine `DayDetailsDialog` to list items and allow single deletions
- [ ] Home Screen & Navigation Update
    - [ ] Add drawer opening button in top-right of `HomeScreen`
    - [ ] Update `MainActivity` navigation logic and drawer callbacks
    - [ ] Update `PilloraBottomBar`: Change "Ajustes" to "Horizonte"
- [ ] High-Density Balance Horizon Grid
    - [ ] Update `BalanceHorizonViewModel` to calculate daily balances per month
    - [ ] Redesign `BalanceHorizonScreen` as a side-by-side month list (Heatmap style)
- [ ] Verification
    - [ ] Build and run
    - [ ] Verify cumulative math across all screens
    - [ ] Test the new "Horizonte" side-by-side view
