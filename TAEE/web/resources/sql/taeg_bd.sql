-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 18-07-2019 a las 06:00:41
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
DROP PROCEDURE IF EXISTS `aprobar_empresa`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `aprobar_empresa` (IN `empresa_id` INT)  begin
	DECLARE flag boolean;
	DECLARE	msj VARCHAR(500);
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
 	BEGIN
 		set flag=false;
 		set msj='Error de sql.';
 		SELECT flag,msj;
 		
 	END; 
 	DECLARE EXIT HANDLER FOR SQLWARNING
 	BEGIN
 		set flag=false;
 		set msj=(select concat(msj,'Error de advertencia.'));
 		SELECT flag,msj;
 		
 	END;
	START TRANSACTION;
 	update empresa
	set status='2'
	where id_empresa=empresa_id;
	commit;
	set msj=(Select concat('La empresa ',(select nombre from empresa where id_empresa=empresa_id),' Ha sido aprobada'));
	set flag=true;
	SELECT flag,msj;
END$$

DROP PROCEDURE IF EXISTS `desaprobar_empresa`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `desaprobar_empresa` (`empresa_id` INT)  begin
	DECLARE flag boolean;
	DECLARE	msj VARCHAR(500);
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
 	BEGIN
 		set flag=false;
 		set msj='Error de sql.';
 		SELECT flag,msj;
 		
 	END; 
 	DECLARE EXIT HANDLER FOR SQLWARNING
 	BEGIN
 		set flag=false;
 		set msj=(select concat(msj,'Error de advertencia.'));
 		SELECT flag,msj;
 		
 	END;
	START TRANSACTION;
 	update empresa
	set status='1'
	where id_empresa=empresa_id;
	commit;
	set msj=(Select concat('La empresa ',(select nombre from empresa where id_empresa=empresa_id),' ha sido desaprobada.'));
	set flag=true;
	SELECT flag,msj;
END$$

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
            END)as status,company.status as status_company,user.id_usuario,company.id_empresa
        from usuario as user
        INNER JOIN usuario_rol as rol ON user.id_rol= rol.id_rol
        LEFT JOIN empresa as company ON company.id_usuario=user.id_usuario
        LEFT JOIN estado as edo ON edo.id_estado=  company.id_estado
        LEFT JOIN ciudad as city ON city.id_ciudad=company.id_ciudad and edo.id_estado=city.id_estado
        WHERE (user.password=MD5(pass) AND user.email=email);
        UPDATE temp_usuario set
        flag='1'
        WHERE email=user;
        SELECT * FROM temp_usuario where email=user;
        
    ELSE
        set flag= false;
        CREATE TEMPORARY TABLE temp_usuario AS
        SELECT flag,null as id_rol,null as nombre,null as apellidos,null as email,
        null as nombre_rol,null as nombre_empresa,null as direccion,null as nombre_estado,
        null as nombre_ciudad,null as codigo_postal,null as num_telefono,null as folio_convenio,
        null as rfc,null as status,null as status_company,null as id_usuario ,null as id_empresa;
        SELECT * FROM temp_usuario ;
    END if;
    
END$$

DROP PROCEDURE IF EXISTS `sp_bulkload`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_bulkload` (IN `empresas` VARCHAR(65000), IN `bulkloads` VARCHAR(65000))  BEGIN
	DECLARE flag boolean;
	DECLARE count int  UNSIGNED DEFAULT 0;
  	DECLARE countFail int UNSIGNED DEFAULT 0;
  	DECLARE countInsert int UNSIGNED DEFAULT 0;
  	DECLARE countUpdate int UNSIGNED DEFAULT 0;
	DECLARE msj varchar(50);
	DECLARE exist int UNSIGNED DEFAULT 0;
	DECLARE id_user int  UNSIGNED DEFAULT 0;
	DECLARE lote_id int UNSIGNED DEFAULT 0;
	DECLARE json_items int UNSIGNED   DEFAULT  JSON_LENGTH(empresas); 
	DECLARE json_items2 int UNSIGNED   DEFAULT  JSON_LENGTH(bulkloads);
	DECLARE _index int UNSIGNED DEFAULT 0;
	DECLARE _index2 int UNSIGNED DEFAULT 0;
	DECLARE suma int UNSIGNED DEFAULT 0;
	DECLARE countbulk int;
	DECLARE id_new int;
  			
			WHILE _index < json_items DO 
  				Select count(*) 
  				from empresa 
  				where rfc=trim((Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].rfc')), '"', ''))) into exist;
				If(exist>0)Then
					Update  empresa set 
					nombre=(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].nombre_empresa')), '"', '')),
					direccion=(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,']. direccion')), '"', '')),
					id_estado=JSON_EXTRACT(empresas, CONCAT('$[',_index,'].id_estado')),
					id_ciudad=JSON_EXTRACT(empresas, CONCAT('$[',_index,'].id_ciudad')),
					codigo_postal=(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,']. codigo_postal')), '"', '')),
					num_telefono=(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].telefono')), '"', '')),
					folio_convenio=(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].convenio')), '"', '')),
					correo_empresa=(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].correo_empresa')), '"', ''))
					Where rfc=(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].rfc')), '"', ''));

					Set countUpdate= countUpdate+1;
				else
					Insert into empresa (nombre,direccion,id_estado,id_ciudad,codigo_postal,num_telefono,folio_convenio,rfc,status,correo_empresa)
					VALUES(
						(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].nombre_empresa')), '"', '')),
						(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,']. direccion')), '"', '')),
						JSON_EXTRACT(empresas, CONCAT('$[',_index,'].id_estado')),
						JSON_EXTRACT(empresas, CONCAT('$[',_index,'].id_ciudad')),
						(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,']. codigo_postal')), '"', '')),
						(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].telefono')), '"', '')),
						(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].convenio')), '"', '')),
						(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].rfc')), '"', '')),
						'3',
						(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].correo_empresa')), '"', ''))

					);
					SET COUNT = (select ROW_count());
					if(COUNT>0)then
						Set countInsert:=countInsert+1;
						set msj='se registro correctamente';
					else set msj='no se inserto prro';
					end if;
					
				end if;
				SET _index := _index + 1; 
				
 
 			END WHILE; 
			

			Select max(lote)+1 from bulkload_empresa into lote_id; 
			if(lote_id is null)then
				Set lote_id=1;
			end if;
			While _index2 < json_items2 Do
				select max(id_bulkload)+1 from bulkload_empresa where lote=lote_id into id_new;
				if(id_new is null)then
					Set id_new=1;
				end if;
				INSERT INTO bulkload_empresa (lote,id_bulkload,nombre,direccion,estado,ciudad,codigo_postal,num_telefono,folio_convenio,rfc,observaciones,correo_empresa) 
				VALUES (
				lote_id,
				id_new,
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].nombre_empresa')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].direccion')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].nombre_estado')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].nombre_ciudad')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].codigo_postal')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].telefono')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].convenio')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].rfc')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].observaciones')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].correo_empresa')), '"', ''))
				); 
				SET _index2 := _index2 + 1; 

			End while;
			SET COUNT = ROW_count();
			if(count>0)then
				
				set flag=true;

			else
				Set msj='ocurrio un error ';
				Set flag =false;

			end if;

			



		
			Select count(*) from bulkload_empresa where lote = lote_id into countbulk;
			set suma=(countInsert+countUpdate);

			Set countFail=countbulk-suma;
			
			COMMIT;
			Select lote,id_bulkload,nombre,direccion,estado,ciudad,codigo_postal,num_telefono,folio_convenio,rfc,observaciones,countbulk, countFail,countUpdate,countInsert,msj,correo_empresa
			from bulkload_empresa where lote =lote_id;
End$$

DROP PROCEDURE IF EXISTS `sp_delete_empresa_estadia`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_delete_empresa_estadia` (IN `tipo` INT)  BEGIN
	DECLARE var_mensaje varchar(50);
	DECLARE var_success boolean;
	DECLARE var_n integer;

	UPDATE empresa emp SET emp.status='0'
	where emp.id_empresa =tipo and emp.status='3';
	SET var_n = (select ROW_count());
	if(var_n >0)then
		set var_mensaje='El registro se Elimino correctamente';
		set var_success= true;
	else
		set var_mensaje='no se inserto prro';
		set var_success= false;
	end if;
    SELECT var_mensaje,var_success;
