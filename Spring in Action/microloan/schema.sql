CREATE TABLE applicants (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    monthly_income NUMERIC(10,2) NOT NULL
);

CREATE TABLE loan_applications (
    id SERIAL PRIMARY KEY,
    applicant_id INTEGER NOT NULL REFERENCES applicants(id),
    amount NUMERIC(10,2) NOT NULL CHECK (amount >= 500 AND amount <= 15000),
    term_months INTEGER NOT NULL CHECK (term_months >= 3 AND term_months <= 36),
    purpose VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE status_history (
    id SERIAL PRIMARY KEY,
    application_id INTEGER NOT NULL REFERENCES loan_applications(id),
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_loan_applications_status ON loan_applications(status);

-- Sample data below, just kept here so it's visible in the repo, not meant to run again

INSERT INTO applicants (full_name, email, monthly_income) VALUES
('Marta Iglesias Roca', 'marta.iglesias@gmail.com', 1450.00),
('Jordi Ferrer Puig', 'jordi.ferrer@hotmail.com', 2100.00),
('Nuria Campos Vidal', 'nuria.campos@yahoo.es', 980.00),
('Rafael Ortega Muñoz', 'rafa.ortega77@gmail.com', 1650.00),
('Carla Sans Bosch', 'carla.sans@gmail.com', 1200.00),
('Adrian Villalba Cano', 'a.villalba@outlook.com', 2450.00),
('Elena Prat Font', 'elena.prat@gmail.com', 1100.00),
('Miquel Rovira Solà', 'mrovira@gmail.com', 1800.00),
('Sofia Nogueira Rey', 'sofia.nogueira@gmail.com', 1350.00),
('Pau Escudero Marí', 'pau.escudero@hotmail.com', 900.00),
('Laia Montoro Ferrando', 'laia.montoro@gmail.com', 2200.00),
('Ignacio Reyes Cabrera', 'nachoreyes@gmail.com', 1500.00),
('Blanca Serra Aymerich', 'blanca.serra@yahoo.es', 1050.00),
('Oriol Bassa Camps', 'oriol.bassa@gmail.com', 1950.00),
('Teresa Galán Uceda', 'teresa.galan@gmail.com', 1300.00);

INSERT INTO loan_applications (applicant_id, amount, term_months, purpose, status, created_at) VALUES
(1, 3000.00, 12, 'Reparación coche', 'APPROVED', '2026-06-02'),
(1, 6500.00, 24, 'Reforma cocina', 'REJECTED', '2026-07-15'),
(2, 8000.00, 36, 'Boda', 'APPROVED', '2026-05-20'),
(3, 900.00, 6, 'Fianza piso', 'APPROVED', '2026-08-01'),
(3, 4000.00, 18, 'Matrícula universidad', 'UNDER_REVIEW', '2026-08-20'),
(4, 6600.00, 24, 'Lavadora y nevera', 'REJECTED', '2026-04-10'),
(5, 4800.00, 12, 'Viaje familiar', 'APPROVED', '2026-06-28'),
(6, 9800.00, 36, 'Coche de segunda mano', 'APPROVED', '2026-03-05'),
(6, 2000.00, 6, 'Dentista', 'SUBMITTED', '2026-08-22'),
(7, 4400.00, 24, 'Deudas tarjeta', 'REJECTED', '2026-05-30'),
(8, 7200.00, 30, 'Reforma baño', 'APPROVED', '2026-02-14'),
(9, 5400.00, 12, 'Fianza y mudanza', 'UNDER_REVIEW', '2026-08-18'),
(10, 3600.00, 18, 'Motocicleta', 'APPROVED', '2026-01-22'),
(10, 900.00, 3, 'Factura urgente', 'DRAFT', '2026-08-25'),
(11, 8800.00, 36, 'Consolidación de deudas', 'APPROVED', '2026-04-01'),
(12, 6000.00, 24, 'Estudios hijo', 'UNDER_REVIEW', '2026-07-30'),
(13, 4200.00, 12, 'Reparación tejado', 'APPROVED', '2026-03-18'),
(14, 7800.00, 30, 'Placas solares', 'APPROVED', '2026-05-09'),
(14, 2500.00, 12, 'Ordenador portátil', 'SUBMITTED', '2026-08-21'),
(15, 5200.00, 18, 'Coche familiar', 'REJECTED', '2026-06-11'),
(2, 3300.00, 12, 'Viaje de aniversario', 'APPROVED', '2026-07-02'),
(9, 6800.00, 24, 'Reforma salón', 'REJECTED', '2026-02-25'),
(5, 1800.00, 6, 'Gastos médicos', 'DRAFT', '2026-08-24'),
(11, 4000.00, 12, 'Muebles casa nueva', 'APPROVED', '2026-06-15'),
(7, 3000.00, 12, 'Reparación electrodomésticos', 'APPROVED', '2026-01-10');

-- Practice queries below, kept for reference, not run by the app


SELECT * FROM loan_applications
WHERE status = 'APPROVED' AND amount > 5000
ORDER BY created_at DESC;


SELECT status, SUM(amount) AS total_amount
FROM loan_applications
GROUP BY status;


SELECT a.full_name, COUNT(la.id) AS total_applications
FROM applicants a
LEFT JOIN loan_applications la ON a.id = la.applicant_id
GROUP BY a.id, a.full_name
ORDER BY total_applications DESC;


SELECT a.full_name, SUM(la.amount) AS total_approved
FROM applicants a
JOIN loan_applications la ON a.id = la.applicant_id
WHERE la.status = 'APPROVED'
GROUP BY a.id, a.full_name
ORDER BY total_approved DESC
LIMIT 1;


SELECT la.id, a.full_name, la.amount, a.monthly_income
FROM loan_applications la
JOIN applicants a ON la.applicant_id = a.id
WHERE la.amount > a.monthly_income * 4;