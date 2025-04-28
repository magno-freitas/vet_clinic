CREATE DATABASE vet_clinic;
USE vet_clinic;

-- Tabela de Clientes
CREATE TABLE clients (
    client_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255)
);

-- Tabela de Pets
CREATE TABLE pets (
    pet_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT,
    name VARCHAR(255) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(50),
    birth_date DATE,
    FOREIGN KEY (client_id) REFERENCES clients(client_id)
);

-- Tabela de Disponibilidade (Agenda)
CREATE TABLE availability (
    slot_id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_available BOOLEAN DEFAULT TRUE
);

-- Tabela de Agendamentos
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    service VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'pendente',
    notes TEXT,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id)
);

drop table appointments;

-- Tabela de Usuários
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Tabela de Recursos da Clínica
CREATE TABLE resources (
    resource_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    availability BOOLEAN DEFAULT TRUE
);

-- Tabela de Notificações
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT,
    message TEXT,
    sent_time DATETIME,
    FOREIGN KEY (client_id) REFERENCES clients(client_id)
);

-- Tabela de Estoque de Vacinas
CREATE TABLE vaccine_stock (
    vaccine_id INT AUTO_INCREMENT PRIMARY KEY,
    vaccine_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL
);

-- Tabela de Logs de Auditoria
CREATE TABLE audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    timestamp DATETIME NOT NULL
);

DELIMITER //

CREATE PROCEDURE generate_availability(startDate DATE, endDate DATE)
BEGIN
    DECLARE currentDate DATE;
    DECLARE startTime TIME;
    DECLARE endTime TIME;
    
    SET currentDate = startDate;

    WHILE currentDate <= endDate DO
        SET startTime = '09:00:00';
        
        WHILE startTime < '17:00:00' DO
            SET endTime = ADDTIME(startTime, '01:00:00');
            INSERT INTO availability (date, start_time, end_time, is_available) VALUES (currentDate, startTime, endTime, TRUE);
            SET startTime = endTime;
        END WHILE;
        
        SET currentDate = DATE_ADD(currentDate, INTERVAL 1 DAY);
    END WHILE;
END //

DELIMITER ;