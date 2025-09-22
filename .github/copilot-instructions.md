# Frühtau - Android Offline Map Application

Frühtau (German for "morning dew") is an Android application for offline mapping using Mapsforge, built with Kotlin, Jetpack Compose, and modern Android architecture components.

**ALWAYS reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.**

## Working Effectively

### Prerequisites and Setup
- **Android SDK Required**: Ensure `ANDROID_HOME=/usr/local/lib/android/sdk` is set
- **Java Version**: Java 17 (confirmed working with OpenJDK 17.0.16)
- **Network Dependencies**: Android Gradle Plugin 8.13.0 requires internet access to download
- **Build Tools**: Uses Gradle 8.13 with Android build tools 34.0.0-36.0.0

### Bootstrap and Build
**CRITICAL BUILD TIMING**: Android builds typically take 5-15 minutes depending on network and cache state.

```bash
# Set required environment variables
export ANDROID_HOME=/usr/local/lib/android/sdk

# Build the application - NEVER CANCEL, set timeout to 30+ minutes
./gradlew assembleDebug
```

**KNOWN LIMITATION**: Build currently fails with network restrictions due to Android Gradle Plugin 8.13.0 download requirements. In restricted environments, consider:
- Use offline mode: `./gradlew assembleDebug --offline` (fails without cached dependencies)
- Downgrade AGP version in `gradle/libs.versions.toml` if network allows partial access

### Code Quality and Formatting
**ALWAYS run these before committing changes - the pre-push hook enforces them:**

```bash
# Format code - takes 1-2 minutes, NEVER CANCEL
./gradlew ktfmtFormat

# Check code formatting - takes 1-2 minutes, NEVER CANCEL  
./gradlew ktfmtCheck

# Run all checks including lint - takes 5-10 minutes, NEVER CANCEL, set timeout to 20+ minutes
./gradlew check
```

### Testing
```bash
# Run unit tests - takes 2-5 minutes, NEVER CANCEL, set timeout to 15+ minutes
./gradlew test

# Run instrumented tests (requires emulator/device) - takes 10-20 minutes, NEVER CANCEL, set timeout to 30+ minutes
./gradlew connectedAndroidTest
```

## Application Architecture

### Key Components
- **Main Entry**: `MainActivity.kt` with `FruehtauApp` application class
- **UI Framework**: Jetpack Compose with Voyager navigation
- **Dependency Injection**: Hilt (configured in `di/` package)
- **Maps**: Mapsforge for offline map rendering (`io/map/` package)
- **Location**: Google Play Services Location API (`service/LocationService.kt`)
- **Database**: Room database for offline storage (`io/db/` package)
- **Background Work**: WorkManager for downloads (`io/download/` package)

### Project Structure
```
app/src/main/java/io/gropp/fruehtau/
├── MainActivity.kt              # Main Android activity
├── FruehtauApp.kt              # Application class with Hilt setup
├── di/                         # Dependency injection modules
├── io/
│   ├── db/                     # Room database entities and DAOs
│   ├── download/               # Download manager for map files
│   └── map/                    # Mapsforge map handling and repository
├── service/                    # Location and background services
├── ui/
│   ├── screens/                # Main app screens (MapScreen, SettingsScreen)
│   ├── map/                    # Map-specific UI components
│   ├── menu/                   # Navigation menu components
│   └── toolbar/                # Custom toolbar components
└── util/                       # Utility functions
```

## Validation and Testing

### Manual Validation Scenarios
**ALWAYS test these scenarios after making changes:**

1. **App Launch Flow**:
   - App starts without crashes (check `FruehtauApp.onCreate()`)
   - `MainActivity` loads and displays `MapScreen` correctly
   - Hilt dependency injection initializes properly
   - No missing permission errors in logcat

2. **Map Display and Navigation**: 
   - App launches and shows map interface from `MapView` component
   - Mapsforge tiles render correctly (check `TileRendererLayerFlow`)
   - Pan and zoom gestures work smoothly
   - Map responds to touch without lag

3. **Location Services**:
   - Location permission requested via `WithLocationPermission` wrapper
   - GPS location updates received by `LocationService`
   - `LocationIndicator` appears on map at correct position
   - "Center on location" toolbar button functions correctly

4. **UI Components and Navigation**:
   - Menu can be opened/closed via `MenuScaffold`
   - Toolbar buttons in `ToolbarScaffold` are responsive  
   - Navigation between `MapScreen` and `SettingsScreen` works
   - All Compose animations and transitions are smooth

5. **Offline Functionality**:
   - App works without internet after initial setup
   - Map files can be imported via `MapRepository.importFromUri()`
   - Downloaded maps persist between app restarts
   - Background downloads complete via `DownloadService`

6. **Data Persistence**:
   - Room database operations succeed
   - App state survives configuration changes
   - WorkManager tasks execute correctly

### Code Quality Validation
**ALWAYS run before committing - matches pre-push hook in `git-hooks/pre-push`:**
```bash
# This matches the pre-push hook exactly - takes 5-10 minutes, NEVER CANCEL
./gradlew ktfmtCheck check
```

