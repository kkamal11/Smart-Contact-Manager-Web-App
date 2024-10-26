package com.kamal.scm_app.forms;

import com.kamal.scm_app.validators.ValidUploadedFile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddContactForm {
    private String id;
    @NotBlank(message = "Name is required.")
    private String name;
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required.")
    private String email;
    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[0-9]{10}$",message = "Invalid phone number.")
    @Size(min = 10, max = 10, message = "10 character is required.")
    private String phoneNumber;
    private String address;

    @ValidUploadedFile(message="Invalid File")
    private MultipartFile ContactPicture;
    private String description;
    private String isFavourite;
    private String websiteLink;
    private String linkedInLink;

    private String contactPictureUrl;

}
