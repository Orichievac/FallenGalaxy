-- Script de migration de la base de donnees du serveur PHP vers le serveur J2EE
ALTER TABLE fg_player CHANGE settings_grid settings_grid BOOL NOT NULL DEFAULT '0';
ALTER TABLE fg_player CHANGE settings_fullscreenmap settings_fullscreenmap BOOL NOT NULL DEFAULT '0';
UPDATE fg_player SET settings_grid = 0 WHERE settings_grid = 1;
UPDATE fg_player SET settings_grid = 1 WHERE settings_grid = 2;
UPDATE fg_player SET settings_fullscreenmap = 0 WHERE settings_fullscreenmap = 1;
UPDATE fg_player SET settings_fullscreenmap = 1 WHERE settings_fullscreenmap = 2;
ALTER TABLE fg_player CHANGE register_date registration_date INT(10) UNSIGNED NOT NULL;
ALTER TABLE fg_ally DROP founder;
ALTER TABLE fg_system DROP built;
ALTER TABLE fg_system DROP max_resources;
ALTER TABLE fg_area CHANGE hostile hostile BOOL NOT NULL DEFAULT '0';
UPDATE fg_area SET hostile = 0 WHERE hostile = 1;
UPDATE fg_area SET hostile = 1 WHERE hostile = 2;
DROP TABLE fg_chat;
ALTER TABLE fg_system CHANGE resources available_resources INT(10) UNSIGNED NOT NULL;
ALTER TABLE fg_report_line CHANGE class type TINYINT(4) UNSIGNED NOT NULL;
ALTER TABLE fg_system CHANGE building current_building TINYINT(3) UNSIGNED NOT NULL;
ALTER TABLE fg_system CHANGE building_end current_building_end INT(10) UNSIGNED NOT NULL;
DROP TABLE fg_update;
ALTER TABLE fg_repertory DROP connected;
ALTER TABLE fg_fleet DROP movement_max;
ALTER TABLE fg_applicant ADD UNIQUE (id_player);
RENAME TABLE fg_repertory TO fg_contact;
RENAME TABLE fg_connection TO fg_log_connection;
ALTER TABLE fg_log_connection DROP active;
ALTER TABLE fg_player CHANGE settings_grid settings_grid_visible TINYINT(1) NOT NULL DEFAULT '0';
ALTER TABLE fg_system CHANGE available_resources encoded_available_resources INT(10) UNSIGNED NOT NULL;
ALTER TABLE fg_fleet CHANGE skill0 encoded_skill0 TINYINT(3) UNSIGNED NOT NULL;
ALTER TABLE fg_fleet CHANGE skill1 encoded_skill1 TINYINT(3) UNSIGNED NOT NULL;
ALTER TABLE fg_fleet CHANGE skill2 encoded_skill2 TINYINT(3) UNSIGNED NOT NULL;
ALTER TABLE fg_fleet CHANGE skill_ultimate encoded_skill_ultimate TINYINT(3) UNSIGNED NOT NULL;
ALTER TABLE fg_system CHANGE current_building encoded_current_building TINYINT(3) UNSIGNED NOT NULL;
ALTER TABLE fg_system CHANGE next_building encoded_next_building TINYINT(3) UNSIGNED NOT NULL;
-- 15/12/07 - jgottero
ALTER TABLE fg_research CHANGE technology id_technology TINYINT(3) UNSIGNED NOT NULL;
-- 18/12/07 - jgottero
ALTER TABLE fg_object CHANGE type type ENUM('gate', 'wormhole_noway', 'wormhole_oneway', 'wormhole_twoway', 'wormhole_forming', 'tradecenter', 'pirates', 'bank', 'asteroid', 'asteroid_dense', 'blackhole') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_area ADD z TINYINT UNSIGNED NOT NULL AFTER y;
UPDATE fg_area SET z = FLOOR(80 + RAND() * 40);
ALTER TABLE fg_area CHANGE type type ENUM('standard', 'trade', 'bank', 'pirate') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT 'standard';
ALTER TABLE fg_player DROP settings_fullscreenmap;
ALTER TABLE fg_research CHANGE current current BOOL NOT NULL;
UPDATE fg_research SET current = 0 WHERE current = 1;
UPDATE fg_research SET current = 1 WHERE current = 2;
-- 30/12/07 - jgottero
ALTER TABLE fg_random_name DROP id;
ALTER TABLE fg_random_name DROP INDEX name;
ALTER TABLE fg_random_name ADD PRIMARY KEY (name);
ALTER TABLE fg_player ADD ai BOOL NOT NULL AFTER password;
ALTER TABLE fg_ally ADD ai BOOL NOT NULL AFTER name;
-- 03/01/08 - jgottero
ALTER TABLE fg_tradecenter ADD rate1 FLOAT UNSIGNED NOT NULL AFTER id_tradecenter;
ALTER TABLE fg_tradecenter ADD rate2 FLOAT UNSIGNED NOT NULL AFTER rate1;
ALTER TABLE fg_tradecenter ADD rate3 FLOAT UNSIGNED NOT NULL AFTER rate2;
ALTER TABLE fg_tradecenter ADD rate4 FLOAT UNSIGNED NOT NULL AFTER rate3;
ALTER TABLE fg_tradecenter ADD rate5 FLOAT UNSIGNED NOT NULL AFTER rate4;
ALTER TABLE fg_tradecenter ADD rate6 FLOAT UNSIGNED NOT NULL AFTER rate5;
ALTER TABLE fg_tradecenter ADD rate7 FLOAT UNSIGNED NOT NULL AFTER rate6;
-- 05/01/08 - jgottero
ALTER TABLE fg_tradecenter CHANGE resource0 resource0 INT NOT NULL;
ALTER TABLE fg_tradecenter CHANGE resource1 resource1 INT NOT NULL;
ALTER TABLE fg_tradecenter CHANGE resource2 resource2 INT NOT NULL;
ALTER TABLE fg_tradecenter CHANGE resource3 resource3 INT NOT NULL;
ALTER TABLE fg_tradecenter CHANGE resource4 resource4 INT NOT NULL;
ALTER TABLE fg_tradecenter CHANGE resource5 resource5 INT NOT NULL;
ALTER TABLE fg_tradecenter CHANGE resource6 resource6 INT NOT NULL;
ALTER TABLE fg_tradecenter CHANGE resource7 resource7 INT NOT NULL;
ALTER TABLE fg_tradecenter ADD rate0 FLOAT UNSIGNED NOT NULL AFTER id_tradecenter;
UPDATE fg_tradecenter SET rate0 = 1, rate1 = 1, rate2 = 1, rate3 = 1, rate4 = 1, rate5 = 1, rate6 = 1, rate7 = 1, resource0 = 0, resource1 = 0, resource2 = 0, resource3 = 0, resource4 = 0, resource5 = 0, resource6 = 0, resource7 = 0;
-- 07/01/08 - jgottero
ALTER TABLE fg_player CHANGE password password CHAR(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
-- 10/01/08 - jgottero
ALTER TABLE fg_area DROP z;
ALTER TABLE fg_system ADD ai BOOL NOT NULL AFTER y;
ALTER TABLE fg_planet DROP building_land;
ALTER TABLE fg_planet DROP resources;
ALTER TABLE fg_planet DROP race;
ALTER TABLE fg_planet DROP inhabitants;
ALTER TABLE fg_planet DROP farms;
ALTER TABLE fg_planet DROP laboratories;
ALTER TABLE fg_planet DROP storehouses;
ALTER TABLE fg_planet DROP spaceshipYard;
ALTER TABLE fg_planet DROP defensiveDeck;
ALTER TABLE fg_planet DROP exploitation0;
ALTER TABLE fg_planet DROP exploitation1;
ALTER TABLE fg_planet DROP exploitation2;
ALTER TABLE fg_planet DROP exploitation3;
ALTER TABLE fg_planet DROP building;
ALTER TABLE fg_planet DROP building_end;
ALTER TABLE fg_planet DROP next_building;
ALTER TABLE fg_planet DROP next_building_end;
ALTER TABLE fg_system DROP building_land;
-- 13/01/08 - jgottero
ALTER TABLE fg_research CHANGE progress progress FLOAT UNSIGNED NOT NULL;
-- 15/01/08 - jgottero
ALTER TABLE fg_player ADD settings_chat TINYINT UNSIGNED NOT NULL DEFAULT '1' AFTER settings_skin;
-- 18/01/08 - jgottero
ALTER TABLE fg_player ADD last_connection INT UNSIGNED NOT NULL AFTER settings_chat;
-- 19/01/08 - jgottero
ALTER TABLE fg_object CHANGE type type ENUM('gate', 'wormhole_noway', 'wormhole_oneway', 'wormhole_twoway', 'wormhole_forming', 'tradecenter', 'pirates', 'bank', 'asteroid', 'asteroid_dense', 'blackhole', 'doodad1', 'doodad2', 'doodad3', 'doodad4', 'doodad5') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
-- 26/01/08 - bmoyet
ALTER TABLE fg_ally_vote ADD id_initiator SMALLINT(5) UNSIGNED NOT NULL;
-- 27/01/08 - jgottero
ALTER TABLE fg_area ADD last_name_update INT UNSIGNED NOT NULL;
-- 30/01/08 - jgottero
ALTER TABLE fg_player ADD ban_chat INT NOT NULL AFTER password;
ALTER TABLE fg_player ADD ban_game INT NOT NULL AFTER ban_chat;
-- 08/02/08 - jgottero
ALTER TABLE fg_bank DROP pending_resource0;
ALTER TABLE fg_bank DROP pending_resource1;
ALTER TABLE fg_bank DROP pending_resource2;
ALTER TABLE fg_bank DROP pending_resource3;
ALTER TABLE fg_bank DROP pending_resource4;
ALTER TABLE fg_bank DROP pending_resource5;
ALTER TABLE fg_bank DROP pending_resource6;
ALTER TABLE fg_bank DROP pending_resource7;
ALTER TABLE fg_bank ADD last_update INT UNSIGNED NOT NULL;
RENAME TABLE fg_bank TO fg_bank_account;
-- 11/02/08 - jgottero
CREATE TABLE fg_marker (
  id int(10) unsigned NOT NULL,
  x tinyint(3) unsigned NOT NULL,
  y tinyint(3) unsigned NOT NULL,
  message varchar(200) collate utf8_unicode_ci NOT NULL,
  visibility enum('player', 'ally', 'allied') collate utf8_unicode_ci NOT NULL,
  date int(10) unsigned NOT NULL,
  id_area smallint(5) unsigned NOT NULL,
  id_owner smallint(5) unsigned NOT NULL,
  PRIMARY KEY  (id),
  KEY id_area (id_area),
  KEY id_owner (id_owner),
  KEY date (date)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
-- 28/02/08 - jgottero
ALTER TABLE fg_fleet ADD tag TINYINT UNSIGNED NOT NULL AFTER name;
-- 13/03/08 - bmoyet
ALTER TABLE fg_message ADD deleted MEDIUMINT(8) NOT NULL;
ALTER TABLE fg_message ADD opened TINYINT(1) NOT NULL;
ALTER TABLE fg_message ADD bookmarked TINYINT(1) NOT NULL;
-- 15/03/08 - bmoyet
ALTER TABLE fg_ally_news ADD id_parent SMALLINT(8) NOT NULL;
ALTER TABLE fg_ally_news ADD sticky TINYINT(1) NOT NULL;
ALTER TABLE fg_event ADD id_area SMALLINT UNSIGNED NOT NULL AFTER date;
ALTER TABLE fg_event ADD x SMALLINT NOT NULL AFTER id_area;
ALTER TABLE fg_event ADD y SMALLINT NOT NULL AFTER x;
UPDATE fg_event SET id_area = 0, x = -1, y = -1;




-- Nettoyage des noms
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'AAS' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'AAVSO' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Acacia' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Acaciacoleman' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Acer' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Ada' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Adamcarolla' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Adamchauvin' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Adamcrandall' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Adamcurry' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Adamsolomon' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Adolfborn' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Adolfine' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Adzhimushkaj' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Africa' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Africano' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Aivazovskij' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Aizuyaichi' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Ajax' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Akihikoito' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Akihikotago' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Akikinoshita' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Akiyamatakashi' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Alain' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Alanboss' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Alaska' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Albanese' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Albertacentenary' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Albinadubois' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Albitskij' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Alexandrinus' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Alexanduribe' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Allen-Beach' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Alpes Maritimes' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Alps' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'ANS' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'ASCII' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Asia' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Asimov' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'ASP' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Bambinidipraga' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Banachiewicza' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Albertus Magnus' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Andrea Doria' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Andres Bello' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Baton Rouge' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Beatrice Tinsley' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Belo Horizonte' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Ben Mayer' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Blansky les' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Bosque Alegre' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Bruce Helin' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Buenos Aires' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Ceske Budejovice' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Cesky Krumlov' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Chen Jiageng' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Ching-Sung Yu' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Christy Carol' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Clocke Roeland' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Daphne Plane' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'David Bender' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'David Hughes' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'De Gasparis' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'di Giovanni' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Di Martino' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Dogo Onsen' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Don Quixote' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Fort Bend' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Franz Schubert' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Frasso Sabino' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Guo Shou-Jing' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Hannu Olavi' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Hohe Meissner' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Hoher List' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Hong Kong' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Isaac Newton' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Jack London' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'James Bond' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'James Bradley' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Jim Young' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Jizni Cechy' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Jonathan Murray' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Josephus Flavius' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Julian Loewe' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Kamenny Ujezd' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Karl Marx' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Kingsford Smith' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Kiriko Matsuri' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Kliment Ohridski' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'La Billardiere' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'La Condamine' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Goethe Link' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'La Harpe' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'La Orotava' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'La Paz' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'La Perouse' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'La Plata' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'La Serena' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'La Silla' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'La Spezia' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Lac d''Orient' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Lake Placid' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Las Casas' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Las Cruces' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Las Vegas' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Le Creusot' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Le Gentil' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Le Poole' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Lev Tolstoj' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Lew Allen' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Linda Susan' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Lomnicky Stit' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Lubos Perek' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Lucretius Carus' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Lysa hora' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Mali Losinj' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Marg Edmondson' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Mark Twain' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Martin Luther' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Midsomer Norton' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Minas Gerais' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Mission Valley' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Mons Naklethi' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Mont Blanc' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Monty Python' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Mount Locke' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Mount Stromlo' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Mr. Spock' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Mr. Tompkins' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Msecke Zehrovice' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Nei Monggol' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Niagara Falls' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Nihon Uchu Forum' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Nizhnij Novgorod' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Nove Hrady' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Olaus Magnus' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Old Joe' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Ole Romer' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Otto Schmidt' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Otto Struve' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Ouro Preto' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Paavo Nurmi' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Patrick Gene' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Per Brahe' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Perth Amboy' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Pink Floyd' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Pino Torinese' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Porta Coeli' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Presque Isle' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Purple Mountain' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Pyotr Pervyj' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Rimavska Sobota' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Rio de Janeiro' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Rockwell Kent' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Rolling Stones' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Rote Kapelle' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Runrun Shaw' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Ruth Wolfe' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Saint Michel' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'San Diego' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'San Jose' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'San Juan' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'San Marcello' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'San Martin' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'San Pedro' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Sawyer Hogg' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Sezimovo Usti' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Shen Guo' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Skalnate Pleso' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'South Dakota' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Spencer Jones' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Table Mountain' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'The NORC' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Thomas Aquinas' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Thuringer Wald' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Tycho Brahe' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Tyn nad Vltavou' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'United Nations' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Vainu Bappu' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Albada' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Altena' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Van Biesbroeck' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Van Citters' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van de Hulst' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van de Kamp' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van den Bergh' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van den Bos' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Van den Eijnde' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van den Heuvel' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van der Brugge' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van der Hucht' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van der Kruit' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van der Laan' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van der Meer' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van der Pol' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van der Velde' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van der Weyden' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van der Woude' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Van Dijck' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Dishoeck' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Eyck' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Van Gaal' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Genderen' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Gent' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Gogh' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Herk' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Houten' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Leverink' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Van Lierde' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Linschoten' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Van Ness' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Ostaijen' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Rhijn' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Riebeeck' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Rijckevorsel' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Van Rompaey' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Schooten' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Sprang' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Van Straten' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Swinden' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Voorthuijsen' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Woerden' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Woerkom' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'van Zee' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Velikij Ustyug' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Victor Jara' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'von Flue' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'von Helfta' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'von Klitzing' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'von Kues' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'von Laue' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'von Liebig' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'von Lude' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'von Matt' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'von Neumann' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'von Suttner' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'von Zeipel' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Walter Adams' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Weisse Rose' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Williams Bay' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Wu Chien-Shiung' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Yi Xing' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Zhang Heng' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Zlata Koruna' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.name USING utf8) = 'Zu Chong-Zhi' LIMIT 1;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.type USING utf8) = 'location' AND CONVERT(fg_random_name.name USING utf8) = 'Dick' LIMIT 1 ;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.type USING utf8) = 'location' AND CONVERT(fg_random_name.name USING utf8) = 'Dickinson' LIMIT 1 ;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.type USING utf8) = 'location' AND CONVERT(fg_random_name.name USING utf8) = 'Dickjoyce' LIMIT 1 ;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.type USING utf8) = 'location' AND CONVERT(fg_random_name.name USING utf8) = 'Dicksmith' LIMIT 1 ;
DELETE FROM fg_random_name WHERE CONVERT(fg_random_name.type USING utf8) = 'location' AND CONVERT(fg_random_name.name USING utf8) = 'Dickwalker' LIMIT 1 ;




