# 🩸 Blood Management System

A production-grade, secure, and real-time emergency blood management system designed for **Donors, Patients, and Blood Bank Managers**.

## 🚀 Key Features
- **Emergency Matching Radar:** Instantly notifies local donors via **Twilio SMS & IVR Voice Calls**.
- **Real-time Tracking:** Blood Bank Managers can monitor local emergencies as they happen.
- **Enterprise Security:** Hardened Authentication with **Spring Security & JWT**.
- **Data Integrity:** Fully managed and SSL-encrypted database via **Aiven MySQL**.

---

## 🏛️ System Architecture
- **Frontend:** React (Vite) + Tailwind CSS (Vercel)
- **Backend:** Spring Boot (Java 17) + Docker (Render)
- **Database:** Managed MySQL (Aiven)
- **Communication:** Twilio SMS & IVR API

---

## 🛡️ Security & Hardening
This project follows enterprise-level security standards:
- [x] **Zero-Secret Codebase:** No passwords are stored in the code; all secrets are handled via Environment Variables.
- [x] **SSL/TLS Mastery:** All database connections are encrypted.
- [x] **Non-Root Docker:** Containers are hardened to prevent breakout vulnerabilities.
- [x] **CORS Locking:** Backend is restricted to only allow your specific Vercel domain.

---

## 🛠️ Deployment & Setup
Detailed instructions for deployment, security checks, and GitHub pushes can be found in our **[Beginner's Deployment Guide](./FINAL_BEGINNER_DEPLOY_GUIDE.md)**.

### Quick Local Start
1.  **Environment Setup:** Add your `Aiven` and `Twilio` credentials to a `.env` file in the `backend/` folder.
2.  **Run with Docker:**
    ```bash
    docker compose up --build
    ```

---

## 🎓 Academic Credit
This project was developed with a focus on **Software Engineering Best Practices, Production Performance**, and **High Availability Security**.
