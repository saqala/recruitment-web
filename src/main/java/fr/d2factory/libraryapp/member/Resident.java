package fr.d2factory.libraryapp.member;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Resident extends Member {

	public final static int MAX_DAYS_TO_BORROW_BOOK = 60;
	public final static BigDecimal NORMAL_CHARGE = new BigDecimal("0.1");
	public final static BigDecimal LATE_CHARGE = new BigDecimal("0.2");

	@Override
	public void payBook(int numberOfDays) {

		BigDecimal toBePayed;
		BigDecimal wallet = getWallet();

		if (numberOfDays <= MAX_DAYS_TO_BORROW_BOOK) {
			toBePayed = NORMAL_CHARGE.multiply(new BigDecimal(numberOfDays));
		}

		else {
			toBePayed = NORMAL_CHARGE.multiply(new BigDecimal(MAX_DAYS_TO_BORROW_BOOK))
					.add(LATE_CHARGE.multiply(new BigDecimal(numberOfDays - MAX_DAYS_TO_BORROW_BOOK)));

		}

		setWallet(wallet.subtract(toBePayed).setScale(3, RoundingMode.FLOOR));

	}

}
