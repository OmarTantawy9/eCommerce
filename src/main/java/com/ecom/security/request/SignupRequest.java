package com.ecom.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String username;

    @NotBlank
    @Email(message = "Email must be Valid")
    private String email;

    @NotBlank
    @Size(min = 5, max = 20, message = "Password must be between 5 and 20 characters")
    private String password;

    private Set<String> roles;

}
