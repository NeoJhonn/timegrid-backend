package br.com.jhonnyazevedo.timegrid_backend.user.service;

import br.com.jhonnyazevedo.timegrid_backend.enums.UserRole;
import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setUsername("john_manager");
        user.setEmail("john.manager@timegrid.test");
        user.setPassword("123456");
        user.setRole(UserRole.MANAGER);
        user.setActive(true);
    }

    @Test
    void createUser_shouldSaveUserWhenDataIsValid() {
        User request = createUserRequest();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.save(request)).thenReturn(request);

        User savedUser = userService.createUser(request);

        assertEquals(true, savedUser.getActive());
        verify(userRepository).save(request);
    }

    @Test
    void createUser_shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
        User request = createUserRequest();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository, never()).save(request);
    }

    @Test
    void createUser_shouldThrowBusinessExceptionWhenUsernameAlreadyExists() {
        User request = createUserRequest();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository, never()).save(request);
    }

    @Test
    void findById_shouldReturnUserWhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.findById(userId);

        assertSame(user, foundUser);
    }

    @Test
    void findById_shouldThrowBusinessExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> userService.findById(userId)
        );
    }

    @Test
    void listUsers_shouldReturnOnlyActiveUsers() {
        List<User> activeUsers = List.of(user);

        when(userRepository.findByActiveTrue()).thenReturn(activeUsers);

        List<User> result = userService.listUsers();

        assertEquals(activeUsers, result);
        verify(userRepository).findByActiveTrue();
    }

    @Test
    void updateUser_shouldUpdateUserWhenDataIsValid() {
        User request = createUserRequest();
        request.setUsername("john_updated");
        request.setEmail("john.updated@timegrid.test");
        request.setPassword("654321");
        request.setRole(UserRole.ADMIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUser(userId, request);

        assertEquals("john_updated", updatedUser.getUsername());
        assertEquals("john.updated@timegrid.test", updatedUser.getEmail());
        assertEquals("654321", updatedUser.getPassword());
        assertEquals(UserRole.ADMIN, updatedUser.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_shouldThrowBusinessExceptionWhenUsernameIsBlank() {
        User request = createUserRequest();
        request.setUsername(" ");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(
                BusinessException.class,
                () -> userService.updateUser(userId, request)
        );

        verify(userRepository, never()).save(user);
    }

    @Test
    void updateUser_shouldThrowBusinessExceptionWhenEmailAlreadyExistsForAnotherUser() {
        User request = createUserRequest();
        request.setEmail("duplicated@timegrid.test");

        User anotherUser = createUserRequest();
        anotherUser.setId(UUID.randomUUID());
        anotherUser.setEmail(request.getEmail());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(anotherUser));

        assertThrows(
                BusinessException.class,
                () -> userService.updateUser(userId, request)
        );

        verify(userRepository, never()).save(user);
    }

    @Test
    void updateUser_shouldThrowBusinessExceptionWhenUsernameAlreadyExistsForAnotherUser() {
        User request = createUserRequest();
        request.setUsername("duplicated_username");

        User anotherUser = createUserRequest();
        anotherUser.setId(UUID.randomUUID());
        anotherUser.setUsername(request.getUsername());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(anotherUser));

        assertThrows(
                BusinessException.class,
                () -> userService.updateUser(userId, request)
        );

        verify(userRepository, never()).save(user);
    }

    @Test
    void deleteUser_shouldSetActiveFalse() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        assertFalse(user.getActive());
        verify(userRepository).save(user);
    }

    @Test
    void setActive_shouldUpdateActiveStatus() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.setActive(userId, false);

        assertFalse(user.getActive());
        verify(userRepository).save(user);
    }

    private User createUserRequest() {
        User request = new User();
        request.setUsername("john_manager");
        request.setEmail("john.manager@timegrid.test");
        request.setPassword("123456");
        request.setRole(UserRole.MANAGER);
        return request;
    }
}
