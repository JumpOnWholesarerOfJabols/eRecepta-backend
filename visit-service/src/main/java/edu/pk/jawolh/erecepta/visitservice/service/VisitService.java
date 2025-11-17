package edu.pk.jawolh.erecepta.visitservice.service;

import com.example.demo.codegen.types.CreateVisitInput;
import edu.pk.jawolh.erecepta.visitservice.exception.DoctorNotFoundException;
import edu.pk.jawolh.erecepta.visitservice.mapper.VisitInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final VisitInputMapper mapper;
    private final GrpcDoctorService grpcDoctorService;

    public int save(String patientId, CreateVisitInput input) {
        if (!grpcDoctorService.checkDoctorExists(input.getDoctorId())) {
            throw new DoctorNotFoundException(input.getDoctorId());
        }
        Visit v = mapper.mapFromInput(patientId, input);
        return visitRepository.save(v);
    }

    public Optional<Visit> findById(int id) {
        return visitRepository.findById(id);
    }

    public List<Visit> findAll() {
        return visitRepository.findAll();
    }

}
