<!-- PROJECT LOGO -->
<br />
<div align="center">
  
  <h3 align="center"># Guide - Travel Planning App </h3>

  <p align="center">
    A comprehensive Android travel planning application with route optimization, weather integration, and offline capabilities.
    <br />
    <a href="https://github.com/IIEMSA/2025-nov-opsc6312-poe-part3-Varlene-Z-Sib/tree/master"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://youtu.be/RNikKhcBVXg?si=zoY5s5A5zlPGFKWy">View Demo Video</a>    
    ·
    <a href="https://github.com/IIEMSA/2025-nov-opsc6312-poe-part3-Varlene-Z-Sib/tree/master">Repository</a>
  </p>
</div>

### Updated Features (2025 Release Build)

Firebase Authentication (google sign in) – Secure user login with SSO.

Route Planning (OpenRoute Service API) – Real-time route calculation and trip optimization.

Weather Integration (OpenWeatherMap API) – Displays current and forecast weather with caching for offline access.

Trip History (Firestore) – Automatically saves and syncs past trips across devices.

Offline Mode (Room Database) – Access saved routes and weather data without an internet connection.


Multi-Language Support (English, Afrikaans and isiZulu) – Instantly switch between languages inside the app.

Real-Time Notifications (WorkManager) – Background weather updates and alerts even when the app is closed.

### Release Notes – Version 1.0

Package Name: vcmsa.projects.travelapp
Release File: app-release.aab

Highlights:

All core travel planning features are fully implemented and tested.

Stable release build signed and verified for deployment.

Offline synchronization and background notifications confirmed working.

Multilingual interface and dark mode added for enhanced user experience.

CI pipeline integrated for automated test validation.

Status: Production Ready


## Overview

Guide is a mobile application designed to enhance efficiency, safety, and convenience of personal travel planning. The app combines route planning, real-time weather data, trip history, and offline mode into a single, user-friendly interface.

## Features

### Implemented
- **User Authentication** - Firebase Authentication with email/password
- **Route Planning** - Google Directions API integration for optimal routes
- **Weather Integration** - Real-time weather data from OpenWeatherMap
- **Trip History** - SQLite/Room database for storing past trips
- **Offline Mode** - Cached routes and weather data for offline access
- **Material Design UI** - Modern, intuitive user interface


## Tech Stack

- **Language:** [![Kotlin][Kotlin.js]][Kotlin-url]
- **Database:** Room (SQLite) and firestore database
- **Networking:** Retrofit, OkHttp
- **routing:** Openroute service API
- **Weather:** OpenWeatherMap API
- **Authentication:** Firebase Auth
- **UI:** Material Design Components

## Quick Start

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11+
- Android SDK API 26+
- Google Cloud account (for Maps API)
- OpenWeatherMap account

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/IIEMSA/2025-nov-opsc6312-poe-part3-Varlene-Z-Sib.git
cd travelapptemp
```

2. **Configure API Keys**

**openroute service API** in `app/src/main/java/vcmsa/projects/travelapp/RouteFragment.kt`:
```xml
 private val apiKey ="enter you openroute service API"
