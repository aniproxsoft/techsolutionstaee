-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 16-06-2019 a las 22:51:38
-- Versión del servidor: 5.7.19
-- Versión de PHP: 5.6.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `taeg_bd`
--

DELIMITER $$
--
-- Procedimientos
--
DROP PROCEDURE IF EXISTS `sp_autentification`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_autentification` (IN `user` VARCHAR(100), IN `pass` VARCHAR(100))  BEGIN
    DECLARE flag boolean;
    DECLARE count int;
    DROP TABLE IF EXISTS temp_usuario;
    
    

    SELECT COUNT(*) FROM usuario as us
    WHERE (us.password=MD5(pass) AND us.email=user) INTO count;

    
    IF(count>0)THEN
        set flag=true;
        CREATE TEMPORARY TABLE temp_usuario AS
        SELECT '0' as flag,user.id_rol, user.nombre, user.apellidos, user.email,rol.rol_nombre as nombre_rol,
            company.nombre as nombre_empresa, company.direccion,edo.nombre_estado,
            city.nombre_ciudad,company.codigo_postal,company.num_telefono,company.folio_convenio,company.rfc,
            (case WHEN company.status=1 THEN 'Activa' 
            WHEN company.status=0 THEN 'Baja'
            WHEN company.status=3 THEN 'Por Aprobar' 
            END)as status
        from usuario as user
        INNER JOIN usuario_rol as rol ON user.id_rol= rol.id_rol
        LEFT JOIN empresa as company ON company.id_usuario=user.id_usuario
        LEFT JOIN estado as edo ON edo.id_estado=  company.id_estado
        LEFT JOIN ciudad as city ON city.id_ciudad=company.id_ciudad and edo.id_estado=city.id_estado
        WHERE (user.password=MD5(pass) AND user.email=email);
        UPDATE temp_usuario set
        flag='1'
        WHERE email=user;

        
    ELSE
        set flag= false;
        CREATE TEMPORARY TABLE temp_usuario AS
        SELECT flag,null as id_rol,null as nombre,null as apellidos,null as email,
        null as nombre_rol,null as nombre_empresa,null as direccion,null as nombre_estado,
        null as nombre_ciudad,null as codigo_postal,null as num_telefono,null as folio_convenio,
        null as rfc,null as status;
    END if;
    SELECT * FROM temp_usuario ;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bulkload_empresa`
--

