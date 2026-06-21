# **ENTEC - Educational & Career Guidance Platform**

## **Project Overview**

ENTEC is an Android mobile application designed to provide students with comprehensive career guidance and educational pathways. The platform connects students with detailed career information, educational resources, and personalized recommendations based on labor market data.

## **Key Features**

### **User Roles & Authentication**
- **Dual-Role System**: Supports both Student and Admin accounts
- **User Registration & Login**: Secure authentication with role-based access control
- **Session Management**: Persistent user sessions using SharedPreferences

### **Student Features**

#### 1. **Career Exploration**
   - Browse comprehensive career database with real labor market data
   - Search careers by occupation title or code
   - View detailed career information including:
     - Median annual wages
     - Employment outlook (% change by 2033)
     - Education & work experience requirements
     - 2023 employment statistics

#### 2. **Text-to-Speech (TTS) Integration**
   - Audio descriptions of career details
   - Abbreviated term expansion for better pronunciation
   - Accessibility-focused design

#### 3. **Class/Course Discovery**
   - Search and browse available educational courses
   - Enroll in relevant classes
   - Integrated course recommendations

#### 4. **Student Dashboard**
   - Personalized landing page with welcome message
   - Quick access to career and course resources
   - Logout functionality

### **Admin Features**
- **Career Database Management**: Import and manage occupation data
- **Admin Dashboard**: Control panel for system administration
- **Data Management**: Bulk upload career data from CSV sources
- **User Management**: Oversee student and admin accounts

### **Career Database**
- **Dataset**: `occupation_15_filtered.csv` (15 occupations filtered)
- **Data Fields**:
  - Occupation title and code
  - Employment statistics (2023 baseline, % change projection)
  - Median annual wages
  - Education/work experience requirements
  - Career descriptions

### **Technical Features**
- **SQLite Database**: Local data persistence for users, courses, and careers
- **CSV Integration**: Seamless career data import from asset files
- **Search Functionality**: Full-text search across careers and courses
- **Responsive UI**: ListView-based navigation and adaptive layouts

---

## **Architecture**

### **Core Components**

| Component | Purpose |
|-----------|---------|
| **MainActivity** | Entry point with role-based routing |
| **LoginActivity** | User authentication |
| **RegisterActivity** | New user registration |
| **StudentDashboardActivity** | Student hub with search & enrollment |
| **StudentLandingPage** | Personalized student welcome page |
| **CareerInfoActivity** | Career listing and browsing |
| **CareerDetailActivity** | Detailed career view with TTS |
| **AdminDashboardActivity** | Admin control panel |
| **ManageRecommendationsActivity** | Career recommendation management |
| **DatabaseManager** | CRUD operations for users, courses, careers |
| **DatabaseHelper** | SQLite schema and database initialization |
| **TTSManager** | Text-to-speech engine management |

### **Data Model**

#### **Users Table**
```
├── id (PRIMARY KEY)
├── username (UNIQUE)
├── password
└── role (Student/Admin)
```

#### **Careers Table**
```
├── id (PRIMARY KEY)
├── occupation_title
├── occupation_code
├── employment_2023
├── employment_percent_change
├── median_annual_wage
└── education_work_experience
```

#### **Courses Table**
```
├── id (PRIMARY KEY)
├── title
└── description
```

#### **Career-Courses Relationship**
```
└── Links occupations to relevant courses
```

---

## **Technology Stack**

| Technology | Version/Details |
|-----------|-----------------|
| **Language** | Java |
| **Platform** | Android (API Level 25+) |
| **Build System** | Gradle |
| **Database** | SQLite |
| **UI Framework** | Android Activities & XML layouts |
| **Accessibility** | Text-to-Speech API |
| **IDE** | Android Studio |
| **Build Tools** | 35.0.1 |
| **Target SDK** | 25 |
| **Compile SDK** | 25 |

---

## **Project Structure**

