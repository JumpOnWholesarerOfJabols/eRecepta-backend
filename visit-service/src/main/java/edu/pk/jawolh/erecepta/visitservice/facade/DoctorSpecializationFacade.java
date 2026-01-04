package edu.pk.jawolh.erecepta.visitservice.facade;

import com.example.demo.codegen.types.DoctorData;
import edu.pk.jawolh.erecepta.common.visit.dtos.UserDataDTO;
import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;
import edu.pk.jawolh.erecepta.visitservice.client.GrpcUserClient;
import edu.pk.jawolh.erecepta.visitservice.model.DoctorSpecialization;
import edu.pk.jawolh.erecepta.visitservice.service.DoctorSpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DoctorSpecializationFacade {
    private final DoctorSpecializationService service;
    private final GrpcUserClient client;

    public boolean createDoctorSpecialization(UUID doctorId, Specialization specialization) {
        return service.createDoctorSpecialization(doctorId, specialization);
    }

    public List<Specialization> getSpecializations(UUID doctorId) {
        return service.getSpecializations(doctorId);
    }

    public List<DoctorSpecialization> findAll() {
        return service.findAll();
    }

    public List<DoctorData> findAllDoctorsBySpecialization(Specialization specialization) {
        return service.findAllDoctorsBySpecialization(specialization).stream().map(id -> {
            UserDataDTO dto = client.getUserData(id.toString());

            return DoctorData.newBuilder()
                    .doctorId(id.toString())
                    .email(dto.email())
                    .firstName(dto.firstName())
                    .lastName(dto.lastName())
                    .build();
        }).toList();
    }

    public boolean deleteDoctorSpecialization(UUID doctorId, Specialization specialization) {
        return service.deleteDoctorSpecialization(doctorId, specialization);
    }
}
