# instructions.md

## Purpose

These instructions define how Cursor AI should modify, refactor, and generate new code for this Android project.
All generated code **must follow the rules** in this document unless explicitly overridden.

---

# Project Overview

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit
- **Async**: Kotlin Coroutines + Flow
- **Storage**: Room Database
- **Monetization**: Google AdMob, Google Pay
- **Testing**: Compose UI Test
- **Backend Services**: Firebase Storage, Firebase Remote Config
- **CI/CD**: Bitrise (builds, tests, signing, deployment)

---

# Architecture Rules

## Layered Clean Architecture

Use **three layers**:

### Domain Layer

- Contains **business logic** only.
- No Android framework imports.
- Includes:
  - Use cases
  - Domain models
  - Repository interfaces

### Data Layer

- Responsible for data sources.
- Includes:
  - Retrofit services
  - Room DAOs
  - DTOs
  - Mappers (`Dto.toDomain()`)
- Implements repository interfaces from domain.

### Presentation Layer

- Jetpack Compose UI
- ViewModels (Hilt-injected)
- UI state + UI actions
- Should not know anything about Retrofit or Room.

---

# Module / Package Structure

Cursor must follow this structure:
