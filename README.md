```markdown
# ClubConnect — University Clubs Management System

Hi — I'm the author of this project. This README explains the entire codebase and how I built and organized the application so you (or I in the future) can run, maintain, and extend it.

I wrote ClubConnect as a Java Swing desktop application to manage university clubs, events, membership workflows, budgets, resources and basic notifications. The UI is split into role-based dashboards (Admin, Leader, Member) and supporting screens (login, register, club management, event management, reports, etc.). Below you'll find a full explanation of the code, database expectations, configuration, known issues I encountered (and how I fixed them), and suggestions for improvement.

Table of contents
- Project summary
- What the app does (features)
- Project structure and key files
- Data model and recommended schema
- Configuration (what to set in Config.java)
- How to build & run
- Walkthrough of each major UI panel and service
- Troubleshooting (including the NPE that occurred and the fix)
- To-do and improvements
- Security notes
- Contributing & license
```

## Project summary

ClubConnect is a single-process desktop Java application that I built using Swing + JDBC. It uses a MySQL database (or any JDBC-compatible relational DB with slight SQL changes). The app supports three main roles:

- Admin — full system management and oversight
- Leader — club-level management for the club they lead
- Member — see clubs they belong to, join/leave, and view events

The UI uses a CardLayout-based navigation pattern and themed Swing components to keep the look consistent across panels.

I intentionally kept the structure simple and used plain JDBC in DAOs for clarity and easy debugging.

---

## Key features

- User registration and login (passwords hashed with SHA-256).
- Admin dashboard: manage users, approve membership requests, promote users, create/delete/edit clubs, view events and budgets, export DB.
- Leader dashboard: club overview (members, events, files), create events, request resources, discussion board, attendance stub.
- Member dashboard: view/join/leave clubs, view upcoming events, RSVP stub.
- Membership workflow: pending → approved (managed by admin).
- Budget requests: budget table & approval panel.
- Backup utility that dumps DB contents as SQL INSERT statements.
- Lightweight connection pool (for demo; replace in production with HikariCP).

---

## Project layout (important files)

I organized the source by responsibility:

- clubconnect.models
  - POJOs used across the app: User, Club, Event, Membership, Resource, Notification, BudgetRequest, Attendance, Discussion, Feedback, etc.
- clubconnect.dao
  - DAO classes for DB access: UserDAO, ClubDAO, MembershipDAO, etc.
  - They use JDBC and Config constants.
- clubconnect.service
  - Business logic helpers, e.g., UserService (authentication, registration, admin operations).
- clubconnect.ui
  - All Swing UI classes (MainFrame, CardLayoutPanel, LoginPanel, RegisterPanel, AdminPanel, LeaderPanel, MemberPanel, ClubManagementPanel, EventManagementPanel, BudgetRequestPanel, ResourceBookingPanel, ReportsPanel, NotificationsPanel, etc.)
- clubconnect.db
  - Utilities: ConnectionPool, BackupHandler.
- clubconnect.util
  - Config (DB constants) and HashUtil (SHA-256 helper).

Main entry points:
- clubconnect.ui.MainFrame — main application window + session holder (current user).
- clubconnect.ui.CardLayoutPanel — registers all screens and controls navigation.

---

## Data model & recommended schema

Below are the tables I used and expect. Run these in your MySQL instance (or adapt to your environment).

