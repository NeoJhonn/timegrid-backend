INSERT INTO users (id, username, email, password, role, active, created_at) VALUES
('11111111-1111-1111-1111-111111111111', 'john_manager', 'john.manager@timegrid.test', '123456', 'MANAGER', TRUE, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', 'ana_manager', 'ana.manager@timegrid.test', '123456', 'MANAGER', TRUE, CURRENT_TIMESTAMP),
('33333333-3333-3333-3333-333333333333', 'admin_timegrid', 'admin@timegrid.test', '123456', 'ADMIN', TRUE, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

INSERT INTO clients (id, name, phone, user_id, created_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Carlos Silva', '11999990001', '11111111-1111-1111-1111-111111111111', CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Marina Souza', '11999990002', '11111111-1111-1111-1111-111111111111', CURRENT_TIMESTAMP),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Pedro Santos', '11999990003', '22222222-2222-2222-2222-222222222222', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

INSERT INTO appointments (id, user_id, client_id, service, appointment_date, start_time, end_time, created_at) VALUES
('aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Corte de cabelo', '2026-08-10', 'T0900', 'T0930', CURRENT_TIMESTAMP),
('bbbbbbbb-1111-1111-1111-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Barba', '2026-08-10', 'T1030', 'T1100', CURRENT_TIMESTAMP),
('cccccccc-1111-1111-1111-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Corte e barba', '2026-08-12', 'T1400', 'T1500', CURRENT_TIMESTAMP),
('dddddddd-2222-2222-2222-dddddddddddd', '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Consulta inicial', '2026-08-11', 'T0830', 'T0930', CURRENT_TIMESTAMP),
('eeeeeeee-2222-2222-2222-eeeeeeeeeeee', '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Retorno', '2026-08-13', 'T1600', 'T1630', CURRENT_TIMESTAMP),
('ffffffff-1111-1111-1111-ffffffffffff', '11111111-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Finalizacao', '2026-08-15', 'T1800', 'T1830', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
