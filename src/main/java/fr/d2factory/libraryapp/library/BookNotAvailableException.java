package fr.d2factory.libraryapp.library;

public class BookNotAvailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BookNotAvailableException() {
		super();
	}

	public BookNotAvailableException(String message) {
		super(message);
	}

}
