# Blue Jays Go

## 📱 Overview
Blue Jays Go is an Android app that displays MLB team information using Jetpack Compose.  
It shows team details such as team logo, name, record, and upcoming games.  

Api: https://site.api.espn.com/apis/site/v2/sports/baseball/mlb/teams

## 🎥 Demo

https://github.com/user-attachments/assets/baf5f9ac-d6c5-4687-9040-2adb813534d5

## 🛠 Tech Stack
- Kotlin
- Jetpack Compose (UI)
- Retrofit (API calls)
- Koin (Dependency Injection)
- Coroutines + Flow (async + reactive state)
- Coil (image loading)

## 🚀 For Now
- Browse MLB teams from a dropdown
- View team details:
  - Logo
  - Abbreviation
  - Wins/Losses
  - Next game
 
## 🧪 Testing
The project follows a layered testing approach:

### Unit Tests (`app/src/androidTest/java/com/app`)
- **SportsRepositoryImplTest** → verifies repository logic with a fake API
- **SportsViewModelTest** → verifies ViewModel state updates with a fake repository


## 📝 TODO (Next Features)
- Sort teams by league (e.g., AL / NL)
- Display standings with ranks and records
- Add error and loading states for API calls
- Improve UI styling with team colors and themes
- Add favorites/bookmarking for teams
- Add team stats when on team details
- Potentially add player stats when on team details

## ⚙️ Setup
1. Clone the repo  
2. Open in **Android Studio Giraffe (or newer)**  
3. Sync Gradle dependencies  
4. Run on an emulator or device  

## ✅ Requirements
- Android Studio Giraffe (or newer)  
- Kotlin 1.9+  
- Min SDK 24  