-- Script de migration de Origin/JavaScript vers Origin/GWT
-- 19/03/08 - jgottero
CREATE TABLE fg_extractor (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  status ENUM('destroyed', 'on_source', 'move_fw', 'on_target', 'move_bw') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  extraction_end INT UNSIGNED NOT NULL,
  x TINYINT UNSIGNED NOT NULL,
  y TINYINT UNSIGNED NOT NULL,
  id_asteroid INT UNSIGNED NOT NULL,
  id_system SMALLINT UNSIGNED NOT NULL
) ENGINE=MYISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
ALTER TABLE fg_system ADD extractor_center INT UNSIGNED NOT NULL AFTER exploitation3;
UPDATE fg_player SET settings_chat = 1 WHERE settings_chat = 2;
UPDATE fg_fleet SET resource0 = resource0 + resource1;
UPDATE fg_fleet SET resource1 = resource2 + resource3;
UPDATE fg_fleet SET resource2 = resource4 + resource5;
UPDATE fg_fleet SET resource3 = resource6 + resource7;
ALTER TABLE fg_fleet DROP resource4;
ALTER TABLE fg_fleet DROP resource5;
ALTER TABLE fg_fleet DROP resource6;
ALTER TABLE fg_fleet DROP resource7;
UPDATE fg_system SET resource0 = resource0 + resource1;
UPDATE fg_system SET resource1 = resource2 + resource3;
UPDATE fg_system SET resource2 = resource4 + resource5;
UPDATE fg_system SET resource3 = resource6 + resource7;
ALTER TABLE fg_system DROP resource4;
ALTER TABLE fg_system DROP resource5;
ALTER TABLE fg_system DROP resource6;
ALTER TABLE fg_system DROP resource7;
ALTER TABLE fg_system DROP exploitation4;
ALTER TABLE fg_system DROP exploitation5;
ALTER TABLE fg_system DROP exploitation6;
ALTER TABLE fg_system DROP exploitation7;
UPDATE fg_system SET encoded_available_resources =
  ((((encoded_available_resources >>  0) & 7) + ((encoded_available_resources >>  3) & 7)) <<  0) |
  ((((encoded_available_resources >>  6) & 7) + ((encoded_available_resources >>  9) & 7)) <<  6) |
  ((((encoded_available_resources >> 12) & 7) + ((encoded_available_resources >> 15) & 7)) << 12) |
  ((((encoded_available_resources >> 18) & 7) + ((encoded_available_resources >> 21) & 7)) << 18);
