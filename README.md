# 🔐 Java Password Manager

A secure, terminal-based password manager built in Java. Stores encrypted passwords locally with master password authentication and full CRUD functionality via a command-line interface.

---

## ✨ Features

- 🔐 **Master Password Authentication** — SHA-256 hashed master password with 3-attempt lockout
- 🔒 **AES Encryption** — Passwords encrypted before being written to disk
- 💾 **Persistent Storage** — Entries saved to a local `.dat` file and loaded on every startup
- ✅ **Duplicate Prevention** — Blocks adding the same website twice
- ✏️ **Full CRUD** — Add, update, delete, and search entries
- 🛡️ **Input Validation** — Handles bad input gracefully without crashing

---

## 🏗️ Project Structure

```
password-manager/
├── src/
│   ├── Main.java             # Entry point — terminal menu loop
│   ├── PasswordManager.java  # Core logic — add, delete, search, update
│   ├── PasswordEntry.java    # Data model — website, username, password
│   ├── FileManager.java      # File I/O — save/load with encryption
│   └── EncryptionUtil.java   # AES encryption + SHA-256 hashing
├── data/
│   └── passwords.dat         # Encrypted password storage (auto-created)
└── README.md
```

---

## 🧰 Tech Stack

| Technology | Usage |
|---|---|
| Java 21 | Core language |
| AES (javax.crypto) | Password encryption at rest |
| SHA-256 (java.security) | Master password hashing |
| BufferedReader / FileWriter | File persistence |
| Base64 | Encoding encrypted bytes as readable strings |

---

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- No external dependencies — pure Java standard library

### Clone the repository

```bash
git clone https://github.com/chaukz/password-manager.git
cd password-manager
```

### Compile

```bash
cd src
javac *.java
```

### Run

```bash
java Main
```

---

## 🖥️ Usage

```
Enter master password: ••••••••
Access granted!

=== Password Manager ===
1. Add Entry
2. Delete Entry
3. Search Entry
4. List Entries
5. Update Entry
6. Exit
Enter your choice:
```

---

## 🔑 Default Master Password

```
BoniSecretKey123
```

> ⚠️ To change it: hash your new password with `EncryptionUtil.hashPassword("yourPassword")` and replace the hash in `Main.java` → `MASTER_PASSWORD_HASH`

---

## 🔒 Security Notes

| Concern | How it's handled |
|---|---|
| Master password | SHA-256 hashed — never stored in plain text |
| Entry passwords | AES-128 encrypted before writing to disk |
| Brute force | 3-attempt lockout on master password |
| Empty fields | Rejected before any processing |

> ⚠️ The AES key is hardcoded for learning purposes. In production, use a secure key derivation strategy like PBKDF2.

---

## 📚 Concepts Demonstrated

- **OOP** — encapsulation, classes, getters/setters
- **File I/O** — `BufferedReader`, `FileWriter`, try-with-resources
- **Cryptography** — AES encryption, SHA-256 hashing, Base64 encoding
- **Input validation** — `NumberFormatException`, empty field checks
- **Data structures** — `ArrayList`, lambda expressions (`removeIf`)
- **Separation of concerns** — each class has a single responsibility

---

## 🗺️ Roadmap

- [x] Terminal CRUD interface
- [x] AES encryption at rest
- [x] SHA-256 master password hashing
- [x] Input validation
- [x] Duplicate entry prevention
- [ ] Swing GUI with dark mode
- [ ] JavaFX migration
- [ ] Password strength indicator
- [ ] Password generator

---

## 👤 Author

**Your Name**
- GitHub: [@chaukz](https://github.com/chaukz)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).