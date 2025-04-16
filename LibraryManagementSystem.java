import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Enums for statuses and notification types
enum BookStatus {
    AVAILABLE, ISSUED, RESERVED, LOST
}

enum ReservationStatus {
    ACTIVE, CANCELLED, COMPLETED
}

enum NotificationType {
    DUE_REMINDER, RESERVATION_AVAILABLE, FINE_ALERT
}

// Author class
class Author {
    private String name;
    private String bio;
    private List<Book> books;

    public Author(String name, String bio) {
        this.name = name;
        this.bio = bio;
        this.books = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addBook(Book book) {
        books.add(book);
    }
}

// Publisher class
class Publisher {
    private String name;
    private String address;
    private List<Book> booksPublished;

    public Publisher(String name, String address) {
        this.name = name;
        this.address = address;
        this.booksPublished = new ArrayList<>();
    }

    public void addBook(Book book) {
        booksPublished.add(book);
    }
}

// Book class
class Book {
    private String bookId;
    private String title;
    private String isbn;
    List<Author> authors;
    private Publisher publisher;
    private BookStatus status;

    public Book(String bookId, String title, String isbn, List<Author> authors, Publisher publisher) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.authors = authors;
        this.publisher = publisher;
        this.status = BookStatus.AVAILABLE;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public BookStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

    public void reserveBook(Member member) {
        if(isAvailable()) {
            status = BookStatus.RESERVED;
            System.out.println("Book " + title + " reserved by " + member.getName());
        } else {
            System.out.println("Book is not available for reservation.");
        }
    }

    public void issueBook(Member member) {
        if(isAvailable() || status == BookStatus.RESERVED) {
            status = BookStatus.ISSUED;
            System.out.println("Book " + title + " issued to " + member.getName());
        } else {
            System.out.println("Book is not available for issuing.");
        }
    }

    public void makeAvailable() {
        status = BookStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return "BookID: " + bookId + ", Title: " + title + ", ISBN: " + isbn + ", Status: " + status;
    }
}

// Abstract user class
abstract class User {
    protected String userId;
    protected String name;
    protected String email;
    protected String phone;

    public User(String userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void login() {
        System.out.println(name + " logged in.");
    }

    public void logout() {
        System.out.println(name + " logged out.");
    }
}

// Member class
class Member extends User {
    private String membershipId;
    private List<Loan> loans;
    private List<Reservation> reservations;
    private double fineAmount;

    public Member(String userId, String name, String email, String phone, String membershipId) {
        super(userId, name, email, phone);
        this.membershipId = membershipId;
        this.loans = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.fineAmount = 0.0;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void addLoan(Loan loan) {
        loans.add(loan);
    }

    public void borrowBook(Book book) {
        if(book.isAvailable() || book.getStatus() == BookStatus.RESERVED) {
            book.issueBook(this);
            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(14);
            Loan loan = new Loan(UUID.randomUUID().toString(), book, this, issueDate, dueDate);
            addLoan(loan);
            System.out.println("Loan created with due date: " + dueDate);
        } else {
            System.out.println("Book is not available for borrowing.");
        }
    }

    public void returnBook(Book book) {
        for (Loan loan : loans) {
            if (loan.getBook().getBookId().equals(book.getBookId()) && loan.getReturnDate() == null) {
                loan.setReturnDate(LocalDate.now());
                book.makeAvailable();
                double fine = loan.calculateFine();
                if(fine > 0) {
                    fineAmount += fine;
                    System.out.println("Book returned late. Fine incurred: $" + fine);
                } else {
                    System.out.println("Book returned on time.");
                }
                return;
            }
        }
        System.out.println("No active loan found for the book.");
    }

    public void payFine(double amount) {
        if(amount <= fineAmount) {
            fineAmount -= amount;
            System.out.println("Paid $" + amount + ". Remaining fine: $" + fineAmount);
        } else {
            System.out.println("Payment exceeds the outstanding fine.");
        }
    }
}

// Librarian class
class Librarian extends User {
    private String employeeId;

