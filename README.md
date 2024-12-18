# Xpensate

This app is your ultimate financial companion! It simplifies expense tracking, shared bill management, debt monitoring, currency conversion, and includes a Budget Builder to help you stay financially disciplined by setting monthly spending limits.

## Features

- **Home Screen**: Overview of personal expenses and group balances.
- **Add Expense**:  Form for recording new transactions.
- **Group Details**:  Manage group members and split bills.
- **Analytics**: Visualize expense and debt breakdowns using interactive charts.
- **Debt & Lend**:  View, add, and manage debt/lend records.
- **Currency Converter**: Perform quick conversions and track foreign expenses.
- **Budget Builder**: Set and monitor your monthly spending limits with visual indicators.
- **Trip Tracker**: Plan trips, manage shared expenses, and settle costs with group members seamlessly.

## Architecture

The app employs a modular architecture that integrates various modern Android development components and best practices to ensure scalability, maintainability, and high performance.

**Components**

1. **User Interface**
  - *RecyclerView*: Developed 10+ dynamic and efficient lists using RecyclerView, significantly enhancing UI responsiveness and user experience.
  - *Fragments & Activities*: Structured UI components to manage different sections of the app seamlessly.

2. **Data Management**
  - *Retrofit*: Integrated APIs with Retrofit for seamless data exchange between the app and backend services.
  - *Glide*: Utilized Glide to efficiently process and load images from the server.
  - *DataStore*: Implemented a secure OTP-based Two-Factor Authentication system with access and refresh token management using DataStore.

3.**Authentication & Security**
  - *Two-Factor Authentication (OTP)*: Ensured secure user authentication with OTP-based verification.
  - *Token Management*: Managed access and refresh tokens securely using DataStore.

4.**Real-Time Features**
  - *Firebase Cloud Messaging (FCM)*: Leveraged FCM to deliver real-time notifications, enhancing user engagement and communication.
    
5. **Asynchronous Operations**
  - *Kotlin Coroutines*: Optimized asynchronous operations by incorporating Kotlin Coroutines for managing background tasks, ensuring smooth and responsive user experiences.

6. **State Management**
  - *ViewModel*: Utilized ViewModel to manage data for RecyclerView adapters, enabling efficient handling of dynamic lists and minimizing redundant API calls.

## Technologies Used

- **Kotlin**: Programming language
- **XML**: Toolkit for building native UI
- **Room DB**: Local database storage
- **Retrofit**: HTTP client for API calls
- **Coroutines**: Asynchronous programming
- **Navigation Component**: Handling navigation within the app

## Getting Started

### Prerequisites

- Android Studio Arctic Fox (or later)
- Kotlin 1.5 (or later)

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/Sneha-005/Xpensate.git
    cd Xpensate
    ```

2. Open the project in Android Studio.

3. Build and run the project on an emulator or physical device.

## Video

<iframe src="https://drive.google.com/open?id=1eLcTS7WGA99YVLyvdP7RiZw1haJVeYq2&authuser=0&t=160" width="640" height="360"></iframe>


## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## Acknowledgements

- [Room DB](https://developer.android.com/training/data-storage/room)
- [Retrofit](https://square.github.io/retrofit/)
  