END$$

DROP PROCEDURE IF EXISTS `sp_delete_vacante_egresados`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_delete_vacante_egresados` (IN `vacante_id` INT)  BEGIN
	DECLARE status_empresa varchar(1);
	DECLARE flag boolean;
	DECLARE msj varchar(500);
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
 	BEGIN
 		set flag=false;
 		set msj='Error de sql.';
 		SELECT flag,msj;
 		
 	END; 
 	DECLARE EXIT HANDLER FOR SQLWARNING
 	BEGIN
 		set flag=false;
 		set msj=(select concat(msj,'Error de advertencia.'));
 		SELECT flag,msj;
 		
 	END;
	START TRANSACTION;

	SELECT em.status 
	from empresa em
	JOIN vacante v on v.id_empresa=em.id_empresa
	where v.id_vacante=vacante_id into status_empresa;
	IF(status_empresa='2')THEN
		UPDATE vacante set
		status='0'
		where id_vacante=vacante_id;
		set flag=true;
		set msj='La vacante ha sido dada de Baja correctamente.';
	ELSE 
		set flag=false;
		set msj='La vacante no se puede eliminar por alguna razón.';
	END IF;
	commit;
	SELECT flag,msj;
END$$

DROP PROCEDURE IF EXISTS `sp_get_ciudades`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_ciudades` ()  BEGIN
	SELECT id_ciudad,id_estado,nombre_ciudad from ciudad;
END$$

DROP PROCEDURE IF EXISTS `sp_get_conco_habil`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_conco_habil` (`clave` INT, `tipo` INT)  BEGIN
	IF(tipo=1)then
		select c.id_vacante,c.id_conocimiento, co.conoc_desc
		from conocimiento_vac c
		INNER JOIN conocimiento co on c.id_conocimiento= co.id_conocimiento
		where c.id_vacante=clave;
	ELSE
		select hv.id_vacante,hv.id_habilidades,h.habilidad_desc
		from habilidad_vac hv
		INNER JOIN habilidad h on hv.id_habilidades= h.id_habilidad
		where hv.id_vacante=clave;
	END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_get_empresas`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_empresas` (`tipo` INT)  BEGIN
	SELECT id_empresa,direccion,nombre,id_estado,id_ciudad,
	codigo_postal,id_usuario,num_telefono,folio_convenio,rfc,
	status,correo_empresa
	FROM empresa
	where status=tipo;
END$$

DROP PROCEDURE IF EXISTS `sp_get_empresas_egresados`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_empresas_egresados` (IN `tipo` INT)  BEGIN
	SELECT id_empresa,direccion,em.nombre as nombre_empresa,
	em.id_estado,em.id_ciudad,codigo_postal,
	num_telefono,rfc,em.status as status_empresa,correo_empresa,
	us.id_usuario,us.nombre as nombre_usuario,apellidos,
	email,id_rol,us.status as 		   status_usuario,es.nombre_estado,ci.nombre_ciudad
	FROM empresa em
	JOIN usuario us on em.id_usuario=us.id_usuario
    JOIN estado es on em.id_estado= es.id_estado
    JOIN ciudad ci on em.id_ciudad= ci.id_ciudad and ci.id_estado= es.id_estado
	where em.status=tipo;
END$$

DROP PROCEDURE IF EXISTS `sp_get_estados`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_estados` ()  BEGIN
	SELECT id_estado,nombre_estado from estado;
END$$

DROP PROCEDURE IF EXISTS `sp_get_listas_estadias`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_listas_estadias` (`tabla` VARCHAR(2), `clave` INT)  BEGIN
	CASE tabla
 	WHEN 'n' THEN
    	SELECT id_nivel,nombre_nivel FROM nivel_academico;
	WHEN 'ca' THEN 
    	SELECT id_carrera,carrera_desc FROM carreras
    	where id_nivel=clave;

	WHEN 'p' THEN
		SELECT id_perfil,nombre_perfil FROM perfil
		where id_carrera=clave;

	WHEN 'h' THEN
		SELECT id_habilidad,habilidad_desc FROM habilidad;
    
	WHEN 'co' THEN
		SELECT id_conocimiento,conoc_desc FROM conocimiento
		where id_perfil=clave;

	END CASE;
END$$

DROP PROCEDURE IF EXISTS `sp_get_municipios`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_municipios` (`tipo` INT)  BEGIN
	SELECT id_ciudad,id_estado,nombre_ciudad from ciudad
	where id_estado= tipo;
END$$

DROP PROCEDURE IF EXISTS `sp_get_vacantes_egresados`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_vacantes_egresados` (IN `perfil` INT, IN `json_conocimiento` VARCHAR(65000), IN `json_habilidades` VARCHAR(65000))  BEGIN
	DECLARE json_items int ;   
	DECLARE json_items2 int ;  
	DECLARE _index int UNSIGNED DEFAULT 0;
	DECLARE _index2 int UNSIGNED DEFAULT 0;
	DECLARE count int UNSIGNED DEFAULT 0;
	DROP TABLE IF EXISTS temp_conocimiento;
	DROP TABLE IF EXISTS temp_habilidad;
	CREATE TEMPORARY TABLE temp_conocimiento(
       id INT NOT NULL
    );
    CREATE TEMPORARY TABLE temp_habilidad(
       id INT NOT NULL
    );
    if (json_conocimiento!='[]')then
    	SET json_items:=JSON_LENGTH(json_conocimiento);
    	WHILE _index < json_items DO 
    		insert into temp_conocimiento(id)
    		values(JSON_EXTRACT(json_conocimiento, CONCAT('$[',_index,'].id_conocimiento')));
    		SET _index := _index + 1; 
    	END WHILE; 
    end if;
    if (json_habilidades!='[]')then
    	SET json_items2:=JSON_LENGTH(json_habilidades);
    	WHILE _index2 < json_items2 DO 
    		insert into temp_habilidad(id)
    		values(JSON_EXTRACT(json_habilidades, CONCAT('$[',_index2,'].id_habilidad')));
    		SET _index2 := _index2 + 1; 
    	END WHILE; 
    end if;
    SELECT COUNT(*) FROM temp_habilidad into count;

    IF(count>0)then
    	SELECT DISTINCT v.id_vacante,titulo,vacante_desc,
		na.nombre_nivel,ca.carrera_desc, 
		v.id_perfil,p.nombre_perfil, edad_min,edad_max,
		salario_min,salario_max, hora_inicial,hora_final, 
		experiencia,v.id_empresa,e.nombre,v.status,concat(e.direccion,',', es.nombre_estado,',',ci.nombre_ciudad)
		,e.num_telefono,e.correo_empresa
		from vacante v 
		RIGHT join conocimiento_vac cv on v.id_vacante=cv.id_vacante
		RIGHT join habilidad_vac hv on v.id_vacante=hv.id_vacante 
		INNER JOIN perfil p on v.id_perfil=p.id_perfil
		INNER JOIN empresa e on v.id_empresa=e.id_empresa
		INNER JOIN carreras ca on p.id_carrera=ca.id_carrera
		INNER JOIN nivel_academico na on ca.id_nivel= na.id_nivel
        INNER JOIN estado es on e.id_estado = es.id_estado
        INNER JOIN ciudad ci on e.id_ciudad= ci.id_ciudad and e.id_estado=ci.id_estado
		WHERE v.id_perfil=perfil 
		and cv.id_conocimiento in(SELECT id from temp_conocimiento)
		and hv.id_habilidades in(SELECT id from temp_habilidad)
		and v.status='1'
		and e.status=2;
    else
    	SELECT DISTINCT v.id_vacante,titulo,vacante_desc,
		na.nombre_nivel,ca.carrera_desc, 
		v.id_perfil,p.nombre_perfil, edad_min,edad_max,
		salario_min,salario_max, hora_inicial,hora_final, 
		experiencia,v.id_empresa,e.nombre,v.status,concat(e.direccion,',', es.nombre_estado,',',ci.nombre_ciudad)
		,e.num_telefono,e.correo_empresa
		from vacante v 
		RIGHT join conocimiento_vac cv on v.id_vacante=cv.id_vacante
		INNER JOIN perfil p on v.id_perfil=p.id_perfil
		INNER JOIN empresa e on v.id_empresa=e.id_empresa
		INNER JOIN carreras ca on p.id_carrera=ca.id_carrera
		INNER JOIN nivel_academico na on ca.id_nivel= na.id_nivel
        INNER JOIN estado es on e.id_estado = es.id_estado
        INNER JOIN ciudad ci on e.id_ciudad= ci.id_ciudad and e.id_estado=ci.id_estado
		WHERE v.id_perfil=perfil 
		and cv.id_conocimiento in(SELECT id from temp_conocimiento)
		and v.status='1'
		and e.status=2;
    end if;

   	



