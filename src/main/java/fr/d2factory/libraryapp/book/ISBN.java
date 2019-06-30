package fr.d2factory.libraryapp.book;

public class ISBN {
	long isbnCode;

	public ISBN(long isbnCode) {
		this.isbnCode = isbnCode;
	}

	// In order to use ISBN as a key in HashMap we should override hashCode and
	// equals methods
	@Override
	public int hashCode() {
		return Long.hashCode(this.isbnCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ISBN other = (ISBN) obj;
		if (this.isbnCode != other.isbnCode)
			return false;
		return true;
	}
}
