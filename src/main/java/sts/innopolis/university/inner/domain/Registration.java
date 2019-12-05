package sts.innopolis.university.inner.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Registration {
  private String username; // в форме должно быть name="username"
  private String password; // в форме должно быть name="password"
  private String confirm; // в форме должно быть name="confirm"
}
