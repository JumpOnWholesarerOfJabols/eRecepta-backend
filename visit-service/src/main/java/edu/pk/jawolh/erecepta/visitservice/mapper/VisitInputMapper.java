package edu.pk.jawolh.erecepta.visitservice.mapper;

import com.example.demo.codegen.types.CreateVisitInput;
import edu.pk.jawolh.erecepta.visitservice.model.Specialization;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class VisitInputMapper {

    public Visit mapFromInput(UUID patientId, CreateVisitInput in) {
        Visit v = new Visit(UUID.randomUUID(), UUID.fromString(in.getDoctorId()), patientId, Specialization.valueOf(in.getSpecialization().name()));
        v.setVisitTime(LocalDateTime.parse(in.getVisitTime()));

        return v;
    }

}
