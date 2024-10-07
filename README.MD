# ThriftWise - Personal Wallet Application

[ThriftWise](https://github.com/jaiswalism/ThriftWise) is a Java-based personal wallet application designed to help manage your financial transactions. The app is built using **Swing** for its front end, with future plans to integrate a **SQL database** for transaction management.

## Features

- **Add Transactions**: Easily record your income and expenses.
- **View Transaction History**: Review all past transactions in a list.
- **User-Friendly GUI**: Developed with Java Swing for a simple and intuitive user experience.
- **SQL Database Support (Coming Soon)**: A future version will support SQL database integration for storing and querying transactions.

## Technology Stack

- **Java**: Core development language.
- **Swing**: For building the GUI interface.
- **JDBC**: Future use for connecting to SQL databases.
- **SQL Database**: Planned integration for transaction storage.

## Setup Instructions

### Prerequisites

- **Java Development Kit (JDK)**: Make sure [JDK 8 or above](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) is installed.
- **Git**: To clone the repository.
- **Eclipse IDE** (or any preferred IDE): Recommended for running the project.
- **MySQL or any SQL Database**: Required once the SQL integration is implemented.

### Steps to Run the Project

1. **Clone the repository**:

    ```bash
    git clone https://github.com/jaiswalism/ThriftWise.git
    ```

2. **Open in Eclipse**:

    - Use `File > Open Projects from File System` to import the project folder.

3. **Compile and Run**:

    - Right-click `Main.java` in the `src/walletapp/` folder, then click `Run As > Java Application`.

4. **Future Database Setup**:

    - SQL connection strings will be configured in `WalletService.java`.
    - Database schema to be provided in future updates.

### File Structure

```plaintext
ThriftWise/
├── src/
│   ├── walletapp/
│   │   ├── Main.java               # Application entry point
│   │   ├── models/
│   │   │   └── Transaction.java     # Model for transaction object
│   │   ├── services/
│   │   │   └── WalletService.java   # Business logic and database connection
│   │   └── ui/
│   │       └── WalletAppUI.java     # User Interface built with Swing
├── .gitignore                       # Ignore file for unnecessary files
└── README.md                        # Project description
```

## Future Improvements

- SQL database integration.
- Enhanced transaction filtering and sorting.
- UI enhancements and additional functionality.

---

Feel free to contribute to the project by creating a pull request or raising an issue!