```

**OpenWeatherMap API** in `WeatherFragment.kt`:
```kotlin
rivate val WEATHER_API_KEY = "YOUR_OPENWEATHERMAP_API_KEY"
```


3. **Build and Run**
```bash
./gradlew clean build
```

See [SETUP_GUIDE.md](SETUP_GUIDE.md) for detailed configuration instructions.

## Project Structure

```
travelapptemp/
├── app/src/main/
│   ├── java/vcmsa/projects/travelapp/
│   │   ├── data/
│   │   │   ├── entity/          # Room database entities
│   │   │   ├── dao/             # Data Access Objects
│   │   │   ├── model/           # API response models
│   │   │   ├── database/        # Database configuration
│   │   │   └── repository/      # Data layer logic
│   │   ├── api/                 # API service interfaces
│   │   ├── network/             # Network configuration
│   │   └── *.kt                 # Activities & Fragments
│   └── res/layout/              # UI layouts
├── IMPLEMENTATION_PROGRESS.md   # Development progress
├── SETUP_GUIDE.md              # Configuration guide
└── build.gradle.kts            # Dependencies
```

## Architecture

The app follows the **Repository Pattern** with:
- **Room Database** for local data persistence
- **Retrofit** for API communication
- **Repositories** for data management
- **LiveData/Flow** for reactive UI updates

## API Integration

### GOpenroute Service APIs
- **Directions API** - Route calculation
- **Places API** - Location search (planned)
- **Maps SDK** - Map visualization (planned)

### OpenWeatherMap API
- Current weather conditions
- Weather forecasting
- Cached for offline access

## Screenshots

### Login
<img src="https://github.com/user-attachments/assets/3b6dc447-08d0-4b50-bf4a-6bf8e8718325" alt="IMG-20251007-WA0012" width="300" />
<img src="https://github.com/user-attachments/assets/4a5c7afc-1266-4afe-9875-70aa19119e70" alt="IMG-20251007-WA0012" width="300" />


* Sign In: Tap here to sign in and access your personal settings, trip history, and saved routes.
* Sign Up: Tap here to sign up for a new account and unlock the full suite of GUIDE app features.

### Home

<img src="https://github.com/user-attachments/assets/35b23bf7-bb88-48ce-b575-05feb92c3784" alt="IMG-20251007-WA0012" width="300" />

The GUIDE app's home screen is designed for instant access to its primary functions, making trip planning quick and efficient.
* Route Planning: Takes you to a page to determine and start a motor vehicle travel route.
* Weather Info: Leads to the real-time and forecasted weather conditions feature.
* Trip History: Navigates to a log where you can view, modify, or re-plan previous journeys.
* Offline Mode: Directs you to settings or downloaded content management for using the app without an internet connection.

### Route Planner
<img src="https://github.com/user-attachments/assets/e2887a2a-e590-4e6d-964e-58c4ee22f708" alt="IMG-20251007-WA0012" width="300" />

* Start Location: Tap to input the point of origin for your trip.
* Destination: Tap to input the final destination for your journey.
* FIND ROUTE: Tap this button to calculate and display the optimal driving route and estimated travel time.
* Route Details Area: This section is where the calculated route information (map, directions, ETA) will appear after searching.
* Recent Searches: Tap to access and quickly reload previous route searches, saving time on re-entering destinations.
  
### Weather

Weather Integration (OpenWeatherMap API)

Displays live weather data for current and destination cities.

Data automatically cached for offline use.

### Trip History
<img src="https://github.com/user-attachments/assets/d80c475a-b5ad-4262-9223-9bf7389469e0" alt="IMG-20251007-WA0012" width="300" />

The Trip History page serves as the user's personal travel archive, where all previously searched and planned motor vehicle routes are logged. It allows users to quickly review details of past journeys, including old weather conditions and route metrics, and provides an essential function to instantly re-plan any saved trip with updated route calculations and the latest weather forecasts. This feature eliminates the need to re-enter frequent destinations.

### Offline mode
<img src="https://github.com/user-attachments/assets/fae7f7b9-ea2e-4072-a295-1614e1afb190" alt="IMG-20251007-WA0012" width="300" />

The Offline Mode page is dedicated to managing the app's capability to function without an internet connection. It allows users to download and manage map data and weather information for specific regions or saved routes, ensuring they have access to essential navigation and basic weather details even when traveling through areas with poor connectivity.

### Settings
<img src="https://github.com/user-attachments/assets/239733d0-52a8-4918-95a5-1e204d021b13" width="300" />

The Profile and Settings page allows users to manage their account details and personalize the app's operation.
* User Info: Displays registered account name and email.
* Dark Mode: Toggles the app's visual theme.
* Notifications: Controls in-app alerts and messages.
* Language: Allows selection of the app's display language (English/isiZulu).

### Dark mode
<img src="https://github.com/user-attachments/assets/de77b87e-3480-4700-8d77-6fd47cc6deb0"  width="300" />

The Dark Mode feature is a display setting that switches the app's color scheme from a light background to a dark background. This toggle enhances user comfort and reduces eye strain, especially in low-light conditions, as seen in the comparison between the two screenshots.

### Language
<img src="https://github.com/user-attachments/assets/a62d87f0-37c3-4690-aece-ec0c04276588"  width="300" />

The Language Change feature is a crucial setting that allows the user to instantly switch the entire app's interface between English, afrikaans and isiZulu. This enables full multu-lingual operation, ensuring all labels, headings, and navigational text are displayed in the user's preferred language.

## Automated Unit Testing
Automated testing is an essential component of modern software development, enabling developers to ensure application reliability and prevent regression errors during future updates.
In this project, automated unit testing was implemented for the TravelApp Android application using GitHub Actions, a continuous integration (CI) service that executes tests automatically whenever code is pushed or merged into the repository.

### Objective
<img width="810" height="472" alt="image" src="https://github.com/user-attachments/assets/6ce28ca9-43b1-47de-928e-d2b549998c76" />

The main objective of this task was to:
* Implement unit tests in the Kotlin project to verify business logic (e.g., functionality in OfflineFragment.kt).
* Configure GitHub Actions to automatically run these tests on every code push or pull request.
*	Generate test reports to verify that all implemented features continue to function correctly across updates.
*	This verifies that the offline fragment correctly loads and displays stored routes when available.

### Configuring the GitHub Workflow
A CI workflow file named android-tests.yml was added in the. github/workflows/ directory: 
<img width="733" height="452" alt="image" src="https://github.com/user-attachments/assets/5a5356f7-d346-4766-b528-23b53ddeb1b7" />

<img width="739" height="451" alt="image" src="https://github.com/user-attachments/assets/09296b9a-15f8-4ee6-9103-06558de775c5" />

This workflow:
* Triggers automatically on every push or pull request.
*	Sets up a Linux environment with Java 17.
*	Caches Gradle dependencies to speed up subsequent builds.
*	Executes the command./gradlew test to run all test cases.
*	Uploads a detailed HTML report containing test results.


### Verification Process
After committing and pushing the workflow file to GitHub:
1.	The Actions tab on GitHub displayed a new workflow named “Android Unit Tests.”
2.	The workflow executed automatically.
3.	Test output confirmed whether the OfflineFragmentTest passed or failed.
4.	Reports were uploaded as artifacts under test-results, allowing detailed inspection.
When successful, a green check mark appeared beside the latest commit — indicating that all unit tests passed.

### Conclusion
The TravelApp project now integrates automated unit testing through GitHub Actions, providing a robust CI pipeline.This configuration guarantees that any future changes to Kotlin code are automatically verified for correctness, promoting long-term maintainability and reliability of the application.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


## Documentation

- [Implementation Progress](IMPLEMENTATION_PROGRESS.md) - Development status
- [Setup Guide](SETUP_GUIDE.md) - Detailed configuration
- [Planning Document](POE_PART_1_PLANNING.pdf) - Original requirements

## Development Team

- **Larsen Claude Canda Nehemia**
- **Mpho Nzibane**
- **Siphesihle Njabulo Zulu**
- **Varlene Zazise Sibanda**

IIE MSA - Group 1

## Roadmap

### Phase 1 (Complete)
- [x] Database layer with Room
- [x] Entity and DAO setup
- [x] Repository pattern

### Phase 2 (Complete)
- [x] Weather API integration
- [x] Weather caching
- [x] UI implementation

### Phase 3 (Complete)
- [x] openroute services
- [x] Route models
- [x] Trip history UI


### Phase 4 📅 (complete)
- [ ] Enhanced offline mode
- [ ] Trip saving workflow
- [ ] Recent searches

## Contributing

This is an academic project for OPSC6312 at IIE MSA.

## License

Educational project - IIE MSA 2025

## Support

For setup issues, see [SETUP_GUIDE.md](SETUP_GUIDE.md)

For API documentation:
- [Google Maps](https://developers.google.com/maps/documentation)
- [OpenWeatherMap](https://openweathermap.org/api)
- [Firebase](https://firebase.google.com/docs)

---

**Last Updated:** Novemer 2025

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[Kotlin.js]: https://img.shields.io/badge/Kotlin-0095D5?style=flat&logo=kotlin&logoColor=white
[Kotlin-url]: https://kotlinlang.org/
