# 🎓 Faculty Review & Deployment Guide

This guide describes how to correctly upload and deploy the **Blood Management System** while following professional security standards.

---

## 🚯 Step 1: What NOT to Push (Critical for Security)
To follow industry best practices, you must **never** upload your secrets to GitHub.

| File | Status | Why? |
| :--- | :--- | :--- |
| `application.properties` | ✅ **SAFE** | All passwords are replaced with `${DB_PASS}` etc. |
| `Dockerfile` | ✅ **SAFE** | It’s a blueprint, not a secret. |
| `.gitignore` | ✅ **SAFE** | It tells Git what to hide. |
| **`.env`** | ❌ **DANGER** | **Never upload this.** It contains your real Aiven and Twilio passwords. |
| **`node_modules/`** | ❌ **SKIP** | Too large to upload; servers install this automatically. |

---

## 🐙 Step 2: GitHub Repository Setup
1.  **Create Repo:** Go to GitHub -> **New Repository**.
2.  **Naming:** `blood-management-system`.
3.  **Privacy:** Select **"Public"** (if your faculty needs to see it) or **"Private"** (and invite your faculty as a collaborator).
4.  **Important:** Do **NOT** check "Add a README" or ".gitignore" on GitHub (they are already in your folder).

### 🖥️ Upload Commands (In your Terminal):
Run these 5 commands exactly in the project root:
```bash
git init
git add .
git commit -m "Initialize professional secure project"
git branch -M main
git remote add origin [PASTE_YOUR_GITHUB_URL_HERE]
git push -u origin main
```

---

## 🚀 Step 3: Cloud Deployment (The "Live" App)

### 1. Render (Backend)
- **Runtime:** Docker.
- **Environment Variables:** You **must** add these in the Render dashboard:
    *   `DB_URL`, `DB_USER`, `DB_PASS` (From Aiven)
    *   `JWT_SECRET` (A long random string)
    *   `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_PHONE_NUMBER`
    *   `FRONTEND_URL` (Your Vercel URL)
    *   `BACKEND_URL` (Your Render URL)

### 2. Vercel (Frontend)
- **Framework Preset:** Vite.
- **Environment Variable:** Add `VITE_API_URL` (Your Render Backend URL + `/api`).

---

## 📝 Step 4: Professional README
I have updated your **`README.md`** to show your faculty that you understand the architecture, security, and deployment of your system.

> [!TIP]
> **Check the "Total Project Readiness Walkthrough" in the `.gemini` folder for the final verification of all hardened files.**
