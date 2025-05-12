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
    end_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'pendente',
    notes TEXT,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id)
);

drop table availability;

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


CREATE TABLE IF NOT EXISTS medical_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pet_id INT NOT NULL,
    record_date DATE NOT NULL,
    weight DECIMAL(5,2),
    temperature VARCHAR(10),
    symptoms TEXT,
    diagnosis TEXT,
    treatment TEXT,
    prescriptions TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(id)
);

-- Vaccine Records table
CREATE TABLE IF NOT EXISTS vaccine_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pet_id INT NOT NULL,
    vaccine_name VARCHAR(100) NOT NULL,
    application_date DATE NOT NULL,
    next_dose_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(id)
);

-- Notification Log table
CREATE TABLE IF NOT EXISTS notification_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reference_id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SMS Log table
CREATE TABLE IF NOT EXISTS sms_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    phone_number VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add price column to appointments table
ALTER TABLE appointments 
ADD COLUMN price DECIMAL(10,2) DEFAULT 0.0;

-- Add status tracking for appointments
ALTER TABLE appointments 
ADD COLUMN status VARCHAR(20) DEFAULT 'agendado';

-- Add reorder level for vaccine stock
ALTER TABLE vaccine_stock 
ADD COLUMN reorder_level INT DEFAULT 10;

-- Add indexes for better performance
CREATE INDEX idx_appointments_start_time ON appointments(start_time);
CREATE INDEX idx_vaccine_records_pet_id ON vaccine_records(pet_id);
CREATE INDEX idx_medical_records_pet_id ON medical_records(pet_id);
CREATE INDEX idx_notification_log_sent_at ON notification_log(sent_at);

CREATE TABLE app_config (
 id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
 admin_email VARCHAR(255) DEFAULT "admin@example.com",
 api_key VARCHAR(255) DEFAULT "SOME_API_KEY",
 true_option BOOLEAN DEFAULT true,
 false_option BOOLEAN DEFAULT false
);