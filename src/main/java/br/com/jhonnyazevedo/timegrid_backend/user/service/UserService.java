package br.com.jhonnyazevedo.timegrid_backend.user.service;

import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User createUser(User user);

    User findById(UUID id);

    List<User> listUsers();

    User updateUser(UUID id, User user);

    void deleteUser(UUID id);

    void setActive(UUID id, Boolean active);
}