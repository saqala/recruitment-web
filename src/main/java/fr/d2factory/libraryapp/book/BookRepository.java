package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {

	private final Map<ISBN, Book> availableBooks = new HashMap<>();
	private final Map<Book, LocalDate> borrowedBooks = new HashMap<>();

	private static BookRepository bookRepository;

	private BookRepository() {
	}

	public static BookRepository getInstance() {

		if (bookRepository == null) {
			bookRepository = new BookRepository();
		}
		return bookRepository;
	}

	public void addBooks(List<Book> books) {
		books.forEach(book -> availableBooks.put(book.getIsbn(), book));
	}

	public Book findBook(long isbnCode) {
		ISBN iSBN = new ISBN(isbnCode);
		return availableBooks.get(iSBN);
	}

	public void saveBookBorrow(Book book, LocalDate borrowedAt) {
		availableBooks.remove(book.getIsbn());
		borrowedBooks.put(book, borrowedAt);
	}

	public LocalDate findBorrowedBookDate(Book book) {
		return borrowedBooks.get(book);
	}

	public void saveReturnedBook(Book book) {
		borrowedBooks.remove(book);
		availableBooks.put(book.getIsbn(), book);
	}

	public Map<ISBN, Book> getAvailableBooks() {
		return availableBooks;
	}

	public Map<Book, LocalDate> getBorrowedBooks() {
		return borrowedBooks;
	}

}
