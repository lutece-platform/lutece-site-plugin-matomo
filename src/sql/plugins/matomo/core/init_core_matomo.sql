
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'MATOMO_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('MATOMO_MANAGEMENT','matomo.adminFeature.ManageMatomo.name',1,'jsp/admin/plugins/matomo/Matomo.jsp','matomo.adminFeature.ManageMatomo.description',0,'matomo',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'MATOMO_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('MATOMO_MANAGEMENT',1);


