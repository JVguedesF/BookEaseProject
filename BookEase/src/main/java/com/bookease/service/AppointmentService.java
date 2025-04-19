package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.model.dto.request.AppointmentRequestDto;
import com.bookease.model.dto.response.AppointmentResponseDto;
import com.bookease.model.entity.Appointment;
import com.bookease.model.entity.DoctorClinic;
import com.bookease.model.entity.Patient;
import com.bookease.model.entity.ProcedureOffered;
import com.bookease.model.entity.WorkSchedule;
import com.bookease.model.enums.AppointmentEnum;
import com.bookease.model.mappers.AppointmentMapper;
import com.bookease.repository.AppointmentRepository;
import com.bookease.repository.DoctorClinicRepository;
import com.bookease.repository.PatientRepository;
import com.bookease.repository.ProcedureOfferedRepository;
import com.bookease.repository.WorkScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private static final String ENTITY_NAME = "Appointment";
    private static final String PATIENT_NAME = "Patient";
    private static final String DOCTOR_CLINIC_NAME = "DoctorClinic";

    private final AppointmentRepository appointmentRepository;
    private final DoctorClinicRepository doctorClinicRepository;
    private final PatientRepository patientRepository;
    private final ProcedureOfferedRepository procedureOfferedRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final AppointmentMapper appointmentMapper;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorClinicRepository doctorClinicRepository,
                              PatientRepository patientRepository,
                              ProcedureOfferedRepository procedureOfferedRepository,
                              WorkScheduleRepository workScheduleRepository,
                              AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.doctorClinicRepository = doctorClinicRepository;
        this.patientRepository = patientRepository;
        this.procedureOfferedRepository = procedureOfferedRepository;
        this.workScheduleRepository = workScheduleRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto) {
        DoctorClinic doctorClinic = getDoctorClinic(requestDto.doctorClinicId());
        Patient patient = getPatient(requestDto.patientId());
        ProcedureOffered procedureOffered = getProcedureOffered(requestDto.procedureOfferedId());
        WorkSchedule workSchedule = getWorkSchedule(requestDto.workScheduleId());
        Appointment appointment = appointmentMapper.toEntity(requestDto, procedureOffered, doctorClinic, patient, workSchedule);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDto(appointment);
    }

    public AppointmentResponseDto getAppointmentById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
        return appointmentMapper.toResponseDto(appointment);
    }

    public List<AppointmentResponseDto> getAppointmentsByPatient(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException(PATIENT_NAME, patientId));
        List<Appointment> appointments = appointmentRepository.findByPatient(patient);
        return appointments.stream()
                .map(appointmentMapper::toResponseDto)
                .toList();
    }

    public List<AppointmentResponseDto> getAppointmentsByDoctorClinic(UUID doctorClinicId) {
        DoctorClinic doctorClinic = doctorClinicRepository.findById(doctorClinicId)
                .orElseThrow(() -> new EntityNotFoundException(DOCTOR_CLINIC_NAME, doctorClinicId));
        List<Appointment> appointments = appointmentRepository.findByDoctorClinic(doctorClinic);
        return appointments.stream()
                .map(appointmentMapper::toResponseDto)
                .toList();
    }

    public List<AppointmentResponseDto> getAppointmentsByStatus(AppointmentEnum status) {
        List<Appointment> appointments = appointmentRepository.findByStatus(status);
        if (appointments.isEmpty()) {
            throw new EntityNotFoundException(ENTITY_NAME, "status " + status);
        }
        return appointments.stream()
                .map(appointmentMapper::toResponseDto)
                .toList();
    }

    public List<AppointmentResponseDto> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Appointment> appointments = appointmentRepository.findByDateTimeBetween(start, end);
        if (appointments.isEmpty()) {
            throw new EntityNotFoundException(ENTITY_NAME, "intervalo de datas " + start + " a " + end);
        }
        return appointments.stream()
                .map(appointmentMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public AppointmentResponseDto updateAppointment(UUID id, AppointmentRequestDto requestDto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
        DoctorClinic doctorClinic = getDoctorClinic(requestDto.doctorClinicId());
        Patient patient = getPatient(requestDto.patientId());
        ProcedureOffered procedureOffered = getProcedureOffered(requestDto.procedureOfferedId());
        WorkSchedule workSchedule = getWorkSchedule(requestDto.workScheduleId());
        appointment.setDateTime(requestDto.dateTime());
        appointment.setDescription(requestDto.description());
        appointment.setStatus(requestDto.status());
        appointment.setDoctorClinic(doctorClinic);
        appointment.setPatient(patient);
        appointment.setProcedureOffered(procedureOffered);
        appointment.setWorkSchedule(workSchedule);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDto(appointment);
    }

    @Transactional
    public void deleteAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
        appointmentRepository.delete(appointment);
    }

    @Transactional
    public AppointmentResponseDto deactivateAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
        appointment.setActive(false);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDto(appointment);
    }

    private DoctorClinic getDoctorClinic(String doctorClinicId) {
        return doctorClinicRepository.findById(UUID.fromString(doctorClinicId))
                .orElseThrow(() -> new EntityNotFoundException(DOCTOR_CLINIC_NAME, doctorClinicId));
    }

    private Patient getPatient(String patientId) {
        return patientRepository.findById(UUID.fromString(patientId))
                .orElseThrow(() -> new EntityNotFoundException(PATIENT_NAME, patientId));
    }

    private ProcedureOffered getProcedureOffered(String procedureOfferedId) {
        return procedureOfferedRepository.findById(UUID.fromString(procedureOfferedId))
                .orElseThrow(() -> new EntityNotFoundException("ProcedureOffered", procedureOfferedId));
    }

    private WorkSchedule getWorkSchedule(String workScheduleId) {
        return workScheduleRepository.findById(UUID.fromString(workScheduleId))
                .orElseThrow(() -> new EntityNotFoundException("WorkSchedule", workScheduleId));
    }
}
