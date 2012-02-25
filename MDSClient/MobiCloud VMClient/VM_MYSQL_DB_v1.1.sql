SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `mds_db` DEFAULT CHARACTER SET latin1 ;
USE `mds_db` ;

-- -----------------------------------------------------
-- Table `mds_db`.`T_Entity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mds_db`.`T_Entity` ;

CREATE  TABLE IF NOT EXISTS `mds_db`.`T_Entity` (
  `E_id` INT(11) NOT NULL AUTO_INCREMENT ,
  `E_type` CHAR(8) NOT NULL ,
  `E_name` CHAR(64) NOT NULL ,
  `E_size` BIGINT(20) NOT NULL ,
  `E_url` TEXT NOT NULL ,
  `E_modate` DATETIME NOT NULL ,
  PRIMARY KEY (`E_id`) )
ENGINE = MyISAM
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mds_db`.`T_GroupPermit`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mds_db`.`T_GroupPermit` ;

CREATE  TABLE IF NOT EXISTS `mds_db`.`T_GroupPermit` (
  `GP_id` INT(11) NOT NULL AUTO_INCREMENT ,
  `E_id` INT(11) NULL DEFAULT NULL ,
  `GP_permission` INT(11) NOT NULL ,
  `G_id` TEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`GP_id`) ,
  INDEX `FK_Entity_R_GroupPermit` (`E_id` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mds_db`.`T_Messages`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mds_db`.`T_Messages` ;

CREATE  TABLE IF NOT EXISTS `mds_db`.`T_Messages` (
  `U_id` INT(11) NOT NULL ,
  `messageBody` TEXT NOT NULL ,
  `isRead` TINYINT(1) NOT NULL ,
  `Date` DATE NOT NULL )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mds_db`.`T_UserPermit`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mds_db`.`T_UserPermit` ;

CREATE  TABLE IF NOT EXISTS `mds_db`.`T_UserPermit` (
  `UP_id` INT(11) NOT NULL AUTO_INCREMENT ,
  `E_id` INT(11) NULL DEFAULT NULL ,
  `UP_permission` INT(11) NOT NULL ,
  `U_id` TEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`UP_id`) ,
  INDEX `FK_Entity_R_UserPermit` (`E_id` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = latin1;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
