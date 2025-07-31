# Xpensate

This app is your ultimate financial companion! It simplifies expense tracking, shared bill management, debt monitoring, currency conversion, and includes a Budget Builder to help you stay financially disciplined by setting monthly spending limits.


## Screenshots
<img width="221" height="491" alt="Image" src="https://github.com/user-attachments/assets/80692a50-cfab-4f6a-bb52-45854d6b0ee1" />
<img width="121" height="509" alt="Image" src="https://github.com/user-attachments/assets/b20f6462-c495-4861-8195-072d9bbfc37f" />
<img width="171" height="636" alt="Image" src="https://github.com/user-attachments/assets/98fccb88-4057-4329-98f9-fbff72562660" />
<img width="184" height="417" alt="Image" src="https://github.com/user-attachments/assets/2f6edcf6-dfb0-4ca8-b622-b15da764d8af" />
<img width="229" height="517" alt="Image" src="https://github.com/user-attachments/assets/d9f817e1-d48f-465f-8538-ce20f1cdafa5" />
<img width="138" height="300" alt="Image" src="https://github.com/user-attachments/assets/c4624a92-35d6-4827-8b6e-556b1e707634" />
<img width="204" height="455" alt="Image" src="https://github.com/user-attachments/assets/6145a218-f3b2-4812-a804-6f5baaf3d988" />
<img width="230" height="507" alt="Image" src="https://github.com/user-attachments/assets/c7eb34d3-25e9-4f8c-9669-4bbca0f16a9f" />
<img width="200" height="446" alt="Image" src="https://github.com/user-attachments/assets/d3eb37f0-b129-4dfe-b126-f5273752e961" />
<img width="214" height="481" alt="Image" src="https://github.com/user-attachments/assets/0f990f19-c265-40c2-aa0c-b01d10ebff58" />
<img width="186" height="418" alt="Image" src="https://github.com/user-attachments/assets/88844091-26b4-4b60-830e-6b6b1021e06e" />
<img width="179" height="398" alt="Image" src="https://github.com/user-attachments/assets/0d23c452-ad05-46f8-8f68-8cda62405959" />
<img width="213" height="474" alt="Image" src="https://github.com/user-attachments/assets/c4f8ee60-19f3-40f7-9114-e905eb516cce" />

## Video
https://github.com/user-attachments/assets/6f00e93e-8090-4fa8-91b4-3bf01a87359c

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

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## Acknowledgements

- [Room DB](https://developer.android.com/training/data-storage/room)
- [Retrofit](https://square.github.io/retrofit/)
  
