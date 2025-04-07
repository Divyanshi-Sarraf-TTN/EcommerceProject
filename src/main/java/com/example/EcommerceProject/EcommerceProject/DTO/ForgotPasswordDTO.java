package com.example.EcommerceProject.EcommerceProject.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString

public class ForgotPasswordDTO {
//    @Email(message = "Invalid email format")
//    @NotBlank(message = "email must not be blank")
    private String email;

}
