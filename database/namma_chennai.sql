CREATE DATABASE namma_chennai;

USE namma_chennai;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15)
);

CREATE TABLE stations (
    station_id INT AUTO_INCREMENT PRIMARY KEY,
    station_name VARCHAR(100) NOT NULL
);

CREATE TABLE routes (
    route_id INT AUTO_INCREMENT PRIMARY KEY,
    route_name VARCHAR(100) NOT NULL,
    transport_type VARCHAR(20) NOT NULL
);

CREATE TABLE route_stops (
    route_id INT,
    station_id INT,
    stop_order INT,
    PRIMARY KEY (route_id, stop_order),
    FOREIGN KEY (route_id) REFERENCES routes(route_id),
    FOREIGN KEY (station_id) REFERENCES stations(station_id)
);

CREATE TABLE transport_bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    route_id INT NOT NULL,
    source_id INT NOT NULL,
    destination_id INT NOT NULL,
    direction VARCHAR(10) NOT NULL,
    stops_count INT NOT NULL,
    ticket_count INT NOT NULL,
    original_fare DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_fare DECIMAL(10,2) NOT NULL,
    booking_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (route_id) REFERENCES routes(route_id),
    FOREIGN KEY (source_id) REFERENCES stations(station_id),
    FOREIGN KEY (destination_id) REFERENCES stations(station_id)
);

CREATE TABLE attractions (
    attraction_id INT AUTO_INCREMENT PRIMARY KEY,
    attraction_name VARCHAR(100),
    location VARCHAR(100),
    ticket_price DECIMAL(10,2)
);

CREATE TABLE attraction_bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    attraction_id INT NOT NULL,
    visit_date DATE,
    ticket_count INT NOT NULL,
    original_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    booking_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (attraction_id) REFERENCES attractions(attraction_id)
);

