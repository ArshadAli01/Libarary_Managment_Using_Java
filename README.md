# liabrary_managment_java_project

Here's a detailed **Class Diagram** for a **Library Management System**, covering core entities and their relationships:

---

## **1. Class Diagram Overview**

This system includes the following key components:

- **Library**  
- **Book**
- **User (abstract)**
  - **Member**
  - **Librarian**
- **Loan**
- **Fine**
- **Catalog**
- **Author**
- **Publisher**
- **Reservation**
- **Notification**

---

## **2. Class Diagram Details**

### **Class: Library**
- **Attributes:**
  - name: String
  - address: String
  - books: List<Book>
  - members: List<Member>
  - librarians: List<Librarian>
- **Methods:**
  - addBook(book: Book)
  - removeBook(bookId: String)
  - registerMember(member: Member)
  - issueBook(bookId: String, memberId: String)

---

### **Class: Book**
- **Attributes:**
  - bookId: String
  - title: String
  - isbn: String
  - authors: List<Author>
  - publisher: Publisher
  - status: BookStatus (Available, Issued, Reserved, Lost)
- **Methods:**
  - isAvailable(): Boolean
  - reserveBook(member: Member)
  - issueBook(member: Member)

---

### **Class: Author**
- **Attributes:**
  - name: String
  - bio: String
  - books: List<Book>

---

### **Class: Publisher**
- **Attributes:**
  - name: String
  - address: String
  - booksPublished: List<Book>

---

### **Abstract Class: User**
- **Attributes:**
  - userId: String
  - name: String
  - email: String
  - phone: String
- **Methods:**
  - login()
  - logout()

---

### **Class: Member (inherits User)**
- **Attributes:**
  - membershipId: String
  - loans: List<Loan>
  - reservations: List<Reservation>
  - fineAmount: Double
- **Methods:**
  - borrowBook(book: Book)
  - returnBook(book: Book)
  - payFine(amount: Double)

---

### **Class: Librarian (inherits User)**
- **Attributes:**
  - employeeId: String
- **Methods:**
  - addBook(book: Book)
  - removeBook(bookId: String)
  - manageUsers()
  - calculateFine(loan: Loan)

---

### **Class: Loan**
- **Attributes:**
  - loanId: String
  - book: Book
  - member: Member
  - issueDate: Date
  - dueDate: Date
  - returnDate: Date?
- **Methods:**
  - isOverdue(): Boolean
  - calculateFine(): Double

---

### **Class: Fine**
- **Attributes:**
  - fineId: String
  - amount: Double
  - issuedDate: Date
  - paid: Boolean
- **Methods:**
  - markAsPaid()

---

### **Class: Reservation**
- **Attributes:**
  - reservationId: String
  - book: Book
  - member: Member
  - reservationDate: Date
  - status: ReservationStatus (Active, Cancelled, Completed)

---

### **Class: Catalog**
- **Attributes:**
  - books: List<Book>
- **Methods:**
  - searchByTitle(title: String): List<Book>
  - searchByAuthor(name: String): List<Book>
  - searchByISBN(isbn: String): Book

---

### **Class: Notification**
- **Attributes:**
  - notificationId: String
  - recipient: User
  - message: String
  - date: Date
  - type: NotificationType (DueReminder, ReservationAvailable, FineAlert)
- **Methods:**
  - send()

---

![image](https://github.com/user-attachments/assets/372848e5-e7a4-4cfe-8fd2-7725333a9acb)
