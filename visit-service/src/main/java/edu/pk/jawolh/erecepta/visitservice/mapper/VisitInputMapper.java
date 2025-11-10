package edu.pk.jawolh.erecepta.visitservice.mapper;

import com.example.demo.codegen.types.CreateVisitInput;
import edu.pk.jawolh.erecepta.visitservice.model.Specialization;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VisitInputMapper {

    public Visit mapFromInput(CreateVisitInput in) {
        Visit v = new Visit(in.getDoctorId(), in.getPatientId(), Specialization.values()[in.getSpecialization()]);
        v.setVisitTime(LocalDateTime.parse(in.getVisitTime()));

        return v;
    }

}
