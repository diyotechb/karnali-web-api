package com.example.starter.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.Map;

@Service
public class CognitoService {

    @Autowired
    private CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.client-id}")
    private String cognitoClientId;

    @Value("${aws.cognito.user-pool-id}")
    private String cognitoUserPoolId;

    @Value("${aws.cognito.default.userGroup}")
    private String cognitoUserGroup;

    public ResponseEntity<String> registerUser(String firstName, String middleName, String lastName,String dateOfBirth, String email, String password) {
        try {
            // Specify email attribute
            AttributeType firstNameAttribute = AttributeType.builder().name("given_name").value(firstName).build();
            AttributeType middleNameAttribute = AttributeType.builder().name("middle_name").value(middleName).build();
            AttributeType lastNameAttribute = AttributeType.builder().name("family_name").value(lastName).build();
            AttributeType dateOfBirthAttribute = AttributeType.builder().name("birthdate").value(dateOfBirth).build();
            AttributeType emailAttribute = AttributeType.builder().name("email").value(email).build();
            AttributeType emailVerifiedAttribute = AttributeType.builder().name("email_verified").value("true").build();

            // Create the SignUpRequest with email attribute and other necessary details
            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(cognitoClientId)
                    .username(email)
                    .password(password)
                    .userAttributes(firstNameAttribute, middleNameAttribute, lastNameAttribute, dateOfBirthAttribute, emailAttribute)
                    .build();

            // Call Cognito to sign up the user
            SignUpResponse signUpResponse = cognitoClient.signUp(signUpRequest);

            AdminUpdateUserAttributesRequest updateRequest = AdminUpdateUserAttributesRequest.builder()
                    .userPoolId(cognitoUserPoolId)
                    .username(email)
                    .userAttributes(emailVerifiedAttribute)
                    .build();

            cognitoClient.adminUpdateUserAttributes(updateRequest);

            // Confirm the user to trigger email verification
            AdminConfirmSignUpRequest confirmSignUpRequest = AdminConfirmSignUpRequest.builder()
                    .userPoolId(cognitoUserPoolId)
                    .username(email)
                    .build();

            cognitoClient.adminConfirmSignUp(confirmSignUpRequest);

            AdminAddUserToGroupRequest addUserToGroupRequest = AdminAddUserToGroupRequest.builder()
                    .userPoolId(cognitoUserPoolId)
                    .username(email)
                    .groupName(cognitoUserGroup)
                    .build();

            cognitoClient.adminAddUserToGroup(addUserToGroupRequest);


            return ResponseEntity.ok().body("User registered successfully. Email confirmed.");
        } catch (CognitoIdentityProviderException e) {
            return ResponseEntity.badRequest().body(e.awsErrorDetails().errorMessage());
        }
    }

    public ResponseEntity<?> loginUser(String email, String password) {
        try {
            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .userPoolId(cognitoUserPoolId)
                    .clientId(cognitoClientId)
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .authParameters(Map.of(
                            "USERNAME", email,
                            "PASSWORD", password))
                    .build();

            AdminInitiateAuthResponse authResponse = cognitoClient.adminInitiateAuth(authRequest);

            if (authResponse.authenticationResult() != null) {
                return ResponseEntity.ok().body("Login successful. Token: " + authResponse.authenticationResult().idToken());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed.");
            }
        } catch (CognitoIdentityProviderException e) {
            return ResponseEntity.badRequest().body(e.awsErrorDetails().errorMessage());
        }
    }
}
