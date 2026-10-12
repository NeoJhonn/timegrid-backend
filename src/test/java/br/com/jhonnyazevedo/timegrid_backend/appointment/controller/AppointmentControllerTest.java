package br.com.jhonnyazevedo.timegrid_backend.appointment.controller;

import br.com.jhonnyazevedo.timegrid_backend.appointment.dto.AppointmentResponse;
import br.com.jhonnyazevedo.timegrid_backend.appointment.entity.Appointment;
import br.com.jhonnyazevedo.timegrid_backend.appointment.mapper.AppointmentMapper;
import br.com.jhonnyazevedo.timegrid_backend.appointment.service.AppointmentService;
import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.enums.TimeGrid;
import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.exception.GlobalExceptionHandler;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private AppointmentMapper appointmentMapper;

    private MockMvc mockMvc;
    private UUID userId;
    private UUID clientId;
    private UUID appointmentId;
    private Appointment appointment;
    private AppointmentResponse appointmentResponse;

    @BeforeEach
    void setUp() {
        AppointmentController appointmentController = new AppointmentController(appointmentService, appointmentMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Client client = new Client();
        client.setId(clientId);
        client.setName("Carlos Silva");

        appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setUser(user);
        appointment.setClient(client);
        appointment.setService("Corte de cabelo");
        appointment.setAppointmentDate(LocalDate.of(2026, 10, 20));
        appointment.setStartTime(TimeGrid.T0900);
        appointment.setEndTime(TimeGrid.T0930);

        appointmentResponse = new AppointmentResponse(
                appointmentId,
                userId,
                clientId,
                "Carlos Silva",
                "Corte de cabelo",
                LocalDate.of(2026, 10, 20),
                TimeGrid.T0900,
                TimeGrid.T0930,
                null
        );
    }

    @Test
    void createAppointment_shouldReturnCreatedAppointment() throws Exception {
        String requestBody = """
                {
                  "clientId": "%s",
                  "service": "Corte de cabelo",
                  "appointmentDate": "2026-10-20",
                  "startTime": "T0900",
                  "endTime": "T0930"
                }
                """.formatted(clientId);

        when(appointmentMapper.toEntity(any())).thenReturn(appointment);
        when(appointmentService.createAppointment(userId, clientId, appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponse);

        mockMvc.perform(post("/users/{userId}/appointments", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(appointmentId.toString()))
                .andExpect(jsonPath("$.clientId").value(clientId.toString()))
                .andExpect(jsonPath("$.service").value("Corte de cabelo"));
    }

    @Test
    void createAppointment_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                  "clientId": null,
                  "service": "",
                  "appointmentDate": null,
                  "startTime": null,
                  "endTime": null
                }
                """;

        mockMvc.perform(post("/users/{userId}/appointments", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.clientId").exists())
                .andExpect(jsonPath("$.fields.service").exists())
                .andExpect(jsonPath("$.fields.appointmentDate").exists())
                .andExpect(jsonPath("$.fields.startTime").exists())
                .andExpect(jsonPath("$.fields.endTime").exists());

        verify(appointmentService, never()).createAppointment(any(), any(), any());
    }

    @Test
    void listAppointmentsByDate_shouldReturnAppointments() throws Exception {
        LocalDate date = LocalDate.of(2026, 10, 20);

        when(appointmentService.listAppointmentsByDate(userId, date)).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponseList(List.of(appointment))).thenReturn(List.of(appointmentResponse));

        mockMvc.perform(get("/users/{userId}/appointments", userId)
                        .param("date", "2026-10-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(appointmentId.toString()))
                .andExpect(jsonPath("$[0].appointmentDate").value("2026-10-20"));
    }

    @Test
    void listAppointmentsByDate_shouldReturnBadRequestWhenDateIsInvalid() throws Exception {
        mockMvc.perform(get("/users/{userId}/appointments", userId)
                        .param("date", "invalid-date"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/users/" + userId + "/appointments"));

        verify(appointmentService, never()).listAppointmentsByDate(any(), any());
    }

    @Test
    void updateAppointment_shouldReturnUpdatedAppointment() throws Exception {
        String requestBody = """
                {
                  "endTime": "T1030",
                  "service": "Corte e barba"
                }
                """;

        Appointment updateRequest = new Appointment();
        updateRequest.setId(appointmentId);
        updateRequest.setEndTime(TimeGrid.T1030);
        updateRequest.setService("Corte e barba");

        AppointmentResponse updatedResponse = new AppointmentResponse(
                appointmentId,
                userId,
                clientId,
                "Carlos Silva",
                "Corte e barba",
                LocalDate.of(2026, 9, 20),
                TimeGrid.T0900,
                TimeGrid.T1030,
                null
        );

        when(appointmentMapper.toEntity(eq(appointmentId), any())).thenReturn(updateRequest);
        when(appointmentService.updateAppointment(userId, updateRequest)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(updatedResponse);

        mockMvc.perform(put("/users/{userId}/appointments/{appointmentId}", userId, appointmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("Corte e barba"))
                .andExpect(jsonPath("$.endTime").value("T1030"));
    }

    @Test
    void updateAppointment_shouldReturnBadRequestWhenBusinessExceptionIsThrown() throws Exception {
        String requestBody = """
                {
                  "endTime": "T1030",
                  "service": "Corte e barba"
                }
                """;

        Appointment updateRequest = new Appointment();
        updateRequest.setId(appointmentId);
        updateRequest.setEndTime(TimeGrid.T1030);
        updateRequest.setService("Corte e barba");

        when(appointmentMapper.toEntity(eq(appointmentId), any())).thenReturn(updateRequest);
        when(appointmentService.updateAppointment(userId, updateRequest))
                .thenThrow(new BusinessException("Ja existe um agendamento neste intervalo"));

        mockMvc.perform(put("/users/{userId}/appointments/{appointmentId}", userId, appointmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ja existe um agendamento neste intervalo"));
    }

    @Test
    void deleteAppointment_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/{userId}/appointments/{appointmentId}", userId, appointmentId))
                .andExpect(status().isNoContent());

        verify(appointmentService).deleteAppointment(userId, appointmentId);
    }
}