```sql
-- users
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(255),
  role VARCHAR(50) DEFAULT 'member',
  email VARCHAR(255),
  student_id VARCHAR(100),
  approved TINYINT(1) DEFAULT 0
);

-- clubs
CREATE TABLE clubs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(100),
  description TEXT,
  created_by INT, -- references users.id (club leader)
  archived TINYINT(1) DEFAULT 0
);

-- memberships
CREATE TABLE memberships (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  club_id INT NOT NULL,
  role VARCHAR(50) DEFAULT 'member',
  status VARCHAR(32) DEFAULT 'pending' -- pending, approved, rejected
);

-- events
CREATE TABLE events (
  id INT AUTO_INCREMENT PRIMARY KEY,
  club_id INT,
  title VARCHAR(255),
  date_time DATETIME,
  venue VARCHAR(255),
  capacity INT DEFAULT 0,
  details TEXT,
  budget_requested TINYINT(1) DEFAULT 0,
  budget_status VARCHAR(50)
);

-- budgets
CREATE TABLE budgets (
  id INT AUTO_INCREMENT PRIMARY KEY,
  event_id INT,
  requested_by INT,
  amount DOUBLE,
  status VARCHAR(50),
  notes TEXT
);

-- resources
CREATE TABLE resources (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  capacity INT DEFAULT 0,
  description TEXT,
  available TINYINT(1) DEFAULT 1
);

-- discussions
CREATE TABLE discussions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  club_id INT,
  user_id INT,
  content TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- notifications
CREATE TABLE notifications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  club_id INT,
  title VARCHAR(255),
  message TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  sent TINYINT(1) DEFAULT 0
);

-- feedback (optional)
CREATE TABLE feedback (
  id INT AUTO_INCREMENT PRIMARY KEY,
  club_id INT,
  user_id INT,
  message TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- attendance (optional)
CREATE TABLE attendance (
  id INT AUTO_INCREMENT PRIMARY KEY,
  event_id INT,
  user_id INT,
  present TINYINT(1) DEFAULT 0
);
```

Notes:
- I didn't strictly enforce foreign keys in the demo code, but you can add them.
- Column names are used directly in queries in DAOs — changing them requires updating queries.

---

## Configuration

Configuration lives in `clubconnect.util.Config`. Example values to adapt:

```java
public class Config {
  public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
  public static final String MYSQL_SERVER_URL = "jdbc:mysql://localhost:3306/";
  public static final String DB_NAME = "clubconnect_db";
  public static final String MYSQL_USER = "root";
  public static final String MYSQL_PASSWORD = "secret";
  public static final String BACKUP_DIR = "backups";
}
```

Make sure your DB is created and accessible at MYSQL_SERVER_URL + DB_NAME and that the DB user has the required privileges.

---

## How I build and run the app

I developed this as a simple Java application. Build & run steps (IDE or CLI):

1. Add MySQL JDBC driver to classpath (maven: `mysql:mysql-connector-java` or the jar).
2. Compile sources (my IDE or `javac`).
3. Ensure `Config` points to your MySQL and that tables above exist.
4. Run `clubconnect.ui.MainFrame` (or whichever main wrapper your repo contains).

From command line (example):

- Compile:
  javac -cp ".:path/to/mysql-connector-java.jar" -d out $(find src -name "*.java")
- Run:
  java -cp "out:path/to/mysql-connector-java.jar" clubconnect.ui.MainFrame

For development I usually:
- Start the DB,
- Run the app from IDE,
- Register a user using the Register screen and then approve as admin in the DB or via an admin account.

---

## Walkthrough: What each major file / UI does

- MainFrame
  - Window and CardLayout manager. Holds currentUser (session).
  - `setCurrentUser(User)` and `getCurrentUser()` are used across panels.

- CardLayoutPanel
  - Registers screens and shows them by name.
  - Creates Leader panel on demand to avoid stale session usage.

- LoginPanel
  - Authenticates via `UserService.authenticate`.
  - Sets `parent.setCurrentUser(user)` and switches to role-specific card.

- RegisterPanel
  - Collects registration info and optionally the club to join.
  - Creates user and membership (pending) using `UserService.register`.

- AdminPanel
  - Admin dashboard: top stats, users table, event table, actions to approve/promote/delete.
  - Important: I added an approve flow that prompts for exact club names and optionally assigns the user to the selected club. Leaders are assigned by updating `clubs.created_by`. Members get an approved membership created or their pending membership approved.
  - `refreshData()` reads counts and events from DB. `loadUsers()` builds the users table with associated clubs (leader or member), using `ClubDAO.getClubForLeader` and `ClubDAO.getClubsForMember`.

- LeaderPanel
  - Dashboard for leaders (club-specific). Shows club stats, members, events, allows event creation and resource requests. Created on demand.

- MemberPanel
  - Member dashboard (I updated this to be null-safe).
  - Shows "not signed in" placeholder if there is no current user (prevents the NPE during startup).
  - When a user is present and the panel becomes visible it builds a full dashboard that matches the admin & leader structure (top bar, sidebar, stat cards, split content).
  - Allows join/leave requests and RSVP stub.

- ClubDAO / UserDAO / MembershipDAO
  - DAO layers. For membership queries (e.g., getMembershipsForUser, getClubsForMember) I implemented suitable queries joining memberships and clubs.

