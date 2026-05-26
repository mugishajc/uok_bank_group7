# UoK Bank — Banking & Mobile Money Simulator

> **MSc.IT — Advanced Computer Programming with Java**  
> Dr. Josbert Nteziriza · University of Kigali · Group 7 · 2026

---

## Overview

A Rwanda-flavoured Banking & Mobile Money Simulator implemented entirely in **Java 17** with an embedded **SQLite** database and a fully custom **Java Swing** UI. The application models the MoMo / Airtel Money transaction flows that Rwandans interact with daily: deposits, withdrawals, P2P transfers, agent cash-in / cash-out, credit requests, and an admin oversight dashboard.

No external runtime is required beyond Java 17. The database is created automatically on first launch.

---

## Features

| Module | Description | Status |
|--------|-------------|--------|
| **Registration** | Create account with phone, name, account type, 5-digit PIN | ✅ |
| **Login** | Phone + PIN authentication; role-based routing (User / Admin) | ✅ |
| **Dashboard** | Live balance card, 6 quick-action tiles, logout | ✅ |
| **Deposit** | Add funds with optional memo | ✅ |
| **Withdraw** | Cash withdrawal with PIN confirmation | ✅ |
| **P2P Transfer** | Live receiver lookup, fresh-balance debit/credit | ✅ |
| **Transaction History** | Full colour-coded statement, count badge | ✅ |
| **Loans** | Request → pending → admin approval → disbursement → repayment | ✅ |
| **Agent Services** | Cash-in and cash-out on behalf of any customer account | ✅ |
| **Admin Panel** | Manage accounts (freeze / unfreeze), approve / reject loans, all transactions | ✅ |

---

## Tech Stack

| Layer | Technology | Why |
|-------|-----------|-----|
| Language | Java 17 (LTS) | Long-term support; modern switch expressions, records |
| UI | Java Swing — custom painted via `Graphics2D` | Zero external dependencies; taught in the module |
| Database | SQLite 3 (embedded) | File-based, zero-install, ACID-compliant |
| JDBC Driver | `sqlite-jdbc 3.36.0.3` (bundled in JAR) | No SLF4J dependency; drop-in standalone |
| Architecture | Model → DAO → Service → UI | Standard layering; keeps business logic out of frames |
| Testing | JUnit 4.13.2 + Hamcrest | Industry-standard; isolated temp-file database per test run |
| Build | Plain `javac` + `jar` | No Maven/Gradle — matches course tooling |

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                    UI Layer (Swing)                 │
│  LoginFrame  RegisterFrame  DashboardFrame          │
│  DepositFrame WithdrawFrame TransferFrame           │
│  HistoryFrame LoanFrame AgentFrame AdminFrame       │
└────────────────────────┬────────────────────────────┘
                         │ calls
┌────────────────────────▼────────────────────────────┐
│              Service Layer (pure logic)             │
│  BankingRules  — validation, limits, rule checks    │
└────────────────────────┬────────────────────────────┘
                         │ delegates to
┌────────────────────────▼────────────────────────────┐
│                  DAO Layer (JDBC)                   │
│  AccountDAO   TransactionDAO   LoanDAO              │
└────────────────────────┬────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────┐
│              Storage Layer (SQLite)                 │
│  DatabaseConnection   DatabaseInitializer           │
│  ~/.uokbank/uok_bank.db  (auto-created)             │
└─────────────────────────────────────────────────────┘
```

### Database Schema

```sql
accounts     (id, phone UNIQUE, full_name, pin, account_type, balance, role, is_frozen)
transactions (id, sender_phone, receiver_phone, type, amount, timestamp, note)
loans        (id, phone, amount, status, requested_at)
```

---

## Local Setup — Clone & Run

### Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Git  | any     | [git-scm.com](https://git-scm.com/) |
| Java | 17 +    | [adoptium.net](https://adoptium.net/) |

### Step 1 — Clone the repository

```bash
git clone https://github.com/mugishajc/uok_bank_group7.git
cd uok_bank_group7/BankingSimulator
```

### Step 2 — Run immediately (no build needed)

The repository contains the compiled `UoKBank.jar`. Launch it directly:

```bash
# macOS / Linux
java -jar UoKBank.jar
# or use the provided launcher script
chmod +x UoKBank.sh && ./UoKBank.sh

# Windows
UoKBank.bat
```

The SQLite database is created automatically at `~/.uokbank/uok_bank.db` on first launch.  
No installation wizard, no SQL server, no environment variables.

### Step 3 — Verify it works

Log in with the default admin account:

| Field | Value |
|-------|-------|
| Phone | `0700000000` |
| PIN   | `00000` |

### Step 4 — Run the test suite (optional)

```bash
chmod +x run_tests.sh && ./run_tests.sh
# Expected: OK (69 tests)
```

### Step 5 — Rebuild the JAR after code changes (optional)

```bash
find src -name "*.java" > sources.txt
mkdir -p out
javac -cp lib/sqlite-jdbc.jar -d out @sources.txt

