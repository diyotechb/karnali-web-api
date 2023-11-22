package com.example.starter.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class User {
    private String email;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dateOfBirth;
}
