````markdown
# 🎉 ClubConnect – University Clubs Management System

<div align="center">

# 🎓 ClubConnect

### A Java Swing Desktop Application for Managing University Clubs, Events, Memberships, and Resources

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue?style=for-the-badge)
![JDBC](https://img.shields.io/badge/JDBC-Database-success?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Platform](https://img.shields.io/badge/Platform-Windows%20Desktop-blueviolet?style=for-the-badge)
![OOP](https://img.shields.io/badge/OOP-Object%20Oriented-orange?style=for-the-badge)
![License](https://img.shields.io/badge/License-Educational-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)
![IDE](https://img.shields.io/badge/IDE-NetBeans%20%7C%20IntelliJ-red?style=for-the-badge)

<img src="images/banner.png" width="100%" alt="ClubConnect Banner">

---

## 📖 Overview

ClubConnect is a modern Java Swing desktop application developed to simplify the management of university clubs. The system enables administrators, club leaders, and students to collaborate through a centralized platform for managing clubs, memberships, events, resources, budgets, and notifications.

The application follows Object-Oriented Programming principles and integrates with a MySQL database using JDBC to provide secure and efficient data management.

---

# ✨ Features

## 👤 User Management

- User Registration
- Secure Login
- SHA-256 Password Encryption
- Role-Based Authentication
- User Approval Workflow
- Profile Management

---

## 👨‍💼 Administrator Features

- Manage Users
- Create Clubs
- Edit Clubs
- Delete Clubs
- Promote Club Leaders
- Approve Membership Requests
- Manage Events
- Manage Budgets
- Export Database Backup
- Generate Reports
- View System Statistics

---

## 👨‍🏫 Club Leader Features

- Manage Club Members
- Create Events
- Manage Discussions
- Request Resources
- Manage Club Activities
- View Club Dashboard
- View Member Statistics

---

## 👨‍🎓 Member Features

- Register Account
- Join Clubs
- Leave Clubs
- View Events
- RSVP to Events
- Receive Notifications
- Participate in Club Discussions

---

# 📊 Modules

- Authentication
- User Management
- Club Management
- Membership Management
- Event Management
- Budget Management
- Resource Booking
- Notification Management
- Reports
- Database Backup

---

# 🏗️ System Architecture

```text
                    +---------------------+
                    |     Login System    |
                    +----------+----------+
                               |
              +----------------+----------------+
              |                                 |
       Authentication                    Authorization
              |                                 |
      +-------+--------+--------+---------------+
      |                |                        |
   Administrator    Club Leader             Member
      |                |                        |
      +----------------+------------------------+
                       |
              Club Management Services
                       |
      +---------+------+-------+---------+
      |         |              |         |
 Membership   Events      Resources   Reports
      |         |              |         |
      +---------+------+-------+---------+
                       |
                  MySQL Database
```

---

# 🛠️ Technologies Used

| Technology | Description |
|------------|-------------|
| Java 17 | Programming Language |
| Java Swing | Desktop GUI |
| JDBC | Database Connectivity |
| MySQL | Database |
| SHA-256 | Password Encryption |
| CardLayout | Navigation |
| Object-Oriented Programming | Software Design |

---

# 📂 Project Structure

```text
ClubConnect
│
├── src
│   ├── clubconnect
│   │
│   ├── dao
│   │   ├── UserDAO.java
│   │   ├── ClubDAO.java
│   │   ├── MembershipDAO.java
│   │   └── EventDAO.java
│   │
│   ├── models
│   │   ├── User.java
│   │   ├── Club.java
│   │   ├── Event.java
│   │   ├── Membership.java
│   │   └── Notification.java
│   │
│   ├── service
│   │   ├── UserService.java
│   │   ├── ClubService.java
│   │   └── EventService.java
│   │
│   ├── ui
│   │   ├── MainFrame.java
│   │   ├── LoginPanel.java
│   │   ├── RegisterPanel.java
│   │   ├── AdminPanel.java
│   │   ├── LeaderPanel.java
│   │   └── MemberPanel.java
│   │
│   ├── util
│   │   ├── Config.java
│   │   └── HashUtil.java
│   │
│   └── db
│       ├── ConnectionPool.java
│       └── BackupHandler.java
│
├── database
│
├── images
│
├── backups
│
└── README.md
```

---

# 📸 Screenshots

## 🔐 Login Screen

![](images/login.png)

---

## 👨‍💼 Administrator Dashboard

![](images/admin-dashboard.png)

---

## 👨‍🏫 Club Leader Dashboard

![](images/leader-dashboard.png)

---

## 👨‍🎓 Member Dashboard

![](images/member-dashboard.png)

---

## 📅 Event Management

![](images/events.png)

---

## 🏛️ Club Management

![](images/clubs.png)

---

## 📊 Reports

![](images/reports.png)

---

# 🚀 Installation

## Clone Repository

```bash
git clone https://github.com/hitman1c/ClubConnect.git
```

## Navigate into Project

```bash
cd ClubConnect
```

## Configure Database

Update the following file:

```
clubconnect/util/Config.java
```

```java
MYSQL_SERVER_URL = "jdbc:mysql://localhost:3306/";
DB_NAME = "clubconnect_db";
MYSQL_USER = "root";
MYSQL_PASSWORD = "password";
```

## Compile

```bash
javac -cp ".;mysql-connector-j.jar" -d out src/**/*.java
```

## Run

```bash
java -cp "out;mysql-connector-j.jar" clubconnect.ui.MainFrame
```

---

# 💾 Database

Main tables used by the application:

- users
- clubs
- memberships
- events
- budgets
- resources
- attendance
- notifications
- discussions
- feedback

---

# 🔐 Security Features

- SHA-256 Password Encryption
- User Authentication
- Role-Based Authorization
- Prepared SQL Statements
- Secure Database Backup

---

# 📈 Future Improvements

- REST API
- Mobile Application
- QR Code Attendance
- Cloud Deployment
- Email Notifications
- PDF Reports
- Analytics Dashboard
- Real-Time Chat
- Calendar Integration
- Two-Factor Authentication

---

# 🎯 Learning Outcomes

This project demonstrates knowledge of:

- Java Programming
- Java Swing
- Object-Oriented Programming
- JDBC
- MySQL
- MVC Design
- CRUD Operations
- Database Design
- Authentication
- Desktop Software Development

---

# 🤝 Contributing

Contributions are welcome.

1. Fork this repository.
2. Create your feature branch.
3. Commit your changes.
4. Push your branch.
5. Open a Pull Request.

---

# 📜 License

This project is released for educational purposes.

---

# 👨‍💻 Developer

## Seabata Jeremiah Sechaba

**Bachelor of Science (Honours) in Computing**  
**Software Engineering Student**

📍 Maseru, Lesotho

🌐 GitHub

https://github.com/hitman1c

💼 Portfolio

https://hitman1c.github.io/Africa-Code-Academy-Builders/

---

<div align="center">

### ⭐ If you found this project useful, please give it a Star!

Made with ❤️ using Java & MySQL

</div>
````
