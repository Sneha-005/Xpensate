# Xpensate

This app is your ultimate financial companion! It simplifies expense tracking, shared bill management, debt monitoring, currency conversion, and includes a Budget Builder to help you stay financially disciplined by setting monthly spending limits.


## Screenshots
<img width="191" height="440" alt="Image" src="https://github.com/user-attachments/assets/8bafc026-4ae8-4526-b629-6f504e7d6592" />&nbsp&nbsp;
<img width="196" height="443" alt="Image" src="https://github.com/user-attachments/assets/93da8ddb-80bf-4180-a9cd-1a00189da0f6" />&nbsp&nbsp;
<img width="211" height="470" alt="Image" src="https://github.com/user-attachments/assets/406a1e4e-b5fc-43b1-b1d1-4095f62c2648" />&nbsp&nbsp; 
<img width="163" height="629" alt="Image" src="https://github.com/user-attachments/assets/8c0a23ea-353a-4d69-9345-9ca2b935e681" />&nbsp&nbsp;
<img width="117" height="498" alt="Image" src="https://github.com/user-attachments/assets/0ff613ce-3e8e-4d53-a347-139db514344f" />&nbsp&nbsp; 
<img width="169" height="354" alt="Image" src="https://github.com/user-attachments/assets/212dc7b6-cfe0-4f7e-9631-04322aa693b5" />&nbsp&nbsp; 
<img width="184" height="415" alt="Image" src="https://github.com/user-attachments/assets/8c58734c-1ee8-446d-b823-69fe7e794601" />&nbsp&nbsp; 
<img width="224" height="509" alt="Image" src="https://github.com/user-attachments/assets/abdf2c60-4f63-4081-9794-74d883b477d7" />&nbsp&nbsp; 
<img width="134" height="294" alt="Image" src="https://github.com/user-attachments/assets/5be70620-25e6-4228-ade5-7e3ddbed4b60" />&nbsp&nbsp; 
<img width="197" height="445" alt="Image" src="https://github.com/user-attachments/assets/cefd3b9e-d471-4a34-9684-94b13e6bf504" />&nbsp&nbsp; 
<img width="201" height="452" alt="Image" src="https://github.com/user-attachments/assets/1ccd9add-601a-4693-90b3-854df9c6d913" />&nbsp&nbsp; 
<img width="224" height="501" alt="Image" src="https://github.com/user-attachments/assets/eb6dd821-351d-42ce-b2eb-16ce4aae9621" />&nbsp&nbsp; 
<img width="196" height="435" alt="Image" src="https://github.com/user-attachments/assets/141f603e-6065-4200-8e20-68fe24122e7f" />&nbsp&nbsp; 
<img width="214" height="477" alt="Image" src="https://github.com/user-attachments/assets/0e121364-29ab-467c-83d6-6ea69e8d36bf" />&nbsp&nbsp; 
<img width="215" height="488" alt="Image" src="https://github.com/user-attachments/assets/3e5798d0-3573-4561-8fac-14793c6668c2" />&nbsp&nbsp; 
<img width="183" height="417" alt="Image" src="https://github.com/user-attachments/assets/2e09c399-ee84-4669-94b3-994d468386fd" />&nbsp&nbsp; 
<img width="176" height="394" alt="Image" src="https://github.com/user-attachments/assets/9dedca4f-320c-4dd4-99c3-4de426ebff62" />&nbsp&nbsp; 

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
  
