# Step-by-Step Deployment Guide

Your codebase has now been configured to handle dynamic production URLs and secure Cross-Origin sharing! Follow this guide exactly to launch your Blood Management System onto the live internet using **Render** (for the Backend and Data) and **Vercel** (for the User Interface).

> [!CAUTION]
> Before you start, **commit and push** all your latest code to a brand new **GitHub Repository**. Both Render and Vercel require your code to be on GitHub so they can automatically build it whenever you make future changes.

---

## Phase 1: Deploying the Database (Render)
Before the backend can run, it needs a place to store data. Render provides free MySQL/PostgreSQL databases.

1. Create a free account at [Render.com](https://render.com).
2. Click **New +** in the top right, and select **PostgreSQL** (Or configure a MySQL instance on an external provider like Aiven or AWS RDS if you strictly prefer MySQL). 
   > *Note: If you use Render's PostgreSQL, you must update your `pom.xml` to include `org.postgresql:postgresql` instead of `mysql-connector-j`, and change the `spring.datasource.url` dialect in `application.properties`.*
3. Name your database (e.g., `blood-management-db`) and click **Create Database**.
4. Once it's created, scroll down and copy the **Internal Database URL** (if hosting backend on Render) or **External Database URL**. Save this for Phase 2!

---

## Phase 2: Deploying the Backend (Render)
1. In your Render Dashboard, click **New +** and select **Web Service**.
2. Connect your GitHub account and select your **Blood Management System repository**.
3. **Configuration Options:**
   - **Name:** `bms-backend`
   - **Root Directory:** `backend` *(This is CRITICAL since your Spring Boot app is in the `backend` folder!)*
   - **Runtime:** `Java`
   - **Build Command:** `./mvnw clean package -DskipTests` *(Make sure you pushed the `.mvn` folder and `mvnw` file to GitHub!)*
   - **Start Command:** `java -jar target/*.jar`
4. Scroll down to **Environment Variables** and add the following keys exactly as they appear in your `.env` file:
   - `DB_URL` (Paste the URL you copied in Phase 1)
   - `DB_USER`
   - `DB_PASS`
   - `JWT_SECRET` (Enter a long string of random characters)
   - `TWILIO_ACCOUNT_SID`
   - `TWILIO_AUTH_TOKEN`
   - `TWILIO_PHONE_NUMBER`
5. Click **Create Web Service**. Render will now automatically download Java, build your `.jar` file, and start it on a live URL!
6. **Copy your live backend URL** from the top left (it will look something like `https://bms-backend.onrender.com`).

---

## Phase 3: Deploying the Frontend (Vercel)
Vercel specializes in perfectly deploying Vite projects for free.

1. Create a free account at [Vercel.com](https://vercel.com) using your GitHub account.
2. Click **Add New...** -> **Project**.
3. Import your **Blood Management System repository**.
4. **Configuration Options:**
   - **Framework Preset:** `Vite`
   - **Root Directory:** Edit this and select `frontend`. *(Like Render, Vercel needs to know the React app isn't explicitly in the root folder).*
5. Open the **Environment Variables** dropdown and add:
   - **Name:** `VITE_API_URL`
   - **Value:** Paste your live backend URL from Phase 2 (e.g., `https://bms-backend.onrender.com`).
6. Click **Deploy**. Vercel will install your NPM packages, build the static files, and push them to their global edge network.

---

## Phase 4: Final Twilio Update

Right now, your Backend texts people links that look like `http://localhost:5173/respond/...`. This won't work on people's phones!

1. In your Java backend code (`EmergencyMatchingService.java`), locate the String formatted SMS message on `Line 72`.
2. Change `http://localhost:5173` to your **live Vercel URL** (e.g., `https://bms-frontend.vercel.app/respond...`).
3. Commit and push this change to GitHub. Render will automatically see the commit and gracefully redeploy your backend with the new link!

Congratulations! Your entire enterprise system is entirely live on the public internet.

---

## Phase 5: Twilio Free Trial Verification (College Projects)

If you are using a **free Twilio Trial account** for your college project, Twilio restricts you from sending SMS or Calls to *unverified* phone numbers to prevent spam.

To ensure your Blood Management System successfully contacts "Donors":
1. Log into your [Twilio Console](https://console.twilio.com/).
2. On the left sidebar, navigate to **Phone Numbers > Manage > Verified Caller IDs**.
3. Click the **Add a new Caller ID** button (or **Verify a number**).
4. Enter the phone numbers of your project partners or the professor evaluating the project (include the country code like `+91` or `+1`).
5. Twilio will text a 6-digit code to those numbers. Enter the code in the dashboard to verify them.
6. Now, when demonstrating the project, register these **verified phone numbers** as your `DONOR` users in your React frontend.
7. Your Spring Boot backend will now successfully dispatch Twilio SMS messages and IVR calls to those donors during an emergency sequence. If you try to send to an unverified number on a trial account, it will silently fail in the background or throw a Twilio 400 error in your STS console.