```
Entec-main/
├── README.md
├── build.gradle                          # Root Gradle configuration
├── settings.gradle                       # Gradle settings
├── gradle.properties                     # Gradle properties
├── local.properties                      # Local development properties
├── gradlew                               # Gradle wrapper (Unix)
├── gradlew.bat                           # Gradle wrapper (Windows)
├── .gradle/                              # Gradle build cache
├── .idea/                                # IDE configuration
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
│
├── app/                                  # Main application module
│   ├── build.gradle                      # App-specific Gradle config
│   ├── proguard-rules.pro               # ProGuard rules for obfuscation
│   ├── build/                           # Build outputs (generated)
│   │   ├── generated/                   # Generated sources
│   │   ├── intermediates/               # Intermediate build files
│   │   └── outputs/                     # APK and artifacts
│   │
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml      # App manifest
│       │   ├── assets/
│       │   │   └── occupation_15_filtered.csv    # Career data
│       │   │
│       │   ├── java/com/example/experiment/
│       │   │   ├── MainActivity.java
│       │   │   ├── LoginActivity.java
│       │   │   ├── RegisterActivity.java
│       │   │   ├── StudentDashboardActivity.java
│       │   │   ├── StudentLandingPage.java
│       │   │   ├── CareerInfoActivity.java
│       │   │   ├── CareerDetailActivity.java
│       │   │   ├── AdminDashboardActivity.java
│       │   │   ├── ManageRecommendationsActivity.java
│       │   │   ├── DatabaseManager.java
│       │   │   ├── DatabaseHelper.java
│       │   │   └── TTSManager.java
│       │   │
│       │   └── res/                     # Resources
│       │       ├── drawable/            # Vector drawables
│       │       ├── drawable-v24/        # API 24+ drawables
│       │       ├── layout/              # XML layouts
│       │       ├── mipmap-*/            # App icons (multiple densities)
│       │       └── values/              # Strings, colors, styles
│       │
│       ├── test/                        # Unit tests
│       │   └── java/com/example/experiment/
│       │
│       └── androidTest/                 # Instrumented tests
│           └── java/com/example/experiment/
│
├── CodeBatonHandoffEG/                  # Alternative project variant
│   └── (Similar structure to app/)
│
└── Project-Entec-main_00/               # Additional variant/backup
```

---

## **Getting Started**

### **Prerequisites**
- Android Studio (latest version)
- Java Development Kit (JDK) 8 or higher
- Android SDK (API 25+)
- Gradle 4.0+

### **Installation Steps**

1. **Clone Repository**
   ```bash
   git clone <repository-url>
   cd Entec-main
   ```

2. **Open in Android Studio**
   ```bash
   # File > Open > Select Entec-main folder
   ```

3. **Sync Gradle**
   - Android Studio will automatically sync dependencies
   - If not, use: `Build > Make Project`

4. **Build APK**
   ```bash
   gradlew build
   ```

5. **Deploy to Device**
   ```bash
   gradlew installDebug
   ```

### **First Time Setup**

1. **Launch App**: Tap ENTEC icon on your Android device
2. **Register**: Create a new account
   - Enter username and password
   - Select role: Student or Admin
3. **Login**: Use your credentials to authenticate
4. **Explore**: 
   - **Students**: Browse careers and enroll in courses
   - **Admins**: Manage career database

---

## **Usage Guide**

### **For Students**

#### **Explore Careers**
1. Navigate to "Career Info" section
2. Browse list of available occupations
3. Tap any occupation to view:
   - Job description
   - Median annual wage
   - Employment outlook
   - Required education
4. Listen to audio description (TTS)

#### **Search for Careers**
1. Use search bar on Student Dashboard
2. Enter occupation name or code
3. View filtered results
4. Select career for detailed information

#### **Enroll in Classes**
1. Go to "Classes" section
2. Search available courses
3. Tap "Enroll" button
4. Confirmation message appears

#### **Manage Recommendations**
1. Access "Recommendations" section
2. View suggested careers based on interests
3. Save or dismiss recommendations

### **For Admins**

#### **Manage Career Database**
1. Log in with Admin credentials
2. Access Admin Dashboard
3. Import careers from CSV:
   - Navigate to "Import Data"
   - Select CSV file
   - Confirm import

#### **View System Data**
1. Check student registrations
2. View enrollment statistics
3. Monitor course availability

---

## **Key Functionality**

### **Authentication Flow**
```
MainActivity (Route Based on Session)
    ├── → LoginActivity (If no session)
    │      ├── → RegisterActivity (New users)
    │      └── → Dashboard (Valid login)
    ├── → StudentDashboardActivity (Student role)
    │      ├── CareerInfoActivity
    │      ├── CareerDetailActivity
    │      └── StudentLandingPage
    └── → AdminDashboardActivity (Admin role)
           └── ManageRecommendationsActivity
```

### **Career Search Workflow**
```
Input: Career Query
    ↓
DatabaseManager.searchCareers()
    ↓
SQLite Query (LIKE matching)
    ↓
Return Filtered Results
    ↓
Display in ListView
    ↓
TTS Audio Description
```

### **Data Persistence**
- **User Sessions**: SharedPreferences
- **Career Data**: SQLite Database
- **Career Details**: CSV Asset Files
- **Recommendations**: Local Database

---

## **Database Schema**

### **Users Table**
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL
);
```

### **Careers Table**
```sql
CREATE TABLE careers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    occupation_title TEXT NOT NULL,
    occupation_code TEXT NOT NULL,
    employment_2023 INTEGER,
    employment_percent_change REAL,
    median_annual_wage REAL,
    education_work_experience TEXT
);
```

### **Courses Table**
```sql
CREATE TABLE courses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT
);
```

---

## **Configuration**

### **Gradle Configuration** (`build.gradle`)
```gradle
android {
    compileSdkVersion 25
    buildToolsVersion "35.0.1"
    
    defaultConfig {
        applicationId "com.example.experiment"
        minSdkVersion 25
        targetSdkVersion 25
        versionCode 1
        versionName "1.0"
    }
}
```

### **Android Manifest** (`AndroidManifest.xml`)
```xml
<manifest package="com.example.experiment">
    <application android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <!-- Additional activities listed in manifest -->
    </application>
