# AGENTS.md - FloatingBallApp

## Build Commands

```bash
# Debug build
./gradlew.bat assembleDebug

# Release build (signed with keystore)
./gradlew.bat assembleRelease

# Clean build
./gradlew.bat clean

# Lint (currently configured lenient: abortOnError=false)
./gradlew.bat lint
```

Release APK output: `app/build/outputs/apk/release/app-release.apk`

## Test Commands

No automated tests exist yet. JUnit 4.13.2 and Espresso 3.4.0 are declared as dependencies but no test source files are present.

```bash
# Unit tests (once test/ directory is populated)
./gradlew.bat test

# Instrumented tests
./gradlew.bat connectedAndroidTest

# Run a single test class
./gradlew.bat test --tests com.example.floatingball.SomeTest

# Run a single test method
./gradlew.bat test --tests "com.example.floatingball.SomeTest.specificMethod"
```

## Project Structure

```
app/src/main/java/com/example/floatingball/
├── MainActivity.java                  # Permission引导Activity
├── AppListActivity.java               # 应用选择界面（定制菜单）
├── application/FloatingBallApplication.java
├── helper/PreferencesHelper.java      # SharedPreferences + 定制菜单持久化
├── helper/PermissionHelper.java
├── receiver/BootCompletedReceiver.java
├── service/FloatingBallService.java   # 悬浮球核心服务
├── service/KeepAliveService.java
├── util/AppLauncher.java             # 应用启动工具
├── util/ScreenUtils.java
├── view/FloatingBallView.java        # 悬浮球自定义View
└── view/MenuPopupView.java           # 动态菜单（1固定+6定制）
```

## Build Configuration

- **AGP**: 7.4.2 | **Gradle**: 8.2
- **compileSdk/targetSdk**: 29 | **minSdk**: 19
- **Java**: source/target compatibility 1.8
- **minifyEnabled**: false (ProGuard not active)
- **Signing**: keystore at `../floatingball-release.keystore`, alias `floatingball`
- **Lint**: `checkReleaseBuilds false`, `abortOnError false`

## Code Style

### Formatting
- 4-space indentation (no tabs)
- K&R brace style: opening brace on same line
- One blank line between methods
- File ends with newline

### Imports
Ordered by group, separated by blank lines:
1. `android.*`
2. `androidx.*`
3. `com.example.floatingball.*`
4. `java.*`

No wildcard imports.

### Naming Conventions
| Element | Style | Example |
|---------|-------|---------|
| Class | PascalCase | `FloatingBallService` |
| Method | camelCase | `goToHomeScreen()` |
| Constant | UPPER_SNAKE_CASE | `ACTION_SHOW`, `ALPHA_DELAY` |
| Member variable | camelCase | `windowManager` |
| Local variable | camelCase | `deltaX`, `savedX` |
| Package | all lowercase, feature-layered | `service`, `view`, `helper` |
| Interface (callback) | `OnXxxListener` | `OnBallClickListener` |
| Layout ID | camelCase | `btnGoSettings`, `appListView` |
| String resource | snake_case | `menu_home`, `app_not_installed` |

### Language Features
- Lambda for single-method callbacks: `v -> { ... }`
- Anonymous inner class for multi-method interfaces
- Use `new Handler(Looper.getMainLooper())` (never deprecated `new Handler()`)
- API level checks: `Build.VERSION.SDK_INT >= Build.VERSION_CODES.O`
- Use `getResources().getColor(R.color.xxx)` (compatible with minSdk 19)

### Error Handling
- `try-catch` with `e.printStackTrace()` for non-critical errors
- Return `null` or `false` on failure for utility methods
- Show `Toast` for user-facing errors (e.g., app not installed)
- Global `UncaughtExceptionHandler` in Application class for crash recovery
- No logging framework; use `e.printStackTrace()` only

### Comments
- Chinese Javadoc for class-level and key method descriptions
- `@param` / `@return` tags on public methods where helpful
- Do not add comments unless they explain non-obvious logic

## Architecture Patterns

- **Service-based**: `FloatingBallService` manages overlay views via `WindowManager`
- **Callback interfaces**: Custom listener interfaces on Views (set via setter methods)
- **PreferencesHelper**: Centralized `SharedPreferences` wrapper; all persistence goes through this class
- **Utility classes**: Static methods in `util/` package (`AppLauncher`, `ScreenUtils`)
- **Keep-alive**: Dual-service pattern (`FloatingBallService` + `KeepAliveService`) with `START_STICKY`
- **Menu system**: `MenuPopupView.rebuildMenu()` dynamically constructs menu from PreferencesHelper; `AppListActivity` saves selections and notifies service via `ACTION_REFRESH_MENU` intent

## Key Constraints

- This is an Android overlay/悬浮窗 app targeting embedded/car navigation devices (480x320 screens)
- All views are added to `WindowManager` as system overlays, not inside Activity layouts
- `FLAG_NOT_FOCUSABLE` on overlay windows — menu clicks must work without focus
- Custom menus are persisted via SharedPreferences keys: `custom_menu_{index}_pkg`, `custom_menu_{index}_label`, `custom_menu_count`
- Max 6 custom menus (`PreferencesHelper.MAX_CUSTOM_MENUS`)
- Long press (3s) on floating ball hides it; state saved to SP

## CLAUDE.md Guidelines

- **Think Before Coding**: State assumptions; if uncertain, ask; present alternatives
- **Simplicity First**: Minimum code solving the problem; no speculative features or over-abstraction
- **Surgical Changes**: Touch only what you must; match existing style; don't refactor adjacent code
- **Goal-Driven Execution**: Define verifiable success criteria; loop until verified
