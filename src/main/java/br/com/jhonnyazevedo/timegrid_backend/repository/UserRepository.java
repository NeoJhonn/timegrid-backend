package br.com.jhonnyazevedo.timegrid_backend.repository;


import br.com.jhonnyazevedo.timegrid_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca um usuário pelo email.
     * Retorna um Optimal pois o usuário pode não existir
     * Vamos usar na parte de login
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca um usuário pelo username
     * Retorna um Optimal para evitar null e forçar tratamento
     */
    Optional<User> findByUsername(String username);

    /**
     * Verifica se já existe um usuário com o email informado
     * Vamos usar para validar antes de criar um novo usuário
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se já existe um usuário com o username informado.
     * Evidta duplicidade no sistema.
     */
    boolean existsByUsername(String username);
}
