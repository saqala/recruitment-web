package fr.d2factory.libraryapp.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.member.Grade;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;

public class LibraryTest {

	BookRepository bookRepository;
	Library library;

	@Before
	public void setup()
			throws FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {

		// BookRepository should implement an interface and instantiate using reflection
		// to respect open for extension, but closed for modification principal
		// for simplicity I make it as a singleton
		bookRepository = BookRepository.getInstance();
		JsonReader reader = new JsonReader(new FileReader("src/test/resources/books.json"));
		List<Book> books = Arrays.asList(new Gson().fromJson(reader, Book[].class));
		bookRepository.addBooks(books);

		// Library is a business class so we should not instantiate it using new keyword
		// instead
		// I used reflection
		// open for extension, but closed for modification principal
		Scanner scanner = new Scanner(new FileReader("src/test/resources/config.txt"));
		String libraryClassName = scanner.next();
		scanner.close();
		Class<?> libraryClass = Class.forName(libraryClassName);
		Method getInstance = libraryClass.getMethod("getInstance", BookRepository.class);
		library = (Library) getInstance.invoke(library, bookRepository);

	}

	@Test
	public void member_can_borrow_a_book_if_book_is_available() {

		// student borrowed a book
		Member student = new Student();
		Book book = library.borrowBook(46578964513l, student, LocalDate.now());
		// check if the returned book not null
		assertNotNull(book);
		// check that the borrowed book has been add to borrowed books list
		assertTrue(bookRepository.getBorrowedBooks().containsKey(book));
		// check that the borrowed book has been deleted from the available books list
		assertFalse(bookRepository.getAvailableBooks().containsKey(book.getIsbn()));
		// check that the borrowed book has been add to borrowed books list by the
		// student
		assertTrue(student.getBorrowedBooks().contains(book));

		// assert that student can borrow another available book
		book = library.borrowBook(3326456467846l, student, LocalDate.now());
		assertNotNull(book);
		assertTrue(bookRepository.getBorrowedBooks().containsKey(book));
		assertFalse(bookRepository.getAvailableBooks().containsKey(book.getIsbn()));
		assertTrue(student.getBorrowedBooks().contains(book));

		// assert that resident can borrow available book
		Member resident = new Resident();
		book = library.borrowBook(968787565445l, resident, LocalDate.now());
		assertNotNull(book);
		assertTrue(bookRepository.getBorrowedBooks().containsKey(book));
		assertFalse(bookRepository.getAvailableBooks().containsKey(book.getIsbn()));
		assertTrue(resident.getBorrowedBooks().contains(book));

		// assert that resident can borrow second book if its vailable
		book = library.borrowBook(465789453149l, resident, LocalDate.now());
		assertNotNull(book);
		assertTrue(bookRepository.getBorrowedBooks().containsKey(book));
		assertFalse(bookRepository.getAvailableBooks().containsKey(book.getIsbn()));
		assertTrue(resident.getBorrowedBooks().contains(book));

	}

	@Test(expected = BookNotAvailableException.class)
	public void borrowed_book_is_no_longer_available() {

		// student borrowed a book 15 days ago
		Member student = new Student();
		library.borrowBook(46578964513l, student, LocalDate.now().minusDays(15));

		// resident try to borrow the same book today
		Member resident = new Resident();
		library.borrowBook(46578964513l, resident, LocalDate.now());
	}

	@Test
	public void residents_are_taxed_10cents_for_each_day_they_keep_a_book() {

		// Instantiate Resident has 5 eu in his wallet
		Resident resident = new Resident();
		resident.setWallet(new BigDecimal(5));

		// Resident borrowed the book for 15 days
		LocalDate befor_15_days = LocalDate.now().minusDays(15);
		Book book = library.borrowBook(46578964513l, resident, befor_15_days);

		library.returnBook(book, resident);

		// Resident should pay 1.5 eu because he is charged for 15 days
		BigDecimal charge = new BigDecimal(0.1).multiply(new BigDecimal(15)).setScale(3, RoundingMode.FLOOR);
		// Resident should have 3.5 eu
		BigDecimal rest = new BigDecimal(5).subtract(charge);
		assertEquals(resident.getWallet(), rest);
	}

	@Test
	public void students_pay_10_cents_the_first_30days() {

		// Instantiate student not in the first year and has 5 eu in his wallet
		Student student = new Student();
		student.setWallet(new BigDecimal(5));
		student.setGrade(Grade.NOT_FIRST_YEAR);

		// student borrowed the book for 25 days
		LocalDate befor_25_days = LocalDate.now().minusDays(25);
		Book book = library.borrowBook(46578964513l, student, befor_25_days);

		library.returnBook(book, student);

		// student should pay 2.5 eu because he is charged only for 25 days
		BigDecimal charge = new BigDecimal(0.1).multiply(new BigDecimal(25)).setScale(3, RoundingMode.FLOOR);
		// student should have 2.5 eu
		BigDecimal rest = new BigDecimal(5).subtract(charge);
		assertEquals(student.getWallet(), rest);

	}

