Hotel Management System - Premium Edition
Project Overview
The Hotel Management System is a comprehensive desktop application designed to streamline hotel operations, including room reservations, guest management, and real-time room availability tracking. Built with Java Swing and MySQL, this system provides an intuitive graphical interface for hotel staff to efficiently manage bookings and monitor hotel occupancy.
Features
Core Functionality

New Reservation Management

Interactive room selection with visual availability indicators
Guest information capture (name, contact, address)
Multiple room types with dynamic pricing (Standard, Deluxe, Suite, Presidential)
Check-in and check-out date selection
Automatic price calculation based on room type and duration


Reservation Viewing & Search

Comprehensive table view of all reservations
Advanced search functionality (by guest name, room number, or contact)
Real-time data refresh capability
Detailed reservation information display


Room Status Dashboard

Visual grid display of all 50 rooms
Color-coded room availability (Available/Occupied)
Click-to-view room details
Live occupancy statistics
Real-time updates on room status changes


Booking Management

Search reservations by ID
Update guest information and dates
Delete reservations with confirmation
Maintain reservation history


Analytics Dashboard

Total rooms overview
Available and occupied room counts
Occupancy rate percentage
Total bookings statistics
Revenue tracking and reporting



User Interface Features

Modern Nimbus Look and Feel
Color-coded visual indicators
Responsive button interactions
Tabbed navigation interface
Professional color scheme
Real-time form validation

Technologies/Tools Used
Programming Language

Java (JDK 8 or higher recommended)

GUI Framework

Java Swing - For creating the graphical user interface
AWT - For event handling and graphics

Database

MySQL 8.0 - For data persistence and storage
JDBC - For database connectivity

Libraries & APIs

javax.swing.* - GUI components
java.awt.* - Graphics and layout management
java.sql.* - Database operations
java.util.* - Utility classes and date handling

Development Tools

Any Java IDE (Eclipse, IntelliJ IDEA, NetBeans, VS Code)
MySQL Server and MySQL Workbench
JDBC MySQL Connector (mysql-connector-java)

Installation & Setup
Prerequisites

Java Development Kit (JDK) - Version 8 or higher

Download from Oracle's website
Verify installation: java -version


MySQL Server - Version 8.0 or compatible

Download from MySQL's website
Note your root password during installation


MySQL JDBC Driver

Download mysql-connector-java JAR file
Add to project classpath



Database Setup

Start MySQL Server

bash   # On Windows
   net start MySQL80
   
   # On macOS/Linux
   sudo systemctl start mysql

Create Database

sql   CREATE DATABASE hotel_db;
   USE hotel_db;

Configure Database Credentials

Open HotelReservationSystem.java
Update these constants with your credentials:



java   private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
   private static final String USERNAME = "root";
   private static final String PASSWORD = "your_password_here";
Project Setup

Clone or Download the Project

bash   # If using Git
   git clone <repository-url>
   cd hotel-management-system

Add MySQL Connector to Classpath

Copy mysql-connector-java-x.x.x.jar to your project
Add to build path in your IDE


Compile the Project

bash   javac HotelReservationSystem.java

Run the Application

bash   java HotelReservationSystem
Or run directly from your IDE by executing the main method.
Testing Instructions
Basic Functionality Tests
Test 1: Database Connection

Launch the application
Verify "Database connected successfully!" message in console
Check that all tabs load without errors

Test 2: Create New Reservation

Navigate to "New Reservation" tab
Fill in guest details:

Guest Name: "John Doe"
Contact: "1234567890"
Address: "123 Main Street"


Select a room type from dropdown
Choose check-in and check-out dates
Click on an available (green) room
Verify price updates correctly
Click "Confirm Reservation"
Verify success message appears
Check that room status changes to occupied (red)

Test 3: View Reservations

Navigate to "All Reservations" tab
Verify all columns display correctly
Test search functionality:

Select "Guest Name" and search for "John"
Verify filtered results appear


Click "Refresh" button
Verify all reservations reload

Test 4: Room Status Dashboard

Navigate to "Room Status" tab
Verify room grid displays all 50 rooms
Check color coding:

Green = Available
Red = Occupied


Click on an occupied room
Verify room details popup appears
Check statistics panel shows correct counts

Test 5: Manage Bookings

Navigate to "Manage Booking" tab
Enter a valid Reservation ID
Click "Search"
Verify guest details populate
Update guest name or contact
Click "Update"
Verify success message
Check changes in "All Reservations" tab

Test 6: Delete Reservation

In "Manage Booking" tab
Search for a reservation
Click "Delete"
Confirm deletion in dialog
Verify room becomes available
Check reservation removed from list

Test 7: Dashboard Analytics

Navigate to "Dashboard" tab
Verify all statistics display:

Total Rooms: 50
Available Rooms: (should match actual)
Occupied Rooms: (should match actual)
Occupancy Rate: (percentage)
Total Revenue: (sum of active bookings)


Create/delete reservations and verify stats update

Edge Case Testing
Test 8: Validation Tests

Try booking without selecting a room - should show warning
Try booking with empty guest name - should show error
Try booking with check-out date before check-in - should show error
Try selecting an occupied room - should show unavailable message

Test 9: Date Handling

Book room with same check-in and check-out dates
Book room for multiple nights
Verify price calculation adjusts correctly
Update dates in Manage Booking tab

Test 10: Concurrent Operations

Open "Room Status" tab
Keep it visible while creating a booking in another window/tab
Verify room status updates when switching back
Test refresh functionality

Database Verification

Check Database Tables

sql   USE hotel_db;
   SHOW TABLES;
   DESCRIBE reservations;
   SELECT * FROM reservations;

Verify Data Integrity

Check that all fields are populated correctly
Verify dates are stored in correct format
Confirm prices match room types



Screenshots (Optional)
Recommended screenshots to include:

Main Dashboard showing statistics
New Reservation tab with room selection grid
All Reservations table view
Room Status Dashboard with color coding
Manage Booking interface
Sample reservation confirmation dialog

Troubleshooting
Common Issues
Issue: Database Connection Failed

Solution: Verify MySQL service is running
Check username and password in code
Ensure database hotel_db exists

Issue: ClassNotFoundException: com.mysql.cj.jdbc.Driver

Solution: Add MySQL Connector JAR to classpath

Issue: Table doesn't exist

Solution: Tables are auto-created on first run
Check database permissions

Issue: GUI doesn't display correctly

Solution: Ensure Java version 8 or higher
Try running with system Look and Feel

Project Structure
hotel-management-system/
│
├── HotelReservationSystem.java    # Main application file
├── README.md                       # This file
├── statement.md                    # Project statement
└── lib/
    └── mysql-connector-java.jar   # JDBC driver
Future Enhancements

Export reservations to PDF/Excel
Email confirmation system
Multiple user roles and authentication
Payment processing integration
Customer loyalty program
Room maintenance scheduling
Multi-hotel support
Mobile app integration

Author
ADITYA ANIL TIWARI
Version: 2.0 Premium Edition
License
This project is created for educational purposes.
Support
For issues, questions, or contributions, please contact the project maintainer.
