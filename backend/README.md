# Legal Task Tracker -- Prototype

A prototype built for a company
The system focuses on granular task management inside legal casework,
supported by audit trails, role-based access, and full governance
alignment.

## 🎯 Purpose

The prototype delivers a structured platform for handling tasks tied to
legal cases.\
It supports compliance-heavy workflows where documentation,
traceability, and access control are essential.

## 🚀 Core Features

### Case & Task Management

-   Create, assign, update, and track tasks for each legal case.
-   Granular task hierarchy (case → task → subtask).
-   Role-based access to ensure correct visibility and permissions.
-   Audit trail for all critical actions.

### Outlook Integration

-   Automatic reminders.
-   Integration for task updates and calendar events.

### Security & Logging

-   Session management.
-   Password hashing.
-   Logging of all sensitive operations.
-   Full audit trail for compliance.

### Optional AI Assistance

-   AI summaries of case progress.
-   Automated follow-up suggestions.
-   Email-to-task extraction.

## 🏗️ Technical Stack

-   **Backend:** Java Spring Boot\
-   **Database:** MySQL\
-   **Frontend:** HTML, CSS, JavaScript (prototype grade)\
-   **Security:** Sessions, hashing, access control\
-   **CI/CD:** GitHub Actions → Azure App Service\
-   **Deployment:** Docker + Compose\
-   **Outlook Integration:** Microsoft Graph API (prototype-level)

## 🧪 Testing

-   Unit tests (JUnit)
-   Integration tests (Spring Test)
-   CI pipeline runs all tests on push/pull request.

## 🐳 Containerization & Deployment

-   Multi-stage Dockerfile (build + runtime)
-   Docker Compose for backend + MySQL
-   Deployment to Azure App Services
-   GitHub Actions workflow with:
    -   Build
    -   Test
    -   Docker package
    -   Deployment

## 📐 System Design

### System Development Artefacts

-   Wireframes & UX flows
-   Use-case diagrams
-   ERD (normalized)
-   Information architecture
-   SCRUM process:
    -   Product backlog
    -   Sprint planning
    -   Daily standup
    -   Sprint reviews
    -   Retrospectives
    -   Burndown charts

## 📊 Strategic Value for the Client

-   Internal sandbox for testing structured legal workflows.
-   Helps governance and compliance alignment across departments.
-   Ensures documentation quality in case handling.
-   Improves overview, planning, and consistency.

## 📌 Status

This is a prototype developed for examination purposes within a 3‑week
SCRUM sprint cycle.\
The focus is demonstrating correct architecture, CI/CD, security, and
functional design -- not a full enterprise-ready product.

## 🧑‍💼 Team Notes

-   Developed by a student engineering team.
-   Built under TEK2 requirements:
    -   GitHub Actions\
    -   Dockerfile\
    -   Docker Compose\
-   Customer feedback loop included.

------------------------------------------------------------------------

If you are reading this as an examiner or reviewer:\
The prototype demonstrates the full pipeline from requirement analysis
to deployment, with focus on clean architecture, DevOps practices, and
compliance-aware design.
