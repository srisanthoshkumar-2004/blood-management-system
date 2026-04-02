# Upgrade to Enterprise Blood Management System

This plan outlines the steps to upgrade the current blood management application to an enterprise-grade system. This involves significant architectural changes, security hardening, new entity additions, logic overhauls for user handling, Twilio SMS/IVR workflows, and frontend redesigns to meet a premium standard.

## User Review Required

> [!WARNING]
> This upgrade requires significant database schema changes (e.g., dropping the `USER` role and introducing `DONOR`, `PATIENT`, and `BLOOD_BANK` roles). Existing user data might need to be migrated or wiped entirely depending on the current development state. 

> [!IMPORTANT]
> The Twilio integration for calls requires an IVR setup (recording or standard text-to-speech). Twilio numbers also require proper configuration for both Voice and SMS capabilities. Real phone numbers need to be verified to work properly in a non-upgraded Twilio trial account.

## Proposed Changes

---

### Backend: Configuration & Security
Security needs to be hardened with stateless robust JWT authentication, specific role-based access control (RBAC), and security endpoint protection.

#### [MODIFY] `application.properties` / `.env`
- Add Twilio config, JWT Secret, DB config.

#### [MODIFY] `SecurityConfig.java` & `JwtAuthFilter.java`
- Ensure CSRF is disabled correctly, session management is stateless, and specific endpoints have role-based authorization (e.g., `/api/admin/**` for ADMIN).
- Provide unified custom error responses for invalid/expired tokens via `AuthenticationEntryPoint`.

---

### Backend: User & Identity Management
Users need distinct lifecycle states as donors and patients. The current generic `Role.USER` will be replaced.

#### [MODIFY] `Role.java`
- Introduce `ADMIN`, `DONOR`, `PATIENT`, `BLOOD_BANK`.

#### [MODIFY] `User.java`
- Add `isDonor` (boolean) and `isPatient` (boolean).
- Keep `available` (boolean).
- Update relationships for histories and health tracking.

#### [MODIFY] `UserService.java` & `UserController.java`
- Implement logic: IF user becomes `PATIENT` (requests blood) -> set `isDonor = false`, `available = false`.
- Implement logic to re-enable them manually or via a scheduled task after 30 days.

---

### Backend: Entities & Tracking Modules
Adding history and physical entities.

#### [MODIFY] `DonorHealth.java`
- Ensure fields: `weight`, `hemoglobinLevel`, `bloodPressure`, `medicalHistory`, `lastCheckupDate`, `isEligible` (auto-calculated based on 90 days since `lastDonationDate` from `User` or `DonationHistory`).

#### [MODIFY] `DonationHistory.java`
- Link logically to enforce the strictly tracked 90-day gap between donations.

#### [NEW] `PatientHistory.java`
- Entity: `id`, `patient` (User), `requestDetails`, `hospitalName`, `status`, `fulfilledDonor` (User).

#### [NEW] `PatientHistoryRepository.java` & `PatientHistoryController.java`
- API layer to fetch user specific histories.

---

### Backend: Blood Bank Module
Blood requested directly from Banks before Donors.

#### [NEW] `BloodBank.java`
- Properties: `id`, `name`, `location`, `contactNumber`, `availableBloodGroups`.

#### [NEW] `BloodBankRepository.java`, `BloodBankService.java`, `BloodBankController.java`
- Modules to register blood banks and fetch availability.

---

### Backend: Request & Emergency Matching Engine
The core logic for requesting blood and automatically matching.

#### [MODIFY] `BloodRequest.java`
- Add `emergencyLevel`. Ensure `status` works nicely.

#### [MODIFY] `EmergencyMatchingService.java`
- **Flow overhaul**: 
  1. Try `Blood Banks` in the system (Notify/Match). 
  2. If unsuccessful/rejected -> Filter `Donors` (blood group, location, available=true, isEligible=true).
  3. Sort by: Age (18-40) [highest priority], Oldest donation [second], Availability schedule [third].

---

### Backend: Twilio Communications
Full system for SMS queues and Calling.

#### [MODIFY] `TwilioService.java`
- Add Batching mechanism: Send 5 Donors an SMS, wait 120 seconds.
- Monitor `DonorResponse` to know when someone accepts (YES / NO / TIMEOUT).
- Add `Voice` capability (IVR using TwiML) calling donors one-by-one.
- If Accepted: Call Patient -> Text Patient -> Text Donor with instruction: "Please bring recent health report".

#### [NEW/MODIFY] `DonorResponse.java` (Ensure Correct fields)
- `donor`, `request`, `response` (YES / NO / TIMEOUT), `responseTime`.

---

### Frontend: UI / Architecture Redesign
A premium layout focusing on responsiveness, visual excellence natively.

#### [NEW/MODIFY] `tailwind.config.js` & `index.css`
- Add premium palettes, modern typography (Inter), glassmorphism utility classes.

#### [MODIFY] `App.jsx` & Router
- Protect routes based on explicit new roles instead of just `isLoggedIn`.

#### [MODIFY/NEW] Feature Components
- **Landing Page**: Completely revamp to be incredibly dynamic and trustworthy. Wow the user with aesthetics and dynamic interactions.
- **Dashboard**: Role specific layouts (Admin vs Donor vs Patient vs Blood Bank).
- **History Tabs**: Display `PatientHistory` vs `DonationHistory` beautifully.
- **Request Page**: Add emergency levels, live tracking.

## Open Questions

> [!WARNING]
> 1. Should we drop/wipe the current database tables completely since the role structures and user models are fundamentally changing (`User` vs `Role.USER`, etc.)? I recommend doing so for a clean slate.
> 2. What exactly determines `Hemoglobin level` and `Blood Pressure` in our app? Does the Donor input this themselves out of good faith, or does an Administrator/Blood Bank input it after a physical checkup?
> 3. Does the system need to run a cron job (e.g. at midnight daily) to check for patients whose 30-day "grace period" has ended to automatically switch them back to donors, or is it evaluated strictly when they log in / view their profile?

## Verification Plan

### Automated/Code Verification
- Attempt compilation with Maven `mvn clean install` to ensure no Constructor Injection loops or compilation errors exist.
- Inspect JSON mappings and JPA cyclic relationships.

### Manual Verification
- Start the server using `mvn spring-boot:run`.
- Register brand new accounts as Donors and Patients.
- Trigger an emergency request and observe the Twilio service logs (Simulating responses).
- Verify JWT expirations reject cleanly instead of generating ugly 500 stack traces.
- Verify that a user acting as a Patient is genuinely removed from the Donor pool.
