
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'KNOWLEDGE_MANAGEMENTDOC';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('KNOWLEDGE_MANAGEMENTDOC','knowledge.adminFeature.ManageDocuments.name',1,'jsp/admin/plugins/knowledge/ManageDocuments.jsp','knowledge.adminFeature.ManageDocuments.description',0,'knowledge',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'KNOWLEDGE_MANAGEMENTDOC';
INSERT INTO core_user_right (id_right,id_user) VALUES ('KNOWLEDGE_MANAGEMENTDOC',1);


--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'KNOWLEDGE_MANAGEMENTTAGS';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('KNOWLEDGE_MANAGEMENTTAGS','knowledge.adminFeature.ManageTags.name',1,'jsp/admin/plugins/knowledge/ManageTags.jsp','knowledge.adminFeature.ManageTags.description',0,'knowledge',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'KNOWLEDGE_MANAGEMENTTAGS';
INSERT INTO core_user_right (id_right,id_user) VALUES ('KNOWLEDGE_MANAGEMENTTAGS',1);

