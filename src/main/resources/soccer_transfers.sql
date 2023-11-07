DROP DATABASE IF EXISTS soccer_transfers;

CREATE DATABASE soccer_transfers;

USE soccer_transfers;

CREATE TABLE league (
id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
league_name VARCHAR(30) NOT NULL,
code_name VARCHAR(15) NOT NULL,
country VARCHAR(30) NOT NULL
);

CREATE UNIQUE INDEX sigla_index ON league(code_name);

CREATE TABLE club (
id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
club_name VARCHAR(30) UNIQUE NOT NULL
);


CREATE TABLE club_league (
idclub INT NOT NULL,
idleague INT NOT NULL,
season YEAR NOT NULL,
FOREIGN KEY (idclub) REFERENCES club(id),
FOREIGN KEY (idleague) REFERENCES league(id),
PRIMARY KEY(idclub, idleague, season)
);

CREATE TABLE player(
id INT NOT NULL AUTO_INCREMENT,
player_name VARCHAR(30) NOT NULL,
age TINYINT,
position VARCHAR(30) NOT NULL,
market_value NUMERIC(10,2),
PRIMARY KEY(id)
);

CREATE TABLE transfer(
id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
id_player INT NOT NULL,
id_club_left INT NOT NULL,
id_club_joined INT NOT NULL,
transfer_fee NUMERIC(10,2),
transfer_season YEAR NOT NULL,
FOREIGN KEY (id_club_joined) REFERENCES club(id),
FOREIGN KEY (id_club_left) REFERENCES club(id),
FOREIGN KEY (id_player) REFERENCES player(id)
);
