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
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping
    public Map<String, Object> registerUser(@RequestBody Map<String, String> payload) {
        String sql = "insert into users(name,email,borrowed_count) values(?,?,0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, payload.get("name"));
            ps.setString(2, payload.get("email"));
            return ps;
        }, keyHolder);

        return Map.of("success", true, "id", keyHolder.getKey());
    }

    @GetMapping
    public List<Map<String, Object>> searchUser(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false) Integer id) {
        
        if (id != null) {
            String sql = "select * from users where name like ? AND user_id = ?";
            return jdbcTemplate.queryForList(sql, "%" + name + "%", id);
        } else {
            String sql = "select * from users where name like ?";
            return jdbcTemplate.queryForList(sql, "%" + name + "%");
        }
    }
}
