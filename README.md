# SFU Course Planner

A comprehensive mobile application designed to help SFU students plan their academic journey, track degree requirements, manage semester schedules, and visualize course prerequisites.

**Developed for CMPT 362 - Mobile Applications Programming and Design**

---

## Overview

The SFU Course Planner is your all-in-one academic companion that helps you:

- Track and complete program requirements
- Monitor your degree progress
- Plan semester schedules with real course data
- Search for courses, majors, and minors
- Visualize prerequisite chains with an interactive Course Tree
- Manage your academic profile

---

## Features

### Profile & Progress Tracking
- View your personalized profile with avatar
- Track overall degree completion progress
- See your major and minor selections
- Edit profile information anytime
- **Dark Mode toggle** - Switch between light and dark themes
- **Avatar selection** - Choose from 6 unique character avatars

### Semester Planning
- Create and manage multiple semesters (Fall, Spring, Summer)
- Add courses to specific semesters
- View total units per semester
- Access detailed schedule view with weekly calendar
- **View important semester dates** (enrollment, drop deadlines, exam period)

### Add Courses
- Search through available CS courses
- View real course information from SFU API:
  - Course descriptions
  - Prerequisites
  - Available sections (Lectures, Labs, Tutorials)
  - **Real instructor names**
  - **Actual class times and locations**
- Select preferred lecture and lab sections
- Add courses to your semester plan

### Schedule View
- Weekly schedule display with colored time blocks
- View all enrolled courses for a semester
- Detailed course cards showing:
  - Instructor information
  - Lecture times and locations
  - Lab/Tutorial times and locations
- Collapsible sections for easy navigation

### Program Requirements
- View Computing Science degree requirements
- Track lower division and upper division courses
- Check off completed courses
- See which requirements you still need

### Course Tree Visualization
- Interactive prerequisite visualization
- Zoom and pan capabilities
- See course dependencies at a glance

---

## Navigation Guide

### Main Navigation (Drawer Menu)
Access the navigation drawer by tapping the **☰ hamburger menu** icon in the top-left corner or swiping from the left edge:

| Menu Item | Description |
|-----------|-------------|
| **Profile** | View/edit your profile, see degree progress |
| **Semester Plan** | Manage your semester schedules and courses |
| **Program Requirements** | View and track degree requirements |
| **Course Tree** | Visualize course prerequisites |

### Semester Plan Page
1. **View existing semesters** - Each semester shows term name and total units
2. **Add a semester** - Tap the **⋮ menu** → "Add Semester"
3. **View Important Dates** - Tap the **ℹ info icon** to see enrollment dates, drop deadlines, exam periods
4. **View Schedule** - Tap the **calendar icon** to see weekly schedule
5. **Edit Schedule** - Tap the **pencil icon** to add/remove courses

### Adding a Course
1. From Semester Plan, tap pencil Edit Schedule** on any semester
2. **Search** for a course using the search bar (e.g., "CMPT 362")
3. **Select a course** from the search results
4. **Choose your sections**:
   - Select preferred lecture section
   - Select lab/tutorial section (if available)
5. Review your selection in the "Selected section" summary
6. Tap **"Add this course"** to confirm

### Viewing Your Schedule
1. From Semester Plan, tap ** View Schedule** on any semester
2. **Schedule section** - Shows weekly time slots with your classes
3. **Courses section** - Lists all enrolled courses with details
4. Tap section headers to expand/collapse

### Profile Management
1. Navigate to **Profile** from the drawer menu
2. Tap the **progress bar** to view the Course Maze
3. To edit profile:
   - Go to Edit Profile from the menu
   - Change your name, avatar, major, or minor
   - Tap **"Save Changes"**

---

## Technical Details

### Project Structure

