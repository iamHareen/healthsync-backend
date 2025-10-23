package com.hareen.patientservice.mapper;

import com.hareen.patientservice.dto.PatientRequestDTO; // 👈 NEW IMPORT
import com.hareen.patientservice.dto.PatientResponseDTO;
import com.hareen.patientservice.model.Patient;

public class PatientMapper {

    /**
     * Converts a Patient entity to a PatientResponseDTO (for READ operations).
     */
    public static PatientResponseDTO toDTO(Patient patient) {
        PatientResponseDTO patientDTO = new PatientResponseDTO();
        patientDTO.setId(patient.getId().toString());
        patientDTO.setName(patient.getName());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setEmail(patient.getEmail());
        patientDTO.setDateOfBirth(patient.getDateOfBirth().toString());
        return patientDTO;
    }

    /**
     * Converts a PatientRequestDTO to a Patient entity (for CREATE operations). 👈 NEW METHOD
     * This signature resolves the original compilation error.
     */
    public static Patient toModel(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(java.time.LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisterDate(java.time.LocalDate.parse(patientRequestDTO.getRegisterDate()));
        return patient;
    }

}