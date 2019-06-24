DELIMITER //
CREATE OR REPLACE PROCEDURE sp_get_vacantes_estadia(perfil int, json_conocimiento varchar(65000), 
	json_habilidades varchar(65000))
BEGIN
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
		and v.status='1';
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
		and v.status='1';
    end if;

   	



END


call sp_get_vacantes_estadia(1,'[{"id_conocimiento":1}]
','[
  {
    "id_habilidad": 1,
    "habilidad_desc": "Proactivo"
  },
  {
    "id_habilidad": 2,
    "habilidad_desc": "Trabajo en equipo"
  }
]')