END$$

DROP PROCEDURE IF EXISTS `sp_get_vacantes_egresados_por_empresa`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_vacantes_egresados_por_empresa` (`empresa` INT)  BEGIN
    	SELECT DISTINCT v.id_vacante,titulo,vacante_desc,
		na.nombre_nivel,ca.carrera_desc, 
		v.id_perfil,p.nombre_perfil, edad_min,edad_max,
		salario_min,salario_max, hora_inicial,hora_final, 
		experiencia,v.id_empresa,e.nombre,v.status,concat(e.direccion,',', es.nombre_estado,',',ci.nombre_ciudad)
		,e.num_telefono,e.correo_empresa,v.ayuda_economica
		from vacante v 
		RIGHT join conocimiento_vac cv on v.id_vacante=cv.id_vacante
		RIGHT join habilidad_vac hv on v.id_vacante=hv.id_vacante 
		INNER JOIN perfil p on v.id_perfil=p.id_perfil
		INNER JOIN empresa e on v.id_empresa=e.id_empresa
		INNER JOIN carreras ca on p.id_carrera=ca.id_carrera
		INNER JOIN nivel_academico na on ca.id_nivel= na.id_nivel
        INNER JOIN estado es on e.id_estado = es.id_estado
        INNER JOIN ciudad ci on e.id_ciudad= ci.id_ciudad and e.id_estado=ci.id_estado
		WHERE  e.id_empresa=empresa
		and v.status='1'
		and e.status=2;
 
END$$

DROP PROCEDURE IF EXISTS `sp_get_vacantes_estadia`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_vacantes_estadia` (IN `perfil` INT, IN `json_conocimiento` VARCHAR(65000), IN `json_habilidades` VARCHAR(65000))  BEGIN
	DECLARE json_items int ;   
	DECLARE json_items2 int ;  
	DECLARE _index int UNSIGNED DEFAULT 0;
	DECLARE _index2 int UNSIGNED DEFAULT 0;
	DECLARE count int UNSIGNED DEFAULT 0;
	DROP TABLE IF EXISTS temp_conocimiento;
	DROP TABLE IF EXISTS temp_habilidad;
	CREATE TEMPORARY TABLE temp_conocimiento(
       id INT NOT NULL
    );
    CREATE TEMPORARY TABLE temp_habilidad(
       id INT NOT NULL
    );
    if (json_conocimiento!='[]')then
    	SET json_items:=JSON_LENGTH(json_conocimiento);
    	WHILE _index < json_items DO 
    		insert into temp_conocimiento(id)
    		values(JSON_EXTRACT(json_conocimiento, CONCAT('$[',_index,'].id_conocimiento')));
    		SET _index := _index + 1; 
    	END WHILE; 
    end if;
    if (json_habilidades!='[]')then
    	SET json_items2:=JSON_LENGTH(json_habilidades);
    	WHILE _index2 < json_items2 DO 
    		insert into temp_habilidad(id)
    		values(JSON_EXTRACT(json_habilidades, CONCAT('$[',_index2,'].id_habilidad')));
    		SET _index2 := _index2 + 1; 
    	END WHILE; 
    end if;
    SELECT COUNT(*) FROM temp_habilidad into count;

    IF(count>0)then
    	SELECT DISTINCT v.id_vacante,titulo,vacante_desc,
		na.nombre_nivel,ca.carrera_desc, 
		v.id_perfil,p.nombre_perfil, edad_min,edad_max,
		salario_min,salario_max, hora_inicial,hora_final, 
		experiencia,v.id_empresa,e.nombre,v.status,concat(e.direccion,',', es.nombre_estado,',',ci.nombre_ciudad)
		,e.num_telefono,e.correo_empresa,v.ayuda_economica
		from vacante v 
		RIGHT join conocimiento_vac cv on v.id_vacante=cv.id_vacante
		RIGHT join habilidad_vac hv on v.id_vacante=hv.id_vacante 
		INNER JOIN perfil p on v.id_perfil=p.id_perfil
		INNER JOIN empresa e on v.id_empresa=e.id_empresa
		INNER JOIN carreras ca on p.id_carrera=ca.id_carrera
		INNER JOIN nivel_academico na on ca.id_nivel= na.id_nivel
        INNER JOIN estado es on e.id_estado = es.id_estado
        INNER JOIN ciudad ci on e.id_ciudad= ci.id_ciudad and e.id_estado=ci.id_estado
		WHERE v.id_perfil=perfil 
		and cv.id_conocimiento in(SELECT id from temp_conocimiento)
		and hv.id_habilidades in(SELECT id from temp_habilidad)
		and v.status='1'
		and e.status=3;
    else
    	SELECT DISTINCT v.id_vacante,titulo,vacante_desc,
		na.nombre_nivel,ca.carrera_desc, 
		v.id_perfil,p.nombre_perfil, edad_min,edad_max,
		salario_min,salario_max, hora_inicial,hora_final, 
		experiencia,v.id_empresa,e.nombre,v.status,concat(e.direccion,',', es.nombre_estado,',',ci.nombre_ciudad)
		,e.num_telefono,e.correo_empresa,v.ayuda_economica
		from vacante v 
		RIGHT join conocimiento_vac cv on v.id_vacante=cv.id_vacante
		INNER JOIN perfil p on v.id_perfil=p.id_perfil
		INNER JOIN empresa e on v.id_empresa=e.id_empresa
		INNER JOIN carreras ca on p.id_carrera=ca.id_carrera
		INNER JOIN nivel_academico na on ca.id_nivel= na.id_nivel
        INNER JOIN estado es on e.id_estado = es.id_estado
        INNER JOIN ciudad ci on e.id_ciudad= ci.id_ciudad and e.id_estado=ci.id_estado
		WHERE v.id_perfil=perfil 
		and cv.id_conocimiento in(SELECT id from temp_conocimiento)
		and v.status='1'
		and e.status=3;
    end if;

   	



END$$

