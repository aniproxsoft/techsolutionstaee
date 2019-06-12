DELIMITER //
CREATE PROCEDURE sp_bulkload(empresas varchar(65000),bulkloads varchar(65000))
BEGIN
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
  		
			WHILE _index < json_items DO 
  				Select count(*) 
  				from users 
  				where email=trim((Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].correo')), '"', ''))) into exist;
				If(exist>0)Then
					Update  users set 
					nombre=(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].nombre')), '"', '')),
					apellidos=(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,']. apellidos')), '"', '')),
					password=(Select replace(MD5(JSON_EXTRACT(empresas, CONCAT('$[',_index,']. contraseña'))), '"', '')),
					id_rol=JSON_EXTRACT(empresas, CONCAT('$[',_index,'].id_rol'))
					Where email=(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].correo')), '"', ''));

					Set countUpdate= countUpdate+1;
				else
					Insert into users (nombre,apellidos,email,password,id_rol,status)
					VALUES(
						(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].nombre')), '"', '')),
						(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].apellidos')), '"', '')),
						(Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].correo')), '"', '')),
						MD5((Select replace(JSON_EXTRACT(empresas, CONCAT('$[',_index,'].contraseña')), '"', ''))),
						JSON_EXTRACT(empresas, CONCAT('$[',_index,'].id_rol')),
						1

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
			

			Select max(lote)+1 from bulkload into lote_id; 
			if(lote_id is null)then
				Set lote_id=1;
			end if;
			While _index2 < json_items2 Do
				INSERT INTO bulkload (lote,nombre,apellidos,email,password, nombre_rol,observaciones) 
				VALUES (
				lote_id,
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].nombre')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].apellidos')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].correo')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].contraseña')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].nombre_rol')), '"', '')),
				(Select replace(JSON_EXTRACT(bulkloads, CONCAT('$[',_index2,'].observaciones')), '"', ''))
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

			



		
			Select count(*) from bulkload where lote = lote_id into countbulk;
			set suma=(countInsert+countUpdate);

			Set countFail=countbulk-suma;
			
			
			Select lote,bulk_id,nombre, apellidos,email, password,nombre_rol,observaciones,countbulk, countFail,countUpdate,countInsert,msj
			from bulkload where lote =lote_id;
End



