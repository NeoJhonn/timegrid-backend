package br.com.jhonnyazevedo.timegrid_backend.user.service;

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
            throw new RuntimeException("Email já cadastrado.");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username já cadastrado.");
        }

        user.setActive(true);

        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID id, User user) {

        User existing = findById(id);

        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());
        existing.setRole(user.getRole());

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public void setActive(UUID id, Boolean active) {
        User user = findById(id);
        user.setActive(active);
        userRepository.save(user);
    }
}
