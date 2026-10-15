package br.com.jhonnyazevedo.timegrid_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.appointment.entity.Appointment;
import br.com.jhonnyazevedo.timegrid_backend.enums.UserRole;
import br.com.jhonnyazevedo.timegrid_backend.enums.TimeGrid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS,
		properties = "spring.flyway.enabled=false")
@ActiveProfiles("test")
class TimegridBackendApplicationTests {

	@Autowired
	private EntityManager entityManager;

	@Test
	void contextLoads() {
		assertEquals(ZoneId.of("America/Sao_Paulo"), ZoneId.systemDefault());
	}

	@Test
	@Transactional
	void shouldPersistCreationTimestampsInSaoPauloTime() {
		ZoneId zone = ZoneId.of("America/Sao_Paulo");
		LocalDateTime before = LocalDateTime.now(zone).minusSeconds(1);
		User user = new User();
		user.setUsername("timezone-test");
		user.setEmail("timezone-test@example.com");
		user.setPassword("test-password");
		user.setRole(UserRole.ADMIN);
		user.setActive(true);
		entityManager.persist(user);

		Client client = new Client();
		client.setName("Timezone client");
		client.setPhone("11999999999");
		client.setUser(user);
		entityManager.persist(client);

		Appointment appointment = new Appointment();
		appointment.setUser(user);
		appointment.setClient(client);
		appointment.setService("Timezone service");
		appointment.setAppointmentDate(LocalDate.now(zone));
		appointment.setStartTime(TimeGrid.T0900);
		appointment.setEndTime(TimeGrid.T1000);
		entityManager.persist(appointment);
		entityManager.flush();
		entityManager.clear();

		LocalDateTime after = LocalDateTime.now(zone).plusSeconds(1);
		for (LocalDateTime createdAt : new LocalDateTime[] {
				entityManager.find(User.class, user.getId()).getCreatedAt(),
				entityManager.find(Client.class, client.getId()).getCreatedAt(),
				entityManager.find(Appointment.class, appointment.getId()).getCreatedAt()}) {
			assertNotNull(createdAt);
			assertFalse(createdAt.isBefore(before));
			assertFalse(createdAt.isAfter(after));
		}
	}

}
