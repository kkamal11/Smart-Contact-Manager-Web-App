package com.kamal.scm_app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "socialMedia")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialMediaLinks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String SocialMediaName;
    private String SocialMediaLink;

    @ManyToOne
    @JsonIgnore //prevents recursive date sent
    private Contact contact;
}
