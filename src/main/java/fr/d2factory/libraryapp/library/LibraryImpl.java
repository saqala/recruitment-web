package fr.d2factory.libraryapp.library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;

public class LibraryImpl implements Library {
	
	private BookRepository bookRepository ;
	
	private static LibraryImpl libraryImpl;

	private LibraryImpl(BookRepository bookRepository) {
		this.bookRepository=bookRepository;
	}

	public static LibraryImpl getInstance(BookRepository bookRepository) {

		if (libraryImpl == null) {
			libraryImpl = new LibraryImpl(bookRepository);
		}
		return libraryImpl;
	}

	@Override
	public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt)
			throws HasLateBooksException, BookNotAvailableException {

		Book book = checkIfBookAvailable(isbnCode);

		if (member instanceof Student) {
			checkIfMemberIsLate(member, borrowedAt, Student.MAX_DAYS_TO_BORROW_BOOK);
		}

		else if (member instanceof Resident) {
			checkIfMemberIsLate(member, borrowedAt, Resident.MAX_DAYS_TO_BORROW_BOOK);
		}

		// save book to the list of borrowed books
		bookRepository.saveBookBorrow(book, borrowedAt);

		// add book to the list of borrowed books by member
		member.addBorrowedBook(book);

		return book;
	}

	@Override
	public void returnBook(Book book, Member member) {

		LocalDate borrowedAt = bookRepository.findBorrowedBookDate(book);
		LocalDate now = LocalDate.now();

		long dayDiff = ChronoUnit.DAYS.between(borrowedAt, now);
		
		member.payBook(Math.toIntExact(dayDiff));
		
		bookRepository.saveReturnedBook(book);

		member.removeReturnedBook(book);

	}

	private void checkIfMemberIsLate(Member member, LocalDate borrowedAt, long maxBorrowDays ) {

		List<Book> borrowedBooks = member.getBorrowedBooks();

		borrowedBooks.forEach(book -> {
			LocalDate borrowedTime = bookRepository.findBorrowedBookDate(book);
			long dayDiff = ChronoUnit.DAYS.between(borrowedTime, borrowedAt);

			if (dayDiff > maxBorrowDays) {
				throw new HasLateBooksException();
			}

		});

	}

	private Book checkIfBookAvailable(long isbnCode ) {

		Book book = bookRepository.findBook(isbnCode);

		if (book == null)
			throw new BookNotAvailableException();
		else
			return book;
	}


}