DROP TABLE IF EXISTS `bulkload_empresa`;
CREATE TABLE IF NOT EXISTS `bulkload_empresa` (
  `lote` int(11) NOT NULL,
  `id_bulkload` int(11) NOT NULL AUTO_INCREMENT,
  `direccion` varchar(200) DEFAULT NULL,
  `nombre` varchar(200) DEFAULT NULL,
  `estado` varchar(200) DEFAULT NULL,
  `ciudad` varchar(200) DEFAULT NULL,
  `codigo_postal` varchar(200) DEFAULT NULL,
  `usuario` varchar(200) DEFAULT NULL,
  `num_telefono` varchar(200) DEFAULT NULL,
  `folio_convenio` varchar(200) DEFAULT NULL,
  `rfc` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`lote`,`id_bulkload`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `carreras`
--

DROP TABLE IF EXISTS `carreras`;
CREATE TABLE IF NOT EXISTS `carreras` (
  `id_carrera` int(11) NOT NULL AUTO_INCREMENT,
  `id_nivel` int(11) DEFAULT NULL,
  `carrera_desc` varchar(100) NOT NULL,
  PRIMARY KEY (`id_carrera`),
  KEY `fk_id_nivel_carrera` (`id_nivel`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ciudad`
--

DROP TABLE IF EXISTS `ciudad`;
CREATE TABLE IF NOT EXISTS `ciudad` (
  `id_ciudad` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_ciudad` varchar(50) DEFAULT NULL,
  `id_estado` int(11) NOT NULL,
  PRIMARY KEY (`id_ciudad`,`id_estado`),
  KEY `fk_estado_ciudad` (`id_estado`)
) ENGINE=MyISAM AUTO_INCREMENT=41 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `ciudad`
--

INSERT INTO `ciudad` (`id_ciudad`, `nombre_ciudad`, `id_estado`) VALUES
(1, 'Acambay', 1),
(1, 'Alvaro Obregon', 2),
(2, 'Acolman', 1),
(2, 'Benito Juarez', 2),
(3, 'Aculco', 1),
(3, 'Coyoacan', 2),
(4, 'Almoloya de Alquisiras', 1),
(4, 'Cuajimalpa', 2),
(5, 'Amecameca', 1),
(5, 'Cuauhtemoc', 2),
(6, 'Cuautitlan', 1),
(6, 'Gustavo A. Madero', 2),
(7, 'Ecatepec de Morelos', 1),
(7, 'Iztacalco', 2),
(8, 'Ixtapaluca', 1),
(8, 'Santa Fe', 2),
(9, 'Jilotepec', 1),
(9, 'Polanco', 2),
(10, 'Morelos', 1),
(10, 'Chapultepec', 2),
(11, 'Nezahualcoyotl', 1),
(12, 'Ozumba', 1),
(13, 'Tejupilco', 1),
(14, 'Valle de Bravo', 1),
(15, ' Zumpango', 1),
(16, 'Cuautitlan Izcalli', 1),
(17, 'Toluca', 1),
(18, 'Tonatico', 1),
(19, 'Almoloya de Juarez', 1),
(20, 'Amanalco', 1),
(21, 'Axapusco', 1),
(22, 'Coyotepec', 1),
(23, 'Chalco', 1),
(24, 'Chapa de Mota', 1),
(25, 'Chimalhuacan', 1),
(26, 'Donato Guerra', 1),
(27, 'Huixquilucan', 1),
(28, 'Ixtlahuaca', 1),
(29, 'Xalatlaco', 1),
(30, 'Lerma', 1),
(31, 'Metepec', 1),
(32, 'Ocoyoacac', 1),
(33, 'El Oro', 1),
(34, 'Otumba', 1),
(35, 'Ozumba', 1),
(36, 'Papalotla', 1),
(37, 'Polotitlan', 1),
(38, 'Temoaya', 1),
(39, 'Tenancingo', 1),
(40, 'Texcoco', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `conocimiento`
--

DROP TABLE IF EXISTS `conocimiento`;
CREATE TABLE IF NOT EXISTS `conocimiento` (
  `id_conocimiento` int(11) NOT NULL,
  `conoc_desc` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_conocimiento`),
  KEY `not null` (`conoc_desc`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `conocimiento_vac`
--

DROP TABLE IF EXISTS `conocimiento_vac`;
CREATE TABLE IF NOT EXISTS `conocimiento_vac` (
  `id_vacante` int(11) NOT NULL,
  `id_conocimiento` int(11) NOT NULL,
  PRIMARY KEY (`id_vacante`,`id_conocimiento`),
  KEY `fk_conoc_vac` (`id_conocimiento`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empresa`
--

DROP TABLE IF EXISTS `empresa`;
CREATE TABLE IF NOT EXISTS `empresa` (
  `id_empresa` int(11) NOT NULL AUTO_INCREMENT,
  `direccion` varchar(50) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `id_estado` int(11) NOT NULL,
  `id_ciudad` int(11) NOT NULL,
  `codigo_postal` varchar(15) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `num_telefono` varchar(13) NOT NULL,
  `folio_convenio` varchar(20) DEFAULT NULL,
  `rfc` varchar(20) NOT NULL,
  `status` varchar(1) NOT NULL,
  PRIMARY KEY (`id_empresa`),
  KEY `fk_usuario` (`id_usuario`),
  KEY `fk_emp_ciudad` (`id_ciudad`),
  KEY `fk_emp_estado` (`id_estado`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `empresa`
--

INSERT INTO `empresa` (`id_empresa`, `direccion`, `nombre`, `id_estado`, `id_ciudad`, `codigo_postal`, `id_usuario`, `num_telefono`, `folio_convenio`, `rfc`, `status`) VALUES
(1, 'Calle Dolores #420', 'Tech Solutions ', 1, 2, '571223', 1, '55334622', 'CONV123450', 'ref0192930291', '1');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estado`
--

DROP TABLE IF EXISTS `estado`;
CREATE TABLE IF NOT EXISTS `estado` (
  `id_estado` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_estado` varchar(50) NOT NULL,
  PRIMARY KEY (`id_estado`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `estado`
--

INSERT INTO `estado` (`id_estado`, `nombre_estado`) VALUES
(1, 'Estado de Mexico'),
(2, 'CDMX');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `habilidad`
--

DROP TABLE IF EXISTS `habilidad`;
CREATE TABLE IF NOT EXISTS `habilidad` (
  `id_habilidad` int(11) NOT NULL,
  `habilidad_desc` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_habilidad`),
  KEY `not null` (`habilidad_desc`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `habilidad_vac`
--

DROP TABLE IF EXISTS `habilidad_vac`;
CREATE TABLE IF NOT EXISTS `habilidad_vac` (
  `id_vacante` int(11) NOT NULL,
  `id_habilidades` int(11) NOT NULL,
  PRIMARY KEY (`id_vacante`,`id_habilidades`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nivel_academico`
--

DROP TABLE IF EXISTS `nivel_academico`;
CREATE TABLE IF NOT EXISTS `nivel_academico` (
  `id_nivel` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_nivel` varchar(50) NOT NULL,
  PRIMARY KEY (`id_nivel`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `perfil`
--

DROP TABLE IF EXISTS `perfil`;
CREATE TABLE IF NOT EXISTS `perfil` (
  `id_perfil` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_perfil` varchar(50) NOT NULL,
  `id_carrera` int(11) NOT NULL,
  PRIMARY KEY (`id_perfil`),
  KEY `fk_carrera_vacante_perfil` (`id_carrera`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

DROP TABLE IF EXISTS `usuario`;
CREATE TABLE IF NOT EXISTS `usuario` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `apellidos` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `id_rol` int(11) NOT NULL,
  `status` varchar(1) NOT NULL,
  PRIMARY KEY (`id_usuario`),
  KEY `fk_usuario_rol` (`id_rol`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `nombre`, `apellidos`, `email`, `password`, `id_rol`, `status`) VALUES
(1, 'Antonio ', 'Rodriguez Barrera', 'antonio.01yea@gmail.com', 'e10adc3949ba59abbe56e057f20f883e', 3, '1'),
(2, 'Administrador ', 'Estadias', 'estadias@utn.com', '7ab309415e40d77219cae3fe0aa313f3', 1, '1'),
(3, 'Administrador', 'Egresados', 'egresados@utn.com', '0a287b25c3570b784675e3aa3ef07892', 2, '1');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_rol`
--

DROP TABLE IF EXISTS `usuario_rol`;
CREATE TABLE IF NOT EXISTS `usuario_rol` (
  `id_rol` int(11) NOT NULL AUTO_INCREMENT,
  `rol_nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id_rol`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `usuario_rol`
--

INSERT INTO `usuario_rol` (`id_rol`, `rol_nombre`) VALUES
(1, 'Administrador Estadias'),
(2, 'Administrador Egresados'),
(3, 'Empresario');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vacante`
--

DROP TABLE IF EXISTS `vacante`;
CREATE TABLE IF NOT EXISTS `vacante` (
  `id_vacante` int(11) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(50) NOT NULL,
  `vacante_desc` varchar(50) NOT NULL,
  `id_perfil` int(11) DEFAULT NULL,
  `edad_min` int(2) DEFAULT NULL,
  `edad_max` int(2) DEFAULT NULL,
  `salario_min` double(9,2) DEFAULT NULL,
  `salario_max` double(9,2) DEFAULT NULL,
  `hora_inicial` varchar(10) DEFAULT NULL,
  `hora_final` varchar(10) DEFAULT NULL,
  `experiencia` int(11) DEFAULT NULL,
  `id_empresa` int(11) DEFAULT NULL,
  `status` varchar(1) NOT NULL,
  `id_carrera` int(11) NOT NULL,
  PRIMARY KEY (`id_vacante`),
  KEY `fk_perfil` (`id_perfil`),
  KEY `fk_carrera_vacante` (`id_carrera`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
