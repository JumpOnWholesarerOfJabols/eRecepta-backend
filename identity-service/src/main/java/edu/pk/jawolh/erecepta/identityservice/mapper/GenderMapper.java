package edu.pk.jawolh.erecepta.identityservice.mapper;

import com.example.demo.codegen.types.Gender;
import edu.pk.jawolh.erecepta.common.user.enums.UserGender;

public class GenderMapper {

    public static UserGender mapGender(Gender gender){
        return switch(gender){
            case MALE -> UserGender.MALE;
            case FEMALE -> UserGender.FEMALE;
        };
    }
}
