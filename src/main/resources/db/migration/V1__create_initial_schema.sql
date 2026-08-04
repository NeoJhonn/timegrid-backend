CREATE TABLE IF NOT EXISTS users (
    id UUID NOT NULL,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP(6),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS clients (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    user_id UUID,
    created_at TIMESTAMP(6),
    CONSTRAINT pk_clients PRIMARY KEY (id),
    CONSTRAINT fk_clients_users FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS appointments (
    id UUID NOT NULL,
    user_id UUID,
    client_id UUID,
    service VARCHAR(255) NOT NULL,
    appointment_date DATE NOT NULL,
    start_time VARCHAR(255) NOT NULL,
    end_time VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6),
    CONSTRAINT pk_appointments PRIMARY KEY (id),
    CONSTRAINT fk_appointments_users FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_appointments_clients FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT uk_appointments_user_date_start UNIQUE (user_id, appointment_date, start_time)
);
