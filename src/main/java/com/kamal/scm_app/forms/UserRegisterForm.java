package com.kamal.scm_app.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRegisterForm {
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required.")
    private String email;
    @NotBlank(message = "Username is required.")
    private String userName;
    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 1000, message = "Min 6 character required.")
    private String password;
    @Pattern(regexp = "^[0-9]{10}$",message = "Invalid phone number.")
    @Size(min = 10, max = 10, message = "10 character is required.")
    private String phoneNumber;
    private String about;
    private String gender;
}
