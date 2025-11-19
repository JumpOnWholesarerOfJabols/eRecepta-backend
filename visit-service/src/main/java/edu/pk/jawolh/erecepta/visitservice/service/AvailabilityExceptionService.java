package edu.pk.jawolh.erecepta.visitservice.service;

import com.example.demo.codegen.types.CreateAvailabilityExceptionInput;
import edu.pk.jawolh.erecepta.visitservice.mapper.AvailabilityExceptionInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import edu.pk.jawolh.erecepta.visitservice.repository.AvailabilityExceptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityExceptionService {
    private final AvailabilityExceptionRepository repository;
    private final AvailabilityExceptionInputMapper mapper;

    public UUID createAvailabilityException(String doctorId, CreateAvailabilityExceptionInput input) {
        AvailabilityException avex = mapper.mapFromInput(doctorId, input);
        repository.save(avex);
        return avex.getId();
    }

    public List<AvailabilityException> findAllByDoctorId(String doctorId) {
        return repository.findAllByDoctorId(doctorId);
    }

    public List<AvailabilityException> findAllBydDoctorIdAndDateEquals(String doctorId, String date) {
        LocalDate localDate = LocalDate.parse(date);
        return repository.findAllByDoctorIdAndDateEquals(doctorId, localDate);
    }

    public List<AvailabilityException> findAllByDoctorIdAndDateBetween(String doctorId, String dateStart, LocalDate dateEnd) {
        LocalDate startDate = LocalDate.parse(dateStart);
        LocalDate endDate = LocalDate.parse(dateEnd.toString());
        return repository.findAllByDoctorIdAndDateBetween(doctorId, startDate, endDate);
    }
}