    public Librarian(String userId, String name, String email, String phone, String employeeId) {
        super(userId, name, email, phone);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void addBook(Library library, Book book) {
        library.addBook(book);
    }

    public void removeBook(Library library, String bookId) {
        library.removeBook(bookId);
    }

    public void manageUsers() {
        // For simplicity, just printing a message.
        System.out.println("Managing users...");
    }

    public double calculateFine(Loan loan) {
        return loan.calculateFine();
    }
}

// Loan class
class Loan {
    String loanId;
    private Book book;
    private Member member;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // can be null

    public Loan(String loanId, Book book, Member member, LocalDate issueDate, LocalDate dueDate) {
        this.loanId = loanId;
        this.book = book;
        this.member = member;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isOverdue() {
        LocalDate currentDate = (returnDate == null) ? LocalDate.now() : returnDate;
        return currentDate.isAfter(dueDate);
    }

    // Fine: $1 per day overdue
    public double calculateFine() {
        if(isOverdue()) {
            LocalDate effectiveReturn = (returnDate == null) ? LocalDate.now() : returnDate;
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, effectiveReturn);
            return daysOverdue;
        }
        return 0.0;
    }
}

// Fine class
class Fine {
    private String fineId;
    private double amount;
    private LocalDate issuedDate;
    private boolean paid;

    public Fine(String fineId, double amount, LocalDate issuedDate) {
        this.fineId = fineId;
        this.amount = amount;
        this.issuedDate = issuedDate;
        this.paid = false;
    }

    public void markAsPaid() {
        paid = true;
    }
}

// Reservation class
class Reservation {
    private String reservationId;
    private Book book;
    private Member member;
    private LocalDate reservationDate;
    private ReservationStatus status;

    public Reservation(String reservationId, Book book, Member member, LocalDate reservationDate) {
        this.reservationId = reservationId;
        this.book = book;
        this.member = member;
        this.reservationDate = reservationDate;
        this.status = ReservationStatus.ACTIVE;
    }

    public void cancel() {
        status = ReservationStatus.CANCELLED;
    }

    public void complete() {
        status = ReservationStatus.COMPLETED;
    }
}

// Catalog class to search for books
class Catalog {
    private List<Book> books;

    public Catalog(List<Book> books) {
        this.books = books;
    }

    public List<Book> searchByTitle(String title) {
        List<Book> results = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(title.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }

    public List<Book> searchByAuthor(String authorName) {
        List<Book> results = new ArrayList<>();
        for (Book b : books) {
            // Check each author in the book
            // (This example assumes Author names are unique identifiers)
            for (Author a : b.authors) {
                if (a.getName().toLowerCase().contains(authorName.toLowerCase())) {
                    results.add(b);
                    break;
                }
            }
        }
        return results;
    }

    public Book searchByISBN(String isbn) {
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                return b;
            }
        }
        return null;
    }
}

// Notification class
class Notification {
    private String notificationId;
    private User recipient;
    private String message;
    private LocalDate date;
    private NotificationType type;

    public Notification(String notificationId, User recipient, String message, LocalDate date, NotificationType type) {
        this.notificationId = notificationId;
        this.recipient = recipient;
        this.message = message;
        this.date = date;
        this.type = type;
    }

    public void send() {
        System.out.println("Sending notification to " + recipient.getName() + ": " + message);
    }
}

// Library class containing the books, members and librarians
class Library {
    private String name;
    private String address;
    private List<Book> books;
    private List<Member> members;
    private List<Librarian> librarians;

    public Library(String name, String address) {
        this.name = name;
        this.address = address;
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.librarians = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
        System.out.println("Book added: " + book.getTitle());
    }

    public void removeBook(String bookId) {
        Iterator<Book> iterator = books.iterator();
        while(iterator.hasNext()){
            Book b = iterator.next();
            if(b.getBookId().equals(bookId)) {
                iterator.remove();
                System.out.println("Book removed: " + b.getTitle());
                return;
            }
        }
        System.out.println("Book with ID " + bookId + " not found.");
    }

    public void registerMember(Member member) {
        members.add(member);
        System.out.println("Member registered: " + member.getName());
    }

    public List<Book> getBooks(){
        return books;
    }

    public List<Member> getMembers(){
        return members;
    }

    public List<Librarian> getLibrarians() {
        return librarians;
    }