mkdir -p fat
cd fat && jar xf ../lib/sqlite-jdbc.jar && cd ..
cp -r out/* fat/
jar cfm UoKBank.jar manifest.txt -C fat .

java -jar UoKBank.jar
```

---

### IntelliJ IDEA (alternative to JAR)

1. **File → Open** → select `BankingSimulator/`
2. Right-click `src/` → **Mark Directory as → Sources Root**
3. **File → Project Structure → Modules → Dependencies → + → JARs** → add `lib/sqlite-jdbc.jar`
4. **Run → Edit Configurations** → Main class: `Main`
5. Press **Shift+F10**

---

## Running the Tests

The test suite covers **all three DAO classes** (69 assertions) and the **pure business-rules layer** (41 assertions) — all against an isolated temp-file SQLite database that is created and destroyed per-test-class.

```bash
./run_tests.sh
```

Expected output:
```
[1/3] Compiling production sources...   OK
[2/3] Compiling test sources...         OK
[3/3] Running tests...

JUnit version 4.13.2
.....................................................................
Time: ~3s

OK (69 tests)
```

### Test Structure

```
test/
  TestHelper.java          — setUp/tearDown: temp SQLite DB, table creation
  AccountDAOTest.java      — 11 tests: CRUD, freeze/unfreeze, sort order
  TransactionDAOTest.java  — 10 tests: record, filter, ordering
  LoanDAOTest.java         — 10 tests: request, status transitions, ordering
  BankingRulesTest.java    — 38 tests: phone/PIN/amount validation, transfer rules
```

---

## Project Structure

```
BankingSimulator/
├── src/
│   ├── Main.java                   entry point
│   ├── model/
│   │   ├── Account.java
│   │   ├── Transaction.java
│   │   └── Loan.java
│   ├── storage/
│   │   ├── DatabaseConnection.java  SQLite URL; test-hook setTestUrl()
│   │   └── DatabaseInitializer.java create tables, seed admin
│   ├── dao/
│   │   ├── AccountDAO.java
│   │   ├── TransactionDAO.java
│   │   └── LoanDAO.java
│   ├── service/
│   │   └── BankingRules.java        pure validation — phone, PIN, amount, limits
│   └── ui/
│       ├── UITheme.java             design system (colours, fonts, logo, components)
│       ├── LoginFrame.java
│       ├── RegisterFrame.java
│       ├── DashboardFrame.java
│       ├── DepositFrame.java
│       ├── WithdrawFrame.java
│       ├── TransferFrame.java
│       ├── HistoryFrame.java
│       ├── LoanFrame.java
│       ├── AgentFrame.java
│       └── AdminFrame.java
├── test/
│   ├── TestHelper.java
│   ├── AccountDAOTest.java
│   ├── TransactionDAOTest.java
│   ├── LoanDAOTest.java
│   └── BankingRulesTest.java
├── lib/
│   ├── sqlite-jdbc.jar              runtime (bundled into UoKBank.jar)
│   ├── junit-4.13.2.jar             test-only
│   └── hamcrest-core-1.3.jar        test-only
├── UoKBank.jar                      ← runnable fat JAR (Java 17+, 9.4 MB)
├── UoKBank.sh                       Mac / Linux launcher
├── UoKBank.bat                      Windows launcher
└── run_tests.sh                     one-command test runner
```

---

## Design Decisions & Engineering Trade-offs

This section documents intentional architectural choices and known limitations — the kind of context a senior engineer records for future maintainers.

### What was prioritised

| Decision | Rationale |
|----------|-----------|
| **Embedded SQLite over H2 or file serialisation** | ACID compliance with zero installation. sqlite-jdbc bundles the native library, so the fat JAR is self-contained on all platforms. |
| **DAO pattern with `PreparedStatement`** | Prevents SQL injection. Separates persistence from UI, making DAOs independently testable. |
| **Pure service layer (`BankingRules`)** | Business rules extracted from frames → can be unit-tested without a UI or database. |
| **Absolute DB path (`~/.uokbank/`)** | Ensures the same database is used regardless of where the JAR is launched from. Eliminates the "admin not found" bug that occurs with relative paths. |
| **Fat/uber JAR packaging** | Teammates double-click one file; no classpath setup required. |
| **`Class.forName("org.sqlite.JDBC")` explicit load** | `DriverManager` auto-discovery does not work reliably with fat JARs on all JVMs; explicit registration is guaranteed. |

### Known Limitations (acceptable for a simulator)

| Limitation | Impact | Production Mitigation |
|------------|--------|----------------------|
| **No SQL transaction wrapping for transfers** | A JVM crash between the debit and credit SQL statements would leave the database inconsistent. | Wrap in a single `Connection.setAutoCommit(false)` / `commit()` block. |
| **Single-connection per operation** | Every DAO method opens and closes its own `Connection`. Under load this is inefficient. | Use a connection pool (HikariCP, c3p0). |
| **Passwords stored as plain text** | PINs are stored verbatim in the DB. | Hash with bcrypt / Argon2 before storage. |
| **No observability / audit log** | App errors go to `stderr` only. | Add structured logging (SLF4J + Logback), write audit events to a separate `audit_log` table. |
| **No session expiry** | A logged-in window stays open indefinitely. | Implement an inactivity timer that returns to `LoginFrame`. |
| **Sequential account/transaction IDs** | Auto-increment integers are predictable. | Use UUIDs for externally-visible identifiers. |
| **UI tests not automated** | Swing component behaviour is verified manually. | Integrate AssertJ-Swing or FEST-Swing for automated UI regression tests. |

---

## Group 7 Members

| # | Name |
|---|------|
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

*Built with Java · For educational purposes · University of Kigali*
