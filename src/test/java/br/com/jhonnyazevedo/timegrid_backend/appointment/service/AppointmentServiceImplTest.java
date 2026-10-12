package br.com.jhonnyazevedo.timegrid_backend.appointment.service;

import br.com.jhonnyazevedo.timegrid_backend.appointment.entity.Appointment;
import br.com.jhonnyazevedo.timegrid_backend.appointment.repository.AppointmentRepository;
import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.client.repository.ClientRepository;
import br.com.jhonnyazevedo.timegrid_backend.enums.TimeGrid;
import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private UUID userId;
    private UUID clientId;
    private UUID appointmentId;
    private User user;
    private Client client;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        client = new Client();
        client.setId(clientId);
        client.setUser(user);
    }

    @Test
    void createAppointment_shouldSaveAppointmentWhenDataIsValid() {
        Appointment appointment = createAppointmentRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(appointmentRepository.existsConflict(
                user,
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                null
        )).thenReturn(false);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        Appointment savedAppointment = appointmentService.createAppointment(userId, clientId, appointment);

        assertSame(user, savedAppointment.getUser());
        assertSame(client, savedAppointment.getClient());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void createAppointment_shouldThrowBusinessExceptionWhenDateIsPast() {
        Appointment appointment = createAppointmentRequest();
        appointment.setAppointmentDate(LocalDate.now().minusDays(1));

        assertThrows(
                BusinessException.class,
                () -> appointmentService.createAppointment(userId, clientId, appointment)
        );

        verify(appointmentRepository, never()).save(appointment);
    }

    @Test
    void createAppointment_shouldThrowBusinessExceptionWhenStartTimeIsAfterEndTime() {
        Appointment appointment = createAppointmentRequest();
        appointment.setStartTime(TimeGrid.T1000);
        appointment.setEndTime(TimeGrid.T0900);

        assertThrows(
                BusinessException.class,
                () -> appointmentService.createAppointment(userId, clientId, appointment)
        );

        verify(appointmentRepository, never()).save(appointment);
    }

    @Test
    void createAppointment_shouldThrowBusinessExceptionWhenClientDoesNotBelongToUser() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        client.setUser(anotherUser);
        Appointment appointment = createAppointmentRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        assertThrows(
                BusinessException.class,
                () -> appointmentService.createAppointment(userId, clientId, appointment)
        );

        verify(appointmentRepository, never()).save(appointment);
    }

    @Test
    void createAppointment_shouldThrowBusinessExceptionWhenScheduleConflicts() {
        Appointment appointment = createAppointmentRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(appointmentRepository.existsConflict(
                user,
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                null
        )).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> appointmentService.createAppointment(userId, clientId, appointment)
        );

        verify(appointmentRepository, never()).save(appointment);
    }

    @Test
    void updateAppointment_shouldUpdateOnlyEndTimeAndService() {
        Appointment existingAppointment = createExistingAppointment();
        Appointment updateRequest = new Appointment();
        updateRequest.setId(appointmentId);
        updateRequest.setEndTime(TimeGrid.T1030);
        updateRequest.setService("Corte e barba");

        LocalDate originalDate = existingAppointment.getAppointmentDate();
        TimeGrid originalStartTime = existingAppointment.getStartTime();
        Client originalClient = existingAppointment.getClient();
        User originalUser = existingAppointment.getUser();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.existsConflict(
                originalUser,
                originalDate,
                originalStartTime,
                updateRequest.getEndTime(),
                appointmentId
        )).thenReturn(false);
        when(appointmentRepository.save(existingAppointment)).thenReturn(existingAppointment);

        Appointment updatedAppointment = appointmentService.updateAppointment(userId, updateRequest);

        assertEquals(TimeGrid.T1030, updatedAppointment.getEndTime());
        assertEquals("Corte e barba", updatedAppointment.getService());
        assertEquals(originalDate, updatedAppointment.getAppointmentDate());
        assertEquals(originalStartTime, updatedAppointment.getStartTime());
        assertSame(originalClient, updatedAppointment.getClient());
        assertSame(originalUser, updatedAppointment.getUser());
        verify(appointmentRepository).save(existingAppointment);
    }

    @Test
    void deleteAppointment_shouldThrowBusinessExceptionWhenAppointmentDoesNotBelongToUser() {
        Appointment appointment = createExistingAppointment();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(
                BusinessException.class,
                () -> appointmentService.deleteAppointment(UUID.randomUUID(), appointmentId)
        );

        verify(appointmentRepository, never()).delete(appointment);
    }

    private Appointment createAppointmentRequest() {
        Appointment appointment = new Appointment();
        appointment.setService("Corte de cabelo");
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setStartTime(TimeGrid.T0900);
        appointment.setEndTime(TimeGrid.T0930);
        return appointment;
    }

    private Appointment createExistingAppointment() {
        Appointment appointment = createAppointmentRequest();
        appointment.setId(appointmentId);
        appointment.setUser(user);
        appointment.setClient(client);
        return appointment;
    }
}
