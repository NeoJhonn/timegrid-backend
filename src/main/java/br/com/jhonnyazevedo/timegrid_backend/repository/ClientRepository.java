package br.com.jhonnyazevedo.timegrid_backend.repository;

import br.com.jhonnyazevedo.timegrid_backend.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    /**
     * Busca todos os clientes de um usuário
     * Garante que cada usuário veja apenas seus próprios clientes.
     */
    List<Client> findByUser(User user);

    /**
     * Verifica se já existe um cliente com o mesmo telefone para o usuário.
     *  Ajuda a evitar duplicidade de clientes.
     */
    boolean existsByUserAndPhone(User user, String phone);
}
