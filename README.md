# 🚀 TeamTrack - Team Project Management Application

> A complete team project management system built with **Spring Boot** and **React** featuring stateless JWT authentication, role-based access control, and real-time collaboration features.

**Live Demo:** [https://teamtrack-production.up.railway.app](https://teamtrack-production.up.railway.app)

**Backend Repository:** [github.com/Shivam-Singh-devops/TeamTrack](https://github.com/Shivam-Singh-devops/TeamTrack)

**Frontend Repository:** [github.com/Shivam-Singh-devops/TeamFront](https://github.com/Shivam-Singh-devops/TeamFront)

---

## 🎯 Key Features

### 🔐 Authentication & Security
- **JWT Token-based Authentication** - Stateless, scalable authentication
- **Role-Based Access Control** - ADMIN and MEMBER roles with different permissions  
- **Secure Password Encryption** - BCrypt hashing for password security
- **Authorization at Service Layer** - Multi-level security checks

### 📋 Project Management
- **Create & Manage Projects** - Admins can create and manage projects
- **Team Member Management** - Add/remove team members to projects
- **Project Dashboard** - Overview of project status and team
- **Project Visibility** - Users see only projects they created or joined

### ✅ Task Management
- **Task Creation & Assignment** - Admins assign tasks to team members
- **Task Status Tracking** - TODO → IN_PROGRESS → COMPLETED
- **Deadline Management** - Set due dates and track overdue tasks
- **My Tasks Section** - Members see only their assigned tasks
- **Real-time Updates** - Status changes reflected immediately

### 📊 Dashboard & Monitoring
- **Project Statistics** - Total tasks, completed, in-progress, overdue counts
- **Team Overview** - See all team members in a project
- **Progress Tracking** - Visual indicators for project health

---

## 🏗️ Architecture

### Layered Architecture
```
┌─────────────────────────────────┐
│      CONTROLLER LAYER           │
│  (HTTP Requests/Responses)      │
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│       SERVICE LAYER             │
│  (Business Logic & Auth)        │
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│      REPOSITORY LAYER           │
│   (Database Operations)         │
└────────────────┬────────────────┘
                 │
┌────────────────▼────────────────┐
│      POSTGRESQL DATABASE        │
└─────────────────────────────────┘
```

### Authentication Flow
```
1. User Login
   ↓
2. JWT Token Generated
   ↓
3. Token Sent to Frontend
   ↓
4. Frontend Stores Token
   ↓
5. Token Sent with Every Request (Authorization Header)
   ↓
6. JwtAuthenticationFilter Validates Token
   ↓
7. Request Proceeds to Controller
   ↓
8. Service Layer Checks Authorization (Role & Permissions)
   ↓
9. Response Returned
```

---

## 🛠️ Technology Stack

### Backend
```
✓ Java 17
✓ Spring Boot 4.0
✓ Spring Security
✓ Spring Data JPA
✓ JWT (JSON Web Tokens)
✓ PostgreSQL
✓ Lombok
✓ Maven
```

### Frontend
```
✓ React 18
✓ JavaScript (ES6+)
✓ CSS3
✓ Fetch API
✓ React Hooks
```

### Deployment
```
✓ Railway (Backend & Database)
✓ Vercel/Netlify (Frontend - Optional)
```

---

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    role ENUM('ADMIN', 'MEMBER'),
    created_at TIMESTAMP
);
```

### Projects Table
```sql
CREATE TABLE projects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    admin_id INT NOT NULL,
    created_date TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES users(id)
);
```

### Project Members Table
```sql
CREATE TABLE project_members (
    id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    role ENUM('ADMIN', 'MEMBER'),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Tasks Table
```sql
CREATE TABLE tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    project_id INT NOT NULL,
    assigned_person_id INT NOT NULL,
    task_status ENUM('TODO', 'IN_PROGRESS', 'COMPLETED'),
    due_date TIMESTAMP,
    created_date TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (assigned_person_id) REFERENCES users(id)
);
```

---

## 🔌 API Endpoints

### Authentication
```http
POST   /api/auth/register      → Register new user
POST   /api/auth/login         → Login user (returns JWT token)
```

### Projects
```http
GET    /api/projects                    → Get all accessible projects
POST   /api/projects                    → Create new project (ADMIN)
GET    /api/projects/{id}               → Get project details
PUT    /api/projects/{id}               → Update project (ADMIN)
DELETE /api/projects/{id}               → Delete project (ADMIN)
POST   /api/projects/{id}/members       → Add member to project (ADMIN)
GET    /api/projects/{id}/members       → Get all project members
```

### Tasks
```http
POST   /api/tasks/projects/{projectId}      → Create task (ADMIN)
GET    /api/tasks/projects/{projectId}      → Get all tasks in project
GET    /api/tasks/{id}                      → Get task details
PUT    /api/tasks/{id}                      → Update task (ADMIN or assignee)
DELETE /api/tasks/{id}                      → Delete task (ADMIN)
GET    /api/tasks/assigned-to-me            → Get user's assigned tasks
GET    /api/tasks/projects/{projectId}/stats → Get dashboard statistics
```

---

## 🚀 Getting Started

### Prerequisites
```
✓ Java 17 or higher
✓ Maven 3.6+
✓ PostgreSQL 12+
✓ Node.js 16+ (for frontend)
✓ Git
```

### Backend Setup

**Step 1: Clone Backend Repository**
```bash
git clone https://github.com/Shivam-Singh-devops/TeamTrack.git
cd TeamTrack
```

**Step 2: Configure Database** (application.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/teamtrack
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**Step 3: Add JWT Secret** (application.properties)
```properties
security.jwt.secret=your-secret-key-here-base64-encoded
security.jwt.expiration-ms=86400000
```

**Step 4: Build & Run**
```bash
mvn clean install
mvn spring-boot:run
```
✅ Backend runs on `http://localhost:8080`

---

### Frontend Setup

**Step 1: Clone Frontend Repository**
```bash
git clone https://github.com/Shivam-Singh-devops/TeamFront.git
cd TeamFront
```

**Step 2: Install Dependencies**
```bash
npm install
```

**Step 3: Configure API URL** (src/api.js)
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

**Step 4: Start Development Server**
```bash
npm start
```
✅ Frontend runs on `http://localhost:3000`

---

## 🧪 Testing

### Test Credentials
```
Email: admin@gmail.com
Password: password
Role: ADMIN

Email: john@gmail.com
Password: password
Role: ADMIN
```

### Test Flow
1. **Login** → Get JWT token
2. **Create Project** → "Website Redesign"
3. **Add Members** → Add john@gmail.com and sarah@gmail.com
4. **Create Tasks** → Assign tasks to members
5. **Update Task** → Change status from TODO to IN_PROGRESS
6. **View Dashboard** → See project statistics

---

## 👥 User Roles & Permissions

### ADMIN Permissions
```
✅ Create projects
✅ Edit project details
✅ Add/remove team members
✅ Create tasks
✅ Assign tasks to members
✅ Delete tasks
✅ View all project tasks
✅ Update any task status
✅ View dashboard stats
```

### MEMBER Permissions
```
✅ View projects they joined
✅ See all tasks in project
✅ Update assigned task status
✅ View task details
✅ View dashboard stats

❌ Cannot create projects
❌ Cannot add members
❌ Cannot create/delete tasks
❌ Cannot reassign tasks
```

---

## 🔒 Security Features

### JWT Authentication
- Tokens expire after 24 hours
- Token contains user ID, email, and role
- Token validated on every request
- Tokens are stateless (no session storage)

### Authorization Checks
- Role-based access control at service layer
- Method-level security checks
- Endpoint protection with JWT filter
- Unauthorized requests return 403 Forbidden

### Password Security
- Passwords encrypted with BCrypt
- Plain passwords never stored in database
- Password validation on login

---

## 🐛 Common Issues

### Issue: CORS Error
**Solution:** CORS is configured in `CorsConfig.java` to allow all origins. Ensure it's enabled.

### Issue: JWT Token Expired
**Solution:** Token expires after 24 hours. User needs to login again to get new token.

### Issue: Cannot Add Member
**Solution:** Only project admin can add members. Ensure you're logged in as admin.

### Issue: Task Status Not Updating
**Solution:** Only assigned user or admin can update task status. Check user role.

---

## 📈 Future Enhancements

- [ ] Email notifications for task assignments
- [ ] Real-time WebSocket updates
- [ ] File attachments for tasks
- [ ] Task comments and discussions
- [ ] Advanced filtering and sorting
- [ ] Export reports to PDF/Excel
- [ ] Mobile app (iOS/Android)
- [ ] Dark mode
- [ ] Two-factor authentication

---

## 📞 Support

For issues or questions:
1. Check the troubleshooting section
2. Review API documentation
3. Check GitHub issues
4. Contact maintainer

---

## 📝 License

This project is licensed under the MIT License - see LICENSE file for details.

---

## 👨‍💻 Author

**Shivam Singh**
- Email: shivamsinghsss8894@gmail.com
- LinkedIn: [linkedin.com/in/shivam-singh-java](https://www.linkedin.com/in/shivam-singh-java/)
- GitHub: [github.com/Shivam-Singh-devops](https://github.com/Shivam-Singh-devops)
- Backend Repo: [TeamTrack](https://github.com/Shivam-Singh-devops/TeamTrack)
- Frontend Repo: [TeamFront](https://github.com/Shivam-Singh-devops/TeamFront)

---

## 🙏 Acknowledgments

- Spring Boot team for excellent framework
- React team for frontend library
- PostgreSQL for reliable database
- JWT.io for token implementation

---

**Built with ❤️ by Shivam Singh**