```
app/src/main/
├── java/darine_abdelmotalib/example/groupproject/
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── api/
│   │   │   ├── Items.kt          # Data models
│   │   │   ├── SfuCourseApi.kt   # SFU API integration
│   │   │   └── CoursePrefs.kt    # SharedPreferences
│   │   └── db/
│   │       ├── CsRequirementsDb.kt   # CS requirements data
│   │       └── UserProgressDb.kt     # User progress tracking
│   ├── ui/
│   │   ├── adapter/              # RecyclerView adapters
│   │   ├── planning/             # Semester planning fragments
│   │   │   ├── AddCourseFragment.kt
│   │   │   ├── CourseDetailAddFragment.kt
│   │   │   ├── ScheduleViewFragment.kt
│   │   │   └── semester/
│   │   ├── profile/              # Profile fragments
│   │   ├── requirements/         # Requirements fragments
│   │   ├── tree/                 # Course tree visualization
│   │   └── maze/                 # Course maze feature
│   └── utils/
└── res/
    ├── layout/                   # XML layouts
    ├── drawable/                 # Icons and backgrounds
    ├── navigation/               # Navigation graph
    ├── values/                   # Colors, strings, styles
    └── menu/                     # Menu definitions
```

### API Integration

The app integrates with the **SFU Course Outlines API** to fetch real-time course data:

```
https://www.sfu.ca/bin/wcm/course-outlines
```

**Data fetched includes:**
- Course titles and descriptions
- Prerequisites
- Credit units
- Available sections (LEC, LAB, TUT)
- Instructor names
- Class schedules (days, times)
- Locations (building, room)

### Threading Model

| Thread | Operations |
|--------|------------|
| **UI Thread** | UI rendering, navigation, user interactions |
| **Background (Coroutines)** | API calls, database operations, data processing |

### Data Persistence

- **SharedPreferences** - User profile, semester data, course selections
- **In-memory** - Course requirements database

---

## Dependencies

### Libraries Used

| Library | Purpose |
|---------|---------|
| **AndroidX Navigation** | Fragment navigation and drawer |
| **Material Components** | UI components and theming |
| **Kotlin Coroutines** | Asynchronous operations |
| **GraphView** | Course tree graph rendering |
| **ZoomLayout** | Zoom/pan for course tree |

### External Resources

- [GraphView by oss-bandb](https://github.com/oss-bandb/GraphView)
- [ZoomLayout by Natario](https://github.com/natario1/ZoomLayout)
- [SFU Course Outlines API](https://www.sfu.ca/outlines.html)

---

##  Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Internet connection (for fetching course data)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   ```

2. **Open in Android Studio**
   - File → Open → Select project folder

3. **Sync Gradle**
   - Android Studio will automatically sync dependencies

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click Run or press `Shift + F10`

### Building APK

```bash
./gradlew assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

---

## Team

| Name | Contribution |
|------|--------------|
| **Angela Liong** | Course Tree Visualization |
| **Cas Sugihwo** | App Structure & Navigation |
| **Darine Abdelmotalib** | Program Requirements & Database |
| **Elaiza Chavez** | Profile Progress Bar |
| **Rana Hoshyarsadeghi** | Presentation, Website, Feature Planning |

---

## Future Enhancements

- [x] Dark mode support *(Completed)*
- [x] Avatar selection *(Completed)*
- [x] Semester timeline info *(Completed)*
- [ ] Course notes and tagging
- [ ] Export schedule to calendar
- [ ] Push notifications for registration dates
- [ ] Course ratings integration
- [ ] Offline mode with cached data
- [ ] Multiple program support (double majors)
- [ ] GPA calculator
- [ ] Degree audit comparison

---

> **Note:** The following features were added after the project deadline for personal enhancement:
> - **Semester Timeline Info Button** (purple ℹ button) - View important semester dates
> - **Dark Mode Toggle** - Switch between light and dark themes on the Profile page
> - **Avatar Selection** - Choose from 6 different character avatars on the Profile page

---

<p align="center">
  Made at Simon Fraser University
</p>
