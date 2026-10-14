INSERT INTO users (id, username, email, password, role, active, created_at) VALUES
('11111111-1111-1111-1111-111111111111', 'Jhonny Alves de Souza Azevedo', 'jhonny.azevedo@gmail.com', '$2a$10$kSnm41lW0A.jl/OX9SjRfeRIqpDkdrszq5/gGQ8o9Cb7J6Uox1KIu', 'MANAGER', TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
