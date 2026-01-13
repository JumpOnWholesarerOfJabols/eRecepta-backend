package edu.pk.jawolh.erecepta.identityservice.mapper;


public class UserMapper {

    public static com.example.demo.codegen.types.UserAccount toDTO(edu.pk.jawolh.erecepta.identityservice.model.UserAccount domainUser) {
        if (domainUser == null) {
            return null;
        }

        return com.example.demo.codegen.types.UserAccount.newBuilder()
                .id(domainUser.getId().toString())
                .email(domainUser.getEmail())
                .pesel(domainUser.getPesel())
                .firstName(domainUser.getFirstName())
                .lastName(domainUser.getLastName())
                .phoneNumber(domainUser.getPhoneNumber())
                .dateOfBirth(domainUser.getDateOfBirth() != null ? domainUser.getDateOfBirth().toString() : null)
                .role(domainUser.getRole() != null ? domainUser.getRole().name() : null)
                .userGender(domainUser.getUserGender() != null ? domainUser.getUserGender().name() : null)
                .hashedPassword(domainUser.getHashedPassword())
                .verified(domainUser.isVerified())
                .build();
    }
}