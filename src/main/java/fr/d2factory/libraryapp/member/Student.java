package fr.d2factory.libraryapp.member;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Student extends Member {

	// add grade enum to differentiate between first year students and others
	// I used enum instead of inheritance because in the next year we can update
	// first year student by only changing their grade
	private Grade grade;

	public final static int MAX_DAYS_TO_BORROW_BOOK = 30;
	public final static int FREE_DAYS = 15;

	public final static BigDecimal NORMAL_CHARGE = new BigDecimal("0.1");
	public final static BigDecimal LATE_CHARGE = new BigDecimal("0.15");

	@Override
	public void payBook(int numberOfDays) {

		BigDecimal toBePayed;
		BigDecimal wallet = getWallet();

		if (grade.equals(Grade.FIRST_YEAR)) {

			BigDecimal chargedBorrowedDays = new BigDecimal(numberOfDays - FREE_DAYS);

			if (numberOfDays <= FREE_DAYS) {
				return;
			}

			else if (numberOfDays <= MAX_DAYS_TO_BORROW_BOOK) {
				toBePayed = NORMAL_CHARGE.multiply(chargedBorrowedDays);
			}

			else {
				toBePayed = NORMAL_CHARGE.multiply(new BigDecimal(FREE_DAYS))
						.add(LATE_CHARGE.multiply(new BigDecimal(numberOfDays - MAX_DAYS_TO_BORROW_BOOK)));
			}

		}

		else {

			if (numberOfDays <= MAX_DAYS_TO_BORROW_BOOK) {
				toBePayed = NORMAL_CHARGE.multiply(new BigDecimal(numberOfDays));
			}

			else {
				toBePayed = NORMAL_CHARGE.multiply(new BigDecimal(MAX_DAYS_TO_BORROW_BOOK))
						.add(LATE_CHARGE.multiply(new BigDecimal(numberOfDays - MAX_DAYS_TO_BORROW_BOOK)));
			}

		}

		setWallet(wallet.subtract(toBePayed).setScale(3, RoundingMode.FLOOR));

	}

	public Grade getGrade() {
		return grade;
	}

	public void setGrade(Grade grade) {
		this.grade = grade;
	}

}
