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
        String bookName = payload.get("bookName");
        String genreName = payload.get("genre");
        String authorName = payload.get("author");
        String isbn = payload.get("isbn");

        Integer genreId = getOrInsertGenre(genreName);
        Integer authorId = getOrInsertAuthor(authorName);

        String sql = "INSERT INTO books(book_name, genre_id, author_id, isbn) VALUES(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, bookName);
            ps.setInt(2, genreId);
            ps.setInt(3, authorId);
            ps.setString(4, isbn);
            return ps;
        }, keyHolder);

        return Map.of("success", true, "id", keyHolder.getKey());
    }

    @GetMapping
    public List<Map<String, Object>> searchBook(@RequestParam(required = false, defaultValue = "") String keyword) {
        String sql = "SELECT b.book_id, b.book_name, b.isbn, b.available, a.author_name as author, g.genre_name as genre " +
                     "FROM books b " +
                     "LEFT JOIN authors a ON b.author_id = a.author_id " +
                     "LEFT JOIN genres g ON b.genre_id = g.genre_id " +
                     "WHERE b.book_name LIKE ? OR a.author_name LIKE ?";
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
        String bookName = payload.get("bookName");
        String genreName = payload.get("genre");
        String authorName = payload.get("author");
        String isbn = payload.get("isbn");

        Integer genreId = getOrInsertGenre(genreName);
        Integer authorId = getOrInsertAuthor(authorName);

        String sql = "update books set book_name = ?, genre_id = ?, author_id = ?, isbn = ? where book_id = ?";
        int rows = jdbcTemplate.update(sql, bookName, genreId, authorId, isbn, id);
        return Map.of("success", rows > 0);
    }

    private Integer getOrInsertGenre(String genreName) {
        if (genreName == null || genreName.trim().isEmpty()) return null;
        List<Integer> ids = jdbcTemplate.queryForList("SELECT genre_id FROM genres WHERE genre_name = ?", Integer.class, genreName);
        if (!ids.isEmpty()) return ids.get(0);
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO genres(genre_name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, genreName);
            return ps;
        }, keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }

    private Integer getOrInsertAuthor(String authorName) {
        if (authorName == null || authorName.trim().isEmpty()) return null;
        List<Integer> ids = jdbcTemplate.queryForList("SELECT author_id FROM authors WHERE author_name = ?", Integer.class, authorName);
        if (!ids.isEmpty()) return ids.get(0);
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO authors(author_name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, authorName);
            return ps;
        }, keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : null;
    }
}
