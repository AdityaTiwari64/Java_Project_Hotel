# Hotel Management System - Premium Edition

## Project Overview

The Hotel Management System is a comprehensive desktop application designed to streamline hotel operations, including room reservations, guest management, and real-time room availability tracking. Built with Java Swing and MySQL, this system provides an intuitive graphical interface for hotel staff to efficiently manage bookings and monitor hotel occupancy.

## Features

### Core Functionality
- **New Reservation Management**
  - Interactive room selection with visual availability indicators
  - Guest information capture (name, contact, address)
  - Multiple room types with dynamic pricing (Standard, Deluxe, Suite, Presidential)
  - Check-in and check-out date selection
  - Automatic price calculation based on room type and duration

- **Reservation Viewing & Search**
  - Comprehensive table view of all reservations
  - Advanced search functionality (by guest name, room number, or contact)
  - Real-time data refresh capability
  - Detailed reservation information display

- **Room Status Dashboard**
  - Visual grid display of all 50 rooms
  - Color-coded room availability (Available/Occupied)
  - Click-to-view room details
  - Live occupancy statistics
  - Real-time updates on room status changes

- **Booking Management**
  - Search reservations by ID
  - Update guest information and dates
  - Delete reservations with confirmation
  - Maintain reservation history

- **Analytics Dashboard**
  - Total rooms overview
  - Available and occupied room counts
  - Occupancy rate percentage
  - Total bookings statistics
  - Revenue tracking and reporting

### User Interface Features
- Modern Nimbus Look and Feel
- Color-coded visual indicators
- Responsive button interactions
- Tabbed navigation interface
- Professional color scheme
- Real-time form validation

## Technologies/Tools Used

### Programming Language
- **Java** (JDK 8 or higher recommended)

### GUI Framework
- **Java Swing** - For creating the graphical user interface
- **AWT** - For event handling and graphics

### Database
- **MySQL 8.0** - For data persistence and storage
- **JDBC** - For database connectivity

### Libraries & APIs
- `javax.swing.*` - GUI components
- `java.awt.*` - Graphics and layout management
- `java.sql.*` - Database operations
- `java.util.*` - Utility classes and date handling

### Development Tools
- Any Java IDE (Eclipse, IntelliJ IDEA, NetBeans, VS Code)
- MySQL Server and MySQL Workbench
- JDBC MySQL Connector (mysql-connector-java)

## Installation & Setup

