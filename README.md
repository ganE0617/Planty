# Planty

Planty is an Android application that helps users manage and care for their plants. This project is built using Kotlin and follows modern Android development practices.

## Project Structure

```
planty/
├── app/                    # Main application module
├── gradle/                 # Gradle wrapper files
├── .gradle/               # Gradle build cache
├── .idea/                 # IDE configuration files
└── build.gradle.kts       # Project-level build configuration
```

## Prerequisites

- Android Studio Arctic Fox (2020.3.1) or newer
- JDK 11 or newer
- Android SDK 21 or newer
- Gradle 7.0 or newer

## Getting Started

1. Clone the repository:
   ```bash
   git clone [repository-url]
   ```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Build and run the application

## Building the Project

To build the project, you can use either:

- Android Studio: Click on "Build > Make Project"
- Command line: 
  ```bash
  ./gradlew build
  ```

## Running Tests

To run the tests:

```bash
./gradlew test        # For unit tests
./gradlew connectedAndroidTest  # For instrumented tests
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details

## Contact

For any questions or concerns, please open an issue in the repository.