    public void addLibrarian(Librarian librarian) {
        librarians.add(librarian);
    }

    public Book getBookById(String bookId) {
        for(Book b : books) {
            if(b.getBookId().equals(bookId)) {
                return b;
            }
        }
        return null;
    }

    // Issues a book to a member
    public void issueBook(String bookId, String memberId) {
        Book book = getBookById(bookId);
        Member member = null;
        for (Member m : members) {
            if (m.getMembershipId().equals(memberId)) {
                member = m;
                break;
            }
        }
        if(book != null && member != null) {
            member.borrowBook(book);
        } else {
            System.out.println("Book or Member not found.");
        }
    }
}

// Main class with the menu-based console interface
public class LibraryManagementSystem {
    private static Library library;
    private static Catalog catalog;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Setup library with some dummy data
        library = new Library("City Library", "123 Library Street");
        initializeDummyData();
        catalog = new Catalog(library.getBooks());

        // Main application menu
        boolean exit = false;
        while(!exit) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Member Login");
            System.out.println("2. Librarian Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch(choice) {
                case "1":
                    memberMenu();
                    break;
                case "2":
                    librarianMenu();
                    break;
                case "3":
                    exit = true;
                    System.out.println("Exiting system.");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    // Simulate member login and menu options
    private static void memberMenu() {
        System.out.print("Enter Membership ID: ");
        String membershipId = scanner.nextLine();
        Member member = null;
        for (Member m : library.getMembers()) {
            if (m.getMembershipId().equals(membershipId)) {
                member = m;
                break;
            }
        }
        if(member == null) {
            System.out.println("Member not found. Please register first.");
            return;
        }
        member.login();
        boolean logout = false;
        while(!logout) {
            System.out.println("\nMember Menu");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. Pay Fine");
            System.out.println("4. Search Books");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch(choice) {
                case "1":
                    borrowBook(member);
                    break;
                case "2":
                    returnBook(member);
                    break;
                case "3":
                    payFine(member);
                    break;
                case "4":
                    searchBooks();
                    break;
                case "5":
                    logout = true;
                    member.logout();
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // Simulate librarian login and menu options
    private static void librarianMenu() {
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.nextLine();
        Librarian librarian = null;
        for (Librarian l : library.getLibrarians()) {
            if (l.getEmployeeId().equals(employeeId)) {
                librarian = l;
                break;
            }
        }
        if(librarian == null) {
            System.out.println("Librarian not found.");
            return;
        }
        librarian.login();
        boolean logout = false;
        while(!logout) {
            System.out.println("\nLibrarian Menu");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Manage Users");
            System.out.println("4. Calculate Fine for a Loan");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch(choice) {
                case "1":
                    addBook(librarian);
                    break;
                case "2":
                    removeBook(librarian);
                    break;
                case "3":
                    librarian.manageUsers();
                    break;
                case "4":
                    calculateFine(librarian);
                    break;
                case "5":
                    logout = true;
                    librarian.logout();
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }

    // Member action: Borrow a book
    private static void borrowBook(Member member) {
        System.out.print("Enter Book ID to borrow: ");
        String bookId = scanner.nextLine();
        Book book = library.getBookById(bookId);
        if(book != null) {
            member.borrowBook(book);
        } else {
            System.out.println("Book not found.");
        }
    }

    // Member action: Return a book
    private static void returnBook(Member member) {
        System.out.print("Enter Book ID to return: ");
        String bookId = scanner.nextLine();
        Book book = library.getBookById(bookId);
        if(book != null) {
            member.returnBook(book);
        } else {
            System.out.println("Book not found.");
        }
    }

    // Member action: Pay fine
    private static void payFine(Member member) {
        System.out.println("Outstanding fine: $" + member.getFineAmount());
        System.out.print("Enter amount to pay: ");
        double amount = Double.parseDouble(scanner.nextLine());
        member.payFine(amount);
    }

    // Member action: Search for books
    private static void searchBooks() {
        System.out.println("\nSearch Books");
        System.out.println("1. By Title");
        System.out.println("2. By Author");
        System.out.println("3. By ISBN");
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine();

        switch(choice) {
            case "1":
                System.out.print("Enter title keyword: ");
                String title = scanner.nextLine();
                List<Book> byTitle = catalog.searchByTitle(title);
                if(byTitle.isEmpty()) {
                    System.out.println("No books found.");
                } else {
                    byTitle.forEach(System.out::println);
                }
                break;
            case "2":
                System.out.print("Enter author name: ");
                String author = scanner.nextLine();
                List<Book> byAuthor = catalog.searchByAuthor(author);
                if(byAuthor.isEmpty()) {
                    System.out.println("No books found.");
                } else {
                    byAuthor.forEach(System.out::println);
                }
                break;
            case "3":
                System.out.print("Enter ISBN: ");
                String isbn = scanner.nextLine();
                Book book = catalog.searchByISBN(isbn);
                if(book == null) {
                    System.out.println("No book found.");
                } else {
                    System.out.println(book);
                }
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // Librarian action: Add a book
    private static void addBook(Librarian librarian) {
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();

        // For simplicity, we will create one author and one publisher per book entry.
        System.out.print("Enter Author Name: ");
        String authorName = scanner.nextLine();
        System.out.print("Enter Author Bio: ");
        String bio = scanner.nextLine();
        Author author = new Author(authorName, bio);
        List<Author> authors = new ArrayList<>();
        authors.add(author);

        System.out.print("Enter Publisher Name: ");
        String publisherName = scanner.nextLine();
        System.out.print("Enter Publisher Address: ");
        String publisherAddress = scanner.nextLine();
        Publisher publisher = new Publisher(publisherName, publisherAddress);

        Book book = new Book(bookId, title, isbn, authors, publisher);
        librarian.addBook(library, book);
    }

    // Librarian action: Remove a book
    private static void removeBook(Librarian librarian) {
        System.out.print("Enter Book ID to remove: ");
        String bookId = scanner.nextLine();
        librarian.removeBook(library, bookId);
    }

    // Librarian action: Calculate fine for a specific loan
    private static void calculateFine(Librarian librarian) {
        System.out.print("Enter Member ID for loan check: ");
        String membershipId = scanner.nextLine();
        Member member = null;
        for (Member m : library.getMembers()) {
            if (m.getMembershipId().equals(membershipId)) {
                member = m;
                break;
            }
        }
        if(member == null) {
            System.out.println("Member not found.");
            return;
        }
        if(member.getLoans().isEmpty()) {
            System.out.println("No loans found for this member.");
            return;
        }
        System.out.println("Loans for " + member.getName() + ":");
        for (Loan loan : member.getLoans()) {
            double fine = librarian.calculateFine(loan);
            System.out.println("Loan ID: " + loan.loanId + " | Book: " + loan.getBook().getTitle() + " | Fine: $" + fine);
        }
    }

    // Initialize dummy data for demonstration
    private static void initializeDummyData() {
        // Create a dummy publisher and author
        Publisher pub1 = new Publisher("O'Reilly", "New york");
        Publisher pub2 = new Publisher("Ruksar Appa", "1Belgachia, Kokata, WB");
        Author auth = new Author("Joshua Bloch", "Java Programming Expert");

        // Create a dummy book
        List<Author> authors = new ArrayList<>();
        authors.add(auth);
        Book book1 = new Book("B001", "Effective Java", "9780134685991", authors, pub1);
        Book book2 = new Book("B002", "Learn Python", "9780134685991", authors, pub1);
        Book book3 = new Book("B003", "Sql Mastery", "9780134685991", authors, pub2);
        Book book4 = new Book("B004", "Data Structure and Algorithms", "9780134685991", authors, pub2);
        library.addBook(book1);
        library.addBook(book2);
        library.addBook(book3);
        library.addBook(book4);

        // Create dummy member and librarian
        Member member1 = new Member("01", "Ali", "ali@example.com", "555-0101", "M01");
        Member member2 = new Member("02", "Cushuu", "cushu@example.com", "555-0101", "M02");
        library.registerMember(member1);
        library.registerMember(member2);
        Librarian librarian1 = new Librarian("L01", "Boss", "boss@example.com", "555-0202", "E01");
        library.addLibrarian(librarian1);
    }
}
