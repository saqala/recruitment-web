package fr.d2factory.libraryapp.member;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.library.Library;

/**
 * A member is a person who can borrow and return books to a {@link Library} A
 * member can be either a student or a resident
 */
public abstract class Member {
	/**
	 * An initial sum of money the member has
	 */

	// wallet should be of type BigDecimal because float loose precision
	private BigDecimal wallet;
	// I add list of borrowed books by a member to check if he has late books
	private final List<Book> borrowedBooks = new ArrayList<Book>();

	/**
	 * The member should pay their books when they are returned to the library
	 *
	 * @param numberOfDays the number of days they kept the book
	 */
	public abstract void payBook(int numberOfDays);

	public List<Book> getBorrowedBooks() {
		return borrowedBooks;
	}

	public void addBorrowedBook(Book book) {
		this.borrowedBooks.add(book);
	}

	public void removeReturnedBook(Book returnedBook) {
		this.borrowedBooks.removeIf(book -> book.equals(returnedBook));
	}

	public BigDecimal getWallet() {
		return wallet;
	}

	public void setWallet(BigDecimal wallet) {
		this.wallet = wallet;
	}
}
