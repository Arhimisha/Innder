package sts.innopolis.university.inner.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;
import sts.innopolis.university.inner.domain.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;



@Repository
@RequiredArgsConstructor
public class UserRepository {
  private final NamedParameterJdbcTemplate template;


//  public UserRepository(NamedParameterJdbcTemplate template) {
//    this.template = template;
//  }

  public sts.innopolis.university.inner.domain.User findByUsername(String username) {
    User user = template.queryForObject(
        "SELECT id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired FROM users WHERE username = :username", // :username - это именованный параметр
        // key - :username (но без двоеточия)
        Map.of("username", username),
        new RowMapper<User>() {
          @Override
          public User mapRow(ResultSet rs, int i) throws SQLException {
            return new User(
                // rs.getInt, rs.getString, rs.getLong, rs.getBoolean
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                Collections.emptyList(), // пока не сходили в другую табличку, никаких прав нет
                rs.getBoolean("enabled"),
                rs.getBoolean("account_non_expired"),
                rs.getBoolean("account_non_locked"),
                rs.getBoolean("credentials_non_expired")
            );
          }
        }
    );

    // генерирует исключение throw EmptyResultDataAccessException, если объекта не будет


    List<GrantedAuthority> authorities = template.query(
        "SELECT authority FROM authorities WHERE user_id = :user_id",
        Map.of("user_id", user.getId()),
        new RowMapper<GrantedAuthority>() {
          @Override
          public GrantedAuthority mapRow(ResultSet rs, int i) throws SQLException {
            return new SimpleGrantedAuthority(
                rs.getString("authority")
            );
          }
        }
    );

    user.setAuthorities(authorities);

    return user;
  }

  // create + update
  public void save(User user) {
    if (user.getId() == 0) {
      // создаём нового пользователя
      // create
      // keyholder - для того, чтобы вытащить автосгенерированное значение id
      KeyHolder keyHolder = new GeneratedKeyHolder();
      MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValues(Map.of(
          "username", user.getUsername(),
          "password", user.getPassword(), // пароль уже должен быть шифрованный
          "enabled", user.isEnabled(),
          "account_non_expired", user.isAccountNonExpired(),
          "account_non_locked", user.isAccountNonLocked(),
          "credentials_non_expired", user.isCredentialsNonExpired()
      ));

      template.update(
          "INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) VALUES (:username, :password, :enabled, :account_non_expired, :account_non_locked, :credentials_non_expired)",
          params,
          keyHolder
      );

      // получаем его id
      int id = keyHolder.getKey().intValue();

      // сохраняем его права
      for (GrantedAuthority authority : user.getAuthorities()) {
        template.update(
            "INSERT INTO authorities (user_id, authority) VALUES (:user_id, :authority)",
            Map.of(
                "user_id", id,
                "authority", authority.getAuthority()
            )
        );
      }

      return;
    }

    // update
    template.update(
        "UPDATE users SET username = :username, password = :password, enabled = :enabled, account_non_expired = :account_non_expired, account_non_locked = :account_non_locked, credentials_non_expired = :credentials_non_expired WHERE user_id = :user_id",
        Map.of(
            "user_id", user.getId(),
            "username", user.getUsername(),
            "password", user.getPassword(), // пароль уже должен быть шифрованный
            "enabled", user.isEnabled(),
            "account_non_expired", user.isAccountNonExpired(),
            "account_non_locked", user.isAccountNonLocked(),
            "credentials_non_expired", user.isCredentialsNonExpired()
        )
    );

    // сначала удаляем все, а потом добавим
    template.update(
        "DELETE FROM authorities WHERE user_id = :user_id",
        Map.of("user_id", user.getId())
    );

    // сохраняем его права
    for (GrantedAuthority authority : user.getAuthorities()) {
      template.update(
          "INSERT INTO authorities (user_id, authority) VALUES (:user_id, :authority)",
          Map.of(
              "user_id", user.getId(),
              "authority", authority.getAuthority()
          )
      );
    }
  }
}
