package com.example.starter.auth.service;

import com.example.starter.auth.entity.User;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CognitoService {

    private static final String COGNITO_IDENTITY_POOL_URL = "https://cognito-idp.%s.amazonaws.com/%s";

    @Autowired
    private CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.client-id}")
    private String cognitoClientId;

    @Value("${aws.cognito.user-pool-id}")
    private String cognitoUserPoolId;

    @Value("${aws.cognito.default.userGroup}")
    private String cognitoUserGroup;

    @Value("${aws.cognito.region}")
    private String awsRegion;

    public String getCognitoIdentityPoolUrl() {
        return String.format(COGNITO_IDENTITY_POOL_URL,awsRegion,cognitoUserPoolId);
    }

    public ResponseEntity<String> registerUser(String firstName, String middleName, String lastName, String dateOfBirth, String email, String password) {
        try {
            AttributeType firstNameAttribute = AttributeType.builder().name("given_name").value(firstName).build();
            AttributeType middleNameAttribute = AttributeType.builder().name("middle_name").value(middleName).build();
            AttributeType lastNameAttribute = AttributeType.builder().name("family_name").value(lastName).build();
            AttributeType dateOfBirthAttribute = AttributeType.builder().name("birthdate").value(dateOfBirth).build();
            AttributeType emailAttribute = AttributeType.builder().name("email").value(email).build();
            AttributeType emailVerifiedAttribute = AttributeType.builder().name("email_verified").value("true").build();

            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(cognitoClientId)
                    .username(email)
                    .password(password)
                    .userAttributes(firstNameAttribute, middleNameAttribute, lastNameAttribute, dateOfBirthAttribute, emailAttribute)
                    .build();

            SignUpResponse signUpResponse = cognitoClient.signUp(signUpRequest);

            AdminUpdateUserAttributesRequest updateRequest = AdminUpdateUserAttributesRequest.builder()
                    .userPoolId(cognitoUserPoolId)
                    .username(email)
                    .userAttributes(emailVerifiedAttribute)
                    .build();

            cognitoClient.adminUpdateUserAttributes(updateRequest);

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
                    .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .authParameters(Map.of(
                            "USERNAME", email,
                            "PASSWORD", password))
                    .build();

            AdminInitiateAuthResponse authResponse = cognitoClient.adminInitiateAuth(authRequest);

            if (authResponse.authenticationResult() != null) {
                return ResponseEntity.ok().body(authResponse.authenticationResult().accessToken()); // or get accesstoken
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed.");
            }
        } catch (CognitoIdentityProviderException e) {
            return ResponseEntity.badRequest().body(e.awsErrorDetails().errorMessage());
        }
    }

    public boolean validateToken(String accessToken) {
        try {
            JWTClaimsSet claimsSet = JWTParser.parse(accessToken).getJWTClaimsSet();
            if (claimsSet.getIssuer().equals(getCognitoIdentityPoolUrl())) {
                if (claimsSet.getClaim("token_use").equals("access")) { // can be changed to 'access'/'id' token/id
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserInfo(String accessToken) {
        try {
            GetUserRequest getUserRequest = GetUserRequest.builder()
                    .accessToken(accessToken)
                    .build();
            GetUserResponse response = cognitoClient.getUser(getUserRequest);
            User user = getUser(response);
            return user;
        } catch (NotAuthorizedException e) {
            System.out.println("Not authorized: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private JWTClaimsSet getClaimSet(String token) throws ParseException {
        return JWTParser.parse(token).getJWTClaimsSet();
    }

    public List<String> extractRoles(String token) throws ParseException {
        JWTClaimsSet claimsSet = getClaimSet(token);

        List<String> roles = (List<String>) claimsSet.getClaims().get("cognito:groups");
        return roles != null ? roles : Collections.emptyList();
    }

    public String extractUserName(String token) throws ParseException {
        JWTClaimsSet claimsSet = getClaimSet(token);
        return claimsSet.getClaims().get("username").toString();
    }

    private static User getUser(GetUserResponse response) {
        User user = new User();
        for (AttributeType attributeType : response.userAttributes()) {
            System.out.println(attributeType);
            switch (attributeType.name()) {
                case "sub":
                    user.setUserName(attributeType.value());
                case "email":
                    user.setEmail(attributeType.value());
                    break;
                case "given_name":
                    user.setFirstName(attributeType.value());
                    break;
                case "middle_name":
                    user.setMiddleName(attributeType.value());
                    break;
                case "family_name":
                    user.setLastName(attributeType.value());
                    break;
                case "birthdate":
                    user.setDateOfBirth(attributeType.value());
                    break;
            }
        }
        return user;
    }


}
