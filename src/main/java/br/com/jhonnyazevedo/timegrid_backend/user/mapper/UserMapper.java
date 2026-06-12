package br.com.jhonnyazevedo.timegrid_backend.user.mapper;

import br.com.jhonnyazevedo.timegrid_backend.user.dto.UserRequest;
import br.com.jhonnyazevedo.timegrid_backend.user.dto.UserResponse;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole(request.role());
        return user;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getActive(),
                user.getCreatedAt()
        );
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .toList();
    }
}