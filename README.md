# Culturunya - Cultural Events Discovery Platform

## Overview

Culturunya is an Android application designed to help users discover, attend, and engage with cultural events in Catalonia. The app provides a comprehensive platform for event discovery, social interaction, and sustainable transportation integration.

## Co-founders
- **Eric Herrero** - @Hefi002
- **Marc Planes** - @Marc-PlanasBosch
- **Richard Pie** - @richardpie
- **Arnau Prohens** - @mapsdos
- **Matteo Verdaguer** - @matteuvf
- **Kirian Roca** - @KirianRoca15
- **Martin Viteri** - @Picklin

### Core Functionality
- **Event Discovery**: Browse and filter cultural events by category, date, and location
- **User Authentication**: Secure login with traditional credentials or Google OAuth
- **Social Features**: Chat with other users and administrators
- **Rating System**: Rate and review attended events
- **Gamification**: Quiz system and leaderboards for user engagement
- **Sustainable Transport**: Integration with electric vehicle charging points

### Key Components

#### API Layer
- **Main API (`Api.kt`)**: Comprehensive REST API interface for all backend operations
- **Charging API (`ChargingApi.kt`)**: Integration with electric vehicle charging infrastructure

#### Data Models
- **Events**: Complete event information with location and categorization
- **Users**: User profiles, authentication, and preferences
- **Chats**: Messaging system between users and administrators
- **Ratings**: Event rating and review system
- **Reports**: Content moderation and reporting system

#### Architecture
- **MVVM Pattern**: Clean separation of concerns with ViewModels
- **Repository Pattern**: Data access abstraction layer
- **Retrofit**: Type-safe HTTP client for API communication
- **Compose UI**: Modern Android UI toolkit

## How to Use

To use the Culturunya app:

1. Download the `app.apk` file from the releases section
2. Enable "Install from unknown sources" in your Android device settings
3. Install the APK file on your Android device
4. Launch the app and create an account or sign in with Google
5. Start discovering cultural events in your area!

## API Endpoints

### Authentication
- `POST /login/` - User authentication
- `POST /auth/google/` - Google OAuth authentication
- `POST /logout/` - Session termination
- `POST /create_user/` - User registration

### Events
- `GET /events/` - Retrieve all events
- `GET /events/filter/` - Filtered event search
- `PUT /user/get_points_event/{event_id}/` - Award event attendance points

### User Management
- `GET /user/profile_info` - User profile information
- `PUT /user/change_password/` - Password update
- `PUT /user/update_username/` - Username change
- `POST /user/profile_pic/` - Profile picture upload
- `DELETE /delete_account/` - Account deletion

### Social Features
- `GET /chat/admin_chats/` - Admin chat list
- `GET /chat/with_admin/` - Messages with administrators
- `GET /chat/with_user/{user_id}` - User-to-user messages
- `POST /chat/send_to_admin/` - Send message to admin
- `POST /chat/send_to_user/` - Send message to user

### Ratings & Reviews
- `GET /ratings/{event_id}/` - Event ratings
- `POST /ratings/create/` - Submit rating
- `POST /reports/create/` - Report inappropriate content

### Gamification
- `GET /leaderboard/quiz/` - Quiz leaderboard
- `GET /leaderboard/events/` - Events leaderboard
- `PUT /user/set_points_quiz/` - Update quiz points

## External Integrations

### Charging Points API
The app integrates with Catalonia's electric vehicle charging infrastructure to promote sustainable transportation:

- **Endpoint**: `http://nattech.fib.upc.edu:40502/api_punts_carrega/`
- **Functionality**: Find nearest charging points to event locations
- **Data**: Real-time charging station information including location, capacity, and connector types

## Development Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Configure API endpoints in the appropriate configuration files
5. Build and run on device or emulator

## Contributing

This project is not open for contributions at this time. However, feedback and suggestions are welcome via issues on the GitHub repository.