**Debugging Failed Checks:**
```bash
# Fix formatting issues automatically
./gradlew ktfmtFormat

# Run only specific checks for faster feedback
./gradlew ktfmtCheck          # Kotlin formatting only
./gradlew lint                # Android lint only  
./gradlew compileDebugKotlin  # Compilation errors only
```

## Common Development Tasks

## Common Tasks and Quick Reference

### Frequently Used Commands
Save time by referencing these common outputs instead of running commands repeatedly:

```bash
# Project structure from repository root
ls -la
# Output: .git .gitignore LICENSE app build.gradle.kts git-hooks gradle gradle.properties gradlew gradlew.bat images settings.gradle.kts

# Main source structure  
find app/src/main/java -type d -maxdepth 3
# Key directories: di/ io/db/ io/download/ io/map/ service/ ui/menu/ ui/screens/ ui/toolbar/ ui/map/

# Gradle tasks relevant for development
./gradlew tasks --group="verification"
# Key tasks: check, ktfmtCheck, lint, test

./gradlew tasks --group="build"  
# Key tasks: assemble, assembleDebug, assembleRelease, build
```

### Development Workflow
1. **Before making changes**: `./gradlew ktfmtCheck` to establish baseline
2. **During development**: Make incremental changes, test locally
3. **Format code**: `./gradlew ktfmtFormat` to fix styling
4. **Validate changes**: `./gradlew ktfmtCheck check` before commit
5. **Test manually**: Launch app, verify UI flows work correctly

### Adding New Features
1. **UI Changes**: Modify Compose components in `ui/` package
   - Follow existing patterns in `MapScreen.kt` and `SettingsScreen.kt`
   - Use `ToolbarScaffold` and `MenuScaffold` for consistent layout
2. **Map Features**: Extend `MapRepository` or add to `ui/map/`
   - Check `MapView.kt` for integration patterns
   - Use `LocationService` for GPS-related features
3. **Data Models**: Add Room entities in `io/db/`
   - Update `AppDatabase.kt` with new DAOs
   - Consider database migrations for schema changes
4. **Background Tasks**: Create Workers in `io/download/`
   - Follow `DownloadService` patterns for long-running operations
   - Use Hilt injection for dependencies

### Dependencies and Versions
Key versions defined in `gradle/libs.versions.toml`:
- Android Gradle Plugin: 8.13.0
- Kotlin: 2.2.20  
- Compose BOM: 2025.09.00
- Hilt: 2.57.1
- Room: 2.8.0
- Mapsforge: 0.25.0

### Git Hooks
Pre-push hook runs: `./gradlew ktfmtCheck check`
- Enforces Kotlin formatting (ktfmt with kotlinLangStyle, maxWidth=120)
- Runs lint checks and code analysis
- **NEVER SKIP**: Always ensure code passes these checks

## Troubleshooting

## Troubleshooting

### Build Issues
- **"Plugin not found" errors**: Network restrictions prevent AGP download
  - Workaround: Use cached Gradle dependencies if available  
  - Alternative: Work offline with `--offline` flag (limited)
  - Last resort: Downgrade AGP version in `gradle/libs.versions.toml`
- **"ANDROID_HOME not set"**: Ensure `export ANDROID_HOME=/usr/local/lib/android/sdk`
- **OutOfMemory errors**: Increase heap in `gradle.properties` (currently `org.gradle.jvmargs=-Xmx2048m`)
- **KSP compilation failures**: Clean build with `./gradlew clean` then rebuild
- **Hilt errors**: Check `@AndroidEntryPoint` annotations and module configurations

### Runtime Issues  
- **App crashes on startup**: 
  - Check `FruehtauApp.onCreate()` Timber initialization
  - Verify Hilt modules in `di/` package are properly configured
  - Look for missing AndroidManifest.xml permissions
- **Location not working**: 
  - Ensure location permissions in AndroidManifest.xml
  - Check Google Play Services availability on device/emulator
  - Verify `LocationService` is properly injected via Hilt
- **Maps not loading**: 
  - Check mapsforge map files exist in app's `files/maps/` directory
  - Verify `MapRepository.loadMapOrDefault()` logic
  - Look for download completion in `DownloadService`
- **Compose UI issues**:
  - Check Voyager navigation setup in `Main.kt`
  - Verify theme application in `AppTheme.kt`
  - Look for missing `@Composable` annotations

### Performance Issues
- **Slow builds**: 
  - Use Gradle daemon: `./gradlew --daemon`
  - Check build cache: `./gradlew --build-cache`
  - Increase parallel workers in `gradle.properties`
- **Large APK size**: 
  - Debug builds have ProGuard disabled (see `app/build.gradle.kts`)
  - Release builds use code shrinking automatically
- **Map rendering lag**:
  - Check tile cache size in mapsforge configuration
  - Verify UI thread isn't blocked by location updates

## Additional Notes

- **No README.md**: This project lacks traditional documentation files
- **AGPL v3 License**: Copyleft license requires source disclosure for derivatives  
- **Target SDK**: Currently targets Android API 36 (Android 14+)
- **Minimum SDK**: Supports Android API 26 (Android 8.0+)
- **Package Name**: `io.gropp.fruehtau`
- **App Name**: "Frühtau" (displayed in launcher)

Always check that your changes maintain the offline-first nature of this mapping application and follow the established Compose + Hilt architecture patterns.