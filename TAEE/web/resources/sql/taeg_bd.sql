-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost
-- Tiempo de generación: 04-08-2019 a las 01:18:27
-- Versión del servidor: 5.7.24
-- Versión de PHP: 7.0.33

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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_delete_vacante_estadia` (IN `vacante_id` INT)  BEGIN
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
	IF(status_empresa='3')THEN
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_ciudades` ()  BEGIN
	SELECT id_ciudad,id_estado,nombre_ciudad from ciudad;
END$$

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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_empresas` (`tipo` INT)  BEGIN
	SELECT id_empresa,direccion,nombre,id_estado,id_ciudad,
	codigo_postal,id_usuario,num_telefono,folio_convenio,rfc,
	status,correo_empresa
	FROM empresa
	where status=tipo;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_empresas_egresados` (IN `tipo` INT)  BEGIN
	SELECT id_empresa,direccion,em.nombre as nombre_empresa,
	em.id_estado,em.id_ciudad,codigo_postal,
	num_telefono,rfc,em.status as status_empresa,correo_empresa,
	us.id_usuario,us.nombre as nombre_usuario,apellidos,
	email,id_rol,us.status as status_usuario,es.nombre_estado,ci.nombre_ciudad,
	(SELECT COUNT(*) from vacante v where v.id_empresa=em.id_empresa )as vacantes_publicadas
	FROM empresa em
	JOIN usuario us on em.id_usuario=us.id_usuario
    JOIN estado es on em.id_estado= es.id_estado
    JOIN ciudad ci on em.id_ciudad= ci.id_ciudad and ci.id_estado= es.id_estado
	where em.status=tipo;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_estados` ()  BEGIN
	SELECT id_estado,nombre_estado from estado;
END$$

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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_municipios` (`tipo` INT)  BEGIN
	SELECT id_ciudad,id_estado,nombre_ciudad from ciudad
	where id_estado= tipo;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_search_adminEstadia` (`empresa_id` INT)  BEGIN
	SELECT id_vacante,titulo,vacante_desc,v.id_perfil, p.nombre_perfil,c.carrera_desc,
	edad_min,edad_max,salario_min,salario_max,n.nombre_nivel,
	hora_inicial,hora_final,experiencia,em.id_empresa,em.nombre,
	v.status as status_empresa,ayuda_economica,n.id_nivel,c.id_carrera
	FROM vacante v
	JOIN empresa em on v.id_empresa=em.id_empresa 
    JOIN perfil p on v.id_perfil=p.id_perfil
    JOIN carreras c ON p.id_carrera=c.id_carrera
    join nivel_academico n on c.id_nivel=n.id_nivel
	where  em.status='3'
	and em.id_empresa= empresa_id
	and v.status='1';
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_vacantes_adminEstadia` ()  BEGIN
	SELECT id_vacante,titulo,vacante_desc,v.id_perfil, p.nombre_perfil,c.carrera_desc,
	edad_min,edad_max,salario_min,salario_max,n.nombre_nivel,
	hora_inicial,hora_final,experiencia,em.id_empresa,em.nombre,
	v.status as status_empresa,ayuda_economica,n.id_nivel,c.id_carrera
	FROM vacante v
	JOIN empresa em on v.id_empresa=em.id_empresa 
    JOIN perfil p on v.id_perfil=p.id_perfil
    JOIN carreras c ON p.id_carrera=c.id_carrera
    join nivel_academico n on c.id_nivel=n.id_nivel
	where  em.status='3'
	and v.status='1';
