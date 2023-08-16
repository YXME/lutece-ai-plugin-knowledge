
--
-- Structure for table knowledge_document
--

DROP TABLE IF EXISTS knowledge_document;
CREATE TABLE knowledge_document (
id_fichiers int AUTO_INCREMENT,
document_name long varchar NOT NULL,
document_data varchar(50) NOT NULL,
PRIMARY KEY (id_fichiers)
);

--
-- Structure for table knowledge_tag
--

DROP TABLE IF EXISTS knowledge_tag;
CREATE TABLE knowledge_tag (
id_tags int AUTO_INCREMENT,
tag_name varchar(255) default '' NOT NULL,
PRIMARY KEY (id_tags)
);