ALTER TABLE fg_bank_account DROP resource4;
ALTER TABLE fg_bank_account DROP resource5;
ALTER TABLE fg_bank_account DROP resource6;
ALTER TABLE fg_bank_account DROP resource7;
UPDATE fg_tradecenter SET resource0 = resource0 + resource1;
UPDATE fg_tradecenter SET resource1 = resource2 + resource3;
UPDATE fg_tradecenter SET resource2 = resource4 + resource5;
UPDATE fg_tradecenter SET resource3 = resource6 + resource7;
ALTER TABLE fg_tradecenter DROP resource4;
ALTER TABLE fg_tradecenter DROP resource5;
ALTER TABLE fg_tradecenter DROP resource6;
ALTER TABLE fg_tradecenter DROP resource7;
UPDATE fg_tradecenter SET rate0 = (rate0 + rate1) / 2;
UPDATE fg_tradecenter SET rate1 = (rate2 + rate3) / 2;
UPDATE fg_tradecenter SET rate2 = (rate4 + rate5) / 2;
UPDATE fg_tradecenter SET rate3 = (rate6 + rate7) / 2;
ALTER TABLE fg_tradecenter DROP rate4;
ALTER TABLE fg_tradecenter DROP rate5;
ALTER TABLE fg_tradecenter DROP rate6;
ALTER TABLE fg_tradecenter DROP rate7;
ALTER TABLE fg_player ADD credits BIGINT UNSIGNED NOT NULL AFTER xp;
ALTER TABLE fg_system ADD population DOUBLE UNSIGNED NOT NULL AFTER race;
ALTER TABLE fg_system ADD civilian_infrastructures INT UNSIGNED NOT NULL AFTER extractor_center;
ALTER TABLE fg_system ADD last_population_update INT UNSIGNED NOT NULL AFTER last_research_update;
ALTER TABLE fg_system ADD corporations INT UNSIGNED NOT NULL AFTER civilian_infrastructures;
ALTER TABLE fg_system ADD trade_port INT UNSIGNED NOT NULL AFTER corporations;
-- 25/03/08 - jgottero
ALTER TABLE fg_extractor ADD extraction_length INT UNSIGNED NOT NULL AFTER status;
-- 29/03/08 - bmoyet
CREATE TABLE fg_fleet_link (
  id_fleet1 MEDIUMINT(8) NOT NULL,
  id_fleet2 MEDIUMINT(8) NOT NULL,
  link ENUM('defensive', 'offensive', 'emp', 'delude') NOT NULL,
  date INT(10) NOT NULL
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_fleet ADD stealth TINYINT(1) NOT NULL AFTER tag;
-- 30/03/08 - jgottero
ALTER TABLE fg_fleet_link CHANGE id_fleet1 id_src_fleet MEDIUMINT UNSIGNED NOT NULL;
ALTER TABLE fg_fleet_link CHANGE id_fleet2 id_dst_fleet MEDIUMINT UNSIGNED NOT NULL;
ALTER TABLE fg_fleet_link CHANGE date end INT UNSIGNED NOT NULL;
-- 13/04/08 - jgottero
ALTER TABLE fg_planet DROP size;
ALTER TABLE fg_planet CHANGE x angle DOUBLE UNSIGNED NOT NULL;
ALTER TABLE fg_planet CHANGE y distance TINYINT UNSIGNED NOT NULL;
ALTER TABLE fg_planet ADD rotation_speed DOUBLE NOT NULL AFTER distance;
ALTER TABLE fg_system DROP star_size;
UPDATE fg_system SET star_image = FLOOR(RAND() * 15);
-- 06/05/08 - jgottero
ALTER TABLE fg_fleet ADD slot0_front TINYINT(1) UNSIGNED NOT NULL AFTER slot0_count;
ALTER TABLE fg_fleet ADD slot1_front TINYINT(1) UNSIGNED NOT NULL AFTER slot1_count;
ALTER TABLE fg_fleet ADD slot2_front TINYINT(1) UNSIGNED NOT NULL AFTER slot2_count;
ALTER TABLE fg_fleet ADD slot3_front TINYINT(1) UNSIGNED NOT NULL AFTER slot3_count;
ALTER TABLE fg_fleet ADD slot4_front TINYINT(1) UNSIGNED NOT NULL AFTER slot4_count;
-- 02/07/08 - jgottero
ALTER TABLE fg_report CHANGE id_report id INT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE fg_report_line CHANGE quantity slot_count INT UNSIGNED NOT NULL;
ALTER TABLE fg_report_line CHANGE type slot_id TINYINT UNSIGNED NOT NULL;
ALTER TABLE fg_report_line DROP id_report;
ALTER TABLE fg_report_line ADD id_report INT UNSIGNED NOT NULL;
ALTER TABLE fg_report_line CHANGE attacking_fleet attacking_fleet TINYINT UNSIGNED NOT NULL;
ALTER TABLE fg_report_line CHANGE round round TINYINT UNSIGNED NOT NULL;
ALTER TABLE fg_report_line CHANGE slot slot_index TINYINT UNSIGNED NOT NULL;
ALTER TABLE fg_report_line ADD INDEX (id_report);
ALTER TABLE fg_report_line CHANGE slot_index position TINYINT UNSIGNED NOT NULL;
RENAME TABLE fg_report_line TO fg_report_slot;
ALTER TABLE fg_report_slot ADD id INT UNSIGNED NOT NULL FIRST;
TRUNCATE TABLE fg_report;
TRUNCATE TABLE fg_report_slot;
TRUNCATE TABLE fg_report_damage;
ALTER TABLE fg_report_damage DROP id_line;
ALTER TABLE fg_report_damage ADD id_report_slot INT UNSIGNED NOT NULL;
ALTER TABLE fg_report_damage ADD INDEX (id_report_slot);
ALTER TABLE fg_report_damage DROP id_damage;
ALTER TABLE fg_report_damage CHANGE slot_hit target_position TINYINT NOT NULL;
ALTER TABLE fg_report_damage DROP class_hit;
ALTER TABLE fg_report_damage CHANGE amount damage BIGINT UNSIGNED NOT NULL;
-- 04/07/08
ALTER TABLE fg_report_slot ADD regeneration_count INT UNSIGNED NOT NULL AFTER slot_count;
ALTER TABLE fg_report_damage ADD modifiers INT UNSIGNED NOT NULL AFTER damage;
ALTER TABLE fg_report_slot DROP id_line;
ALTER TABLE fg_report_slot ADD PRIMARY KEY (id);
ALTER TABLE fg_report_slot CHANGE id id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT;
-- 16/07/08
ALTER TABLE fg_report_slot ADD slot_front TINYINT(1) UNSIGNED NOT NULL AFTER slot_count;
-- 31/07/08
CREATE TABLE fg_sector (
  id TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name CHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  x SMALLINT NOT NULL,
  y SMALLINT NOT NULL,
  nebula TINYINT UNSIGNED NOT NULL,
  PRIMARY KEY (id)
) ENGINE=MYISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
ALTER TABLE fg_area DROP nebula;
ALTER TABLE fg_area ADD id_sector TINYINT UNSIGNED NOT NULL;
ALTER TABLE fg_area ADD INDEX (id_sector);
ALTER TABLE fg_area DROP type;
ALTER TABLE fg_area CHANGE width width SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_area CHANGE height height SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_object CHANGE x x SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_object CHANGE y y SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_fleet CHANGE x x SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_fleet CHANGE y y SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_system CHANGE x x SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_system CHANGE y y SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_marker CHANGE x x SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_marker CHANGE y y SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_extractor CHANGE x x SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_extractor CHANGE y y SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_planet CHANGE id id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE fg_random_name ADD type ENUM('star', 'character', 'ally') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL FIRST;
ALTER TABLE fg_random_name CHANGE type type ENUM('location', 'male_fname', 'female_fname', 'lname', 'ally') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_random_name DROP PRIMARY KEY;
ALTER TABLE fg_ally ADD tag CHAR(3) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER name;
ALTER TABLE fg_object CHANGE type type ENUM('gate', 'wormhole_noway', 'wormhole_oneway', 'wormhole_twoway', 'wormhole_forming', 'tradecenter', 'pirates', 'bank', 'asteroid', 'asteroid_dense', 'blackhole', 'doodad0', 'doodad1', 'doodad2', 'doodad3', 'doodad4') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_fleet ADD movement_reload INT UNSIGNED NOT NULL AFTER last_move;
ALTER TABLE fg_fleet DROP hyperspace_end;
ALTER TABLE fg_fleet DROP hypergate_in;
ALTER TABLE fg_fleet CHANGE hypergate_out hyperspace_id_area SMALLINT(5) UNSIGNED NOT NULL;
ALTER TABLE fg_fleet ADD hyperspace_x SMALLINT UNSIGNED NOT NULL AFTER hyperspace_id_area;
ALTER TABLE fg_fleet ADD hyperspace_y SMALLINT UNSIGNED NOT NULL AFTER hyperspace_x;
ALTER TABLE fg_fleet DROP moves_left;
DROP TABLE fg_blacklist;
ALTER TABLE fg_player ADD birthday INT UNSIGNED NOT NULL AFTER registration;
ALTER TABLE fg_player ADD recover_email VARCHAR(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER registration;
ALTER TABLE fg_player ADD recover_password VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER recover_email;
CREATE TABLE fg_connection (
  id INT UNSIGNED NOT NULL,
  ip INT UNSIGNED NOT NULL,
  start INT UNSIGNED NOT NULL,
  end INT UNSIGNED NOT NULL,
  id_player SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY (id)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_player CHANGE settings_skin settings_theme TINYINT(3) UNSIGNED NOT NULL DEFAULT '0';
ALTER TABLE fg_player ADD settings_censorship TINYINT(1) UNSIGNED NOT NULL AFTER settings_chat;
ALTER TABLE fg_player ADD settings_general_volume TINYINT UNSIGNED NOT NULL AFTER settings_censorship;
ALTER TABLE fg_player ADD settings_sound_volume TINYINT UNSIGNED NOT NULL AFTER settings_general_volume;
ALTER TABLE fg_player ADD settings_music_volume TINYINT UNSIGNED NOT NULL AFTER settings_sound_volume;
ALTER TABLE fg_player ADD settings_graphics TINYINT UNSIGNED NOT NULL AFTER settings_music_volume;
ALTER TABLE fg_marker ADD galaxy TINYINT UNSIGNED NOT NULL AFTER visibility;
ALTER TABLE fg_area CHANGE hostile type TINYINT UNSIGNED NOT NULL;
ALTER TABLE fg_sector ADD type TINYINT UNSIGNED NOT NULL AFTER y;
ALTER TABLE fg_area ADD full TINYINT(1) UNSIGNED NOT NULL AFTER type;
CREATE TABLE fg_mission_data (
  id_mission INT UNSIGNED NOT NULL,
  key VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  value VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
CREATE TABLE fg_mission (
  id INT UNSIGNED NOT NULL,
  type SMALLINT UNSIGNED NOT NULL,
  date INT UNSIGNED NOT NULL
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_player ADD sex TINYINT(1) UNSIGNED NOT NULL AFTER ai;
CREATE TABLE fg_mission_parameter (
  type SMALLINT UNSIGNED NOT NULL,
  key VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  value VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_mission_parameter CHANGE type type INT NOT NULL;
ALTER TABLE fg_mission CHANGE type type INT NOT NULL;
ALTER TABLE fg_fleet ADD reservation SMALLINT UNSIGNED NOT NULL AFTER tag;
ALTER TABLE fg_fleet ADD npc_action ENUM('none', 'talk', 'mission') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER reservation;
ALTER TABLE fg_fleet CHANGE id id INT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE fg_fleet_link CHANGE id_src_fleet id_src_fleet INT UNSIGNED NOT NULL;
ALTER TABLE fg_fleet_link CHANGE id_dst_fleet id_dst_fleet INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally CHANGE id_creator id_creator INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_news CHANGE id_author id_author INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_vote CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_vote CHANGE id_initiator id_initiator INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_voter CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_applicant CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_bank_account CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_connection CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_contact CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_contact CHANGE id_contact id_contact INT UNSIGNED NOT NULL;
ALTER TABLE fg_election_voter CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_election_voter CHANGE id_candidate id_candidate INT UNSIGNED NOT NULL;
ALTER TABLE fg_event CHANGE id_target id_target INT UNSIGNED NOT NULL;
ALTER TABLE fg_fleet CHANGE reservation reservation INT UNSIGNED NOT NULL;
ALTER TABLE fg_fleet CHANGE id_owner id_owner INT UNSIGNED NOT NULL;
ALTER TABLE fg_marker CHANGE id_owner id_owner INT UNSIGNED NOT NULL;
ALTER TABLE fg_message CHANGE id_author id_author INT UNSIGNED NOT NULL;
ALTER TABLE fg_message CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_report CHANGE id_player_attacking id_player_attacking INT UNSIGNED NOT NULL;
ALTER TABLE fg_report CHANGE id_player_defending id_player_defending INT UNSIGNED NOT NULL;
ALTER TABLE fg_research CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_system CHANGE id_owner id_owner INT UNSIGNED NOT NULL DEFAULT '0'; 
ALTER TABLE fg_treaty CHANGE id_player1 id_player1 INT UNSIGNED NOT NULL;
ALTER TABLE fg_treaty CHANGE id_player2 id_player2 INT UNSIGNED NOT NULL;
ALTER TABLE fg_visited_area CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_mission_parameter CHANGE key key_name VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_mission_data CHANGE key key_name VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_mission_player CHANGE status status ENUM('initialized', 'registered', 'in_progress', 'finished') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_mission_data ADD id_player INT UNSIGNED NOT NULL AFTER id_mission;
ALTER TABLE fg_mission_player DROP id_mission;
ALTER TABLE fg_mission_player DROP status;
ALTER TABLE fg_mission_player ADD key_name VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_mission_player ADD value VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
RENAME TABLE fg_mission_data TO fg_mission_local_parameter;
RENAME TABLE fg_mission_parameter TO fg_mission_global_parameter;
RENAME TABLE fg_mission_player TO fg_mission_player_parameter;
ALTER TABLE fg_fleet ADD encoded_skirmish_slots CHAR(5) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER encoded_skill_ultimate;
ALTER TABLE fg_fleet ADD encoded_battle_slots CHAR(15) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER encoded_skirmish_slots;
ALTER TABLE fg_fleet ADD encoded_skirmish_abilities CHAR(5) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER encoded_skirmish_slots;
ALTER TABLE fg_fleet ADD encoded_battle_abilities CHAR(15) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER encoded_battle_slots;
ALTER TABLE fg_report_slot DROP attacking_fleet;
ALTER TABLE fg_report_slot DROP round;
ALTER TABLE fg_report_slot DROP regeneration_count;
CREATE TABLE fg_report_action (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  action_index TINYINT UNSIGNED NOT NULL,
  slot_index TINYINT UNSIGNED NOT NULL,
  ability TINYINT NOT NULL,
  target_slots INT UNSIGNED NOT NULL,
  damage INT UNSIGNED NOT NULL,
  id_report INT UNSIGNED NOT NULL,
  PRIMARY KEY (id)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_report_action CHANGE damage damage BIGINT UNSIGNED NOT NULL;
ALTER TABLE fg_report_action DROP damage;
ALTER TABLE fg_report_damage CHANGE id_report_slot id_report_action INT UNSIGNED NOT NULL;
ALTER TABLE fg_report_action DROP target_slots;
ALTER TABLE fg_report_damage CHANGE damage damage BIGINT(20) NOT NULL;
ALTER TABLE fg_report_action CHANGE slot_index slot_index TINYINT NOT NULL;
ALTER TABLE fg_report_action ADD front_slots MEDIUMINT UNSIGNED NOT NULL AFTER ability;
ALTER TABLE fg_object CHANGE type type ENUM('gate', 'wormhole_noway', 'wormhole_oneway', 'wormhole_twoway', 'wormhole_forming', 'tradecenter', 'pirates', 'bank', 'asteroid', 'asteroid_dense', 'blackhole', 'doodad0', 'doodad1', 'doodad2', 'doodad3', 'doodad4', 'doodad5') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_marker ADD system TINYINT(1) UNSIGNED NOT NULL AFTER date;
ALTER TABLE fg_marker CHANGE message message TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_fleet ADD scheduled_x SMALLINT NOT NULL AFTER y;
ALTER TABLE fg_fleet ADD scheduled_y SMALLINT NOT NULL AFTER scheduled_x;
ALTER TABLE fg_fleet CHANGE scheduled_x scheduled_x SMALLINT(6) UNSIGNED NOT NULL;
ALTER TABLE fg_fleet CHANGE scheduled_y scheduled_y SMALLINT(6) UNSIGNED NOT NULL;
ALTER TABLE fg_fleet ADD scheduled_move TINYINT(1) UNSIGNED NOT NULL AFTER y;
ALTER TABLE fg_applicant ADD date INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_news CHANGE id id INT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE fg_ally_news CHANGE id_parent id_parent INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_news CHANGE sticky sticky TINYINT(1) UNSIGNED NOT NULL;
ALTER TABLE fg_ally_news ADD id_vote INT UNSIGNED NOT NULL AFTER id_author;
ALTER TABLE fg_ally_news ADD id_election INT UNSIGNED NOT NULL AFTER id_vote;
ALTER TABLE fg_ally_news ADD id_applicant INT UNSIGNED NOT NULL AFTER id_election;
ALTER TABLE fg_applicant DROP cv;
ALTER TABLE fg_object CHANGE type type ENUM('gate', 'wormhole_noway', 'wormhole_oneway', 'wormhole_twoway', 'wormhole_forming', 'tradecenter', 'pirates', 'bank', 'asteroid', 'asteroid_dense', 'asteroid_low_titanium', 'asteroid_low_crystal', 'asteroid_low_andium', 'asteroid_avg_titanium', 'asteroid_avg_crystal', 'asteroid_avg_andium', 'asteroid_high_titanium', 'asteroid_high_crystal', 'asteroid_high_andium', 'blackhole', 'doodad0', 'doodad1', 'doodad2', 'doodad3', 'doodad4', 'doodad5') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_report ADD attacker_environment TINYINT UNSIGNED NOT NULL AFTER last_view;
ALTER TABLE fg_report ADD defender_environment TINYINT UNSIGNED NOT NULL AFTER attacker_environment;
ALTER TABLE fg_report CHANGE attacker_environment attacker_environment VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_report CHANGE defender_environment defender_environment VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
UPDATE fg_report SET attacker_environment = '' WHERE attacker_environment = '0';
UPDATE fg_report SET defender_environment = '' WHERE defender_environment = '0';
ALTER TABLE fg_report_damage ADD count INT UNSIGNED NOT NULL AFTER damage;
ALTER TABLE fg_report_damage ADD hull INT UNSIGNED NOT NULL AFTER count;
ALTER TABLE fg_report_damage CHANGE count kills INT(10) NOT NULL;
ALTER TABLE fg_report_damage CHANGE hull hull_damage INT(10) UNSIGNED NOT NULL;
ALTER TABLE fg_system ADD build_slot0_ordered INT UNSIGNED NOT NULL AFTER build_slot0_count;
ALTER TABLE fg_system ADD build_slot1_ordered INT UNSIGNED NOT NULL AFTER build_slot1_count;
ALTER TABLE fg_system ADD build_slot2_ordered INT UNSIGNED NOT NULL AFTER build_slot2_count;
ALTER TABLE fg_tradecenter DROP resource0;
ALTER TABLE fg_tradecenter DROP resource1;
ALTER TABLE fg_tradecenter DROP resource2;
ALTER TABLE fg_tradecenter DROP resource3;
ALTER TABLE fg_tradecenter ADD variation DOUBLE UNSIGNED NOT NULL;
ALTER TABLE fg_tradecenter CHANGE rate0 rate0 DOUBLE NOT NULL;
ALTER TABLE fg_tradecenter CHANGE rate1 rate1 DOUBLE NOT NULL;
ALTER TABLE fg_tradecenter CHANGE rate2 rate2 DOUBLE NOT NULL;
ALTER TABLE fg_tradecenter CHANGE rate3 rate3 DOUBLE NOT NULL;
UPDATE fg_tradecenter SET rate0 = 0, rate1 = 0, rate2 = 0, rate3 = 0, variation = .00001;
ALTER TABLE fg_treaty ADD last_activity INT UNSIGNED NOT NULL AFTER date;
ALTER TABLE fg_ally_treaty ADD last_activity INT UNSIGNED NOT NULL AFTER date;
CREATE TABLE fg_ward (
  id INT UNSIGNED NOT NULL,
  x SMALLINT UNSIGNED NOT NULL,
  y SMALLINT UNSIGNED NOT NULL,
  type ENUM('observer', 'observer_invisible', 'sentry', 'sentry_invisible') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  date INT UNSIGNED NOT NULL,
  id_area SMALLINT UNSIGNED NOT NULL,
  id_owner SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  INDEX (id_area, id_owner)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
CREATE TABLE fg_report_slot_state (
  id_report_action INT UNSIGNED NOT NULL,
  damage_modifier DOUBLE NOT NULL,
  protection_modifier TINYINT NOT NULL,
  life_modifier DOUBLE NOT NULL,
  INDEX (id_report_action)
) ENGINE = MYISAM;
ALTER TABLE fg_report_slot_state DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_report_slot_state ADD position TINYINT UNSIGNED NOT NULL FIRST;
ALTER TABLE fg_report_slot_state CHANGE life_modifier hull_modifier DOUBLE NOT NULL;
ALTER TABLE fg_report ADD retreat TINYINT(1) UNSIGNED NOT NULL AFTER defender_environment;
ALTER TABLE fg_system ADD slot5_id TINYINT UNSIGNED NOT NULL AFTER slot4_count;
ALTER TABLE fg_system ADD slot5_count INT UNSIGNED NOT NULL AFTER slot5_id;
ALTER TABLE fg_system ADD slot6_id TINYINT UNSIGNED NOT NULL AFTER slot5_count;
ALTER TABLE fg_system ADD slot6_count INT UNSIGNED NOT NULL AFTER slot6_id;
ALTER TABLE fg_system ADD slot7_id TINYINT UNSIGNED NOT NULL AFTER slot6_count;
ALTER TABLE fg_system ADD slot7_count INT UNSIGNED NOT NULL AFTER slot7_id;
ALTER TABLE fg_system ADD slot8_id TINYINT UNSIGNED NOT NULL AFTER slot7_count;
ALTER TABLE fg_system ADD slot8_count INT UNSIGNED NOT NULL AFTER slot8_id;
ALTER TABLE fg_system ADD slot9_id TINYINT UNSIGNED NOT NULL AFTER slot8_count;
ALTER TABLE fg_system ADD slot9_count INT UNSIGNED NOT NULL AFTER slot9_id;
ALTER TABLE fg_system ADD available_space TINYINT UNSIGNED NOT NULL AFTER population;
UPDATE fg_system SET available_space = 3;
DROP TABLE fg_extractor;
ALTER TABLE fg_player ADD events_read TINYINT(1) UNSIGNED NOT NULL AFTER credits;
ALTER TABLE fg_player CHANGE events_read events_read_date INT UNSIGNED NOT NULL;
ALTER TABLE fg_report ADD attacker_damage_factor DOUBLE UNSIGNED NOT NULL AFTER defender_environment;
ALTER TABLE fg_report ADD defender_damage_factor DOUBLE UNSIGNED NOT NULL AFTER attacker_damage_factor;
UPDATE fg_report SET attacker_damage_factor = 1, defender_damage_factor = 1;
CREATE TABLE fg_space_station (
  id INT UNSIGNED NOT NULL,
  x SMALLINT UNSIGNED NOT NULL,
  y SMALLINT UNSIGNED NOT NULL,
  date INT UNSIGNED NOT NULL,
  id_owner SMALLINT UNSIGNED NOT NULL,
  id_area SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  INDEX (id_owner, id_area)
) ENGINE = InnoDB;
ALTER TABLE fg_space_station ENGINE = MYISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_space_station ADD name VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER id;
ALTER TABLE fg_space_station ADD resource0 INT UNSIGNED NOT NULL AFTER y;
ALTER TABLE fg_space_station ADD resource1 INT UNSIGNED NOT NULL AFTER resource0;
ALTER TABLE fg_space_station ADD resource2 INT UNSIGNED NOT NULL AFTER resource1;
ALTER TABLE fg_space_station ADD resource3 INT UNSIGNED NOT NULL AFTER resource2;
ALTER TABLE fg_space_station ADD level TINYINT UNSIGNED NOT NULL AFTER name;
ALTER TABLE fg_area ADD space_stations_limit TINYINT UNSIGNED NOT NULL AFTER type;
UPDATE fg_area SET space_stations_limit = 2 WHERE type = 1;
ALTER TABLE fg_space_station CHANGE id_owner id_ally MEDIUMINT UNSIGNED NOT NULL;
ALTER TABLE fg_space_station ADD hull SMALLINT UNSIGNED NOT NULL AFTER level;
ALTER TABLE fg_space_station ADD credits INT UNSIGNED NOT NULL AFTER y;
UPDATE fg_area SET space_stations_limit = 1 WHERE type = 1;
ALTER TABLE fg_sector ADD value DOUBLE UNSIGNED NOT NULL;
CREATE TABLE fg_ally_influence (
  id_ally MEDIUMINT UNSIGNED NOT NULL,
  id_sector TINYINT UNSIGNED NOT NULL,
  influence DOUBLE UNSIGNED NOT NULL
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_sector CHANGE value strategic_value INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_influence ADD systems_count SMALLINT UNSIGNED NOT NULL;
UPDATE fg_fleet SET slot0_count = CEIL(slot0_count / 2.) WHERE slot0_id >= 21 AND slot0_id < 40;
UPDATE fg_fleet SET slot1_count = CEIL(slot1_count / 2.) WHERE slot1_id >= 21 AND slot1_id < 40;
UPDATE fg_fleet SET slot2_count = CEIL(slot2_count / 2.) WHERE slot2_id >= 21 AND slot2_id < 40;
UPDATE fg_fleet SET slot3_count = CEIL(slot3_count / 2.) WHERE slot3_id >= 21 AND slot3_id < 40;
UPDATE fg_fleet SET slot4_count = CEIL(slot4_count / 2.) WHERE slot4_id >= 21 AND slot4_id < 40;
UPDATE fg_fleet SET slot0_count = CEIL(slot0_count / 4.) WHERE slot0_id >= 41 AND slot0_id < 60;
UPDATE fg_fleet SET slot1_count = CEIL(slot1_count / 4.) WHERE slot1_id >= 41 AND slot1_id < 60;
UPDATE fg_fleet SET slot2_count = CEIL(slot2_count / 4.) WHERE slot2_id >= 41 AND slot2_id < 60;
UPDATE fg_fleet SET slot3_count = CEIL(slot3_count / 4.) WHERE slot3_id >= 41 AND slot3_id < 60;
UPDATE fg_fleet SET slot4_count = CEIL(slot4_count / 4.) WHERE slot4_id >= 41 AND slot4_id < 60;
UPDATE fg_fleet SET slot0_count = CEIL(slot0_count / 8.) WHERE slot0_id >= 61 AND slot0_id < 80;
UPDATE fg_fleet SET slot1_count = CEIL(slot1_count / 8.) WHERE slot1_id >= 61 AND slot1_id < 80;
UPDATE fg_fleet SET slot2_count = CEIL(slot2_count / 8.) WHERE slot2_id >= 61 AND slot2_id < 80;
UPDATE fg_fleet SET slot3_count = CEIL(slot3_count / 8.) WHERE slot3_id >= 61 AND slot3_id < 80;
UPDATE fg_fleet SET slot4_count = CEIL(slot4_count / 8.) WHERE slot4_id >= 61 AND slot4_id < 80;
UPDATE fg_system SET slot0_count = CEIL(slot0_count / 2.) WHERE slot0_id >= 21 AND slot0_id < 40;
UPDATE fg_system SET slot1_count = CEIL(slot1_count / 2.) WHERE slot1_id >= 21 AND slot1_id < 40;
UPDATE fg_system SET slot2_count = CEIL(slot2_count / 2.) WHERE slot2_id >= 21 AND slot2_id < 40;
UPDATE fg_system SET slot3_count = CEIL(slot3_count / 2.) WHERE slot3_id >= 21 AND slot3_id < 40;
UPDATE fg_system SET slot4_count = CEIL(slot4_count / 2.) WHERE slot4_id >= 21 AND slot4_id < 40;
UPDATE fg_system SET slot5_count = CEIL(slot5_count / 2.) WHERE slot5_id >= 21 AND slot5_id < 40;
UPDATE fg_system SET slot6_count = CEIL(slot6_count / 2.) WHERE slot6_id >= 21 AND slot6_id < 40;
UPDATE fg_system SET slot7_count = CEIL(slot7_count / 2.) WHERE slot7_id >= 21 AND slot7_id < 40;
UPDATE fg_system SET slot8_count = CEIL(slot8_count / 2.) WHERE slot8_id >= 21 AND slot8_id < 40;
UPDATE fg_system SET slot9_count = CEIL(slot9_count / 2.) WHERE slot9_id >= 21 AND slot9_id < 40;
UPDATE fg_system SET slot0_count = CEIL(slot0_count / 4.) WHERE slot0_id >= 41 AND slot0_id < 60;
UPDATE fg_system SET slot1_count = CEIL(slot1_count / 4.) WHERE slot1_id >= 41 AND slot1_id < 60;
UPDATE fg_system SET slot2_count = CEIL(slot2_count / 4.) WHERE slot2_id >= 41 AND slot2_id < 60;
UPDATE fg_system SET slot3_count = CEIL(slot3_count / 4.) WHERE slot3_id >= 41 AND slot3_id < 60;
UPDATE fg_system SET slot4_count = CEIL(slot4_count / 4.) WHERE slot4_id >= 41 AND slot4_id < 60;
UPDATE fg_system SET slot5_count = CEIL(slot5_count / 4.) WHERE slot5_id >= 41 AND slot5_id < 60;
UPDATE fg_system SET slot6_count = CEIL(slot6_count / 4.) WHERE slot6_id >= 41 AND slot6_id < 60;
UPDATE fg_system SET slot7_count = CEIL(slot7_count / 4.) WHERE slot7_id >= 41 AND slot7_id < 60;
UPDATE fg_system SET slot8_count = CEIL(slot8_count / 4.) WHERE slot8_id >= 41 AND slot8_id < 60;
UPDATE fg_system SET slot9_count = CEIL(slot9_count / 4.) WHERE slot9_id >= 41 AND slot9_id < 60;
UPDATE fg_system SET slot0_count = CEIL(slot0_count / 8.) WHERE slot0_id >= 61 AND slot0_id < 80;
UPDATE fg_system SET slot1_count = CEIL(slot1_count / 8.) WHERE slot1_id >= 61 AND slot1_id < 80;
UPDATE fg_system SET slot2_count = CEIL(slot2_count / 8.) WHERE slot2_id >= 61 AND slot2_id < 80;
UPDATE fg_system SET slot3_count = CEIL(slot3_count / 8.) WHERE slot3_id >= 61 AND slot3_id < 80;
UPDATE fg_system SET slot4_count = CEIL(slot4_count / 8.) WHERE slot4_id >= 61 AND slot4_id < 80;
UPDATE fg_system SET slot5_count = CEIL(slot5_count / 8.) WHERE slot5_id >= 61 AND slot5_id < 80;
UPDATE fg_system SET slot6_count = CEIL(slot6_count / 8.) WHERE slot6_id >= 61 AND slot6_id < 80;
UPDATE fg_system SET slot7_count = CEIL(slot7_count / 8.) WHERE slot7_id >= 61 AND slot7_id < 80;
UPDATE fg_system SET slot8_count = CEIL(slot8_count / 8.) WHERE slot8_id >= 61 AND slot8_id < 80;
UPDATE fg_system SET slot9_count = CEIL(slot9_count / 8.) WHERE slot9_id >= 61 AND slot9_id < 80;
UPDATE fg_system SET build_slot0_count = CEIL(build_slot0_count / 2.) WHERE build_slot0_id >= 21 AND build_slot0_id < 40;
UPDATE fg_system SET build_slot1_count = CEIL(build_slot1_count / 2.) WHERE build_slot1_id >= 21 AND build_slot1_id < 40;
UPDATE fg_system SET build_slot2_count = CEIL(build_slot2_count / 2.) WHERE build_slot2_id >= 21 AND build_slot2_id < 40;
UPDATE fg_system SET build_slot0_count = CEIL(build_slot0_count / 4.) WHERE build_slot0_id >= 41 AND build_slot0_id < 60;
UPDATE fg_system SET build_slot1_count = CEIL(build_slot1_count / 4.) WHERE build_slot1_id >= 41 AND build_slot1_id < 60;
UPDATE fg_system SET build_slot2_count = CEIL(build_slot2_count / 4.) WHERE build_slot2_id >= 41 AND build_slot2_id < 60;
UPDATE fg_system SET build_slot0_count = CEIL(build_slot0_count / 8.) WHERE build_slot0_id >= 61 AND build_slot0_id < 80;
UPDATE fg_system SET build_slot1_count = CEIL(build_slot1_count / 8.) WHERE build_slot1_id >= 61 AND build_slot1_id < 80;
UPDATE fg_system SET build_slot2_count = CEIL(build_slot2_count / 8.) WHERE build_slot2_id >= 61 AND build_slot2_id < 80;
UPDATE fg_fleet SET slot0_count = 100000 WHERE slot0_count = 10000000;
ALTER TABLE fg_ally_influence CHANGE influence influence_coef DOUBLE UNSIGNED NOT NULL;
ALTER TABLE fg_ally_influence ADD influence_value SMALLINT UNSIGNED NOT NULL AFTER influence_coef;
ALTER TABLE fg_fleet CHANGE npc_action mission_type INT NOT NULL;
ALTER TABLE fg_fleet CHANGE mission_type mission_type INT UNSIGNED NOT NULL;
ALTER TABLE fg_fleet DROP reservation;
ALTER TABLE fg_fleet ADD npc_type TINYINT UNSIGNED NOT NULL AFTER mission_type;
TRUNCATE TABLE fg_mission;
TRUNCATE TABLE fg_mission_global_parameter;
TRUNCATE TABLE fg_mission_local_parameter;
TRUNCATE TABLE fg_mission_player_parameter;
DELETE FROM fg_player WHERE ai = 1 AND id >= 25;
DELETE FROM fg_fleet WHERE id_owner NOT IN (SELECT id FROM fg_player);
DELETE FROM fg_fleet WHERE id_owner = 3 OR id_owner = 4;
UPDATE fg_fleet SET mission_type = 0 WHERE mission_type = 1 OR mission_type = 2;
DROP TABLE fg_wormhole;
ALTER TABLE fg_player ADD settings_optimize_connection TINYINT(1) UNSIGNED NOT NULL AFTER settings_graphics;
ALTER TABLE fg_player CHANGE settings_grid_visible settings_grid_visible TINYINT(1) UNSIGNED NOT NULL DEFAULT '0';
ALTER TABLE fg_report_action ADD modifiers INT UNSIGNED NOT NULL AFTER front_slots;
CREATE TABLE fg_ally_news_read (
  id_ally_news INT UNSIGNED NOT NULL,
  id_player SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY (id_ally_news, id_player)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_fleet ADD tactics_defined TINYINT(1) UNSIGNED NOT NULL AFTER encoded_battle_abilities;
UPDATE fg_fleet SET tactics_defined = 1;
ALTER TABLE fg_player ADD settings_fleet_name_prefix VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER settings_optimize_connection;
ALTER TABLE fg_player ADD settings_fleet_name_suffix TINYINT UNSIGNED NOT NULL AFTER settings_fleet_name_prefix;
UPDATE fg_player SET settings_fleet_name_prefix = 'Flotte';
ALTER TABLE fg_object CHANGE type type ENUM('gate', 'tradecenter', 'pirates', 'bank', 'asteroid', 'asteroid_dense', 'asteroid_low_titanium', 'asteroid_low_crystal', 'asteroid_low_andium', 'asteroid_avg_titanium', 'asteroid_avg_crystal', 'asteroid_avg_andium', 'asteroid_high_titanium', 'asteroid_high_crystal', 'asteroid_high_andium', 'blackhole', 'doodad0', 'doodad1', 'doodad2', 'doodad3', 'doodad4', 'doodad5') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_fleet CHANGE mission_type mission_type INT(10) NOT NULL;
ALTER TABLE fg_report_damage ADD stealed_resource0 INT UNSIGNED NOT NULL AFTER modifiers;
ALTER TABLE fg_report_damage ADD stealed_resource1 INT UNSIGNED NOT NULL AFTER stealed_resource0;
ALTER TABLE fg_report_damage ADD stealed_resource2 INT UNSIGNED NOT NULL AFTER stealed_resource1;
ALTER TABLE fg_report_damage ADD stealed_resource3 INT UNSIGNED NOT NULL AFTER stealed_resource2;
ALTER TABLE fg_report_damage CHANGE stealed_resource0 stealed_resource0 DOUBLE UNSIGNED NOT NULL;
ALTER TABLE fg_report_damage CHANGE stealed_resource1 stealed_resource1 DOUBLE UNSIGNED NOT NULL;
ALTER TABLE fg_report_damage CHANGE stealed_resource2 stealed_resource2 DOUBLE UNSIGNED NOT NULL;
ALTER TABLE fg_report_damage CHANGE stealed_resource3 stealed_resource3 DOUBLE UNSIGNED NOT NULL;
ALTER TABLE fg_player ADD diplomacy TINYINT(1) UNSIGNED NOT NULL AFTER tutorial;
UPDATE fg_player SET diplomacy = 1;
ALTER TABLE fg_player ADD switch_diplomacy_date INT UNSIGNED NOT NULL AFTER diplomacy;
ALTER TABLE fg_player CHANGE diplomacy diplomacy_activated TINYINT(1) UNSIGNED NOT NULL;
ALTER TABLE fg_fleet_link CHANGE link link ENUM('defensive', 'offensive', 'delude') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
CREATE TABLE fg_achievement (
  id_player SMALLINT UNSIGNED NOT NULL,
  type TINYINT UNSIGNED NOT NULL,
  score INT UNSIGNED NOT NULL
) ENGINE = MYISAM;
ALTER TABLE fg_achievement DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_achievement CHANGE score score BIGINT UNSIGNED NOT NULL;
ALTER TABLE fg_report_slot ADD available_abilities INT UNSIGNED NOT NULL AFTER slot_front;
ALTER TABLE fg_tradecenter ADD fees DOUBLE UNSIGNED NOT NULL AFTER variation;
UPDATE fg_tradecenter SET fees = .05;
ALTER TABLE fg_research CHANGE current queue_position TINYINT NOT NULL;
UPDATE fg_research SET queue_position = -1 WHERE queue_position = 0;
UPDATE fg_research SET queue_position = 0 WHERE queue_position = 1;
ALTER TABLE fg_tradecenter CHANGE rate0 rate0 DOUBLE UNSIGNED NOT NULL;
ALTER TABLE fg_tradecenter CHANGE rate1 rate1 DOUBLE UNSIGNED NOT NULL;
ALTER TABLE fg_tradecenter CHANGE rate2 rate2 DOUBLE UNSIGNED NOT NULL;
ALTER TABLE fg_tradecenter CHANGE rate3 rate3 DOUBLE UNSIGNED NOT NULL;
UPDATE fg_tradecenter SET rate0 = 1;
UPDATE fg_tradecenter SET rate1 = 1;
UPDATE fg_tradecenter SET rate2 = 1;
UPDATE fg_tradecenter SET rate3 = 1;
UPDATE fg_tradecenter SET variation = variation / 10;
ALTER TABLE fg_player CHANGE settings_theme settings_theme VARCHAR(250) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
UPDATE fg_player SET settings_theme = 'http://media3.fallengalaxy.com/style/FallenCraft2Red';
CREATE TABLE fg_bank (
  id_bank INT UNSIGNED NOT NULL,
  base_rate DOUBLE UNSIGNED NOT NULL,
  variable_rate DOUBLE UNSIGNED NOT NULL,
  fees INT UNSIGNED NOT NULL,
  PRIMARY KEY (id_bank)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
CREATE TABLE fg_advancement (
  id_player SMALLINT UNSIGNED NOT NULL,
  type TINYINT UNSIGNED NOT NULL,
  level TINYINT UNSIGNED NOT NULL,
  PRIMARY KEY (id_player, type)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_ward CHANGE type type ENUM('observer', 'observer_invisible', 'sentry', 'sentry_invisible', 'stun', 'stun_invisible', 'mine', 'mine_invisible') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_fleet_link CHANGE end date INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_news CHANGE title title VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_ward ADD power TINYINT UNSIGNED NOT NULL AFTER type;
UPDATE fg_ward SET power = 2;
CREATE TABLE fg_account_action (
  login VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  action ENUM('registered', 'closed', 'idle') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  email VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  birthday INT UNSIGNED NOT NULL,
  date INT UNSIGNED NOT NULL,
  ip INT NOT NULL,
  played_time INT UNSIGNED NOT NULL,
  INDEX (login)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_account_action ADD reason VARCHAR(250) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_player CHANGE id id INT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE fg_achievement CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_advancement CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_news_read CHANGE id_player id_player INT UNSIGNED NOT NULL;
ALTER TABLE fg_ward CHANGE id_owner id_owner INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally CHANGE id id INT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE fg_ally_influence CHANGE id_ally id_ally INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_news CHANGE id_ally id_ally INT UNSIGNED NOT NULL DEFAULT '0';
ALTER TABLE fg_ally_treaty CHANGE id_ally1 id_ally1 INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_treaty CHANGE id_ally2 id_ally2 INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_treaty CHANGE source source INT UNSIGNED NOT NULL;
ALTER TABLE fg_ally_vote CHANGE id_ally id_ally INT UNSIGNED NOT NULL;
ALTER TABLE fg_applicant CHANGE id_ally id_ally INT UNSIGNED NOT NULL;
ALTER TABLE fg_election CHANGE id id INT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE fg_election CHANGE id_ally id_ally INT UNSIGNED NOT NULL;
ALTER TABLE fg_player CHANGE id_ally id_ally INT UNSIGNED NOT NULL DEFAULT '0';
ALTER TABLE fg_space_station CHANGE id_ally id_ally INT UNSIGNED NOT NULL;
ALTER TABLE fg_player ADD close_account_hash VARCHAR(32) NOT NULL AFTER recover_password;
ALTER TABLE fg_player ADD close_account_reason VARCHAR(250) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER close_account_hash;
ALTER TABLE fg_system DROP race;
ALTER TABLE fg_system ADD asteroid_belt TINYINT UNSIGNED NOT NULL AFTER star_image;
CREATE TABLE fg_tactic (
  id_player INT UNSIGNED NOT NULL,
  hash VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  INDEX (id_player)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_tactic ADD date INT UNSIGNED NOT NULL;
CREATE TABLE fg_contract (
  id INT UNSIGNED NOT NULL,
  type SMALLINT UNSIGNED NOT NULL,
  target ENUM('player', 'ally') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  state ENUM('waiting', 'running') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  points INT UNSIGNED NOT NULL,
  date INT UNSIGNED NOT NULL,
  PRIMARY KEY (id)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
CREATE TABLE fg_contract_attendee_player (
  id_contract INT UNSIGNED NOT NULL,
  id_player INT UNSIGNED NOT NULL,
  PRIMARY KEY (id_contract, id_player)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
CREATE TABLE fg_contract_attendee_ally (
  id_contract INT UNSIGNED NOT NULL,
  id_ally INT UNSIGNED NOT NULL,
  PRIMARY KEY (id_contract, id_ally)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_contract_attendee_ally ADD state ENUM('offer', 'registered') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_contract_attendee_ally ADD date INT UNSIGNED NOT NULL;
ALTER TABLE fg_contract_attendee_player ADD state ENUM('offer', 'registered') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_contract_attendee_player ADD date INT UNSIGNED NOT NULL;
ALTER TABLE fg_contract ADD mode ENUM('player_single', 'player_versus', 'ally_versus') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER type;
ALTER TABLE fg_contract DROP target;
RENAME TABLE fg_contract_attendee_ally TO fg_contract_ally_attendee;
RENAME TABLE fg_contract_attendee_player TO fg_contract_player_attendee;
ALTER TABLE fg_contract CHANGE mode target ENUM('player', 'ally') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_contract ADD max_attendees TINYINT UNSIGNED NOT NULL AFTER target;
ALTER TABLE fg_contract CHANGE type type VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_contract_ally_attendee CHANGE state registered TINYINT(1) UNSIGNED NOT NULL;
ALTER TABLE fg_contract_player_attendee CHANGE state registered TINYINT(1) UNSIGNED NOT NULL;
CREATE TABLE fg_report_action_ability (
  id_report_action INT UNSIGNED NOT NULL,
  slot_id TINYINT UNSIGNED NOT NULL,
  ability TINYINT UNSIGNED NOT NULL,
  INDEX (id_report_action)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_report_action DROP ability;
ALTER TABLE fg_report_action_ability ADD index TINYINT UNSIGNED NOT NULL AFTER id_report_action;
ALTER TABLE fg_report_action_ability CHANGE ability ability SMALLINT NOT NULL;
ALTER TABLE fg_tactic ADD name VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER id_player;
ALTER TABLE fg_fleet ADD shortcut TINYINT(1) UNSIGNED NOT NULL AFTER tag;
ALTER TABLE fg_fleet CHANGE shortcut shortcut TINYINT(1) NOT NULL;
UPDATE fg_fleet SET shortcut = -1;
ALTER TABLE fg_space_station CHANGE hull hull INT UNSIGNED NOT NULL;
ALTER TABLE fg_system ADD shortcut TINYINT(1) UNSIGNED NOT NULL AFTER y;
ALTER TABLE fg_system CHANGE shortcut shortcut TINYINT(1) NOT NULL;
UPDATE fg_system SET shortcut = -1;
ALTER TABLE fg_player ADD ekey CHAR(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER password;
ALTER TABLE fg_contract CHANGE state state ENUM('waiting', 'running', 'finalizing') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_contract ADD step TINYINT UNSIGNED NOT NULL AFTER state;
ALTER TABLE fg_contract CHANGE date state_date INT(10) UNSIGNED NOT NULL;
CREATE TABLE fg_contract_area (
  id_contract INT UNSIGNED NOT NULL,
  id_area INT UNSIGNED NOT NULL,
  PRIMARY KEY (id_contract)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_contract_area CHANGE id_area id_area SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_fleet ADD unstuckable TINYINT(1) UNSIGNED NOT NULL AFTER scheduled_y;
ALTER TABLE fg_fleet ADD stuck_count TINYINT UNSIGNED NOT NULL AFTER unstuckable;
CREATE TABLE fg_contract_reward (
  id_contract INT UNSIGNED NOT NULL,
  type TINYINT UNSIGNED NOT NULL,
  key INT UNSIGNED NOT NULL,
  value INT UNSIGNED NOT NULL,
  PRIMARY KEY (id_contract,type, key)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_contract_reward CHANGE type type ENUM('xp', 'resource', 'ship') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_fleet ADD id_contract INT UNSIGNED NOT NULL;
CREATE TABLE fg_contract_dialog (
  id_fleet INT UNSIGNED NOT NULL,
  id_player INT UNSIGNED NOT NULL,
  current_option VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (id_fleet, id_player)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
CREATE TABLE fg_contract_parameter (
  id INT UNSIGNED NOT NULL,
  name VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  type ENUM('value', 'array', 'map')  CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  id_contract INT UNSIGNED NOT NULL,
  id_player INT UNSIGNED NOT NULL,
  PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
CREATE TABLE fg_contract_parameter_value (
  id_contract_parameter INT UNSIGNED NOT NULL,
  key_name VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  value VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (id_contract_parameter, key_name)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
ALTER TABLE fg_contract_parameter ADD id_ally INT UNSIGNED NOT NULL AFTER id_player;
ALTER TABLE fg_contract CHANGE points difficulty INT UNSIGNED NOT NULL;
ALTER TABLE fg_contract CHANGE target target ENUM('player', 'ally', 'all') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_contract_ally_attendee ADD id_player INT UNSIGNED NOT NULL AFTER id_contract;
RENAME TABLE fg_contract_ally_attendee TO fg_contract_attendee;
ALTER TABLE fg_contract_attendee ADD UNIQUE (
  id_contract,
  id_player,
  id_ally
);
DROP TABLE fg_contract_player_attendee;
ALTER TABLE fg_contract_area ADD id INT UNSIGNED NOT NULL FIRST;
ALTER TABLE fg_contract_area DROP PRIMARY KEY;
ALTER TABLE fg_contract_area ADD PRIMARY KEY (id);
ALTER TABLE fg_contract_reward CHANGE type type ENUM('xp', 'resource', 'ship', 'relationship') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
CREATE TABLE contract_relationship (
  id_contract INT UNSIGNED NOT NULL,
  id_player INT UNSIGNED NOT NULL,
  modifier SMALLINT NOT NULL,
  PRIMARY KEY (id_contract, id_player)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_contract ADD id_giver INT UNSIGNED NOT NULL;
ALTER TABLE fg_contract ADD variant VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER type;
ALTER TABLE fg_fleet CHANGE npc_type npc_type VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_contract_dialog CHANGE current_option current_entry VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_contract_reward CHANGE key key_name INT(10) UNSIGNED NOT NULL;
ALTER TABLE fg_research CHANGE id_technology id_technology SMALLINT UNSIGNED NOT NULL;
ALTER TABLE fg_fleet DROP mission_type;
ALTER TABLE fg_player ADD id_contract INT UNSIGNED NOT NULL;
ALTER TABLE fg_player ADD avatar VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER ban_game;
ALTER TABLE fg_report_action_ability CHANGE index position TINYINT(3) UNSIGNED NOT NULL;
ALTER TABLE fg_ally ADD right_accept TINYINT UNSIGNED NOT NULL AFTER points;
ALTER TABLE fg_ally ADD right_manage_news TINYINT UNSIGNED NOT NULL AFTER right_accept;
ALTER TABLE fg_ally ADD right_manage_stations TINYINT UNSIGNED NOT NULL AFTER right_manage_news;
ALTER TABLE fg_ally ADD right_manage_diplomacy TINYINT UNSIGNED NOT NULL AFTER right_manage_stations;
ALTER TABLE fg_ally ADD right_manage_description TINYINT UNSIGNED NOT NULL AFTER right_manage_diplomacy;
ALTER TABLE fg_ally ADD right_manage_contracts TINYINT UNSIGNED NOT NULL AFTER right_manage_description;
UPDATE fg_ally SET
	right_accept = 2,
	right_manage_news = 2,
	right_manage_stations = 3,
	right_manage_diplomacy = 3,
	right_manage_description = 3,
	right_manage_contracts = 3
WHERE organization LIKE 'tyranny';
UPDATE fg_ally SET
	right_accept = 2,
	right_manage_news = 3,
	right_manage_stations = 3,
	right_manage_diplomacy = 4,
	right_manage_description = 4,
	right_manage_contracts = 4
WHERE organization LIKE 'warmonger';
UPDATE fg_ally SET
	right_accept = 2,
	right_manage_news = 2,
	right_manage_stations = 2,
	right_manage_diplomacy = 3,
	right_manage_description = 3,
	right_manage_contracts = 3
WHERE organization LIKE 'democracy';
UPDATE fg_ally SET
	right_accept = 2,
	right_manage_news = 2,
	right_manage_stations = 3,
	right_manage_diplomacy = 3,
	right_manage_description = 3,
	right_manage_contracts = 3
WHERE organization LIKE 'oligarchy';
UPDATE fg_ally SET
	right_accept = 1,
	right_manage_news = 1,
	right_manage_stations = 1,
	right_manage_diplomacy = 1,
	right_manage_description = 1,
	right_manage_contracts = 1
WHERE organization LIKE 'anarchy';
ALTER TABLE fg_contract_relationship CHANGE id_player id_ally INT UNSIGNED NOT NULL;
CREATE TABLE fg_relationship (
  id_player INT UNSIGNED NOT NULL,
  id_ally INT UNSIGNED NOT NULL,
  value SMALLINT NOT NULL,
  PRIMARY KEY (id_player, id_ally)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_relationship CHANGE value value DOUBLE NOT NULL;
ALTER TABLE fg_contract_reward CHANGE type type ENUM('xp', 'resource', 'ship', 'fleet_xp') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
ALTER TABLE fg_marker DROP system;
ALTER TABLE fg_marker ADD id_contract INT UNSIGNED NOT NULL;
ALTER TABLE fg_fleet CHANGE scheduled_x scheduled_x SMALLINT NOT NULL;
ALTER TABLE fg_fleet CHANGE scheduled_y scheduled_y SMALLINT NOT NULL;
ALTER TABLE fg_contract_reward ADD id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;
ALTER TABLE fg_fleet ADD skill0_reload INT UNSIGNED NOT NULL AFTER encoded_skill_ultimate;
ALTER TABLE fg_fleet ADD skill1_reload INT UNSIGNED NOT NULL AFTER skill0_reload;
ALTER TABLE fg_fleet ADD skill2_reload INT UNSIGNED NOT NULL AFTER skill1_reload;
ALTER TABLE fg_fleet ADD skill_ultimate_reload INT UNSIGNED NOT NULL AFTER skill2_reload;
ALTER TABLE fg_fleet ADD skill0_last_use INT UNSIGNED NOT NULL AFTER skill_ultimate_reload;
ALTER TABLE fg_fleet ADD skill1_last_use INT UNSIGNED NOT NULL AFTER skill0_last_use;
ALTER TABLE fg_fleet ADD skill2_last_use INT UNSIGNED NOT NULL AFTER skill1_last_use;
ALTER TABLE fg_fleet ADD skill_ultimate_last_use INT UNSIGNED NOT NULL AFTER skill2_last_use;
ALTER TABLE fg_ally ADD color TINYINT UNSIGNED NOT NULL AFTER ai;
CREATE TABLE fg_structure (
  id INT UNSIGNED NOT NULL,
  name VARCHAR( 20 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  hull INT UNSIGNED NOT NULL,
  x SMALLINT UNSIGNED NOT NULL,
  y SMALLINT UNSIGNED NOT NULL,
  id_player INT UNSIGNED NOT NULL,
  id_ally INT UNSIGNED NOT NULL,
  id_area INT UNSIGNED NOT NULL,
  PRIMARY KEY (id)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_structure ADD type ENUM('storehouse') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER id;
CREATE TABLE fg_structure_storehouse (
  id_structure INT UNSIGNED NOT NULL,
  id_player INT UNSIGNED NOT NULL,
  resource0 INT UNSIGNED NOT NULL,
  resource1 INT UNSIGNED NOT NULL,
  resource2 INT UNSIGNED NOT NULL,
  resource3 INT UNSIGNED NOT NULL,
  PRIMARY KEY (id_structure, id_player)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_structure ADD resource0 INT UNSIGNED NOT NULL AFTER y;
ALTER TABLE fg_structure ADD resource1 INT UNSIGNED NOT NULL AFTER resource0;
ALTER TABLE fg_structure ADD resource2 INT UNSIGNED NOT NULL AFTER resource1;
ALTER TABLE fg_structure ADD resource3 INT UNSIGNED NOT NULL AFTER resource2;
ALTER TABLE fg_structure ADD credits INT UNSIGNED NOT NULL AFTER y;
ALTER TABLE fg_structure CHANGE type type TINYINT UNSIGNED NOT NULL;
UPDATE fg_structure SET type = 0;
CREATE TABLE fg_structure_spaceship_yard (
  id_structure INT UNSIGNED NOT NULL,
  id_player INT UNSIGNED NOT NULL,
  slot0_id TINYINT UNSIGNED NOT NULL,
  slot0_count INT UNSIGNED NOT NULL,
  slot1_id TINYINT UNSIGNED NOT NULL,
  slot1_count INT UNSIGNED NOT NULL,
  slot2_id TINYINT UNSIGNED NOT NULL,
  slot2_count INT UNSIGNED NOT NULL,
  slot3_id TINYINT UNSIGNED NOT NULL,
  slot3_count INT UNSIGNED NOT NULL,
  slot4_id TINYINT UNSIGNED NOT NULL,
  slot4_count INT UNSIGNED NOT NULL,
  PRIMARY KEY (id_structure, id_player)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_structure_storehouse CHANGE id_structure id_area SMALLINT UNSIGNED NOT NULL;
TRUNCATE TABLE fg_structure;
TRUNCATE TABLE fg_structure_spaceship_yard;
TRUNCATE TABLE fg_structure_storehouse;
ALTER TABLE fg_structure DROP credits;
ALTER TABLE fg_structure DROP resource0;
ALTER TABLE fg_structure DROP resource1;
ALTER TABLE fg_structure DROP resource2;
ALTER TABLE fg_structure DROP resource3;
ALTER TABLE fg_structure DROP id_ally;
ALTER TABLE fg_structure CHANGE id_player id_owner INT(10) UNSIGNED NOT NULL;
CREATE TABLE fg_structure_module (
  id_structure INT UNSIGNED NOT NULL,
  type TINYINT UNSIGNED NOT NULL,
  level TINYINT UNSIGNED NOT NULL,
  PRIMARY KEY (id_structure, type)
) ENGINE = MYISAM CHARACTER SET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE fg_fleet ADD current_action ENUM('none', 'jump', 'mine', 'build_structure', 'build_ward', 'attack_structure', 'attack_ward', 'colonize', 'pick_up', 'defuse', 'battle') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL AFTER npc_type;
ALTER TABLE fg_structure ADD id_item_container INT UNSIGNED NOT NULL;
ALTER TABLE fg_structure ADD activated TINYINT(1) UNSIGNED NOT NULL AFTER hull;
UPDATE fg_structure SET activated = 1;
ALTER TABLE fg_structure_spaceship_yard ADD last_bought_fleet INT UNSIGNED NOT NULL AFTER build_slot2_ordered;
ALTER TABLE fg_object ADD variant INT UNSIGNED NOT NULL AFTER type;
UPDATE fg_object SET variant = 1 WHERE type = 'doodad1';
UPDATE fg_object SET variant = 2 WHERE type = 'doodad2';
UPDATE fg_object SET variant = 3 WHERE type = 'doodad3';
UPDATE fg_object SET variant = 4 WHERE type = 'doodad4';
UPDATE fg_object SET variant = 5 WHERE type = 'doodad5';
ALTER TABLE fg_object CHANGE type type ENUM('gate', 'tradecenter', 'pirates', 'bank', 'asteroid', 'asteroid_dense', 'asteroid_low_titanium', 'asteroid_low_crystal', 'asteroid_low_andium', 'asteroid_avg_titanium', 'asteroid_avg_crystal', 'asteroid_avg_andium', 'asteroid_high_titanium', 'asteroid_high_crystal', 'asteroid_high_andium', 'blackhole', 'doodad', 'gravity_well') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL;
DELETE FROM fg_object WHERE type LIKE '';
ALTER TABLE fg_structure ADD shared TINYINT UNSIGNED NOT NULL AFTER y;
ALTER TABLE fg_structure ADD id_energy_supplier_structure INT UNSIGNED NOT NULL AFTER shared;
ALTER TABLE fg_structure DROP activated;
ALTER TABLE fg_structure ADD shortcut TINYINT NOT NULL AFTER shared;
UPDATE fg_structure SET shortcut = -1;
ALTER TABLE fg_area ADD id_dominating_ally INT UNSIGNED NOT NULL AFTER last_name_update;
ALTER TABLE fg_area ADD product SMALLINT UNSIGNED NOT NULL AFTER space_stations_limit;
