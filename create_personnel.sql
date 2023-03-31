DROP DATABASE IF EXISTS personnel;

CREATE DATABASE personnel;

USE personnel;

CREATE TABLE employe (
  `id_employe` BIGINT NOT NULL AUTO_INCREMENT,
  `nom_employe` VARCHAR(50),
  `prenom_employe` VARCHAR(50),
  `password` VARCHAR(50),
  `mail` VARCHAR(100),
  `date_arrivee` DATE,
  `date_depart` DATE,
  `id_ligue` BIGINT,
  PRIMARY KEY (`id_employe`)
) ENGINE = InnoDB DEFAULT CHARSET = UTF8MB4;

CREATE TABLE ligue (
  `id_ligue` BIGINT NOT NULL AUTO_INCREMENT,
  `nom_ligue` VARCHAR(42),
  `administrateur` BIGINT,
  PRIMARY KEY (`id_ligue`)
) ENGINE = InnoDB DEFAULT CHARSET = UTF8MB4;

ALTER TABLE
  `employe`
ADD
  CONSTRAINT `FK_employe_ligue` FOREIGN KEY (`id_ligue`) REFERENCES `ligue` (`id_ligue`) ON DELETE CASCADE;

ALTER TABLE
  `ligue`
ADD
  FOREIGN KEY (`administrateur`) REFERENCES `employe` (`id_employe`);

GRANT ALL PRIVILEGES ON personnel TO passerelle @localhost;