DROP PROCEDURE IF EXISTS `sp_get_vacantes_estadia_por_empresa`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_vacantes_estadia_por_empresa` (`empresa` INT)  BEGIN
    	SELECT DISTINCT v.id_vacante,titulo,vacante_desc,
		na.nombre_nivel,ca.carrera_desc, 
		v.id_perfil,p.nombre_perfil, edad_min,edad_max,
		salario_min,salario_max, hora_inicial,hora_final, 
		experiencia,v.id_empresa,e.nombre,v.status,concat(e.direccion,',', es.nombre_estado,',',ci.nombre_ciudad)
		,e.num_telefono,e.correo_empresa,v.ayuda_economica
		from vacante v 
		RIGHT join conocimiento_vac cv on v.id_vacante=cv.id_vacante
		RIGHT join habilidad_vac hv on v.id_vacante=hv.id_vacante 
		INNER JOIN perfil p on v.id_perfil=p.id_perfil
		INNER JOIN empresa e on v.id_empresa=e.id_empresa
		INNER JOIN carreras ca on p.id_carrera=ca.id_carrera
		INNER JOIN nivel_academico na on ca.id_nivel= na.id_nivel
        INNER JOIN estado es on e.id_estado = es.id_estado
        INNER JOIN ciudad ci on e.id_ciudad= ci.id_ciudad and e.id_estado=ci.id_estado
		WHERE  e.id_empresa=empresa
		and v.status='1'
		and e.status=3;
 
END$$

DROP PROCEDURE IF EXISTS `sp_get_vacantes_usuario`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_vacantes_usuario` (`empresa_id` INT)  BEGIN
	SELECT id_vacante,titulo,vacante_desc,id_perfil,
	edad_min,edad_max,salario_min,salario_max,
	hora_inicial,hora_final,experiencia,em.id_empresa,
	v.status as status_empresa,ayuda_economica
	FROM vacante v
	JOIN empresa em on v.id_empresa=em.id_empresa 
	where em.id_empresa=empresa_id
	and em.status='2'
	and v.status='1';
END$$

DROP PROCEDURE IF EXISTS `sp_insert_update_empresaEst`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insert_update_empresaEst` (IN `editar` INT, IN `ln_id_empresa` INT, IN `ls_direccion` VARCHAR(200), IN `ls_nombre` VARCHAR(200), IN `ln_id_estado` INT, IN `ln_id_ciudad` INT, IN `ls_codigo_postal` VARCHAR(50), IN `ln_id_usuario` INT, IN `ls_num_telefono` VARCHAR(50), IN `ls_folio_convenio` VARCHAR(50), IN `ls_rfc` VARCHAR(50), IN `ls_status` VARCHAR(1), IN `ls_correo_empresa` VARCHAR(50))  BEGIN
	DECLARE var_id integer;
	DECLARE var_mensaje varchar(50);
	DECLARE var_success boolean;
	DECLARE var_n integer;
	DECLARE var_exist integer;

	IF(editar= 0)THEN
		set var_exist =0;
		select count(*) from empresa emp 
		where (UPPER(emp.nombre)=UPPER(ls_nombre) or (UPPER(emp.id_empresa)=UPPER(ln_id_empresa))) and emp.status='3'
		INTO var_exist;
		IF(var_exist=0)THEN
			select MAX(id_empresa)+1 INTO var_id From empresa;
			IF(var_id IS NULL)THEN
				set var_id=1;
			END IF;
			INSERT INTO empresa(
			id_empresa,
			direccion,
			nombre,
			id_estado,
			id_ciudad,
			codigo_postal,
			id_usuario,
			num_telefono,
			folio_convenio,
			rfc,
			status,
			correo_empresa)
			VALUES(var_id,ls_direccion,ls_nombre,ln_id_estado,ln_id_ciudad,ls_codigo_postal,ln_id_usuario,ls_num_telefono,ls_folio_convenio,ls_rfc,ls_status,ls_correo_empresa);
			SET var_n = (select ROW_count());
			IF(var_n>0)THEN
				set var_success= true;
				set var_mensaje ='La Empresa se registro Correctamente';
			ELSE 
				set var_success= false;
				set var_mensaje ='Error';	
			END IF;
        ELSE
		set var_success= false;
		set var_mensaje ='Error La Empresa ya Existe';
		END IF;
	
	ELSEIF(editar=1 AND (SELECT COUNT(*) from empresa where status='3' and id_empresa=ln_id_empresa)>0)THEN
			SELECT COUNT(*) From empresa emp where emp.id_empresa <> ln_id_empresa and  (UPPER(nombre)= UPPER(ls_nombre)) and emp.status='3' INTO var_exist;
			IF(var_exist=0)THEN
				UPDATE empresa SET
				direccion = ls_direccion,
				nombre =ls_nombre,
				id_estado = ln_id_estado,
				id_ciudad = ln_id_ciudad,
				codigo_postal =ls_codigo_postal,
				id_usuario =ln_id_usuario,
				num_telefono =ls_num_telefono,
				folio_convenio =ls_folio_convenio,
				rfc =ls_rfc,
				status =ls_status,
				correo_empresa = ls_correo_empresa
				where status='3' and id_empresa=ln_id_empresa;
				SET var_n = (select ROW_count());
				IF(var_n>0)THEN
					set var_success= true;
					set var_mensaje ='La Empresa se actualizó Correctamente';
				ELSE 
					set var_success= false;
					set var_mensaje ='Error';	
				END IF;
			ELSE
			set var_success= false;
			set var_mensaje ='La Empresa NO se Actualizó';
			END IF;
		
	END IF;
	SELECT var_mensaje,var_success;
END$$

DROP PROCEDURE IF EXISTS `sp_insert_update_vacante_egresados`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insert_update_vacante_egresados` (IN `json_vacante` VARCHAR(65000), IN `json_conocimiento` VARCHAR(65000), IN `json_habilidades` VARCHAR(65000), IN `opcion` INT)  begin
	DECLARE flag boolean;
	DECLARE	msj VARCHAR(500);
	DECLARE id_new int;
	DECLARE json_items int UNSIGNED   DEFAULT  JSON_LENGTH(json_vacante); 
	DECLARE json_items2 int UNSIGNED   DEFAULT  JSON_LENGTH(json_conocimiento);
	DECLARE json_items3 int UNSIGNED   DEFAULT  JSON_LENGTH(json_habilidades);
	DECLARE _index int UNSIGNED DEFAULT 0;
	DECLARE _index2 int UNSIGNED DEFAULT 0;
	DECLARE _index3 int UNSIGNED DEFAULT 0;
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
 	BEGIN
 		set flag=false;
 		set msj='Error de sql.';
 		set id_new=0;
 		resignal;
 		ROLLBACK;
 		SELECT flag,msj,id_new;
 		
 	END; 
 	DECLARE EXIT HANDLER FOR SQLWARNING
 	BEGIN
 		set flag=false;
 		set msj=(select concat(msj,'Error de advertencia.'));
 		set id_new=0;
 		resignal;
 		ROLLBACK;
 		SELECT flag,msj;
 		
 	END;
 	SET autocommit=0;
	START TRANSACTION;
 	
	IF(opcion=1)THEN
		IF (json_vacante!='[]')THEN
			
				SELECT max(id_vacante)+1 from vacante into id_new;
				Insert into vacante (id_vacante,titulo,vacante_desc,id_perfil,edad_min,edad_max,
									salario_min,salario_max,hora_inicial,hora_final,
									experiencia,id_empresa,
									status,ayuda_economica)
				VALUES(
					id_new,
					(Select replace(JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].titulo')), '"', '')),
					(Select replace(JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].vacante_desc')), '"', '')),
					JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].id_perfil')),
					JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].edad_min')),
					JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].edad_max')),
					JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].salario_min')),
					JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].salario_max')),
					(Select replace(JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].hora_inicial')), '"', '')),
					(Select replace(JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].hora_final')), '"', '')),
					(Select replace(JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].experiencia')), '"', '')),
					JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].id_empresa')),
					'1',
					JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].ayuda_economica'))
				
				);
			
			
			IF (json_habilidades!='[]')THEN
				WHILE _index3 < json_items3 DO 
					Insert into habilidad_vac (id_vacante,id_habilidades)
					VALUES(
						id_new,
						JSON_EXTRACT(json_habilidades, CONCAT('$[',_index3,'].id_habilidad'))
					);
					SET _index3:= _index3 + 1; 
				END WHILE; 
			END IF;
			IF (json_conocimiento!='[]')THEN
				WHILE _index2 < json_items2 DO 
					Insert into conocimiento_vac (id_vacante,id_conocimiento)
					VALUES(
						id_new,
						JSON_EXTRACT(json_conocimiento, CONCAT('$[',_index2,'].id_conocimiento'))
					);
					SET _index2 := _index2 + 1; 
				END WHILE; 
			ELSE
				SET flag=false;
				SET msj='Conocimientos no seleccionados';
				SET id_new=0;
 				ROLLBACK;
			END IF;
			commit;
			SET flag=true;
			SET msj='Se agrego la vacante correctamente.';
		ELSE
			SET flag=false;
			SET msj='Vacante vacia';
			SET id_new=0;
		END IF;
	ELSE IF(opcion=2)THEN
			IF (json_vacante!='[]')THEN
				WHILE _index < json_items DO 
					Update  vacante set 
    					titulo=(Select replace(JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].titulo')), '"', '')),
    					vacante_desc=(Select replace(JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].vacante_desc')), '"', '')),
    					id_perfil=JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].id_perfil')),
    					edad_min=JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].edad_min')),
    					edad_max=JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].edad_max')),
    					salario_min=JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].salario_min')),
    					salario_max=JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].salario_max')),
    					hora_inicial=(Select replace(JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].hora_inicial')), '"', '')),
    					hora_final=(Select replace(JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].hora_final')), '"', '')),
    					experiencia=(Select replace(JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].experiencia')), '"', '')),
    					id_empresa=JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].id_empresa')),
    					ayuda_economica=JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].ayuda_economica'))
						Where id_vacante=JSON_EXTRACT(json_vacante, CONCAT('$[',_index,'].id_vacante'));
					SET _index := _index + 1; 
				END WHILE; 
				
				IF (json_habilidades!='[]')THEN
					DELETE FROM habilidad_vac where id_vacante=JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].id_vacante'));
					WHILE _index3 < json_items3 DO 
						Insert into habilidad_vac (id_vacante,id_habilidades)
						VALUES(
							JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].id_vacante')),
							JSON_EXTRACT(json_habilidades, CONCAT('$[',_index3,'].id_habilidad'))
						);
						SET _index3:= _index3 + 1; 
					END WHILE; 
				END IF;
				IF (json_conocimiento!='[]')THEN
					DELETE FROM conocimiento_vac where id_vacante=JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].id_vacante'));
					WHILE _index2 < json_items2 DO 
						Insert into conocimiento_vac (id_vacante,id_conocimiento)
						VALUES(
							JSON_EXTRACT(json_vacante, CONCAT('$[',0,'].id_vacante')),
							JSON_EXTRACT(json_conocimiento, CONCAT('$[',_index2,'].id_conocimiento'))
						);
						SET _index2 := _index2 + 1; 
					END WHILE; 
				ELSE
					SET flag=false;
					SET msj='Conocimientos no seleccionados';
					SET id_new=0;
 					ROLLBACK;
				END IF;
				commit;
				SET flag=true;
				SET msj='Se modifico la vacante correctamente.';
		 END IF;
	  END IF;
	END IF;
	SELECT flag,msj,id_new;
