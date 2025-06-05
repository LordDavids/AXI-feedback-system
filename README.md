# 📝 AXI Feedback System

The **AXI Feedback System** is a web-based feedback application for internal use, designed to improve team collaboration and communication within organizations. It enables structured and direct feedback flows between employees, managers, and administrators, and is built for scalability and efficiency.

> Semester 3 — Group project (5 members)  
> Role: **Communication  Coordinato / Software ingenieur**  
> Focus: **Back-end development**, front-end development, presentation, and planning.

---
Project built for educational purposes at Fontys University of Applied Sciences.

## ⚙️ Tech Stack

- **Front-End:** Vue.js, TailwindCSS, Typescript
- **Back-End:** Java Spring Boot  
- **Database:** SQL  
- **Testing:** Cypress (E2E testing)  
- **Email Service:** Integrated and tested in E2E  
- **Architecture:** 3-layer system (Controller, Service, Repository)  
- **No microservices** due to low expected load 

---

## 👥 User Roles

1. **Admin**
   - Create new user accounts (name + email only).
   - System sends a secure auto-generated password by email.
   - Users must reset password on first login.

2. **Manager**
   - Create and manage teams.
   - Create feedback forms with custom questions.
   - Send feedback requests to team members via email.
   - View:
     - General feedback submitted by team members
     - Personal (direct) feedback between individuals
     - Filter reports between two dates
     - Feedback from a specific person
   - Generate PDF reports of feedback results.

3. **Employee**
   - Join one or more teams.
   - Fill in feedback forms sent by their manager.
   - Receive personal feedback directly.
   - View feedback **they submitted or received**.
   - Search coworkers' names and emails for communication.

---

## ✨ Features

- 🧾 **Custom Feedback Forms**: Created by managers with dynamic questions.
- 📥 **Two Feedback Types**:
  - **General Feedback**: For team-wide insights.
  - **Personal Feedback**: One-to-one, private communication.
- 📄 **PDF Reports**: Automatically generated from feedback results.
- 📧 **Email Notifications**: When a form is requested, team members receive an email.
- 🔒 **Login System**:
  - Inspired by the Fontys Flights login system.
  - No refresh token used (simplified for project scope).

---

## ✅ Testing

- Fully tested using **Cypress** for end-to-end flows:
  - Login, form submission, report generation, role-based access
  - Mail service functionality tested for all critical flows

---

## 🧩 Architecture

The project uses a traditional **3-layer architecture**:
- **Controller Layer**: Handles HTTP requests, routes to services.
- **Service Layer**: Contains business logic (permissions, feedback filtering, report generation).
- **Repository Layer**: Manages interaction with the SQL database via Spring Data JPA.

---

## 📈 Project Impact

- Stakeholder feedback was **very positive**.
- Successfully implemented all requested features.
- Delivered on time with full documentation, testing, and working mail service.

---