CREATE TABLE student_passes (
    pass_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    college_name VARCHAR(100) NOT NULL,
    student_id VARCHAR(50) NOT NULL,
    discount_percent INT NOT NULL DEFAULT 20,
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE saved_journeys (
    journey_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    journey_name VARCHAR(100) NOT NULL,
    source_id INT NOT NULL,
    destination_id INT NOT NULL,
    route_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (source_id) REFERENCES stations(station_id),
    FOREIGN KEY (destination_id) REFERENCES stations(station_id),
    FOREIGN KEY (route_id) REFERENCES routes(route_id)
);

SHOW TABLES;

INSERT INTO stations (station_id, station_name) VALUES
(1, 'Chennai Central'),
(2, 'LIC'),
(3, 'Saidapet'),
(4, 'Guindy'),
(5, 'Airport'),
(6, 'Egmore'),
(7, 'Shenoy Nagar'),
(8, 'Anna Nagar East'),
(9, 'Anna Nagar Tower'),
(10, 'CMBT'),
(11, 'Vadapalani'),
(12, 'Ashok Nagar'),
(13, 'Alandur'),
(14, 'Poonamallee'),
(15, 'Porur Junction'),
(16, 'Valasaravakkam');

INSERT INTO routes (route_id, route_name, transport_type) VALUES
(1, 'Blue Line', 'Metro'),
(2, 'Green Line', 'Metro'),
(3, 'Yellow Line', 'Metro');

INSERT INTO route_stops (route_id, station_id, stop_order) VALUES

-- Blue Line
(1, 1, 1),
(1, 2, 2),
(1, 3, 3),
(1, 4, 4),
(1, 5, 5),

-- Green Line
(2, 1, 1),
(2, 6, 2),
(2, 7, 3),
(2, 8, 4),
(2, 9, 5),
(2, 10, 6),
(2, 11, 7),
(2, 12, 8),
(2, 13, 9),

-- Yellow Line
(3, 14, 1),
(3, 15, 2),
(3, 16, 3),
(3, 11, 4);

INSERT INTO routes (route_id, route_name, transport_type) VALUES
(4, '25', 'Bus'),
(5, '27D', 'Bus'),
(6, '17D', 'Bus'),
(7, '27B', 'Bus'),
(8, '47D', 'Bus'),
(9, 'A1', 'Bus');

INSERT INTO stations (station_id, station_name) VALUES
(17, 'Anna Square'),
(18, 'Vivekananda House'),
(19, 'Royapettah Hospital'),
(20, 'Gemini'),
(21, 'Veterinary Hospital'),
(22, 'Trustpuram'),
(23, 'Virugambakkam'),
(24, 'Porur'),
(25, 'Iyyappanthangal'),
(26, 'Kumananchavadi'),
(27, 'Foreshore Estate'),
(28, 'Queen Mary''s College (QMC)'),
(29, 'V.M. Street'),
(30, 'U.S.I.S.'),
(31, 'TVS'),
(32, 'Anna Road Post Office'),
(33, 'Dharmaprakash'),
(34, 'Kelleys'),
(35, 'Ayanavaram'),
(36, 'Kambar Arangam'),
(37, 'Villivakkam'),
(38, 'Island Ground'),
(39, 'M.G.R. Central'),
(40, 'Maternity Hospital'),
(41, 'DPI'),
(42, 'Sterling Road'),
(43, 'Pudur High School'),
(44, 'P.T.R. Road / Kamaraj Road Junction'),
(45, 'Muthumariamman Koil'),
(46, 'K.K. Nagar'),
(47, 'Arumbakkam'),
(48, 'Naduvankarai'),
(49, 'Aminjikarai'),
(50, 'Taylors Road'),
(51, 'T.B. Institute'),
(52, 'Periyar Bridge'),
(53, 'Triplicane'),
(54, 'Thiruvanmiyur'),
(55, 'Adyar Depot'),
(56, 'Adyar Bus Stand'),
(57, 'Engineering College'),
(58, 'Concorde'),
(59, 'T. Nagar'),
(60, 'Vidyodaya'),
(61, 'Nungambakkam Railway Station'),
(62, 'Anna Nagar Circle'),
(63, 'Thirumangalam'),
(64, 'Ambattur Estate'),
(65, 'Ambattur O.T.'),
(66, 'Avadi'),
(67, 'Tirusulam'),
(68, 'Pallavaram'),
(69, 'Chromepet'),
(70, 'Tambaram Sanatorium'),
(71, 'Tambaram'),
(72, 'Irumbuliyur'),
(73, 'Perungalathur'),
(74, 'Vandalur Gate'),
(75, 'Vandalur Zoo'),
(76, 'Kilambakkam');

INSERT INTO route_stops (route_id, station_id, stop_order) VALUES
-- Route 25
(4,17,1),(4,18,2),(4,19,3),(4,20,4),
(4,21,5),(4,22,6),(4,11,7),(4,23,8),
(4,16,9),(4,24,10),(4,25,11),(4,26,12),
(4,14,13),

-- Route 27D
(5,27,1),(5,28,2),(5,29,3),(5,30,4),
(5,31,5),(5,32,6),(5,6,7),(5,33,8),
(5,34,9),(5,35,10),(5,36,11),(5,37,12),

-- Route 17D
(6,38,1),(6,39,2),(6,6,3),(6,40,4),
(6,41,5),(6,42,6),(6,21,7),(6,22,8),
(6,43,9),(6,44,10),(6,45,11),(6,46,12),

-- Route 27B
(7,10,1),(7,47,2),(7,48,3),(7,49,4),
(7,50,5),(7,51,6),(7,6,7),(7,52,8),
(7,53,9),(7,17,10),

-- Route 47D
(8,54,1),(8,55,2),(8,56,3),(8,57,4),
(8,58,5),(8,3,6),(8,59,7),(8,60,8),
(8,42,9),(8,61,10),(8,62,11),(8,63,12),
(8,64,13),(8,65,14),(8,66,15),

-- Route A1
(9,5,1),(9,67,2),(9,68,3),(9,69,4),
(9,70,5),(9,71,6),(9,72,7),(9,73,8),
(9,74,9),(9,75,10),(9,76,11);

INSERT INTO routes (route_id, route_name, transport_type) VALUES
(10, 'MRTS Beach - Velachery', 'Train'),
(11, 'Suburban Central - Avadi', 'Train'),
(12, 'Suburban Beach - Vandalur', 'Train');

INSERT INTO stations (station_id, station_name) VALUES
(77, 'Chennai Beach'),
(78, 'Chennai Fort'),
(79, 'Park Town'),
(80, 'Chepauk'),
(81, 'Light House'),
(82, 'Thirumayilai'),
(83, 'Mandaveli'),
(84, 'Greenways Road'),
(85, 'Kotturpuram'),
(86, 'Kasturba Nagar'),
(87, 'Velachery'),
(88, 'Basin Bridge'),
(89, 'Vyasarpadi Jeeva'),
(90, 'Perambur'),
(91, 'Perambur Carriage Works'),
(92, 'Perambur Loco Works'),
(93, 'Korattur'),
(94, 'Pattaravakkam'),
(95, 'Ambattur'),
(96, 'Chennai Park'),
(97, 'Nungambakkam'),
(98, 'Kodambakkam'),
(99, 'Mambalam'),
(100, 'St. Thomas Mount');

INSERT INTO route_stops (route_id, station_id, stop_order) VALUES

-- MRTS: Chennai Beach to Velachery
(10,77,1),
(10,78,2),
(10,79,3),
(10,80,4),
(10,81,5),
(10,82,6),
(10,83,7),
(10,84,8),
(10,85,9),
(10,86,10),
(10,54,11),
(10,87,12),

-- Suburban West: Chennai Central to Avadi
(11,1,1),
(11,88,2),
(11,89,3),
(11,90,4),
(11,91,5),
(11,92,6),
(11,37,7),
(11,93,8),
(11,94,9),
(11,95,10),
(11,66,11),

-- Suburban South: Chennai Beach to Vandalur
(12,77,1),
(12,78,2),
(12,96,3),
(12,6,4),
(12,97,5),
(12,98,6),
(12,99,7),
(12,3,8),
(12,4,9),
(12,100,10),
(12,68,11),
(12,71,12),
(12,75,13);

INSERT INTO attractions
(attraction_id, attraction_name, location, ticket_price)
VALUES
(1, 'Vandalur Zoo', 'Vandalur', 100.00),
(2, 'Valluvar Kottam', 'Nungambakkam', 20.00),
(3, 'Anna Nagar Tower', 'Anna Nagar', 0.00),
(4, 'Chennai Rail Museum', 'ICF, Chennai', 50.00),
(5, 'Marina Beach', 'Marina', 0.00),
(6, 'Government Museum, Egmore', 'Egmore', 50.00);

SELECT * FROM stations;
SELECT * FROM routes;
SELECT * FROM route_stops;
SELECT * FROM attractions;

SELECT r.route_name, r.transport_type, COUNT(*) AS total_stops
FROM routes r
JOIN route_stops rs ON r.route_id = rs.route_id
GROUP BY r.route_id, r.route_name, r.transport_type
ORDER BY r.route_id;