	@Test
	public void students_in_1st_year_are_not_taxed_for_the_first_15days() {

		// Instantiate student in the first year and has 5 eu in his wallet
		Student student = new Student();
		student.setWallet(new BigDecimal(5));
		student.setGrade(Grade.FIRST_YEAR);

		// student borrowed the book for 20 days
		LocalDate befor_20_days = LocalDate.now().minusDays(20);
		Book book = library.borrowBook(46578964513l, student, befor_20_days);

		library.returnBook(book, student);

		// student should pay 0.5 eu because he is charged only for 5 days
		BigDecimal charge = new BigDecimal(0.1).multiply(new BigDecimal(5)).setScale(3, RoundingMode.FLOOR);
		// student should have 4.5 eu
		BigDecimal rest = new BigDecimal(5).subtract(charge);
		assertEquals(student.getWallet(), rest);
	}

	@Test
	public void students_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days() {

		// Instantiate student in the first year and has 5 eu in his wallet
		Student student = new Student();
		student.setWallet(new BigDecimal(5));
		student.setGrade(Grade.FIRST_YEAR);

		// student borrowed the book for 35 days
		LocalDate befor_35_days = LocalDate.now().minusDays(35);
		Book book = library.borrowBook(46578964513l, student, befor_35_days);

		library.returnBook(book, student);

		// student should pay 2.25 eu
		BigDecimal charge = new BigDecimal(0.1).multiply(new BigDecimal(15))
				.add(new BigDecimal(5).multiply(new BigDecimal(0.15))).setScale(3, RoundingMode.FLOOR);
		// student should have 2.75 eu
		BigDecimal rest = new BigDecimal(5).subtract(charge);
		assertEquals(student.getWallet(), rest);

		// Instantiate student not in the first year and has 5 eu in his wallet
		student = new Student();
		student.setWallet(new BigDecimal(5));
		student.setGrade(Grade.NOT_FIRST_YEAR);

		// student borrowed the book for 40 days
		LocalDate befor_40_days = LocalDate.now().minusDays(40);
		book = library.borrowBook(3326456467846l, student, befor_40_days);

		library.returnBook(book, student);

		// student should pay 2.25 eu
		charge = new BigDecimal(0.1).multiply(new BigDecimal(30)).add(new BigDecimal(0.15).multiply(new BigDecimal(10)))
				.setScale(3, RoundingMode.FLOOR);
		// student should have 2.75 eu
		rest = new BigDecimal(5).subtract(charge);
		assertEquals(student.getWallet(), rest);

	}

	@Test
	public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days() {

		// Instantiate Resident has 10 eu in his wallet
		Resident resident = new Resident();
		resident.setWallet(new BigDecimal(10));

		// Resident borrowed the book for 45 days
		LocalDate befor_70_days = LocalDate.now().minusDays(70);
		Book book = library.borrowBook(46578964513l, resident, befor_70_days);

		library.returnBook(book, resident);

		// Resident should pay 8 eu because he is charged for 60 days with 0.1 eu and 10
		// days with 0.2 eu
		BigDecimal charge = new BigDecimal(0.1).multiply(new BigDecimal(60))
				.add(new BigDecimal(0.2).multiply(new BigDecimal(10))).setScale(3, RoundingMode.FLOOR);
		// Resident should have 2 eu
		BigDecimal rest = new BigDecimal(10).subtract(charge);
		assertEquals(resident.getWallet(), rest);
	}

	@Test(expected = HasLateBooksException.class)
	public void members_cannot_borrow_book_if_they_have_late_books() {

		// student borrowed a book 40 days ago
		Member student = new Student();
		library.borrowBook(46578964513l, student, LocalDate.now().minusDays(40));

		try {
			// student try to borrow another book without returning the late book
			library.borrowBook(3326456467846l, student, LocalDate.now());
		}

		catch (Exception e) {

			// inside catch block means that student c'ant borrow second book if he has late book
			assertTrue(e instanceof HasLateBooksException);

			// resident borrowed a book 70 days ago
			Member resident = new Resident();
			library.borrowBook(968787565445l, resident, LocalDate.now().minusDays(70));
			// resident try to borrow another book without returning the late book
			// throw HasLateBooksException
			library.borrowBook(465789453149l, resident, LocalDate.now());

		}

	}

}
