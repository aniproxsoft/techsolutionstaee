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
End

