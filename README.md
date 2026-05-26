# UoK Bank — Banking & Mobile Money Simulator

> **Advanced Computer Programming with Java**  
> Dr. Josbert Nteziriza · University of Kigali · Group 7 · May 2026

---

## About

A Rwanda-flavoured Banking & Mobile Money Simulator built entirely in **Java 17** with a **SQLite** database and a custom **Java Swing** UI.  
Inspired by MoMo/Airtel Money flows, the app demonstrates:

- Object-Oriented design (model → DAO → service → UI layers)
- JDBC database connectivity (SQLite — zero installation required)
- Exception handling throughout every transaction
- Multi-user sessions with PIN authentication
- Real-time balance updates across screens

---

## Features

| Module | Description |
|---|---|
| **Login / Register** | Phone number + 5-digit PIN authentication |
| **Dashboard** | Live balance card, quick-action tiles |
| **Deposit** | Add funds with optional note |
| **Withdraw** | Cash withdrawal with PIN confirmation |
| **Transfer** | MoMo-style P2P transfer with live receiver lookup |
| **Agent Services** | Cash-In and Cash-Out via agent |
| **Loans** | Request credit → Admin approves → Repay |
| **Transaction History** | Colour-coded full statement |
| **Admin Panel** | Manage accounts, approve loans, view all transactions |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 (OpenJDK) |
| UI Framework | Java Swing (custom painted components) |
| Database | SQLite 3 via JDBC |
| JDBC Driver | sqlite-jdbc 3.36.0.3 (bundled in JAR) |
| Build | Plain `javac` + `jar` |

---

## How to Run

### Option A — Double-click (Mac & Windows)

```bash
java -jar UoKBank.jar
```

Or double-click `UoKBank.jar` if your system has Java 17+ associated with `.jar` files.  
The database (`data/uok_bank.db`) is created **automatically** on first launch — no setup needed.

### Option B — From IntelliJ IDEA

1. Open the `BankingSimulator/` folder as a project
2. Right-click `src/` → **Mark Directory as → Sources Root**
3. Add `lib/sqlite-jdbc.jar` to **Project Structure → Libraries**
4. Set **Run Configuration working directory** to the project root
5. Run `Main.java`

### Default Admin Account

| Field | Value |
|---|---|
| Phone | `0700000000` |
| PIN | `00000` |

---

## Project Structure

```
BankingSimulator/
├── src/
│   ├── Main.java
│   ├── model/          Account · Transaction · Loan
│   ├── storage/        DatabaseConnection · DatabaseInitializer
│   ├── dao/            AccountDAO · TransactionDAO · LoanDAO
│   └── ui/             UITheme · LoginFrame · RegisterFrame
│                       DashboardFrame · DepositFrame · WithdrawFrame
│                       TransferFrame · HistoryFrame · LoanFrame
│                       AgentFrame · AdminFrame
├── lib/
│   └── sqlite-jdbc-3.36.0.3.jar
├── UoKBank.jar          ← runnable fat JAR (Java 17+)
└── data/
    └── uok_bank.db      ← created automatically at runtime
```

---

## Group 7 Members

| # | Name |
|---|---|
| 40 | SIBOMANA Ildephonse |
| 41 | Hatangimbabazi Hilaire |
| 42 | Fredrick OBA |
| 43 | NIYIRERA Yannick |
| 44 | Jean Claude NSHIMIYIMNA |
| 45 | TURABUMUKIZA Theogene |
| 46 | MUGWANEZA Rebecca |
| 47 | MUGISHA Jean Claude |
| 48 | GATO Bernard |
| 49 | MUTUYIMANA Clement |
| 50 | Isingizwe munezero Victor |

---

## Lecturer

**Dr. Josbert Nteziriza**  
Advanced Computer Programming with Java  
University of Kigali — 2026

---

*Built with Java — for educational purposes only.*
