# 🔐 Java Password Manager — Extended

An extended version of [password-manager](https://github.com/chaukz/password-manager), upgraded from a terminal-based CLI tool to a full Swing GUI application with SQLite persistence and stronger AES/CBC encryption.

---

## ✨ What's New vs V1

| Feature | V1 (Terminal) | V2 (This repo) |
| --- | --- | --- |
| Interface | Terminal CLI | Swing GUI with dark mode |
| Storage | Flat `.dat` file | SQLite database |
| Encryption | AES/ECB (no IV) | AES/CBC with random IV per entry |
| Master password | Hardcoded hash in source | Set by user on first run, stored in DB |
| First-run setup | None | Setup screen to create master password |
| Duplicate prevention | ✅ | ✅ |
| Search | Manual input | Live search bar |

---

## ✨ Features

* 🔐 **Master Password Authentication** — SHA-256 hashed, stored in SQLite, 3-attempt lockout
* 🔑 **First-Run Setup** — Set your own master password on launch, no hardcoded credentials
* 🔒 **AES/CBC Encryption** — Each password encrypted with a unique random IV
* 💾 **SQLite Persistence** — All entries stored in a local `vault.db` database
* 🖥️ **Dark Mode GUI** — Clean Swing interface with indigo accent theme
* 🔍 **Live Search** — Filter entries by website in real time
* 👁️ **Show/Hide Passwords** — Toggle password visibility in the table
* ✅ **Full CRUD** — Add, update, delete entries with confirmation dialogs
* 🛡️ **Input Validation** — All fields validated before saving

---

## 🏗️ Project Structure

```
password-manager-extended/
├── src/
│   ├── Main.java             # Entry point — initializes DB then launches GUI
│   ├── GUI.java              # Swing GUI — login, setup, dashboard, dialogs
│   ├── PasswordManager.java  # Core logic — CRUD with in-memory cache
│   ├── PasswordEntry.java    # Data model — website, username, password
│   ├── DatabaseManager.java  # SQLite layer — all DB reads/writes
│   └── EncryptionUtil.java   # AES/CBC encryption + SHA-256 hashing
├── data/
│   └── vault.db              # SQLite database (auto-created, gitignored)
├── sqlite-jdbc-3.51.2.0.jar  # SQLite JDBC driver
└── README.md
```

---

## 🧰 Tech Stack

| Technology | Usage |
| --- | --- |
| Java 21 | Core language |
| Swing | GUI framework |
| SQLite (via JDBC) | Local database persistence |
| AES/CBC (javax.crypto) | Password encryption at rest |
| SHA-256 (java.security) | Master password hashing |
| Base64 | Encoding encrypted bytes |

---

## 🚀 Getting Started

### Prerequisites

* Java 21 or higher
* `sqlite-jdbc-3.51.2.0.jar` (included in repo)

### Clone the repository

```bash
git clone https://github.com/chaukz/password-manager-extended.git
cd password-manager-extended/src
```

### Compile

```bash
javac -cp .:sqlite-jdbc-3.51.2.0.jar *.java
```

### Run

```bash
java -cp .:sqlite-jdbc-3.51.2.0.jar Main
```

> On Windows replace `:` with `;`

---

## 🖥️ Usage

On first launch you'll be prompted to set a master password. After that, the dashboard lets you add, update, delete, and search entries. Passwords are hidden by default — use the **Show Password** button to reveal them.

---

## 🔒 Security Notes

| Concern | How it's handled |
| --- | --- |
| Master password | SHA-256 hashed, stored in SQLite — never plain text |
| Entry passwords | AES/CBC encrypted with a unique random IV per entry |
| Brute force | 3-attempt lockout on master password |
| Empty fields | Rejected before any processing |

> ⚠️ The AES key is hardcoded for learning purposes. In production, use a secure key derivation strategy like PBKDF2.

---

## 📚 Concepts Demonstrated

* **OOP** — encapsulation, classes, getters/setters
* **Swing GUI** — JFrame, JTable, JDialog, custom renderers, dark theming
* **SQLite / JDBC** — PreparedStatements, connection management, schema creation
* **Cryptography** — AES/CBC encryption, SHA-256 hashing, random IV generation
* **In-memory caching** — cache synced with DB to avoid stale reads
* **Input validation** — empty field checks, duplicate prevention
* **Separation of concerns** — each class has a single responsibility

---

## 🗺️ Roadmap

* [x] Terminal CRUD interface
* [x] AES encryption at rest
* [x] SHA-256 master password hashing
* [x] Input validation + duplicate prevention
* [x] Swing GUI with dark mode
* [x] SQLite database persistence
* [x] AES/CBC with random IV
* [x] User-defined master password (first-run setup)
* [ ] Password strength indicator
* [ ] Password generator
* [ ] JavaFX migration

---

## 👤 Author

* GitHub: [@chaukz](https://github.com/chaukz)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).