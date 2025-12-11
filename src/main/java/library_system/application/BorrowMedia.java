	package library_system.application;
	
	import java.time.LocalDate;
	import java.util.List;
	
	import library_system.domain.Book;
	import library_system.domain.CD;
	
	public class BorrowMedia {
	
	    private final BookRepository bookRepo;
	    private final CdRepository cdRepo;
	    private final BorrowLedger ledger;
	    private final OverdueMedia overdueMedia;
	
	    public BorrowMedia(BookRepository bookRepo, CdRepository cdRepo, BorrowLedger ledger, OverdueMedia overdueMedia) {
	
	        if (bookRepo == null || cdRepo == null || ledger == null || overdueMedia == null)
	            throw new IllegalArgumentException("Dependencies cannot be null");
	
	        this.bookRepo = bookRepo;
	        this.cdRepo = cdRepo;
	        this.ledger = ledger;
	        this.overdueMedia = overdueMedia;
	    }
	
	    public boolean borrow(String mediaId, String userId, LocalDate today) {
	        if (mediaId == null || userId == null || today == null) return false;
	        if (mediaId.trim().isEmpty() || userId.trim().isEmpty()) return false;
	
	        if (overdueMedia.userHasOverdues(userId, today)) return false;
	
	        Book book = findBook(mediaId);
	        if (book != null) {
	            return borrowBook(book, userId, today);
	        }
	
	        CD cd = findCd(mediaId);
	        if (cd != null) {
	            return borrowCd(cd, userId, today);
	        }
	
	        return false;
	    }
	
	    private boolean borrowBook(Book book, String userId, LocalDate today) {
	        if (book.isBorrowed()) return false;
	
	        int days = book.getBorrowDays();
	        LocalDate due = today.plusDays(days);
	
	        book.borrow(userId, due);
	        ledger.recordBorrow(book.getId(), userId, due);
	
	        List<Book> all = bookRepo.getAll();
	        replaceBook(all, book);
	        bookRepo.saveAll(all);
	
	        return true;
	    }
	
	    private Book findBook(String isbn) {
	        for (Book b : bookRepo.getAll()) {
	            if (b.getIsbn().equalsIgnoreCase(isbn)) {
	                return b;
	            }
	        }
	        return null;
	    }
	
	    private void replaceBook(List<Book> books, Book updated) {
	        books.removeIf(b -> b.getIsbn().equals(updated.getIsbn()));
	        books.add(updated);
	    }
	
	    private boolean borrowCd(CD cd, String userId, LocalDate today) {
	        if (cd.isBorrowed()) return false;
	
	        int days = cd.getBorrowDays();
	        LocalDate due = today.plusDays(days);
	
	        cd.borrow(userId, due);
	        ledger.recordBorrow(cd.getId(), userId, due);
	
	        List<CD> all = cdRepo.getAll();
	        replaceCd(all, cd);
	        cdRepo.saveAll(all);
	
	        return true;
	    }
	
	    private CD findCd(String id) {
	        for (CD cd : cdRepo.getAll()) {
	            if (cd.getId().equalsIgnoreCase(id)) {
	                return cd;
	            }
	        }
	        return null;
	    }
	
	    private void replaceCd(List<CD> cds, CD updated) {
	        cds.removeIf(c -> c.getId().equals(updated.getId()));
	        cds.add(updated);
	    }
	}