END$$

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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_vacantes_usuario` (IN `empresa_id` INT)  BEGIN
	SELECT id_vacante,titulo,vacante_desc,v.id_perfil, p.nombre_perfil,c.carrera_desc,
	edad_min,edad_max,salario_min,salario_max,n.nombre_nivel,
	hora_inicial,hora_final,experiencia,em.id_empresa,
	v.status as status_empresa,ayuda_economica,n.id_nivel,c.id_carrera
	FROM vacante v
	JOIN empresa em on v.id_empresa=em.id_empresa 
    JOIN perfil p on v.id_perfil=p.id_perfil
    JOIN carreras c ON p.id_carrera=c.id_carrera
    join nivel_academico n on c.id_nivel=n.id_nivel
	where em.id_empresa=empresa_id
	and em.status='2'
	and v.status='1';
END$$

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
			VALUES(var_id,ls_direccion,ls_nombre,ln_id_estado,ln_id_ciudad,ls_codigo_postal,null,ls_num_telefono,ls_folio_convenio,ls_rfc,ls_status,ls_correo_empresa);
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_registrar_user` (IN `nombre_u` VARCHAR(50), IN `apellido_u` VARCHAR(50), IN `email_u` VARCHAR(50), IN `pass` VARCHAR(50), IN `direccion_em` VARCHAR(50), IN `nombre_em` VARCHAR(50), IN `estado` INT, IN `ciudad` INT, IN `cp` INT, IN `telefono` VARCHAR(13), IN `rfc_em` VARCHAR(15), IN `email_em` VARCHAR(50))  BEGIN
    DECLARE flag boolean;
    DECLARE count int;
    DECLARE id_user int;
    DECLARE msj varchar (100);
    DECLARE countEmail int;
    DECLARE countRFC int;
    SELECT (max(id_usuario) +1) as id_u from usuario INTO id_user;
    if(id_user<1)THEN
    	set id_user=1;
    end if;
    SELECT COUNT(*) FROM usuario WHERE email=email_u INTO countEmail;
    SELECT COUNT(*) FROM empresa WHERE rfc=rfc_em INTO countRFC;

    IF(countEmail >0 OR countRFC>0)THEN
        SET flag=false;
        SET msj='Un usuario con este email o RFC ya ha sido registrado';
        SET id_user=0;
    ELSE
        INSERT INTO usuario (id_usuario,nombre,apellidos,email,password,id_rol,status)      
        VALUES (id_user,nombre_u,apellido_u,email_u,MD5(pass),3,1);
        
        SET COUNT = ROW_COUNT();
        IF(COUNT>0) THEN 
        
            INSERT INTO empresa (direccion, nombre, id_estado, id_ciudad,        
                codigo_postal,id_usuario,num_telefono,rfc, status,correo_empresa) 
            VALUES(direccion_em,
                nombre_em,estado,ciudad,cp,id_user,telefono,rfc_em,1,email_em);
            SET COUNT = ROW_count();
            IF (COUNT>0) THEN
            SET flag = true;
            SET msj = 'Registro exitoso. Su solicitud esta en proceso.';
            ELSE
            SET flag = false;
            SET msj = 'Error, no se insertó en la segunda tabla.';
            END IF;
        ELSE
            SET flag = false;
            SET msj = 'Error no se inserto en la primera tabla';
        END IF;
    END IF;



    
    SELECT flag, msj, id_user;
END$$

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

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_search_empresas_estadias` (`tipo` INT, `empresa_id` INT)  BEGIN
	SELECT id_empresa,direccion,nombre,id_estado,id_ciudad,
	codigo_postal,id_usuario,num_telefono,folio_convenio,rfc,
	status,correo_empresa
	FROM empresa
	where status=tipo and id_empresa=empresa_id;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bulkload_empresa`
--

