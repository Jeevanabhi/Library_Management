package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping
    public Map<String, Object> addBook(@RequestBody Map<String, String> payload) {
        String sql = "insert into books(book_name,genre,author,isbn) values(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, payload.get("bookName"));
            ps.setString(2, payload.get("genre"));
            ps.setString(3, payload.get("author"));
            ps.setString(4, payload.get("isbn"));
            return ps;
        }, keyHolder);

        return Map.of("success", true, "id", keyHolder.getKey());
    }

    @GetMapping
    public List<Map<String, Object>> searchBook(@RequestParam(required = false, defaultValue = "") String keyword) {
        String sql = "SELECT * FROM books WHERE book_name LIKE ? OR author LIKE ?";
        String searchParam = "%" + keyword + "%";
        return jdbcTemplate.queryForList(sql, searchParam, searchParam);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteBook(@PathVariable int id) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return Map.of("success", rows > 0);
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateBook(@PathVariable int id, @RequestBody Map<String, String> payload) {
        String sql = "update books set book_name = ?,genre = ?, author = ?, isbn = ? where book_id = ?";
        int rows = jdbcTemplate.update(sql, 
            payload.get("bookName"), 
            payload.get("genre"), 
            payload.get("author"), 
            payload.get("isbn"), 
            id);
        return Map.of("success", rows > 0);
    }
}
