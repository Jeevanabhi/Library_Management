package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "*")
public class BorrowController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/checkout")
    public Map<String, Object> borrowBook(@RequestBody Map<String, Integer> payload) {
        int userId = payload.get("userId");
        int bookId = payload.get("bookId");
        
        // 1. Check availability
        List<Map<String, Object>> books = jdbcTemplate.queryForList("SELECT book_name, available FROM books WHERE book_id = ?", bookId);
        
        if (books.isEmpty()) {
            return Map.of("success", false, "message", "Book not found");
        }
        
        Map<String, Object> book = books.get(0);
        
        boolean available = false;
        Object availObj = book.get("available");
        if (availObj instanceof Boolean) available = (Boolean) availObj;
        if (availObj instanceof Number) available = ((Number) availObj).intValue() == 1;

        if (!available) {
            return Map.of("success", false, "message", "Book is not available");
        }
        
        String bookName = (String) book.get("book_name");
        Date sqlDate = Date.valueOf(LocalDate.now());
        
        // 2. Insert into borrowed_books
        jdbcTemplate.update("INSERT INTO borrowed_books (user_id, book_id, borrow_date, return_date) VALUES (?, ?, ?, NULL)",
                userId, bookId, sqlDate);
                
        // 3. Update User
        jdbcTemplate.update("UPDATE users SET borrowed_count = borrowed_count + 1, borrowed_books = ? WHERE user_id = ?",
                bookName, userId);
                
        // 4. Update Book
        jdbcTemplate.update("UPDATE books SET available = false WHERE book_id = ?", bookId);
        
        return Map.of("success", true, "message", "Book Borrowed Successfully!");
    }

    @PostMapping("/return")
    public Map<String, Object> returnBook(@RequestBody Map<String, Integer> payload) {
        int userId = payload.get("userId");
        int bookId = payload.get("bookId");
        Date sqlDate = Date.valueOf(LocalDate.now());

        int updated = jdbcTemplate.update("UPDATE borrowed_books SET return_date = ? WHERE user_id = ? AND book_id = ? AND return_date IS NULL",
                sqlDate, userId, bookId);

        if (updated > 0) {
            jdbcTemplate.update("UPDATE books SET available = true WHERE book_id = ?", bookId);
            jdbcTemplate.update("UPDATE users SET borrowed_count = borrowed_count - 1 WHERE user_id = ?", userId);
            
            return Map.of("success", true, "message", "Book Returned Successfully!");
        } else {
            return Map.of("success", false, "message", "No active borrow record found.");
        }
    }
}
