package sts21.innopolis.university.innder.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  @Override
  public void configure(WebSecurity web) throws Exception {
    web
            .ignoring()
            .antMatchers("/resources/**")
            .antMatchers("/login/**")
            .antMatchers("/css/**")
            .antMatchers("/fonts/**")
            .antMatchers("/images/**")
            .antMatchers("/js/**");

  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests()
          // на главную можно всем
          .antMatchers("/").permitAll()
            .antMatchers("/css/**", "/js/**").permitAll()
          // на регистрацию - только неаутентифицированным
          .antMatchers("/register").anonymous()

          .antMatchers("/admin**").hasRole("ADMIN")

          // все остальные запросы, которые не подпали под правила должны быть аутентифицированы
          .anyRequest().authenticated()
        .and()
        .formLogin()
          // куда кидаем после успешного логина
          .loginPage("/login")
          .loginProcessingUrl("/login")
          .defaultSuccessUrl("/")
          .permitAll()
        .and()
        .logout()
          .logoutSuccessUrl("/")
    ;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
