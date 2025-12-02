package edu.pk.jawolh.erecepta.visitservice.mapper;

import com.example.demo.codegen.types.CreateVisitInput;
import edu.pk.jawolh.erecepta.common.visit.dtos.UserDataDTO;
import edu.pk.jawolh.erecepta.common.visit.dtos.VisitDataDTO;
import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;
import edu.pk.jawolh.erecepta.common.visit.messages.VisitMessage;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class VisitMapper {

    public Visit mapFromInput(UUID patientId, CreateVisitInput in) {
        Visit v = new Visit(UUID.randomUUID(), UUID.fromString(in.getDoctorId()), patientId, Specialization.valueOf(in.getSpecialization().name()));
        v.setVisitTime(LocalDateTime.parse(in.getVisitTime()));

        return v;
    }

    public VisitMessage mapToMessage(UserDataDTO patientData, UserDataDTO doctorData, Visit visit) {
        VisitDataDTO dto = new VisitDataDTO(visit.getVisitTime(), visit.getSpecialization(), visit.getVisitStatus());
        return new VisitMessage(patientData, doctorData, dto);
    }

}