- EventManagementPanel, BudgetRequestPanel, ResourceBookingPanel, ReportsPanel, NotificationsPanel
  - UI for specific features. They may directly use JDBC in places — this is OK for the initial prototype though I prefer centralizing DB access in DAOs.

- BackupHandler
  - Exports the database into a portable SQL file (INSERT statements).

---

## Troubleshooting & the NullPointerException I encountered

You saw this exception at startup:

```
Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException: Cannot invoke "clubconnect.models.User.getId()" because the return value of "clubconnect.ui.MainFrame.getCurrentUser()" is null
	at clubconnect.ui.MemberPanel.loadMemberClubsAndEvents(MemberPanel.java:299)
	...
```

Cause:
- I created `MemberPanel` (and some other panels) when the CardLayoutPanel initialized. At that moment no one had logged in yet, so `MainFrame.getCurrentUser()` returned `null`. The MemberPanel constructor called `loadMemberClubsAndEvents()` which did `parent.getCurrentUser().getId()` — causing the NPE.

Fix I applied:
- I rewrote `MemberPanel` so it is null-safe:
  - It does not dereference `getCurrentUser()` in the constructor.
  - If `getCurrentUser()` is null it shows a friendly placeholder asking the user to login.
  - When the panel becomes visible and there's a current user, it builds the dashboard UI and loads user-specific data.
  - Actions that require a user check `parent.getCurrentUser()` and prompt to login if null.
- I also ensured `LeaderPanel` is created on-demand (CardLayoutPanel already recreates the Leader panel when showing "leader") so it has fresh session context.

If you see similar errors in other panels:
- Search for `parent.getCurrentUser()` calls inside constructors and either:
  - Make them null-safe, or
  - Defer construction until after login.

---

## Common runtime issues & how I resolved them

- "Cannot find symbol: getClubsForMember" (compile time)
  - Cause: AdminPanel called `clubDAO.getClubsForMember(...)` but the method was missing in ClubDAO.
  - Fix: Implemented `getClubsForMember(int userId)` in `ClubDAO` which joins `memberships` and `clubs` and returns a List<Club>.

- DB schema mismatch errors
  - If a query references a missing table/column, verify the schema and adjust DAO SQL accordingly.
  - Example: `events` table expects `date_time`, `budget_requested` etc.

- Password mismatch on login
  - I use SHA-256 via HashUtil.sha256(password). If you manually insert a user into DB be sure to store the hashed password, not plaintext.

---

## Things I intentionally left simple / To-do

This is a learning / demo project. Improvements I plan or recommend:

- Use a proper build tool (Maven or Gradle) and declare dependencies (MySQL connector).
- Replace the simple ConnectionPool with HikariCP for production reliability.
- Replace SHA-256 without salt with BCrypt/Argon2 (password hashing best practices).
- Add unit tests and integration tests for DAOs (use an in-memory DB for CI).
- Add server-side authorization checks if you ever expose functionality over a network API.
- Implement real notifications storage & delivery.
- Add file upload / download for resources (currently resources are only rows in the DB).
- Add pagination for long lists (users, clubs, events).
- Add detailed logging (SLF4J + Logback) instead of System.out/printStackTrace.

---

## Security notes

- Current password hashing is SHA-256 (HashUtil) — good for demo but not production.
- Protect database credentials — don't commit them to public repos.
- Use prepared statements (I did in most places) to avoid SQL injection.

---

## Development tips & sample seed data

If you want an admin account quickly:

1. Compute sha256 of a password (or register via UI then update role/approved).
2. Insert an admin:

```sql
INSERT INTO users (username, password, full_name, role, email, student_id, approved)
VALUES ('admin', '<sha256(password)>', 'System Admin', 'admin', 'admin@example.com', '0000', 1);
```

Seed a demo club:

```sql
INSERT INTO clubs (name, category, description, created_by, archived)
VALUES ('Robotics Club', 'Tech', 'Robotics and AI', 1, 0);
```

Seed a demo event:

```sql
INSERT INTO events (club_id, title, date_time, venue, capacity, details, budget_requested, budget_status)
VALUES (1, 'AI Workshop', '2025-11-13 14:00:00', 'Room 204', 100, 'Intro to AI', 0, 'none');
```

---

## Contributing

seabata sechaba 2333779
```