<div align="center">

# CampusKit

### The Intelligent Campus Companion for Android

**A beautifully crafted, feature-rich Android application that reimagines the campus experience, built with love ❤️ for college students.**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-brightgreen)](https://developer.android.com/topic/architecture)
[![DI](https://img.shields.io/badge/DI-Hilt-FF6F00?logo=google&logoColor=white)](https://dagger.dev/hilt/)
[![Room](https://img.shields.io/badge/Persistence-Room-003B57)](https://developer.android.com/training/data-storage/room)
[![API](https://img.shields.io/badge/API-Retrofit%20%2B%20Moshi-blue)](https://square.github.io/retrofit/)
[![RAG](https://img.shields.io/badge/AI-RAG%20%2B%20Vector%20DB-blueviolet)](https://github.com/Sandesh282/CampusKit)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-brightgreen)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-brightgreen)](https://developer.android.com)

</div>

---

## Why CampusKit?

Every college student juggles a chaotic mix of timetables, attendance spreadsheets, mess menus, lost belongings, and event posters scattered across WhatsApp groups. **CampusKit** consolidates all of it into one elegant, offline-first Android app with a stunning AMOLED-optimized dark UI.

> **CampusKit doesn't just track your campus life, it *understands* it.** Smart attendance health indicators tell you exactly how many classes you can safely skip. The "Yuck List" learns your food preferences and highlights items you hate on the mess menu. The calendar gives you a bird's-eye view of your academic and personal schedule. Everything works offline, syncs intelligently, and looks absolutely gorgeous.

---

## Feature Highlights

### Intelligent Attendance Tracker
- **Per-subject tracking** with one-tap attended/bunked marking
- **Safe Bunk Calculator** — instantly know how many classes you can skip while staying above your minimum attendance threshold
- **Recovery Estimator** — if you're below the cutoff, see exactly how many consecutive classes you need to attend to recover
- **Tri-state health indicators** (Safe / Warning / Critical) with color-coded visual feedback
- **Semester-aware filtering** — attendance auto-scopes to your current program and semester
- **Reactive Kotlin Flows** — UI updates instantly as attendance data changes

### Mess Menu with Yuck List
- **Full weekly menu** with Breakfast, Lunch, Snacks, and Dinner for every day
- **Today's Menu** auto-highlights based on the current day
- **Personal "Yuck List"** — mark food items you dislike and they get visually flagged across the entire menu
- **Room-backed persistence** — your yuck preferences survive app restarts
- **Notification Channel** — optional daily mess menu alerts so you never walk into an unpleasant surprise

### Academic Resource Hub
- **Semester-specific subjects** auto-populated from a comprehensive catalog covering **4 programs (IT, CSE, CSAI, CSB) across 8 semesters**
- **Question Papers and Notes** — browse and view past year papers and notes per subject with an integrated PDF viewer
- **Remote sync via StudentHub API** — resources are fetched from the StudentHub backend using Retrofit + Moshi, cached in Room with a **24-hour TTL** smart sync strategy
- **Offline-first architecture** — stale data is served from cache while background sync runs silently
- **SyncMetadata tracking** — the app knows exactly when data was last refreshed and only hits the network when needed

### Campus Events Board
- **Event discovery feed** with title, date, venue, organizer, and description
- **Reminder toggle** — set reminders for events you don't want to miss
- **Search and filter** — quickly find events by keyword
- **Long-press delete** with confirmation dialogs
- **Room-backed persistence** with automatic first-launch seeding

### Lost and Found
- **Community lost-and-found board** for reporting and finding misplaced items
- **Multi-channel contact** — WhatsApp, Telegram, and phone number support
- **Image support** — attach photos of found items
- **Long-press delete** with confirmation
- **Room-backed with automatic mock data seeding** for first-launch experience

### Calendar and Scheduling
- **Dual-view calendar** — toggle between a **monthly grid view** with attendance dot overlays and a **daily timeline view** with hour-by-hour event blocks
- **Add custom events** with title, date, time range, color coding, location, and description
- **Course attendance overlay** — see which days you attended each course directly on the calendar grid
- **Color-coded event blocks** (Pink, Peach, Sky Blue, Green, Lavender) for visual distinction
- **Room-backed** with full CRUD operations

### Campus Guide
- **Curated nearby locations** — restaurants, hotels, shopping, and landmarks around campus
- **Category-based filtering** — browse by type
- **Direct Google Maps links** — one tap to navigate
- **Distance indicators** — know how far each place is from campus

### Quick Reads
- **Curated article feed** — handpicked articles on Coding, AI and ML, College Life, and Open Source
- **Multi-source aggregation** — content from Medium, GitHub, Dev.to, and more
- **Category-based browsing** — discover content that matters to you

### Campus AI Assistant
- **Natural language queries** — ask anything about your campus life in plain English
- **On-device RAG pipeline** — user query is embedded on-device using `all-MiniLM-L6-v2` via ONNX Runtime, then matched against a local vector store of all campus data via cosine similarity
- **Room-backed vector store** — 384-dimensional float embeddings stored as BLOBs in a `vector_chunks` table; cosine similarity search runs entirely on-device in Kotlin
- **Semantic retrieval** — finds relevant data by *meaning*, not keywords (e.g. "what should I eat before my hardest class" retrieves both mess menu and attendance chunks)
- **Grounded Gemini responses** — top-k retrieved chunks are injected as context into the Gemini prompt, eliminating hallucinations
- **Background indexing** — `VectorIndexingWorker` re-indexes all campus data at startup via WorkManager
- **Streaming UI** — Gemini response streams token-by-token via Kotlin Flow for a real-time chat feel

### Onboarding Experience
- **Beautiful multi-page onboarding flow** with animated transitions
- **Personalization** — set your name, program, and semester during onboarding
- **Light-themed onboarding** that transitions smoothly into the dark AMOLED main app
- **One-time display** — preferences persisted so you only see it once

---

## Architecture and Engineering

CampusKit is engineered with a **Clean Architecture** approach using the **MVVM** pattern, ensuring separation of concerns across well-defined layers:

```
┌───────────────────────────────────────────────────┐
│                    UI Layer                       │
│  Jetpack Compose Screens + ViewModels             │
│  (HomeScreen, MessScreen, AssistantScreen, ...)   │
├───────────────────────────────────────────────────┤
│                 Domain Layer                      │
│  Use Cases + Repository Interfaces                │
│  (CalculateSafeBunksUseCase, AskAssistantUseCase) │
├───────────────────────────────────────────────────┤
│                  Data Layer                       │
│  Room DAOs + Entities + Repositories              │
│  Retrofit API + DTOs + DataStore Preferences      │
│  ONNX Runtime (embedding) + Vector Store (RAG)    │
└───────────────────────────────────────────────────┘
```

### RAG Pipeline

```
User Query
    │
    ▼
[ONNXEmbeddingService]   ← on-device, all-MiniLM-L6-v2 (~86MB)
    │  embed query → FloatArray(384)
    ▼
[VectorStore (Room)]     ← dot product over vector_chunks table
    │  top-5 semantically similar chunks
    ▼
[GeminiAssistantService] ← cloud generation with grounded context
    │  streamed token response
    ▼
 AssistantViewModel / UI
```

### Key Architectural Decisions

| Decision | Rationale |
|---|---|
| **Hilt DI** | Compile-time verified dependency injection with minimal boilerplate; modules clearly separate data providers from repository bindings |
| **Room Database** | Single `AppDatabase` managing 11 entity tables with type-safe DAOs; supports reactive `Flow`-based queries |
| **Kotlin Flows + StateFlow** | Fully reactive data pipeline from Room to Repository to ViewModel to Compose UI; no LiveData anywhere |
| **DataStore Preferences** | Modern, coroutine-native preference storage for user settings (program, semester, student name) |
| **Retrofit + Moshi** | Type-safe HTTP client with Moshi for JSON parsing; KSP-powered code generation for adapters |
| **Offline-first with TTL Sync** | Serve cached data immediately, sync in background only when stale (24-hour TTL tracked via `SyncMetadataEntity`) |
| **Sealed Classes for Resources** | Polymorphic `Resource` model (PastYearPaper, Note) enables type-safe resource handling |
| **Use Cases** | Stateless domain logic extracted into injectable use cases for testability and reuse |
| **On-device Vector Embeddings** | `all-MiniLM-L6-v2` runs via ONNX Runtime; 384-dim unit vectors stored as BLOBs in Room; cosine similarity = dot product, no external vector DB needed at this data scale |
| **RAG over keyword routing** | Semantic retrieval handles fuzzy, multi-topic queries that keyword matching misses; context is grounded in the user's actual Room data |

## Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin 2.0 |
| **UI Framework** | Jetpack Compose with Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **Dependency Injection** | Hilt (Dagger) |
| **Local Database** | Room (11 entities, 10 DAOs) |
| **Vector DB** | Room-backed `vector_chunks` table with `FloatArray` BLOB embeddings |
| **On-device ML** | ONNX Runtime + `all-MiniLM-L6-v2` (sentence embeddings, 384-dim) |
| **LLM / Generation** | Gemini API (gemini-flash) with RAG-grounded context |
| **Preferences** | DataStore Preferences |
| **Networking** | Retrofit 2 + Moshi |
| **Async** | Kotlin Coroutines + Flow |
| **Background Work** | WorkManager |
| **Image Loading** | Coil Compose |
| **Navigation** | Custom AnimatedContent-based tab navigation with sub-screen stack |
| **Build System** | Gradle KTS with Version Catalogs |
| **Code Generation** | KSP (Kotlin Symbol Processing) |
| **Testing** | JUnit 4 + MockK + Turbine + Robolectric + Espresso + Hilt Testing |
| **Min SDK** | 26 (Android 8.0 Oreo) |
| **Target SDK** | 36 |

## Testing

CampusKit maintains a comprehensive test suite across both unit and instrumented test layers:

### Unit Tests (`test/`)
| Test | Coverage Area |
|---|---|
| `CalculateSafeBunksUseCaseTest` | Core attendance math: percentages, safe bunks, recovery calculations |
| `UpdateAttendanceUseCaseTest` | Attendance state mutation logic |
| `FilterEventsUseCaseTest` | Event search and filtering with edge cases |
| `AttendanceViewModelTest` | ViewModel state management for attendance UI |
| `MessViewModelTest` | Menu loading and yuck list interaction |
| `EventsViewModelTest` | Event search, delete, and reminder flows |
| `LostFoundViewModelTest` | Item CRUD and delete confirmation |
| `CalendarViewModelTest` | Mode toggling and date navigation |
| `AttendanceRepositoryTest` | Repository and DAO interaction |
| `MessRepositoryTest` | Yuck item persistence |
| `EventsRepositoryTest` | Event seeding and CRUD operations |
| `CalendarRepositoryTest` | Calendar event mapping and seeding |
| `LostFoundRepositoryTest` | Room-backed lost/found operations |

### Instrumented Tests (`androidTest/`)
| Test | Coverage Area |
|---|---|
| `AttendanceDaoTest` | Room DAO queries for attendance tracking |
| `TimetableDaoTest` | Timetable entity persistence |
| `YuckItemDaoTest` | Yuck list CRUD operations |
| `EventDaoTest` | Event insertion, queries, and reminder toggling |
| `LostFoundDaoTest` | Lost/found item persistence |
| `CalendarEventDaoTest` | Calendar event storage and retrieval |

## Project Structure

```
CampusKit/
├── app/src/main/java/com/example/campuskit/
│   ├── CampusKitApplication.kt          # Hilt app + notification channels + vector index enqueue
│   ├── MainActivity.kt                  # Single-activity entry point with onboarding flow
│   │
│   ├── data/                            # Data Layer
│   │   ├── AppDatabase.kt               # Room database (11 entities, v6)
│   │   ├── assistant/
│   │   │   ├── AssistantContextBuilder.kt   # RAG retrieval: embed query → cosine search → top-k chunks
│   │   │   ├── GeminiAssistantService.kt    # Gemini API wrapper, streaming Flow<String>
│   │   │   ├── embedding/
│   │   │   │   ├── BertTokenizer.kt          # WordPiece tokenizer (vocab.txt from assets)
│   │   │   │   └── ONNXEmbeddingService.kt  # ONNX Runtime inference → 384-dim unit vector
│   │   │   └── vector/
│   │   │       ├── VectorChunkEntity.kt     # Room entity: text + FloatArray(384) embedding
│   │   │       ├── VectorChunkDao.kt        # upsert / getAll / getBySource / deleteBySource
│   │   │       ├── FloatArrayConverter.kt   # TypeConverter: FloatArray ↔ ByteArray BLOB
│   │   │       ├── VectorStore.kt           # Index all campus data + dot product search
│   │   │       └── VectorIndexingWorker.kt  # HiltWorker: runs indexAll() in background
│   │   ├── academic/
│   │   │   ├── SubjectCatalog.kt         # Static catalog: 4 programs x 8 semesters
│   │   │   ├── local/                    # Room entities + DAOs (Subject, Offering, Resource, SyncMetadata)
│   │   │   ├── remote/                   # Retrofit API + DTOs (StudentHub integration)
│   │   │   ├── repository/               # DefaultAcademicRepository (offline-first + TTL sync)
│   │   │   └── prefs/                    # DataStore-backed AcademicPreferencesManager
│   │   ├── attendance/                   # AttendanceEntity, DAO, Repository, TimetableEntity
│   │   ├── mess/                         # MessMenu, YuckItemEntity, DAO, Repository
│   │   ├── events/                       # Event model, Entity, DAO, Repository + mock data
│   │   ├── lostfound/                    # LostFoundItem, Entity, DAO, Repository + mock data
│   │   ├── calendar/                     # CalendarEvent, CourseInfo, Entity, DAO, Repository
│   │   ├── campusguide/                  # NearbyPlace catalog with Google Maps links
│   │   └── quickreads/                   # QuickRead catalog (articles from Medium, GitHub, etc.)
│   │
│   ├── domain/                          # Domain Layer
│   │   ├── academic/
│   │   │   ├── model/                    # Program, Subject, Offering, Resource (sealed class)
│   │   │   └── repository/               # AcademicRepository interface
│   │   ├── attendance/
│   │   │   ├── AttendanceStatus.kt       # Sealed type: Safe, Warning, Critical, NoData
│   │   │   ├── CalculateSafeBunksUseCase.kt  # Pure math for bunk/recovery calculations
│   │   │   └── UpdateAttendanceUseCase.kt
│   │   └── events/
│   │       └── FilterEventsUseCase.kt    # Search filtering logic
│   │
│   ├── di/                              # Dependency Injection
│   │   ├── DataModule.kt                # Provides Database + all 9 DAOs
│   │   └── RepositoryModule.kt          # Binds repositories + Retrofit + Moshi
│   │
│   └── ui/                              # UI Layer
│       ├── theme/                        # AMOLED color system, typography, shapes
│       ├── navigation/                   # Custom bottom nav with bouncy spring animations
│       ├── onboarding/                   # Multi-page onboarding with animated transitions
│       ├── home/                         # Home screen: attendance, QP, notes + search
│       ├── mess/                         # Weekly mess menu with yuck highlighting
│       ├── academic/                     # Notes list, QP list, PDF viewer
│       ├── events/                       # Event board with search and reminders
│       ├── lostfound/                    # Lost and found community board
│       ├── calendar/                     # Grid view + timeline view + add event
│       ├── campusguide/                  # Nearby locations with map links
│       └── quickreads/                   # Curated article feed
│
├── app/src/test/                        # 15 unit test files
├── app/src/androidTest/                 # 8 instrumented test files
└── gradle/                              # Version catalog + wrapper
```

## Getting Started

### Prerequisites
- **Android Studio** Ladybug or newer
- **JDK 11+**
- **Android SDK 36**

### Build and Run

```bash
# Clone the repository
git clone https://github.com/Sandesh282/CampusKit.git
cd CampusKit

# Open in Android Studio and sync Gradle
# Or build from command line:
./gradlew assembleDebug

# Run tests
./gradlew test                  # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests (requires emulator/device)
```

## Future Additions

- [ ] **Timetable Auto-Sync** — Automatic class schedule import and smart attendance reminders
- [ ] **Multi-Campus Support** — Configurable campus profiles for different institutions
- [ ] **Collaborative Lost and Found** — Real-time community board with image uploads and chat
- [ ] **Widget Support** — Home screen widgets for today's attendance, mess menu, and upcoming events
- [ ] **Wear OS Companion** — Quick attendance marking from your wrist
