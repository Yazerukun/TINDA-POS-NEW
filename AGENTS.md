# TINDA POS - Agent Persistent Memory & Instructions

This repository is governed by the following permanent agent guidelines and memory context, auto-loaded into every session:

## Permanent Directives
1. **Language & Tone**: All UI elements, labels, buttons, receipt texts, dialogues, notifications, error messages, and reports must be in clean, professional English.
2. **Real-time Architecture**: Maintain reactive real-time database flow (Room DB -> Flow -> StateFlow) for all reports, cash drawer reconciliations, and sales tracking. No static/mock data.
3. **Inventory & Alerts**: Preserve active tracking for Low Stock, Expired items, Expiring Soon (<30 days), and Need Date Review.
4. **Credit / Ledger**: Maintain the Credit Ledger ("Customer Credit") with SMS reminders, balance tracking, and partial payment capabilities.
5. **Store Setup**: Ensure the Store Onboarding Wizard and Store Profile settings are always available for configuring store name, owner, contact details, cash drawer float, and receipt messages.

## Memory Log & Decisions
- Converted entire user interface from mixed Tagalog/Cebuano to standard professional English.
- Set up initial float and cash drawer reconciliation in USD/PHP (₱) currency formatting.
- Persistent preferences stored in `SharedPreferences` for store identity.