</manifest>
```

---

## **Use Cases**

### **Student Use Cases**
- 📚 **Career Discovery**: Explore 15+ career options with detailed information
- 🔍 **Job Search**: Search occupations by title or code
- 💰 **Salary Research**: Check median wages for different careers
- 📈 **Job Outlook**: Review employment trends and growth projections
- 🎓 **Education Planning**: Find education/experience requirements
- 📞 **Audio Support**: Listen to career descriptions via TTS
- 📝 **Course Enrollment**: Discover and enroll in relevant courses

### **Admin Use Cases**
- 📊 **Database Management**: Import and update career data
- 👥 **User Management**: Manage student and admin accounts
- 📈 **System Analytics**: Monitor platform usage and enrollment
- 🔧 **Data Maintenance**: Update career information from CSV files

### **Educator/Counselor Use Cases**
- 🎯 **Career Advising**: Reference current labor market data
- 👨‍🎓 **Student Guidance**: Help students explore career paths
- 📚 **Curriculum Planning**: Align courses with job market demands

---

## **Contributing**

### **Code Style**
- Follow Java naming conventions (camelCase for methods/variables, PascalCase for classes)
- Use meaningful variable names
- Add comments for complex logic
- Keep methods focused and concise

### **Git Workflow**
1. Create feature branch: `git checkout -b feature/your-feature`
2. Commit changes: `git commit -am 'Add your feature'`
3. Push branch: `git push origin feature/your-feature`
4. Create pull request for review

---

## **Troubleshooting**

### **Build Issues**
- **Gradle sync fails**: Delete `.gradle` folder and resync
- **Build tools missing**: Update Android SDK via SDK Manager
- **Dependency conflicts**: Run `gradlew clean build`

### **Runtime Issues**
- **App crashes on launch**: Check AndroidManifest.xml permissions
- **Database errors**: Verify DatabaseHelper initialization
- **TTS not working**: Ensure TextToSpeech initialization completes
- **CSV import fails**: Verify file format and encoding

### **Data Issues**
- **Careers not showing**: Check `occupation_15_filtered.csv` exists in assets/
- **Login fails**: Verify user exists in database
- **Search returns nothing**: Check database has populated data

---

## **Testing**

### **Unit Tests**
```bash
gradlew test
```

### **Instrumented Tests** (on device/emulator)
```bash
gradlew connectedAndroidTest
```

### **Manual Testing Checklist**
- [ ] User registration works for both roles
- [ ] Login authentication functions correctly
- [ ] Career search returns accurate results
- [ ] TTS audio plays properly
- [ ] Course enrollment saves to database
- [ ] Admin can import career data
- [ ] Session persistence works after app restart
- [ ] Logout clears session properly

---

## **Performance Considerations**

- **CSV Parsing**: Cached after first load
- **Database Queries**: Indexed on occupation_title and occupation_code
- **TTS**: Lazy initialized on demand
- **UI Rendering**: ListView with adapter for efficient scrolling

---

## **Security**

- **Passwords**: Stored in SQLite (consider hashing in production)
- **Sessions**: Managed via SharedPreferences (device-specific)
- **CSV Imports**: Validated on format and content
- **Database**: Local SQLite (no network transmission)

### **Production Recommendations**
- [ ] Implement password hashing (BCrypt)
- [ ] Add data encryption for sensitive fields
- [ ] Validate all user inputs
- [ ] Use HTTPS for any remote API calls
- [ ] Implement rate limiting on authentication

---

## **Future Enhancements**

- 🌐 Remote backend integration
- 👤 User profile customization
- 💾 Cloud data synchronization
- 📊 Advanced analytics and reporting
- 🔔 Push notifications for job alerts
- 🌍 Multi-language support
- 📱 Responsive design improvements
- 🎮 Gamification features
- 🔗 LinkedIn/Indeed integration
- 📝 Personalized career assessments

---

## **License**

[Specify your license here - MIT, Apache 2.0, etc.]

---

## **Contact & Support**

For issues, questions, or contributions:
- **Project Lead**: [Name/Email]
- **Repository**: [GitHub URL]
- **Issues**: [Issue Tracker URL]

---

## **Changelog**

### **Version 1.0** (Current)
- Initial release
- User authentication system
- Career database with 15 occupations
- Student dashboard and course enrollment
- Admin dashboard for data management
- Text-to-Speech integration
- Search functionality

---

**Last Updated**: June 2026  
**Status**: Active Development

