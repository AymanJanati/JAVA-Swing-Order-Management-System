# JAVA-Swing-Order-Management-System

![Java](https://img.shields.io/badge/Java-17-blue)
![Swing](https://img.shields.io/badge/Java%20Swing-GUI-orange)
![MySQL](https://img.shields.io/badge/MySQL-Database-green)

This is a **Java Swing-based Order Management System** designed to manage orders, clients, products, and invoices. It uses a MySQL database for data storage and includes a user-friendly graphical interface.

## Features
- **Client Management**: Add, update, and view client details.
- **Product Management**: Manage product inventory.
- **Order Management**: Create and track orders.
- **Invoice Generation**: Generate invoices for orders.
- **Dark Theme**: Built with FlatLaf for a modern dark theme.

## Technologies Used
- **Java**: Core programming language.
- **Java Swing**: For the graphical user interface (GUI).
- **MySQL**: For database management.
- **Ikonli**: For FontAwesome icons in the UI.
- **FlatLaf**: For a modern and customizable look and feel.

## Prerequisites
Before running the project, ensure you have the following installed:
- **Java Development Kit (JDK) 17** or higher.
- **MySQL Server** (or XAMPP for easy MySQL setup).
- **Git** (optional, for cloning the repository).

## Setup Instructions

### 1. Clone the Repository
Clone this repository to your local machine using the following command:
```bash
git clone https://github.com/AymanJanati/JAVA-Swing-Order-Management-System.git
```

### 2. Set Up the Database
1. Start your MySQL server (e.g., using XAMPP).
2. Open phpMyAdmin or any MySQL client.
3. Create a new database (e.g., `order_management`).
4. Import the database schema and data from the `database_dump.sql` file located in the `database` folder.

### 3. Configure the Database Connection
Update the database connection details in the `DatabaseConnection` class if necessary:
```java
String url = "jdbc:mysql://localhost:3306/order_management";
String user = "root";
String password = ""; // Leave empty if no password is set
```

### 4. Run the Application
1. Navigate to the `Executable` folder in the repository.
2. Ensure all dependency JARs are in the same folder as `GestionCommandesFactures.jar`.
3. Run the JAR file using the following command:
   ```bash
   java -jar GestionCommandesFactures.jar
   ```

## Contributing
Contributions are welcome! If you'd like to contribute, please follow these steps:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeatureName`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeatureName`).
5. Open a pull request.


## Contact
For any questions or feedback, feel free to reach out:
- **Ayman Janati**  
- GitHub: [AymanJanati](https://github.com/AymanJanati)  
- Email: aymanjanati08@gmail.com