END$$

DROP PROCEDURE IF EXISTS `sp_search_empresas_egresados`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_search_empresas_egresados` (`empresa_id` INT)  BEGIN
	SELECT id_empresa,direccion,em.nombre as nombre_empresa,
	em.id_estado,em.id_ciudad,codigo_postal,
	num_telefono,rfc,em.status as status_empresa,correo_empresa,
	us.id_usuario,us.nombre as nombre_usuario,apellidos,
	email,id_rol,us.status as status_usuario,es.nombre_estado,ci.nombre_ciudad
	FROM empresa em
	JOIN usuario us on em.id_usuario=us.id_usuario
    JOIN estado es on em.id_estado= es.id_estado
    JOIN ciudad ci on em.id_ciudad= ci.id_ciudad and ci.id_estado= es.id_estado
	where em.id_empresa=empresa_id;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bulkload_empresa`
--

DROP TABLE IF EXISTS `bulkload_empresa`;
CREATE TABLE IF NOT EXISTS `bulkload_empresa` (
  `lote` int(11) NOT NULL,
  `id_bulkload` int(11) NOT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `nombre` varchar(200) DEFAULT NULL,
  `estado` varchar(200) DEFAULT NULL,
  `ciudad` varchar(200) DEFAULT NULL,
  `codigo_postal` varchar(200) DEFAULT NULL,
  `usuario` varchar(200) DEFAULT NULL,
  `num_telefono` varchar(200) DEFAULT NULL,
  `folio_convenio` varchar(200) DEFAULT NULL,
  `rfc` varchar(200) DEFAULT NULL,
  `observaciones` varchar(5000) DEFAULT NULL,
  `correo_empresa` varchar(500) NOT NULL,
  PRIMARY KEY (`lote`,`id_bulkload`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `bulkload_empresa`
--

INSERT INTO `bulkload_empresa` (`lote`, `id_bulkload`, `direccion`, `nombre`, `estado`, `ciudad`, `codigo_postal`, `usuario`, `num_telefono`, `folio_convenio`, `rfc`, `observaciones`, `correo_empresa`) VALUES
(1, 1, 'Tlalpan, Ciudad de México (Distrito Federal)', 'Universidad Pedregal Del Sur S C', 'CDMX', 'Alvaro Obregon', '566576', NULL, '5566778899', 'CONV019293', 'UPS7172636', NULL, 'upds@gmail.com'),
(1, 2, 'Calle 2 el faisan', 'Grupo salinas', 'CDMX', 'Cuauhtemoc', '57888', NULL, '555565435', 'CONV826364', 'GP88273746', NULL, 'salinas.e@gmail.com'),
(1, 3, 'Calle 5 el pericles', 'JOBFIT', 'CDMX', 'Gustavo A. Madero', '67888', NULL, '45366789', 'CONV5152367', 'JO626536', NULL, 'jobfit@yahoo.com'),
(1, 4, 'Almoyta num 91', 'MAR SYSTEMS', 'Estado de mexico', 'Almoloya de Juarez', '5466577', NULL, '5566442233', 'CONV526373', 'MS9173636', NULL, 'mar@hotmail.com'),
(1, 5, 'Calle pequeña', 'Royal Software', 'Guanajuato', 'Jerecuaro', '5454646', NULL, '67577383', 'CONV192183', 'RSFA554556', 'El estado no existe en la base de datos.La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.', 'rsf@gmail.com'),
(1, 6, 'Calle 102', '', 'Estado de mexico', 'Alvaro Obregon', '558879', NULL, '', '', '', 'El campo Nombre esta en blanco. La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.El campo Telefono esta en blanco. El campo Folio de convenio esta en blanco. El campo RFC de la Empresa esta en blanco. El campo Correo de Empresa esta en blanco', ''),
(2, 7, 'Tlalpan, Ciudad de México (Distrito Federal)', 'Universidad Pedregal Del Sur S C', 'CDMX', 'Alvaro Obregon', '566576', NULL, '5566778899', 'CONV019293', 'UPS7172636', NULL, 'upds@gmail.com'),
(2, 8, 'Calle 2 el faisan', 'Grupo salinas', 'CDMX', 'Cuauhtemoc', '57888', NULL, '555565435', 'CONV826364', 'GP88273746', NULL, 'salinas.e@gmail.com'),
(2, 9, 'Calle 5 el pericles', 'JOBFIT', 'CDMX', 'Gustavo A. Madero', '67888', NULL, '45366789', 'CONV5152367', 'JO626536', NULL, 'jobfit@yahoo.com'),
(2, 10, 'Almoyta num 91', 'MAR SYSTEMS', 'Estado de mexico', 'Almoloya de Juarez', '5466577', NULL, '5566442233', 'CONV526373', 'MS9173636', NULL, 'mar@hotmail.com'),
(2, 11, 'Calle pequeña', 'Royal Software', 'Guanajuato', 'Jerecuaro', '5454646', NULL, '67577383', 'CONV192183', 'RSFA554556', 'El estado no existe en la base de datos.La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.', 'rsf@gmail.com'),
(2, 12, 'Calle 102', '', 'Estado de mexico', 'Alvaro Obregon', '558879', NULL, '', '', '', 'El campo Nombre esta en blanco. La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.El campo Telefono esta en blanco. El campo Folio de convenio esta en blanco. El campo RFC de la Empresa esta en blanco. El campo Correo de Empresa esta en blanco', ''),
(3, 1, 'Tlalpan, Ciudad de México (Distrito Federal)', 'Universidad Pedregal Del Sur S C', 'CDMX', 'Alvaro Obregon', '566576', NULL, '5566778899', 'CONV019293', 'UPS7172636', NULL, 'upds@gmail.com'),
(3, 2, 'Calle 2 el faisan', 'Grupo salinas', 'CDMX', 'Cuauhtemoc', '57888', NULL, '555565435', 'CONV826364', 'GP88273746', NULL, 'salinas.e@gmail.com'),
(3, 3, 'Calle 5 el pericles', 'JOBFIT', 'CDMX', 'Gustavo A. Madero', '67888', NULL, '45366789', 'CONV5152367', 'JO626536', NULL, 'jobfit@yahoo.com'),
(3, 4, 'Almoyta num 91', 'MAR SYSTEMS', 'Estado de mexico', 'Almoloya de Juarez', '5466577', NULL, '5566442233', 'CONV526373', 'MS9173636', NULL, 'mar@hotmail.com'),
(3, 5, 'Calle pequeña', 'Royal Software', 'Guanajuato', 'Jerecuaro', '5454646', NULL, '67577383', 'CONV192183', 'RSFA554556', 'El estado no existe en la base de datos.La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.', 'rsf@gmail.com'),
(3, 6, 'Calle 102', '', 'Estado de mexico', 'Alvaro Obregon', '558879', NULL, '', '', '', 'El campo Nombre esta en blanco. La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.El campo Telefono esta en blanco. El campo Folio de convenio esta en blanco. El campo RFC de la Empresa esta en blanco. El campo Correo de Empresa esta en blanco', ''),
(4, 1, 'Tlalpan, Ciudad de México (Distrito Federal)', 'Universidad Pedregal Del Sur S C', 'CDMX', 'Alvaro Obregon', '566576', NULL, '5566778899', 'CONV019293', 'UPS7172639', NULL, 'upds@gmail.com'),
(4, 2, 'Calle 2 el faisan', 'Grupo salinas', 'CDMX', 'Cuauhtemoc', '57888', NULL, '555565435', 'CONV826364', 'GP88273746', NULL, 'salinas.e@gmail.com'),
(4, 3, 'Calle 5 el pericles', 'JOBFIT', 'CDMX', 'Gustavo A. Madero', '67888', NULL, '45366789', 'CONV5152367', 'JO626536', NULL, 'jobfit@yahoo.com'),
(4, 4, 'Almoyta num 91', 'MAR SYSTEMS', 'Estado de mexico', 'Almoloya de Juarez', '5466577', NULL, '5566442233', 'CONV526373', 'MS9173636', NULL, 'mar@hotmail.com'),
(4, 5, 'Calle pequeña', 'Royal Software', 'Guanajuato', 'Jerecuaro', '5454646', NULL, '67577383', 'CONV192183', 'RSFA554556', 'El estado no existe en la base de datos.La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.', 'rsf@gmail.com'),
(4, 6, 'Calle 102', '', 'Estado de mexico', 'Alvaro Obregon', '558879', NULL, '', '', '', 'El campo Nombre esta en blanco. La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.El campo Telefono esta en blanco. El campo Folio de convenio esta en blanco. El campo RFC de la Empresa esta en blanco. El campo Correo de Empresa esta en blanco', ''),
(5, 1, 'Tlalpan, Ciudad de México (Distrito Federal)', 'Universidad Pedregal Del Sur S C', 'CDMX', 'Alvaro Obregon', '50000', NULL, '5566778899', 'CONV019293', 'UPS7172639', NULL, 'upds@gmail.com'),
(5, 2, 'Calle 2 el faisan', 'Grupo salinas', 'CDMX', 'Cuauhtemoc', '57888', NULL, '555565435', 'CONV826364', 'GP88273746', NULL, 'salinas.e@gmail.com'),
(5, 3, 'Calle 5 el pericles', 'JOBFIT', 'CDMX', 'Gustavo A. Madero', '67888', NULL, '45366789', 'CONV5152367', 'JO626536', NULL, 'jobfit@yahoo.com'),
(5, 4, 'Almoyta num 91', 'MAR SYSTEMS', 'Estado de mexico', 'Almoloya de Juarez', '5466577', NULL, '5566442233', 'CONV526373', 'MS9173636', NULL, 'mar@hotmail.com'),
(5, 5, 'Calle pequeña', 'Royal Software', 'Guanajuato', 'Jerecuaro', '5454646', NULL, '67577383', 'CONV192183', 'RSFA554556', 'El estado no existe en la base de datos.La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.', 'rsf@gmail.com'),
(5, 6, 'Calle 102', '', 'Estado de mexico', 'Alvaro Obregon', '558879', NULL, '', '', '', 'El campo Nombre esta en blanco. La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.El campo Telefono esta en blanco. El campo Folio de convenio esta en blanco. El campo RFC de la Empresa esta en blanco. El campo Correo de Empresa esta en blanco', ''),
(6, 1, 'Tlalpan, Ciudad de México (Distrito Federal)', 'Universidad Pedregal Del Sur S C', 'CDMX', 'Alvaro Obregon', '50000', NULL, '5566778899', 'CONV019293', 'UPS7172639', NULL, 'upds@gmail.com'),
(6, 2, 'Calle 2 el faisan', 'Grupo salinas', 'CDMX', 'Cuauhtemoc', '57888', NULL, '555565435', 'CONV826364', 'GP88273746', NULL, 'salinas.e@gmail.com'),
(6, 3, 'Calle 5 el pericles', 'JOBFIT', 'CDMX', 'Gustavo A. Madero', '67888', NULL, '45366789', 'CONV5152367', 'JO626536', NULL, 'jobfit@yahoo.com'),
(6, 4, 'Almoyta num 91', 'MAR SYSTEMS', 'Estado de mexico', 'Almoloya de Juarez', '5466577', NULL, '5566442233', 'CONV526373', 'MS9173636', NULL, 'mar@hotmail.com'),
(6, 5, 'Calle pequeña', 'Royal Software', 'Guanajuato', 'Jerecuaro', '5454646', NULL, '67577383', 'CONV192183', 'RSFA554556', 'El estado no existe en la base de datos.La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.', 'rsf@gmail.com'),
(6, 6, 'Calle 102', '', 'Estado de mexico', 'Alvaro Obregon', '558879', NULL, '', '', '', 'El campo Nombre esta en blanco. La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.El campo Telefono esta en blanco. El campo Folio de convenio esta en blanco. El campo RFC de la Empresa esta en blanco. El campo Correo de Empresa esta en blanco', '');

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
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `carreras`
--

INSERT INTO `carreras` (`id_carrera`, `id_nivel`, `carrera_desc`) VALUES
(1, 1, 'TIC Área Desarrollo de Software multiplataforma'),
(2, 1, 'TIC Área Multimedia y Comercio Electrónico'),
(3, 1, 'Administración Área Recursos Humanos'),
(4, 2, 'Tecnologías de la Información y Comunicación'),
(5, 2, 'Negocios y Gestión Empresarial'),
(7, 1, 'Desarrollo de Negocios Área Mercadotecnia'),
(8, 1, 'Química Área Tecnología Ambiental'),
(9, 2, 'Ingeniería en Tecnologías de la Producción'),
(10, 2, 'Ingeniería en Negocios y Gestión Empresarial');

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
  `id_perfil` int(11) NOT NULL,
  PRIMARY KEY (`id_conocimiento`),
  KEY `not null` (`conoc_desc`),
  KEY `fk_conoc_perfil` (`id_perfil`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `conocimiento`
--

INSERT INTO `conocimiento` (`id_conocimiento`, `conoc_desc`, `id_perfil`) VALUES
(1, 'JAVA', 1),
(2, '.NET', 1),
(3, 'Metodología SCRUM', 3),
(4, 'Oracle', 5),
(5, 'PostgresSQL', 5),
(6, 'PMBok', 2),
(7, 'Scripts de Prueba JAVA', 4),
(8, 'Administración Financiera ', 6),
(9, 'Administración Gerencial', 6),
(10, 'Metodos ad hoc', 7),
(11, ' Google analytics', 9),
(12, 'Hubspot', 9),
(13, 'Android', 1),
(14, 'IOS', 1),
(15, 'SQL SERVER', 5);

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `conocimiento_vac`
--

INSERT INTO `conocimiento_vac` (`id_vacante`, `id_conocimiento`) VALUES
(4, 1),
(5, 1),
(18, 1),
(19, 1),
(20, 1),
(4, 2),
(6, 3),
(7, 3),
(8, 4),
(9, 5),
(10, 6),
(1, 7),
(2, 7),
(11, 8),
(11, 9),
(12, 10),
(14, 11),
(13, 12),
(15, 12),
(16, 13),
(16, 14),
(17, 15);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empresa`
--

DROP TABLE IF EXISTS `empresa`;
CREATE TABLE IF NOT EXISTS `empresa` (
  `id_empresa` int(11) NOT NULL AUTO_INCREMENT,
  `direccion` varchar(200) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `id_estado` int(11) NOT NULL,
  `id_ciudad` int(11) NOT NULL,
  `codigo_postal` varchar(15) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `num_telefono` varchar(13) NOT NULL,
  `folio_convenio` varchar(20) DEFAULT NULL,
  `rfc` varchar(20) NOT NULL,
  `status` varchar(1) NOT NULL,
  `correo_empresa` varchar(50) NOT NULL,
  PRIMARY KEY (`id_empresa`),
  KEY `fk_usuario` (`id_usuario`),
  KEY `fk_emp_ciudad` (`id_ciudad`),
  KEY `fk_emp_estado` (`id_estado`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `empresa`
--

INSERT INTO `empresa` (`id_empresa`, `direccion`, `nombre`, `id_estado`, `id_ciudad`, `codigo_postal`, `id_usuario`, `num_telefono`, `folio_convenio`, `rfc`, `status`, `correo_empresa`) VALUES
(1, 'Calle Dolores #420', 'Tech Solutions ', 1, 2, '571223', 1, '55334622', 'CONV123450', 'ref0192930291', '1', 'tech@gmail.com'),
(2, 'Tlalpan, Ciudad de México (Distrito Federal)', 'Universidad Pedregal Del Sur S C', 2, 2, '566576', 0, '5566778899', 'CONV019293', 'UPS7172636', '3', 'upds@gmail.com'),
(3, 'Calle 2 el faisan', 'Grupo salinas', 2, 2, '57888', 0, '555565435', 'CONV826364', 'GP88273746', '3', 'salinas.e@gmail.com'),
(4, 'Calle 5 el pericles', 'JOBFIT', 2, 2, '67888', NULL, '45366789', 'CONV5152367', 'JO626536', '3', 'jobfit@yahoo.com'),
(5, 'Almoyta num 91', 'MAR SYSTEMS', 1, 1, '5466577', 0, '5566442233', 'CONV526373', 'MS9173636', '3', 'mar@hotmail.com'),
(6, '\nCiudad de Mexico ,Distrito Federal', 'Apoyo Economico Familiar', 2, 5, '54665', 4, '5566779922', 'EMP0103344', 'ROM92938D', '2', 'aef@hotmail.com'),
(7, 'Iztacalco, Ciudad de Mexico', 'LOGUERKIM SA DE CV', 2, 7, '509494', 5, '578930948', 'CONV001OID9', 'ROPAUE33', '2', 'log@gmail.com'),
(8, 'Tlalpan, Ciudad de Mexico ', 'Grupo Financiero Inbursa', 2, 4, '56788', 6, '55667788', 'CONVHDH9393', 'RFCIMBUR883', '2', 'prod@imbursa.com'),
(9, 'Atlacomulco de Fabela', 'Test and QA Corporation', 1, 4, '56788', 7, '6363627', 'CONVHDJD92', 'RFCQU8383S', '2', 'qua@gmail.com'),
(10, 'Calle bolivar 192', 'INSTITUTO MEXICANO DE DESARROLLO DE SOFTWARE SC', 1, 8, '37374', 8, '5553626263', 'CONVUEURY', 'IMDDHD829', '2', 'imd@hotmail.com'),
(11, 'la bamba 429', 'TEECH096', 1, 1, '5700', 0, '5548150571', 'CO5555', 'RFC77267', '0', 'tec@gmail.com'),
(13, 'Tlalpan, Ciudad de México (Distrito Federal)', 'Universidad Pedregal Del Sur S C', 2, 2, '50000', NULL, '5566778899', 'CONV019293', 'UPS7172639', '3', 'upds@gmail.com'),
(14, 'El barro 320', 'ANIPROX SOFT', 1, 4, '57800', 9, '5562386648', NULL, 'RFCEMP012', '1', 'aniproxsoft@gmail.com');

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

--
-- Volcado de datos para la tabla `habilidad`
--

INSERT INTO `habilidad` (`id_habilidad`, `habilidad_desc`) VALUES
(1, 'Proactivo'),
(2, 'Trabajo en equipo'),
(3, 'Optimista'),
(4, 'Toma de decisiones'),
(5, 'Pasión'),
(6, 'Adaptabilidad '),
(7, 'Comunicación efectiva'),
(8, 'Creatividad'),
(9, 'Perseverancia'),
(10, 'Organización'),
(11, 'Flexibilidad'),
(12, 'Tolerancia');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `habilidad_vac`
--

DROP TABLE IF EXISTS `habilidad_vac`;
CREATE TABLE IF NOT EXISTS `habilidad_vac` (
  `id_vacante` int(11) NOT NULL,
  `id_habilidades` int(11) NOT NULL,
  PRIMARY KEY (`id_vacante`,`id_habilidades`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `habilidad_vac`
--

INSERT INTO `habilidad_vac` (`id_vacante`, `id_habilidades`) VALUES
(1, 1),
(2, 2),
(3, 1),
(3, 2),
(4, 2),
(6, 1),
(7, 1),
(7, 2),
(8, 1),
(9, 2),
(10, 1),
(10, 2),
(11, 1),
(11, 2),
(11, 3),
(11, 4),
(12, 1),
(13, 1),
(13, 2),
(14, 4),
(15, 1),
(16, 2),
(17, 1),
(17, 2),
(17, 3),
(17, 4),
(18, 1),
(18, 2),
(19, 1),
(19, 2),
(20, 1),
(20, 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nivel_academico`
--

DROP TABLE IF EXISTS `nivel_academico`;
CREATE TABLE IF NOT EXISTS `nivel_academico` (
  `id_nivel` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_nivel` varchar(50) NOT NULL,
  PRIMARY KEY (`id_nivel`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `nivel_academico`
--

INSERT INTO `nivel_academico` (`id_nivel`, `nombre_nivel`) VALUES
(1, 'Técnico Superior Universitario'),
(2, 'Ingeniería');

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
) ENGINE=MyISAM AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `perfil`
--

INSERT INTO `perfil` (`id_perfil`, `nombre_perfil`, `id_carrera`) VALUES
(1, 'Programador', 1),
(2, 'Líder de proyecto', 4),
(3, 'Analista de software', 1),
(4, 'Tester', 4),
(5, 'Administrador de Base de datos', 1),
(6, 'Especialista en Recursos Humanos', 3),
(7, 'Consultor ambiental', 8),
(8, 'Analista de soporte a la produccion', 9),
(9, 'Cordinador de mercadotecnia', 7),
(10, 'Consultor de procesos', 7),
(11, 'Administrador de Redes', 1),
(12, 'Tester', 1),
(13, 'Diseñador Front-End', 1);

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
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `nombre`, `apellidos`, `email`, `password`, `id_rol`, `status`) VALUES
(1, 'Antonio ', 'Rodriguez Barrera', 'antonio.01yea@gmail.com', 'e10adc3949ba59abbe56e057f20f883e', 3, '1'),
(2, 'Administrador ', 'Estadias', 'estadias@utn.com', '7ab309415e40d77219cae3fe0aa313f3', 1, '1'),
(3, 'Administrador', 'Egresados', 'egresados@utn.com', '0a287b25c3570b784675e3aa3ef07892', 2, '1'),
(4, 'Paco', 'Rodriguez Perez', 'pac@gmail.com', 'e10adc3949ba59abbe56e057f20f883e', 3, '1'),
(5, 'María Fernanda', 'Zamudio Peláez', 'zam@gmail.com', 'e10adc3949ba59abbe56e057f20f883e', 3, '1'),
(6, 'Ramiro', 'Hernandez', 'ram@hotmail.com', 'e10adc3949ba59abbe56e057f20f883e', 3, '1'),
(7, 'Ariana', 'Palencia Escobar', 'arp@yahoo.com', 'e10adc3949ba59abbe56e057f20f883e', 3, '1'),
(8, 'Ramses', 'El Grande', 'rami@gmail.com', 'e10adc3949ba59abbe56e057f20f883e', 3, '1'),
(9, 'Tony ', 'RodzBar', 'barrera.01yea@gmail.com', 'e10adc3949ba59abbe56e057f20f883e', 3, '1');

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
  `titulo` varchar(500) NOT NULL,
  `vacante_desc` varchar(500) NOT NULL,
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
  `ayuda_economica` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id_vacante`),
  KEY `fk_perfil` (`id_perfil`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `vacante`
--

INSERT INTO `vacante` (`id_vacante`, `titulo`, `vacante_desc`, `id_perfil`, `edad_min`, `edad_max`, `salario_min`, `salario_max`, `hora_inicial`, `hora_final`, `experiencia`, `id_empresa`, `status`, `ayuda_economica`) VALUES
(1, 'Tester Jr contratacion inmediata', 'MAR SYSTEMS, es una empresa Mexicana Lider en Tecnologias de la Informacion, con presencia Internacional, y con mas de 15 años proporcionando servicios de TI.', 4, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 5, '1', 1),
(2, 'TESTER', 'Funciones: \r\nPruebas funcionales, de regresion, integracion y uat en apps y software a la medida.\r\nEjecucion de plan de pruebas, pruebas de escritorio, preparacion de datos de prueba, preparacion de ambientes para verificar que los requisitos se hayan cumplido.\r\nRegistro de evidencia de las pruebas realizadas.\r\nConocimiento de herramientas para registro y seguimiento de incidencias.\r\nConfiguracion de ambientes para pruebas\r\nSoporte a usuarios.', 4, NULL, NULL, 6000.00, 8000.00, NULL, NULL, NULL, 2, '1', 1),
(3, 'DESARROLLADOR WEB', 'Contratacion Tiempo completo Permanente', 1, NULL, NULL, 4000.00, 6000.00, NULL, NULL, NULL, 3, '1', 1),
(4, 'DESARROLLADOR WEB', 'Ofrecemos: Sueldo competitivo, prestaciones superiores a la ley seguro de vida, seguro de gastos medicos mayores, comedor, fondo de ahorro, etc   bonos por desempeño', 1, NULL, NULL, 6000.00, 10000.00, NULL, NULL, NULL, 4, '1', 1),
(5, 'Desarrollo Web', 'Somos una empresa mexicana proveedora de servicios de tecnologia de informacion de mision critica, busca nuevos integrantes para fortalecer su equipo de trabajo', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, '1', 0),
(6, 'ANALISTA DE SISTEMAS', 'Principales funciones:  Medir utilizacion de herramientas Soporte a Usuarios Plan de Actualizacion Medir efectividad de la informacion', 3, NULL, NULL, 6000.00, 10000.00, NULL, NULL, NULL, 2, '1', 1),
(7, 'Analista UI Jr.', 'Beneficios: Tarjeta de Descuentos Medicos 8 dias de vacaciones Seguro de Vida Gastos funerarios', 3, NULL, NULL, 3000.00, 6000.00, NULL, NULL, NULL, 3, '1', 1),
(8, 'DBA  Oracle', 'OFRECEMOS: Sueldo competitivo. Prestaciones de ley y superiores. Horario laboral de lunes a viernes. Lugar de trabajo Periferico Sur.', 5, NULL, NULL, 5000.00, 7000.00, NULL, NULL, NULL, 4, '1', 1),
(9, 'Administrador base de datos DBA Postgres', 'Actividades: Administracion de la infraestructura de TI', 5, NULL, NULL, 4500.00, 5500.00, NULL, NULL, NULL, 3, '1', 1),
(10, 'CONSULTOR IT , MANAGER, LIDER DE PROYECTO IT', 'OFRECEMOS: SEGURO MEDICO DE GASTOS MAYORES. CRECIMIENTO. ESTABILIDAD. DESARROLLO.', 2, NULL, NULL, 8000.00, 10000.00, NULL, NULL, NULL, 2, '1', 1),
(11, 'ESPECIALISTA EN RECURSOS HUMANOS', 'IMPORTANTE FINANCIERA\r\nAPOYO ECONOMICO FAMILIAR\r\nSOLICITA\r\nESPECIALISTA EN RECURSOS HUMANOS\r\nPARA LA ZONA EN ALVARO OBREGON \r\nCON DISPONIBILIDAD PARA ACUDIR A TACUBAYA, SAN BERNABE, CUAJIMALPA\r\nManejara a su cargo 10 sucursales aledanias a la zona\r\n', 6, 0, 0, 13000.00, 15000.00, '', '', 2, 6, '1', 1),
(12, 'Becario ambiental', 'Empresa dedicada a la fabricacion de productos quimicos solicita becario ambiental con experiencia, puntualidad,\r\n', 7, 0, 0, 8000.00, 12000.00, '', '', 0, 7, '1', 1),
(13, 'ANALISTA DE SOPORTE A LA PRODUCCION', 'Contratacion Tiempo completo o Permanente\r\n', 8, 0, 0, 0.00, 0.00, '', '', 2, 8, '', 1),
(14, 'CONSULTOR DE PROCESOS RAD', 'Contratacion: Tiempo completo Permanente\r\n', 10, 22, 0, 18000.00, 18000.00, '', '', 2, 8, '1', 1),
(15, 'COORDINADOR DE MERCADOTECNIA', 'Contratacion Permanente', 9, 20, 0, 7000.00, 7000.00, '8:00 am', '18:00 pm', 1, 9, '1', 1),
(16, 'Desarrollador movil Android/IOS JR12 a 20 mil y SR20 a 35mil', 'En IMDS instituto mexicano de desarrolladores de software solicitamos\r\nDesarrollador movil\r\n', 1, 24, 0, 15000.00, 30000.00, '', '', 1, 10, '1', 1),
(17, 'Administrador de la Base de Datos MS SqlServer', 'En GINgroup ofrecemos las mejores soluciones integrales de vanguardia en administración de capital humano de esta manera nuestros clientes pueden enfocar su talento y sus recursos en hacer crecer su negocio, mientras nosotros nos encargamos de administrar su capital humano de manera eficiente.\r\n', 5, 0, 0, 22000.00, 30000.00, '', '', 5, 10, '1', 1),
(20, 'PROGRAMADOR WEB', 'Desarrollar apis en java para consulta de services', 1, 0, 0, 3000.00, 3000.00, NULL, NULL, NULL, 6, '0', 1);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
