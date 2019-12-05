package sts.innopolis.university.inner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sts.innopolis.university.inner.domain.Registration;
import sts.innopolis.university.inner.domain.User;
import sts.innopolis.university.inner.repository.UserRepository;

import java.util.List;



@Service
@Transactional // оборачивает все методы в транзакции
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    try {
      return repository.findByUsername(username);
    } catch (EmptyResultDataAccessException e) {
      throw new UsernameNotFoundException("Username not found");
    }
  }

  public void register(Registration data) {
    // 1. уникальный ли логин
    // 2. сравниваем пароли
    if (!data.getPassword().equals(data.getConfirm())) {
      throw new RuntimeException("Пароли не совпадают");
    }
    // 3. сохраняем
    repository.save(new User(
        0,
        data.getUsername(),
        passwordEncoder.encode(data.getPassword()), // потому что пароль в открытом виде
        List.of(new SimpleGrantedAuthority("ROLE_USER")),
        true,
        true,
        true,
        true
    ));
  }
}