CREATE TABLE `bulkload_empresa` (
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
  `correo_empresa` varchar(500) NOT NULL
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
(6, 6, 'Calle 102', '', 'Estado de mexico', 'Alvaro Obregon', '558879', NULL, '', '', '', 'El campo Nombre esta en blanco. La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.El campo Telefono esta en blanco. El campo Folio de convenio esta en blanco. El campo RFC de la Empresa esta en blanco. El campo Correo de Empresa esta en blanco', ''),
(7, 1, 'Tlalpan, Ciudad de México (Distrito Federal)', 'Universidad Pedregal Del Sur S C', 'CDMX', 'Alvaro Obregon', '50000', NULL, '5566778899', 'CONV019293', 'UPS7172639', NULL, 'upds@gmail.com'),
(7, 2, 'Calle 2 el faisan', 'Grupo salinas', 'CDMX', 'Cuauhtemoc', '57888', NULL, '555565435', 'CONV826364', 'GP88273746', NULL, 'salinas.e@gmail.com'),
(7, 3, 'Calle 5 el pericles', 'JOBFIT', 'CDMX', 'Gustavo A. Madero', '67888', NULL, '45366789', 'CONV5152367', 'JO626536', NULL, 'jobfit@yahoo.com'),
(7, 4, 'Almoyta num 91', 'MAR SYSTEMS', 'Estado de mexico', 'Almoloya de Juarez', '5466577', NULL, '5566442233', 'CONV526373', 'MS9173636', NULL, 'mar@hotmail.com'),
(7, 5, 'Calle pequeña', 'Royal Software', 'Guanajuato', 'Jerecuaro', '5454646', NULL, '67577383', 'CONV192183', 'RSFA554556', 'El estado no existe en la base de datos.La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.', 'rsf@gmail.com'),
(7, 6, 'Calle 102', '', 'Estado de mexico', 'Alvaro Obregon', '558879', NULL, '', '', '', 'El campo Nombre esta en blanco. La ciudad no existe en la base de datos o esa ciudad no pertenece a el estado elegido.El campo Telefono esta en blanco. El campo Folio de convenio esta en blanco. El campo RFC de la Empresa esta en blanco. El campo Correo de Empresa esta en blanco', ''),
(8, 1, 'BLVD. GRAN SUR 100, PEDREGAL DE CARRASCO', 'Grupo Salinas', 'CDMX', 'coyoacan', '23211', NULL, '5517201313', 'CO1934765', 'GS193489U3', NULL, 'gruposalinas@gmail.com'),
(8, 2, 'Cafetal 240, Granjas México', 'Grupo Tecnovidrio, S.A. de C.V.', 'CDMX', 'Gustavo A. Madero', '8400', NULL, '55 3000 2000', 'CO1934464', 'GT34212T3', NULL, 'grupotec@gmail.com'),
(8, 3, ' Av. Insurgentes Sur 670', 'Bacher Zoppi, S.A. de C.V.', 'CDMX', 'Gustavo A. Madero', '3100', NULL, '55 3640 2000', 'CO1633469', 'BAZO23212', NULL, 'zoppi@gmail.com'),
(8, 4, 'Av Nuevo León 202, Hipódromo', 'Operadora de Personal Incoraxis', 'CDMX', 'coyoacan', '6100', NULL, '55 5584 2918', 'CO2623479', 'OPI1423452', NULL, 'incoraxis@hotmail.com'),
(9, 1, 'BLVD. GRAN SUR 100, PEDREGAL DE CARRASCO', 'Grupo Luxis', 'CDMX', 'coyoacan', '23211', NULL, '5517201313', 'CO1934765', 'GS193489U3', NULL, 'grupoluxis@gmail.com'),
(9, 2, 'Cafetal 240, Granjas México', 'Grupo Tecnovidrio, S.A. de C.V.', 'CDMX', 'Gustavo A. Madero', '8400', NULL, '55 3000 2000', 'CO1934464', 'GT34212T3', NULL, 'grupotec@gmail.com'),
(9, 3, ' Av. Insurgentes Sur 670', 'Bacher Zoppi, S.A. de C.V.', 'CDMX', 'Gustavo A. Madero', '3100', NULL, '55 3640 2000', 'CO1633469', 'BAZO23212', NULL, 'zoppi@gmail.com'),
(9, 4, 'Av Nuevo León 202, Hipódromo', 'Operadora de Personal Incoraxis', 'CDMX', 'coyoacan', '6100', NULL, '55 5584 2918', 'CO2623479', 'OPI1423452', NULL, 'incoraxis@hotmail.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `carreras`
--

CREATE TABLE `carreras` (
  `id_carrera` int(11) NOT NULL,
  `id_nivel` int(11) DEFAULT NULL,
  `carrera_desc` varchar(100) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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

CREATE TABLE `ciudad` (
  `id_ciudad` int(11) NOT NULL,
  `nombre_ciudad` varchar(50) DEFAULT NULL,
  `id_estado` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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

CREATE TABLE `conocimiento` (
  `id_conocimiento` int(11) NOT NULL,
  `conoc_desc` varchar(100) DEFAULT NULL,
  `id_perfil` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `conocimiento`
--

INSERT INTO `conocimiento` (`id_conocimiento`, `conoc_desc`, `id_perfil`) VALUES
(1, 'Aplicar tecnicas de programacion para apps web ', 1),
(2, 'Saber organizar para automatizar procesos ', 1),
(3, 'Implementar tecnicas avanzadas de desarrollo de SW para automatizar procesos organizacionales', 3),
(4, 'Implementar y administrar sistemas manejadores de la BD', 5),
(5, 'Administrar servidores de Base de Datos\r\n', 5),
(6, 'Saber liderar un equipo con metodos del Pmbok', 2),
(7, 'implementar y realizar soporte tecnico', 4),
(8, 'Administración Financiera ', 6),
(9, 'Administración Gerencial', 6),
(10, 'Metodos ad hoc', 7),
(11, ' Google analytics', 9),
(12, 'Hubspot', 9),
(13, 'Saber implementar logica de programación ', 1),
(14, 'Manejo de al menos un lenguaje de programación', 1),
(15, 'Administrar estructuras de Base de Datos', 5),
(16, 'Implementar equipo de computo de acuerdo con las necesidades de una organizacion', 4),
(17, 'implementar sistemas operativos de acuerdo con las necesidades de una organizacion', 4),
(18, 'implementar redes locales de acuerdo con las necesidades de una organizacion', 4),
(19, 'Implementar sistemas de calidad ', 3),
(20, 'Saber liderar un equipo con metodos del Pmbok', 11),
(21, 'implementar equipo de cómputo de acuerdo con las necesidades de una organización\r\n', 12),
(22, 'implementar sistemas operativos de acuerdo con las necesidades de una organización\r\n', 12),
(23, 'implementar redes locales de acuerdo con las necesidades de una organización\r\n', 12);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `conocimiento_vac`
--

CREATE TABLE `conocimiento_vac` (
  `id_vacante` int(11) NOT NULL,
  `id_conocimiento` int(11) NOT NULL
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
(21, 1),
(22, 1),
(23, 1),
(24, 1),
(26, 1),
(31, 1),
(33, 1),
(34, 1),
(40, 1),
(4, 2),
(31, 2),
(40, 2),
(6, 3),
(7, 3),
(25, 3),
(27, 3),
(8, 4),
(32, 4),
(37, 4),
(8, 5),
(9, 5),
(37, 5),
(10, 6),
(28, 6),
(29, 6),
(30, 6),
(36, 6),
(41, 6),
(1, 7),
(2, 7),
(38, 7),
(42, 7),
(11, 8),
(11, 9),
(12, 10),
(14, 11),
(13, 12),
(15, 12),
(16, 13),
(31, 13),
(33, 13),
(40, 13),
(16, 14),
(31, 14),
(40, 14),
(17, 15),
(37, 15),
(38, 16),
(42, 16),
(38, 17),
(42, 17),
(38, 18),
(42, 18),
(35, 20),
(39, 21),
(39, 22),
(39, 23);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empresa`
--

CREATE TABLE `empresa` (
  `id_empresa` int(11) NOT NULL,
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
  `correo_empresa` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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
(13, 'Tlalpan, Ciudad de México (Distrito Federal)', 'Universidad Pedregal Del Sur S C', 2, 2, '50000', NULL, '5566778899', 'CONV019293', 'UPS7172639', '0', 'upds@gmail.com'),
(14, 'El barro 320', 'ANIPROX SOFT', 1, 4, '57800', 9, '5562386648', NULL, 'RFCEMP012', '1', 'aniproxsoft@gmail.com'),
(15, 'Las Flores 5454', 'Mozcaltli', 2, 6, '654554', NULL, '8558545515', 'FO515151', 'RFC2116541631', '0', 'moz@hotmail.com'),
(16, 'Matanzas 888', 'Phoenix', 1, 28, '578282', NULL, '5636363663', 'CONV33773', 'RFC26267627', '0', 'ave@hotmail.com'),
(17, 'Casa de las flores', 'Ubisoft', 1, 1, '15432', 10, '5533443322', NULL, 'RFCASEAA', '1', 'ubi@soft.com'),
(18, 'El caiman 34', 'King Soft', 1, 6, '5566447', 11, '5546438394', NULL, 'RFC83847HDH', '1', 'king@yahoo.com'),
(19, 'Loma bonita 582', 'Luteam', 1, 2, '572021', NULL, '5915141531', 'co524125156546655645', '15szc5s1xc5ds51c51d6', '3', 'lu@hotmaul.com'),
(20, 'LAS FLORES 678', 'Prosystem', 2, 6, '896665', NULL, '8662525652', 'X5Z75ZXXXXXXXXXXXXF5', 'RFC88848484448428888', '3', 'pro@gmail.com'),
(21, 'seneca 123', 'QUICK', 2, 9, '96559', NULL, '8454548498', 'FOL454548', 'rfc4445877', '3', 'quick@gmail.com'),
(22, 'BLVD. GRAN SUR 100, PEDREGAL DE CARRASCO', 'Grupo Luxis', 2, 2, '23211', NULL, '5517201313', 'CO1934765', 'GS193489U3', '3', 'grupoluxis@gmail.com'),
(23, 'Cafetal 240, Granjas México', 'Grupo Tecnovidrio, S.A. de C.V.', 2, 2, '8400', NULL, '55 3000 2000', 'CO1934464', 'GT34212T3', '3', 'grupotec@gmail.com'),
(24, ' Av. Insurgentes Sur 670', 'Bacher Zoppi, S.A. de C.V.', 2, 2, '3100', NULL, '55 3640 2000', 'CO1633469', 'BAZO23212', '3', 'zoppi@gmail.com'),
(25, 'Av Nuevo León 202, Hipódromo', 'Operadora de Personal Incoraxis', 2, 2, '6100', NULL, '55 5584 2918', 'CO2623479', 'OPI1423452', '3', 'incoraxis@hotmail.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estado`
--

CREATE TABLE `estado` (
  `id_estado` int(11) NOT NULL,
  `nombre_estado` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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

CREATE TABLE `habilidad` (
  `id_habilidad` int(11) NOT NULL,
  `habilidad_desc` varchar(50) DEFAULT NULL
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

CREATE TABLE `habilidad_vac` (
  `id_vacante` int(11) NOT NULL,
  `id_habilidades` int(11) NOT NULL
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
(8, 2),
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
(16, 1),
(16, 2),
(16, 3),
(16, 4),
(16, 5),
(16, 6),
(16, 7),
(16, 8),
(16, 9),
(16, 10),
(16, 11),
(16, 12),
(17, 1),
(17, 2),
(17, 3),
(17, 4),
(18, 1),
(18, 2),
(19, 1),
(19, 2),
(20, 1),
(20, 4),
(21, 1),
(23, 2),
(27, 1),
(27, 2),
(27, 3),
(27, 4),
(27, 5),
(27, 6),
(27, 7),
(27, 8),
(27, 9),
(27, 10),
(27, 11),
(27, 12),
(28, 1),
(28, 5),
(29, 1),
(29, 5),
(30, 1),
(30, 5),
(31, 1),
(31, 2),
(31, 3),
(31, 4),
(31, 5),
(31, 6),
(31, 7),
(31, 8),
(31, 9),
(31, 10),
(31, 11),
(31, 12),
(32, 10),
(34, 1),
(35, 2),
(35, 3),
(35, 4),
(35, 5),
(35, 6),
(35, 7),
(35, 8),
(35, 9),
(35, 10),
(35, 11),
(35, 12),
(36, 1),
(36, 2),
(36, 3),
(36, 4),
(36, 5),
(36, 6),
(36, 7),
(36, 8),
(36, 9),
(36, 10),
(36, 11),
(36, 12),
(37, 2),
(37, 11),
(38, 1),
(38, 2),
(38, 4),
(39, 1),
(39, 2),
(39, 3),
(39, 4),
(39, 5),
(39, 6),
(39, 7),
(39, 8),
(39, 9),
(39, 10),
(39, 11),
(39, 12),
(40, 1),
(40, 2),
(40, 3),
(40, 4),
(40, 5),
(40, 6),
(40, 7),
(40, 8),
(40, 9),
(40, 10),
(40, 11),
(40, 12),
(41, 1),
(41, 2),
(41, 3),
(41, 4),
(41, 5),
(41, 6),
(41, 7),
(41, 8),
(41, 9),
(41, 10),
(41, 11),
(41, 12),
(42, 1),
(42, 2),
(42, 3),
(42, 4),
(42, 5),
(42, 6),
(42, 7),
(42, 8),
(42, 9),
(42, 10),
(42, 11),
(42, 12);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nivel_academico`
--

CREATE TABLE `nivel_academico` (
  `id_nivel` int(11) NOT NULL,
  `nombre_nivel` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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

CREATE TABLE `perfil` (
  `id_perfil` int(11) NOT NULL,
  `nombre_perfil` varchar(50) NOT NULL,
  `id_carrera` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `perfil`
--

INSERT INTO `perfil` (`id_perfil`, `nombre_perfil`, `id_carrera`) VALUES
(1, 'Programador', 1),
(2, 'Líder de proyecto', 4),
(3, 'Analista de software', 1),
(4, 'Soporte Tecnico', 4),
(5, 'Administrador de Base de datos', 1),
(6, 'Especialista en Recursos Humanos', 3),
(7, 'Consultor ambiental', 8),
(8, 'Analista de soporte a la produccion', 9),
(9, 'Cordinador de mercadotecnia', 7),
(10, 'Consultor de procesos', 7),
(11, 'Líder de Proyecto', 1),
(12, 'Soporte Tecnico', 1),
(13, 'Diseñador Front-End', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellidos` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `id_rol` int(11) NOT NULL,
  `status` varchar(1) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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
(9, 'Tony ', 'RodzBar', 'barrera.01yea@gmail.com', 'e10adc3949ba59abbe56e057f20f883e', 3, '1'),
(10, 'Rogelio', 'Espinoza', 'esp@hotmail.com', 'e10adc3949ba59abbe56e057f20f883e', 3, '1'),
(11, 'Enrrique', 'Gaitan', 'erri@hotmail.com', 'e10adc3949ba59abbe56e057f20f883e', 3, '1');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_rol`
--

CREATE TABLE `usuario_rol` (
  `id_rol` int(11) NOT NULL,
  `rol_nombre` varchar(50) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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

CREATE TABLE `vacante` (
  `id_vacante` int(11) NOT NULL,
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
  `ayuda_economica` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `vacante`
--

INSERT INTO `vacante` (`id_vacante`, `titulo`, `vacante_desc`, `id_perfil`, `edad_min`, `edad_max`, `salario_min`, `salario_max`, `hora_inicial`, `hora_final`, `experiencia`, `id_empresa`, `status`, `ayuda_economica`) VALUES
(1, 'Tester Jr contratacion inmediata', 'MAR SYSTEMS, es una empresa Mexicana Lider en Tecnologias de la Informacion, con presencia Internacional, y con mas de 15 años proporcionando servicios de TI.', 4, NULL, NULL, 850.00, 1100.00, NULL, NULL, 0, 5, '1', 1),
(2, 'TESTER', 'Funciones: \\r\\nPruebas funcionales, de regresion, integracion y uat en apps y software a la medida.\\r\\nEjecucion de plan de pruebas, pruebas de escritorio, preparacion de datos de prueba, preparacion de ambientes para verificar que los requisitos se hayan cumplido.\\r\\nRegistro de evidencia de las pruebas realizadas.\\r\\nConocimiento de herramientas para registro y seguimiento de incidencias.\\r\\nConfiguracion de ambientes para pruebas\\r\\nSoporte a usuarios.', 4, NULL, NULL, 6000.00, 8000.00, NULL, NULL, 0, 2, '1', 1),
(3, 'DESARROLLADOR WEB', 'Contratacion Tiempo completo Permanente', 1, NULL, NULL, 4000.00, 6000.00, NULL, NULL, NULL, 3, '1', 1),
(4, 'DESARROLLADOR WEB', 'Ofrecemos: Sueldo competitivo, prestaciones superiores a la ley seguro de vida, seguro de gastos medicos mayores, comedor, fondo de ahorro, etc   bonos por desempeño', 1, NULL, NULL, 6000.00, 10000.00, NULL, NULL, NULL, 4, '0', 1),
(5, 'Desarrollo Web', 'Somos una empresa mexicana proveedora de servicios de tecnologia de informacion de mision critica, busca nuevos integrantes para fortalecer su equipo de trabajo', 1, NULL, NULL, NULL, NULL, '8:00 am', '16:00 pm', NULL, 2, '1', 0),
(6, 'ANALISTA DE SISTEMAS', 'Principales funciones:  Medir utilizacion de herramientas Soporte a Usuarios Plan de Actualizacion Medir efectividad de la informacion', 3, NULL, NULL, 6000.00, 10000.00, NULL, NULL, NULL, 2, '1', 1),
(7, 'Analista UI Jr.', 'Beneficios: Tarjeta de Descuentos Medicos 8 dias de vacaciones Seguro de Vida Gastos funerarios', 3, NULL, NULL, 3000.00, 6000.00, NULL, NULL, NULL, 3, '1', 1),
(8, 'DBA  Oracle', 'OFRECEMOS: Sueldo competitivo. Prestaciones de ley y superiores. Horario laboral de lunes a viernes. Lugar de trabajo Periferico Sur.', 5, NULL, NULL, 5000.00, 7000.00, '07:58 am', '06:00 pm', 0, 4, '1', 1),
(9, 'Administrador base de datos DBA Postgres', 'Actividades: Administracion de la infraestructura de TI en proyectos individuales', 5, NULL, NULL, 4500.00, 5500.00, NULL, NULL, 0, 3, '1', 1),
(10, 'CONSULTOR IT , MANAGER, LIDER DE PROYECTO IT', 'OFRECEMOS: SEGURO MEDICO DE GASTOS MAYORES. CRECIMIENTO. ESTABILIDAD. DESARROLLO.', 2, NULL, NULL, 8000.00, 10000.00, NULL, NULL, NULL, 2, '1', 1),
(11, 'ESPECIALISTA EN RECURSOS HUMANOS', 'IMPORTANTE FINANCIERA\r\nAPOYO ECONOMICO FAMILIAR\r\nSOLICITA\r\nESPECIALISTA EN RECURSOS HUMANOS\r\nPARA LA ZONA EN ALVARO OBREGON \r\nCON DISPONIBILIDAD PARA ACUDIR A TACUBAYA, SAN BERNABE, CUAJIMALPA\r\nManejara a su cargo 10 sucursales aledanias a la zona\r\n', 6, 0, 0, 13000.00, 15000.00, '', '', 2, 6, '1', 1),
(12, 'Becario ambiental', 'Empresa dedicada a la fabricacion de productos quimicos solicita becario ambiental con experiencia, puntualidad,\r\n', 7, 0, 0, 8000.00, 12000.00, '', '', 0, 7, '1', 1),
(13, 'ANALISTA DE SOPORTE A LA PRODUCCION', 'Contratacion Tiempo completo o Permanente\r\n', 8, 0, 0, 0.00, 0.00, '', '', 2, 8, '1', 1),
(14, 'CONSULTOR DE PROCESOS RAD', 'Contratacion: Tiempo completo Permanente\r\n', 10, 22, 0, 18000.00, 18000.00, '', '', 2, 8, '1', 1),
(15, 'COORDINADOR DE MERCADOTECNIA', 'Contratacion Permanente', 9, 20, 0, 7000.00, 7000.00, '8:00 am', '18:00 pm', 1, 9, '1', 1),
(16, 'Desarrollador movil Android/IOS JR12 a 20 mil y SR20 a 35mil', 'En IMDS instituto mexicano de desarrolladores de software solicitamos Desarrollador movil', 1, 24, 12, 20000.00, 30000.00, NULL, NULL, 1, 10, '1', 1),
(17, 'Administrador de la Base de Datos MS SqlServer', 'En GINgroup ofrecemos las mejores soluciones integrales de vanguardia en administración de capital humano de esta manera nuestros clientes pueden enfocar su talento y sus recursos en hacer crecer su negocio, mientras nosotros nos encargamos de administrar su capital humano de manera eficiente.\\\\r\\\\n', 5, 18, 40, 22000.00, 30000.00, '09:00 pm', '07:00 am', 5, 10, '1', 1),
(20, 'PROGRAMADOR WEB', 'Desarrollar apis en java para consulta de services', 1, 0, 0, 3000.00, 3000.00, NULL, NULL, NULL, 6, '0', 1),
(21, 'sssss', 'sssssssssssssssssssssssss', 1, 33, 33, 3333.00, 333.00, '11:11 am', '11:11 am', 3, 10, '0', 0),
(22, 'aaaaaaaaaaa', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 1, 11, 11, NULL, NULL, NULL, NULL, 1, 10, '0', 0),
(23, 'aaaaa', 'ssssssssssssssssss', 1, NULL, NULL, NULL, NULL, NULL, NULL, 1, 10, '0', 0),
(24, 'aaaax', 'xxxxxxxxxxxxxxxxxxxxxxxxx', 2, NULL, NULL, NULL, NULL, NULL, NULL, 1, 10, '0', 0),
(25, 'dcccccccc', 'ccvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv', 3, NULL, NULL, NULL, NULL, NULL, NULL, 0, 10, '0', 0),
(26, 'hhhhhhhh', 'nnnnnnnnnnnnnnnnnnn', 1, NULL, NULL, NULL, NULL, NULL, NULL, 0, 10, '0', 0),
(27, 'Analista de software', 'Analizar el funcionamiento del software', 3, 23, 60, 50000.00, 7000.00, '09:00 am', '06:00 pm', 1, 10, '1', 0),
(28, 'lider de proyecto de ventas', 'esto es la prueba de update con el buscador activo', 2, NULL, NULL, 1300.00, 3000.00, '07:00 am', '04:00 pm', 0, 19, '1', 1),
(29, 'lider de proyecto p', 'Solicita puesto para lider de proyecto para desarrollar un proyecto de estadias ', 2, NULL, NULL, 1300.00, 3000.00, '07:00 am', '04:00 pm', 0, 19, '0', 1),
(30, 'lider de proyecto p', 'kjdfvbkjsbvdj', 2, NULL, NULL, 1300.00, 3000.00, '07:00 am', '04:00 pm', 0, 19, '0', 1),
(31, 'Programacion POO', 'Necesitamos programadores que se especialicen en programacion orientada a objetos ', 1, NULL, NULL, NULL, NULL, NULL, NULL, 0, 19, '1', 0),
(32, 'Base de datos POO', 'Rol de base de datos que maneje oracle un 50%', 5, NULL, NULL, 750.00, 1500.00, NULL, NULL, 0, 2, '0', 1),
(33, 'Programador de App web', 'Desarrollador de aplicaciones web y moviles', 1, NULL, NULL, 500.00, 800.00, NULL, NULL, 0, 19, '1', 1),
(34, 'Implementacion de software', 'Es para implementar aplicaciones', 1, NULL, NULL, NULL, NULL, NULL, NULL, 0, 21, '0', 0),
(35, 'lider de proyecto', 'Asegurar la implementación de iniciativas y estrategia comercial. Gestión y seguimiento a proyectos apegado al plan comercial y forecast de ventas. Análisis de bases de datos para presentar propuestas de mejora. Elaboración de reportes ejecutivos, presentación de resultados para la Dirección Comercial. Evaluación de la importancia y efectos de los riesgos asociados con la toma de decisiones. Reunir, analizar e interpretar todos los datos que le sean encomendados por la empresa.\\\\\\\\r\\\\\\\\n', 11, NULL, NULL, 800.00, 1500.00, NULL, NULL, 0, 22, '1', 1),
(36, 'lider de proyecto', 'Desarrollo del plan del proyecto\\\\r\\\\n\\\\r\\\\nIdentificación de los requerimientos y el alcance del proyecto, comunicación, administración de los recursos humanos y materiales\\\\r\\\\n\\\\r\\\\nAdministración de los costos, presupuesto y aseguramiento de la calidad\\\\r\\\\n\\\\r\\\\nReportes de avance de proyecto\\\\r\\\\n\\\\r\\\\nControl de incidentes', 2, NULL, NULL, NULL, NULL, '08:00 am', '07:00 pm', 0, 23, '1', 0),
(37, 'Administrador Base de Datos DB2', 'Mantener en óptimo rendimiento las Bases de Datos mediante procesos de mantenimiento.\\r\\nValidar y actualizar configuraciones.\\r\\nMonitorear 24/7 las condiciones y/o potenciales problemáticas\\r\\nDiagnósticar problemas y aportar una solución efectiva.\\r\\nAdministrar temas de almacenamiento, respaldos, seguridad.\\r\\nManipular objetos de Base de Datos.\\r\\nApoyar en requerimientos diarios sobre ambientes de Desarrollo, Pruebas y Producción.\\r\\nCreación, replicación y mantenimiento de Base de Datos', 5, NULL, NULL, NULL, NULL, NULL, NULL, 0, 25, '1', 0),
(38, 'AUXILIAR DE SISTEMAS', 'Conocimientos en software y hardware de computadora y servidores', 4, NULL, NULL, NULL, NULL, '07:00 am', '05:09 pm', 0, 22, '1', 0),
(39, 'AUXILIAR DE SISTEMAS', ' Mantenimiento correctivo y preventivo a equipos de computo\\r\\nInstalación de redes\\r\\n\\r\\nConfiguración de software y hardware\\r\\n\\r\\nSoporte técnico\\r\\n\\r\\n', 12, 23, 35, 9000.00, 9500.00, '07:00 am', '04:00 pm', 1, 7, '1', 0),
(40, 'DESARROLLADOR .NET', 'desarrolladores con perfil Sr\\r\\n\\r\\nCon conocimientos en Base de datos SQL Server y/o Oracle 11 Y 12', 1, 22, 40, 9000.00, 15000.00, '09:00 am', '07:00 pm', 0, 6, '1', 0),
(41, ' Lider de Proyecto', 'Cumplir con los requisitos de licitaciones de proyectos\\r\\nAnálisis de base de licitaciones\\r\\nEntregar documentación correspondiente\\r\\nJuntas de aclaración\\r\\nSi cubres con el perfil postúlate por este medio \\r\\n\\r\\n', 2, 28, 45, 15000.00, 20000.00, '09:00 am', '07:00 pm', 1, 9, '1', 0),
(42, ' Soporte Técnico', 'Mantenimiento preventivo y correctivo de equipos de cómputo, soporte técnico, software, hardware, mantenimiento a red de datos.', 4, 25, 35, 8000.00, 8000.00, '08:00 am', '05:30 pm', 0, 7, '1', 0);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `bulkload_empresa`
--
ALTER TABLE `bulkload_empresa`
  ADD PRIMARY KEY (`lote`,`id_bulkload`);

--
-- Indices de la tabla `carreras`
--
ALTER TABLE `carreras`
  ADD PRIMARY KEY (`id_carrera`),
  ADD KEY `fk_id_nivel_carrera` (`id_nivel`);

--
-- Indices de la tabla `ciudad`
--
ALTER TABLE `ciudad`
  ADD PRIMARY KEY (`id_ciudad`,`id_estado`),
  ADD KEY `fk_estado_ciudad` (`id_estado`);

--
-- Indices de la tabla `conocimiento`
--
ALTER TABLE `conocimiento`
  ADD PRIMARY KEY (`id_conocimiento`),
  ADD KEY `not null` (`conoc_desc`),
  ADD KEY `fk_conoc_perfil` (`id_perfil`);

--
-- Indices de la tabla `conocimiento_vac`
--
ALTER TABLE `conocimiento_vac`
  ADD PRIMARY KEY (`id_vacante`,`id_conocimiento`),
  ADD KEY `fk_conoc_vac` (`id_conocimiento`);

--
-- Indices de la tabla `empresa`
--
ALTER TABLE `empresa`
  ADD PRIMARY KEY (`id_empresa`),
  ADD KEY `fk_usuario` (`id_usuario`),
  ADD KEY `fk_emp_ciudad` (`id_ciudad`),
  ADD KEY `fk_emp_estado` (`id_estado`);

--
-- Indices de la tabla `estado`
--
ALTER TABLE `estado`
  ADD PRIMARY KEY (`id_estado`);

--
-- Indices de la tabla `habilidad`
--
ALTER TABLE `habilidad`
  ADD PRIMARY KEY (`id_habilidad`),
  ADD KEY `not null` (`habilidad_desc`);

--
-- Indices de la tabla `habilidad_vac`
--
ALTER TABLE `habilidad_vac`
  ADD PRIMARY KEY (`id_vacante`,`id_habilidades`);

--
-- Indices de la tabla `nivel_academico`
--
ALTER TABLE `nivel_academico`
  ADD PRIMARY KEY (`id_nivel`);

--
-- Indices de la tabla `perfil`
--
ALTER TABLE `perfil`
  ADD PRIMARY KEY (`id_perfil`),
  ADD KEY `fk_carrera_vacante_perfil` (`id_carrera`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD KEY `fk_usuario_rol` (`id_rol`);

--
-- Indices de la tabla `usuario_rol`
--
ALTER TABLE `usuario_rol`
  ADD PRIMARY KEY (`id_rol`);

--
-- Indices de la tabla `vacante`
--
ALTER TABLE `vacante`
  ADD PRIMARY KEY (`id_vacante`),
  ADD KEY `fk_perfil` (`id_perfil`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `carreras`
--
ALTER TABLE `carreras`
  MODIFY `id_carrera` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `ciudad`
--
ALTER TABLE `ciudad`
  MODIFY `id_ciudad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT de la tabla `empresa`
--
ALTER TABLE `empresa`
  MODIFY `id_empresa` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT de la tabla `estado`
--
ALTER TABLE `estado`
  MODIFY `id_estado` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `nivel_academico`
--
ALTER TABLE `nivel_academico`
  MODIFY `id_nivel` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `perfil`
--
ALTER TABLE `perfil`
  MODIFY `id_perfil` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `usuario_rol`
--
ALTER TABLE `usuario_rol`
  MODIFY `id_rol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `vacante`
--
ALTER TABLE `vacante`
  MODIFY `id_vacante` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
