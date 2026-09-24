package br.com.jhonnyazevedo.timegrid_backend.user.controller;

import br.com.jhonnyazevedo.timegrid_backend.user.dto.UserRequest;
import br.com.jhonnyazevedo.timegrid_backend.user.dto.UserResponse;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.user.mapper.UserMapper;
import br.com.jhonnyazevedo.timegrid_backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints para gerenciamento de usuarios")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @Operation(summary = "Cria usuario", description = "Cria um novo usuario. Endpoint restrito a usuarios com role MANAGER.")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest request) {
        User user = userMapper.toEntity(request);
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(createdUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca usuario por ID", description = "Retorna os dados de um usuario pelo seu identificador.")
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @GetMapping
    @Operation(summary = "Lista usuarios ativos", description = "Retorna a lista de usuarios ativos cadastrados no sistema.")
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<User> users = userService.listUsers();
        return ResponseEntity.ok(userMapper.toResponseList(users));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza usuario", description = "Atualiza username, email, senha e role de um usuario existente.")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UserRequest request
    ) {
        User user = userMapper.toEntity(request);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativa usuario", description = "Realiza soft delete do usuario, alterando o campo active para false.")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/active")
    @Operation(summary = "Altera status do usuario", description = "Ativa ou desativa um usuario pelo parametro active.")
    public ResponseEntity<Void> setActive(
            @PathVariable UUID id,
            @RequestParam Boolean active
    ) {
        userService.setActive(id, active);
        return ResponseEntity.noContent().build();
    }
}