### Prerequisites
1. **Java Development Kit (JDK)** - Version 8 or higher
   - Download from [Oracle's website](https://www.oracle.com/java/technologies/downloads/)
   - Verify installation: `java -version`

2. **MySQL Server** - Version 8.0 or compatible
   - Download from [MySQL's website](https://dev.mysql.com/downloads/)
   - Note your root password during installation

3. **MySQL JDBC Driver**
   - Download mysql-connector-java JAR file
   - Add to project classpath

### Database Setup

1. **Start MySQL Server**
   ```bash
   # On Windows
   net start MySQL80
   
   # On macOS/Linux
   sudo systemctl start mysql
   ```

2. **Create Database**
   ```sql
   CREATE DATABASE hotel_db;
   USE hotel_db;
   ```

3. **Configure Database Credentials**
   - Open `HotelReservationSystem.java`
   - Update these constants with your credentials:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
   private static final String USERNAME = "root";
   private static final String PASSWORD = "your_password_here";
   ```

### Project Setup

1. **Clone or Download the Project**
   ```bash
   # If using Git
   git clone <repository-url>
   cd hotel-management-system
   ```

2. **Add MySQL Connector to Classpath**
   - Copy `mysql-connector-java-x.x.x.jar` to your project
   - Add to build path in your IDE

3. **Compile the Project**
   ```bash
   javac HotelReservationSystem.java
   ```

4. **Run the Application**
   ```bash
   java HotelReservationSystem
   ```

   Or run directly from your IDE by executing the `main` method.

## Testing Instructions

### Basic Functionality Tests

#### Test 1: Database Connection
1. Launch the application
2. Verify "Database connected successfully!" message in console
3. Check that all tabs load without errors

#### Test 2: Create New Reservation
1. Navigate to "New Reservation" tab
2. Fill in guest details:
   - Guest Name: "John Doe"
   - Contact: "1234567890"
   - Address: "123 Main Street"
3. Select a room type from dropdown
4. Choose check-in and check-out dates
5. Click on an available (green) room
6. Verify price updates correctly
7. Click "Confirm Reservation"
8. Verify success message appears
9. Check that room status changes to occupied (red)

#### Test 3: View Reservations
1. Navigate to "All Reservations" tab
2. Verify all columns display correctly
3. Test search functionality:
   - Select "Guest Name" and search for "John"
   - Verify filtered results appear
4. Click "Refresh" button
5. Verify all reservations reload

#### Test 4: Room Status Dashboard
1. Navigate to "Room Status" tab
2. Verify room grid displays all 50 rooms
3. Check color coding:
   - Green = Available
   - Red = Occupied
4. Click on an occupied room
5. Verify room details popup appears
6. Check statistics panel shows correct counts

#### Test 5: Manage Bookings
1. Navigate to "Manage Booking" tab
2. Enter a valid Reservation ID
3. Click "Search"
4. Verify guest details populate
5. Update guest name or contact
6. Click "Update"
7. Verify success message
8. Check changes in "All Reservations" tab

#### Test 6: Delete Reservation
1. In "Manage Booking" tab
2. Search for a reservation
3. Click "Delete"
4. Confirm deletion in dialog
5. Verify room becomes available
6. Check reservation removed from list

#### Test 7: Dashboard Analytics
1. Navigate to "Dashboard" tab
2. Verify all statistics display:
   - Total Rooms: 50
   - Available Rooms: (should match actual)
   - Occupied Rooms: (should match actual)
   - Occupancy Rate: (percentage)
   - Total Revenue: (sum of active bookings)
3. Create/delete reservations and verify stats update

### Edge Case Testing

#### Test 8: Validation Tests
1. Try booking without selecting a room - should show warning
2. Try booking with empty guest name - should show error
3. Try booking with check-out date before check-in - should show error
4. Try selecting an occupied room - should show unavailable message

#### Test 9: Date Handling
1. Book room with same check-in and check-out dates
2. Book room for multiple nights
3. Verify price calculation adjusts correctly
4. Update dates in Manage Booking tab

#### Test 10: Concurrent Operations
1. Open "Room Status" tab
2. Keep it visible while creating a booking in another window/tab
3. Verify room status updates when switching back
4. Test refresh functionality

### Database Verification

1. **Check Database Tables**
   ```sql
   USE hotel_db;
   SHOW TABLES;
   DESCRIBE reservations;
   SELECT * FROM reservations;
   ```

2. **Verify Data Integrity**
   - Check that all fields are populated correctly
   - Verify dates are stored in correct format
   - Confirm prices match room types

## Screenshots (Optional)

*Recommended screenshots to include:*
1. Main Dashboard showing statistics
2. New Reservation tab with room selection grid
3. All Reservations table view
4. Room Status Dashboard with color coding
5. Manage Booking interface
6. Sample reservation confirmation dialog

## Troubleshooting

### Common Issues

**Issue: Database Connection Failed**
- Solution: Verify MySQL service is running
- Check username and password in code
- Ensure database `hotel_db` exists

**Issue: ClassNotFoundException: com.mysql.cj.jdbc.Driver**
- Solution: Add MySQL Connector JAR to classpath

**Issue: Table doesn't exist**
- Solution: Tables are auto-created on first run
- Check database permissions

**Issue: GUI doesn't display correctly**
- Solution: Ensure Java version 8 or higher
- Try running with system Look and Feel

## Project Structure

```
hotel-management-system/
│
├── HotelReservationSystem.java    # Main application file
├── README.md                       # This file
├── statement.md                    # Project statement
└── lib/
    └── mysql-connector-java.jar   # JDBC driver
```

## Future Enhancements

- Export reservations to PDF/Excel
- Email confirmation system
- Multiple user roles and authentication
- Payment processing integration
- Customer loyalty program
- Room maintenance scheduling
- Multi-hotel support
- Mobile app integration

## Author

**ADITYA ANIL TIWARI**

Version: 2.0 Premium Edition

## License

This project is created for educational purposes.

## Support

For issues, questions, or contributions, please contact the project maintainer.

---

*Last Updated: November 2025*
