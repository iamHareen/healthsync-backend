package com.hareen.patientservice.service;

import com.hareen.patientservice.dto.PatientRequestDTO;
import com.hareen.patientservice.dto.PatientResponseDTO;
import com.hareen.patientservice.exception.EmailAlreadyExistsException;
import com.hareen.patientservice.exception.PatientNotFoundException;
import com.hareen.patientservice.grpc.BillingServiceGrpcClient;
import com.hareen.patientservice.mapper.PatientMapper;
import com.hareen.patientservice.model.Patient;
import com.hareen.patientservice.repository.PatientRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient) {

        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }


//    method-01
//    public List<PatientResponseDTO> getAllPatients() {
//        List<Patient> patients = patientRepository.findAll();
//        List<PatientResponseDTO> patientResponseDTOs = patients.stream().map(patient -> PatientMapper.toDTO(patient)).toList();
//        return patientResponseDTOs;
//    }

//    method-02
    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(PatientMapper::toDTO).toList(); 
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A Patient with this Email already exists: " + patientRequestDTO.getEmail());
        }
        Patient toSave = PatientMapper.toModel(patientRequestDTO);

        Patient saved = patientRepository.save(toSave);

        billingServiceGrpcClient.createBillingAccount(saved.getId().toString(), saved.getName(), saved.getEmail());
        return PatientMapper.toDTO(saved);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(()->
                new PatientNotFoundException("Patient not found with ID: " + id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("A Patient with this Email already exists: " + patientRequestDTO.getEmail());
        }
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        if(!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient not found with ID: " + id);
        }
        patientRepository.deleteById(id);
    }
}
