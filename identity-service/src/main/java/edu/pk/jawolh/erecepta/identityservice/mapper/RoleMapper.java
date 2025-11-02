package edu.pk.jawolh.erecepta.identityservice.mapper;

import com.example.demo.codegen.types.Role;
import edu.pk.jawolh.erecepta.identityservice.model.UserRole;

public class RoleMapper {
    public static UserRole mapRole(Role role) {
        return switch (role) {
            case PATIENT -> UserRole.PATIENT;
        };
    }
}
