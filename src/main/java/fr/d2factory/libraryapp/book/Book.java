package fr.d2factory.libraryapp.book;

/**
 * A simple representation of a book
 */
public class Book {
	private String title;
	private String author;
	private ISBN isbn;

	public Book(String title, String author, ISBN isbn) {
		this.title = title;
		this.author = author;
		this.isbn = isbn;
	}

	// In order to use Book as a key in HashMap we should override hashCode and
	// equals methods
	@Override
	public int hashCode() {

		return isbn.hashCode();

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (isbn == null) {
			if (other.isbn != null)
				return false;
		} else if (!isbn.equals(other.isbn))
			return false;
		return true;
	}

	public ISBN getIsbn() {
		return isbn;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}


	
	

	
	

}
