package br.com.jhonnyazevedo.timegrid_backend.user.service;

import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email já cadastrado.");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("Username já cadastrado.");
        }

        user.setActive(true);

        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findByActiveTrue();
    }

    @Override
    public User updateUser(UUID id, User user) {

        User existing = findById(id);

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new BusinessException("Username é obrigatório.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BusinessException("Email é obrigatório.");
        }

        userRepository.findByEmail(user.getEmail())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new BusinessException("Email já cadastrado.");
                });

        userRepository.findByUsername(user.getUsername())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new BusinessException("Username já cadastrado.");
                });

        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());
        existing.setRole(user.getRole());

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = findById(id);
        user.setActive(false);

        userRepository.save(user);
    }

    @Override
    public void setActive(UUID id, Boolean active) {
        User user = findById(id);
        user.setActive(active);
        userRepository.save(user);
    }
}
