USE cdss_supplier;

DROP TABLE IF EXISTS cdss_supplier.trigger_definition;
DROP TABLE IF EXISTS cdss_supplier.service_definition_use_context;
DROP TABLE IF EXISTS cdss_supplier.use_context;
DROP TABLE IF EXISTS cdss_supplier.service_definition;
DROP TABLE IF EXISTS cdss_supplier.questionnaire;
DROP TABLE IF EXISTS cdss_supplier.result;
DROP TABLE IF EXISTS cdss_supplier.resource;
DROP TABLE IF EXISTS cdss_supplier.coded_data;
DROP TABLE IF EXISTS cdss_supplier.data_requirement;

CREATE TABLE cdss_supplier.resource (
  id                     BIGINT NOT NULL AUTO_INCREMENT,
  resource_type        VARCHAR(255),
  parent_id            BIGINT NULL,
  resource_json        BLOB,
  PRIMARY KEY (id)
);

CREATE TABLE cdss_supplier.data_requirement (
  id                     BIGINT NOT NULL AUTO_INCREMENT,
  service_definition_id  BIGINT NULL,
  questionnaire_id       BIGINT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE cdss_supplier.coded_data (
  id                  BIGINT NOT NULL AUTO_INCREMENT,
  data_requirement_id BIGINT NULL,
  data_type           VARCHAR(20) NULL,
  code                VARCHAR(50) NULL,
  display             VARCHAR(100) NULL,
  value               BOOLEAN NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (data_requirement_id) REFERENCES cdss_supplier.data_requirement(id)
);

CREATE TABLE cdss_supplier.service_definition (
  id               BIGINT NOT NULL AUTO_INCREMENT,
  scenario_id      BIGINT NULL,
  description      VARCHAR(255) NULL,
  purpose          VARCHAR(255) NULL,
  status 		   VARCHAR(50) NULL,
  effective_from   DATETIME NULL,
  effective_to	   DATETIME NULL,
  jurisdiction	   VARCHAR(50) NULL,
  experimental	   TINYINT NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE cdss_supplier.questionnaire (
  id        BIGINT NOT NULL AUTO_INCREMENT,
  question  VARCHAR(1000) NULL,
  answers   VARCHAR(4000) NULL,
  PRIMARY KEY (id)
);

CREATE TABLE cdss_supplier.result (
  id                     BIGINT NOT NULL AUTO_INCREMENT,
  service_definition_id  BIGINT NULL,
  result                 VARCHAR(255) NULL,
  coded_data_id 		 BIGINT NULL,
  FOREIGN KEY (coded_data_id) REFERENCES cdss_supplier.coded_data(id),
  PRIMARY KEY (id)
);

CREATE TABLE cdss_supplier.use_context (
  id                     BIGINT NOT NULL,
  code			         VARCHAR(255) NULL,
  display				 VARCHAR(255) NULL,
  system				 VARCHAR(2048) NULL,
  PRIMARY KEY (id)
);

CREATE TABLE cdss_supplier.service_definition_use_context (
  id					 BIGINT NOT NULL AUTO_INCREMENT,
  service_definition_id  BIGINT NOT NULL,
  use_context_id		 BIGINT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (service_definition_id) REFERENCES cdss_supplier.service_definition(id),
  FOREIGN KEY (use_context_id) REFERENCES cdss_supplier.use_context(id)
);

CREATE TABLE cdss_supplier.trigger_definition (
  id                     BIGINT NOT NULL AUTO_INCREMENT,
  service_definition_id  BIGINT NULL,
  code       			 VARCHAR(255) NULL,
  system				 VARCHAR(2048) NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (service_definition_id) REFERENCES cdss_supplier.service_definition(id)
);

INSERT INTO cdss_supplier.service_definition
 (id, scenario_id, description, purpose, status, jurisdiction, effective_from, effective_to, experimental) 
VALUES
 (1, '1', 'Vomiting and fever 3rd Party', 'To triage a patient who is vomiting and has a fever', 'ACTIVE', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 0),
 (2, '2', 'Headache 1st Party', 'To triage a patient who has a headache', 'ACTIVE', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 0),
 (3, '3', 'Leg injury and Blunt trauma 1st Party', 'To triage a patient who has a leg injury', 'ACTIVE', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 0),
 (4, '4', 'Hand injury 1st Party', 'To triage a patient who has a hand injury', 'ACTIVE', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 0),
 (5, '5', 'Palpitations 1st Party', 'To triage a patient who has palpitations', 'ACTIVE', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 0),
 (6, '6', 'Leg injury and Blunt trauma 1st Party', 'To triage a patient who has a leg injury', 'ACTIVE', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 0),
 (7, '7', 'Mental Health 1st Party', 'Specialist Mental Health CDSS', 'ACTIVE', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 0),
 (8, '8', 'None of the above 1st Party', 'None of the above', 'ACTIVE', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00',  0),
 (9, '9', 'Mental Health Table 1st Party', 'Mental Health Table', 'ACTIVE', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 0),
 (10, '10', 'CDSS Switch 1st Party', 'CDSS Switch', 'ACTIVE', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 0),
 (11, '1', 'Vomiting and fever 3rd Party 2', 'To triage a patient who is vomiting and has a fever', 'ACTIVE', 'ENG', '2018-01-01 00:00:00', '2018-12-01 00:00:00', 0),
 (12, '2', 'Headache 1st Party 2', 'To triage a patient who has a headache', 'RETIRED', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 0),
 (13, '3', 'Leg injury and Blunt trauma 1st Party 2', 'To triage a patient who has a leg injury', 'ACTIVE', 'ENG', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 1),
 (14, '4', 'Hand injury 1st Party 2', 'To triage a patient who has a hand injury', 'ACTIVE', 'WEL', '2019-01-01 00:00:00', '2019-12-01 00:00:00', 0);
 
INSERT INTO cdss_supplier.questionnaire
 (id, question, answers) 
VALUES
 (1, 'Has a rash appeared on your skin?', 'yes,no,not sure'),
 (2, 'Are you experiencing any abdominal pain?', 'yes,no,not sure'),
 (3, 'Has there been any blood in your vomit?', 'yes,no,not sure'),
 (4, 'Is your vision blurred?', 'yes,no,not sure'),
 (5, 'Do you have a headache?', 'yes,no,not sure'),
 (6, 'Are you experiencing pain when looking at the light?', 'yes,no,not sure'),
 (7, 'Do you have diarrhoea?', 'yes,no,not sure'),
 (8, 'Do you have a sore throat?', 'yes,no,not sure'),
 (9, 'Are you able to move your neck?', 'yes,no,not sure'),
 (10, 'Have you suddenly become confused or distressed?', 'yes,no,not sure'),
 (11, 'Have you been drinking water?', 'yes,no,not sure'),
 (12, 'Have you been to a West African country in the last 4 weeks?', 'yes,no,not sure'),
 (13, 'Have you taken any precautions against Malaria?', 'yes,no,not sure'),
 (14, 'Have you fainted at all?', 'yes,no,not sure'),
 (15, 'Do your limbs feel weaker than usual?', 'yes,no,not sure'),
 (16, 'Have you had a head injury?', 'yes,no,not sure'),
 (17, 'Have you been vomiting?', 'yes,no,not sure'),
 (18, 'Are you feeling nauseous?', 'yes,no,not sure'),
 (19, 'Have you got severe pain in the back of your head?', 'yes,no,not sure'),
 (20, 'Are you suffering from any pain in your joints?', 'yes,no,not sure'),
 (21, 'Is your knee deformed?', 'yes,no,not sure'),
 (22, 'Are you able to walk?', 'yes,no,not sure'),
 (23, 'Are you up to date with your tetanus shots?', 'yes,no,not sure'),
 (24, 'Have you lost any feeling in your hand?', 'yes,no,not sure'),
 (25, 'Is the wound oozing?', 'yes,no,not sure'),
 (26, 'Are you suffering from any pain in your chest?', 'yes,no,not sure'),
 (27, 'Are you suffering from any pain in your neck or shoulders?', 'yes,no,not sure'),
 (28, 'Are you experiencing shortness of breath?', 'yes,no,not sure'),
 (29, 'Have you got a history of heart problems?', 'yes,no,not sure'),
 (30, 'Have you caused yourself any harm?', 'yes,no,not sure'),
 (31, 'Do you feel like you may cause harm to yourself or others?', 'yes,no,not sure'),
 (32, 'Are you feeling irritated or agitated?', 'yes,no,not sure'),
 (33, 'Are you hallucinating?', 'yes,no,not sure'),
 (34, 'Are you struggling to concentrate?', 'yes,no,not sure'),
 (35, 'How many days have you been feeling like this?', 'yes,no,not sure'),
 (36, 'Are you struggling to sleep?', 'yes,no,not sure'),
 (37, 'On average, how many hours of sleep are you getting each night?', 'yes,no,not sure'),
 (38, 'What date did this begin?', 'yes,no,not sure'),
 (39, 'Is the individual experiencing any of the following (which are different from their normal)?:', 'Choking and unable to stop or having trouble breathing or feeling that the airways are getting blocked,Breathing noisily or unable to swallow saliva,Too breathless to speak or gasping for breath,Turning blue around the mouth or lips,Feeling unwell and skin feels cold and sweaty or is very pale or blotchy,Unconscious or hard to wake up or keep awake,None of the above'),
 (40, 'Ask the individual to draw a clock at 11:10. Upload the image.', 'yes,no,not sure'),
 (41, 'Which colour do you prefer: red, blue, green or yellow?', 'red,blue,green,yellow'),
 (42, 'Over the past 2 weeks, how often have you been bothered by any of the following problems?', 'yes,no,not sure'),
 (43, 'Feeling down, depressed, or hopeless', 'Not at all,Several days,More than half the days,Nearly every day'),
 (44, 'Little interest or pleasure in doing things', 'Not at all,Several days,More than half the days,Nearly every day'),
 (45, 'What is your problem?', 'Pain of any kind,Skin or hair or nail problem,Typical common cold or flu symptoms help,Breathing problem,Cough,Blocked or runny nose or sinus problem help,Sore throat,Eye or vision problem,Vomiting or nausea help,Diarrhoea (looser or more frequent stools) help,Constipation help,Difficulties with urination help,Female problem (breast or genital or vaginal bleeding problem),Anal or rectal problem or including problem with stools or haemorrhoids help,Other problem not listed'),
 (46, 'End of Test', 'yes,no,not sure');
 
INSERT INTO cdss_supplier.data_requirement
 (id, service_definition_id, questionnaire_id) 
VALUES
 (1, 1, 1),
 (2, 1, 2),
 (3, 1, 3),
 (4, 1, 4),
 (5, 1, 5),
 (6, 1, 6),
 (7, 1, 7),
 (8, 1, 8),
 (9, 1, 9),
 (10, 1, 10),
 (11, 1, 11),
 (12, 1, 12),
 (13, 1, 13),
 (14, 2, 1),
 (15, 2, 14),
 (16, 2, 6),
 (17, 2, 7),
 (18, 2, 15),
 (19, 2, 16),
 (20, 2, 17),
 (21, 2, 18),
 (22, 2, 19),
 (23, 3, 20),
 (24, 3, 21),
 (25, 3, 22),
 (26, 3, 23),
 (27, 4, 23),
 (28, 4, 24),
 (29, 4, 25),
 (30, 5, 26),
 (31, 5, 27),
 (32, 5, 28),
 (33, 5, 29),
 (34, 6, 20),
 (35, 6, 21),
 (36, 6, 22),
 (37, 7, 30),
 (38, 7, 31),
 (39, 7, 32),
 (40, 7, 33),
 (41, 7, 34),
 (42, 7, 35),
 (43, 7, 36),
 (44, 7, 37),
 (45, 7, 38),
 (46, 8, 39),
 (47, 7, 40),
 (48, 7, 41),
 (49, 9, 43),
 (50, 9, 44),
 (51, 10, 45),
 (52, 10, 46),
 (101, 11, 1),
 (102, 11, 2),
 (103, 11, 3),
 (104, 11, 4),
 (105, 11, 5),
 (106, 11, 6),
 (107, 11, 7),
 (108, 11, 8),
 (109, 11, 9),
 (110, 11, 10),
 (111, 11, 11),
 (112, 11, 12),
 (113, 11, 13),
 (114, 12, 1),
 (115, 12, 14),
 (116, 12, 6),
 (117, 12, 7),
 (118, 12, 15),
 (119, 12, 16),
 (120, 12, 17),
 (121, 12, 18),
 (122, 12, 19),
 (123, 13, 20),
 (124, 13, 21),
 (125, 13, 22),
 (126, 13, 23),
 (127, 14, 23),
 (128, 14, 24),
 (129, 14, 25);
 
INSERT INTO cdss_supplier.coded_data
 (id, data_requirement_id, data_type, code, display, value) 
VALUES
 (1, 1, 'observation', '271807003', 'Eruption of skin', false),
 (2, 2, 'observation', '21522001', 'Abdominal pain', false),
 (3, 3, 'observation', '281102003', 'Blood in vomit – symptom', false),
 (4, 4, 'observation', '240091000000105', 'Blurred vision', false),
 (5, 5, 'observation', '25064002', 'Headache', false),
 (6, 6, 'observation', '409668002', 'Photophobia', false),
 (7, 7, 'observation', '267060006', 'Diarrhoea symptom', false),
 (8, 8, 'observation', '267102003', 'Sore throat', false),
 (9, 9, 'observation', '161882006', 'Stiff neck', false),
 (10, 10, 'observation', '62476001', 'Disorientated', false),
 (11, 11, 'observation', '289154005', 'Drinking fluids normally', true),
 (12, 12, 'observation', '223552000', 'West African country', true),
 (13, 13, 'medication', '420848008', 'Antimalarial prophylaxis (procedure)', false),
 (14, 14, 'observation', '271807003', 'Eruption of skin', false),
 (15, 15, 'observation', '271594007', 'Fainting', false),
 (16, 16, 'observation', '409668002', 'Photophobia', false),
 (17, 17, 'observation', '267060006', 'Diarrhoea symptom', false),
 (18, 18, 'observation', '713514005', 'Muscle weakness in limb', false),
 (19, 19, 'observation', '82271004', 'Injury of head', false),
 (20, 20, 'observation', '422400008', 'Vomiting', true),
 (21, 21, 'observation', '422587007', 'Nausea', true),
 (22, 22, 'observation', '25064002', 'Headache', false),
 (23, 23, 'observation', '57676002', 'Joint pain', false),
 (24, 24, 'observation', '250087009', 'Joint deformity', false),
 (25, 25, 'observation', '129006008', 'Walking', true),
 (26, 26, 'immunization', '333641007', 'Adsorbed tetanus vaccine injection solution prefilled syringe', true),
 (27, 27, 'immunization', '333641007', 'Adsorbed tetanus vaccine injection solution prefilled syringe', false),
 (28, 28, 'observation', '309521004', 'Numbness of hand', false),
 (29, 29, 'observation', '122568004', 'Exudate specimen from wound', true),
 (30, 30, 'observation', '29857009', 'Chest pain', false),
 (31, 31, 'observation', '81680005', 'Neck pain', false),
 (32, 31, 'observation', '45326000', 'Shoulder pain', false),
 (33, 32, 'observation', '230145002', 'Difficulty breathing', false),
 (34, 33, 'observation', '275544003', 'History of heart disorder', false),
 (35, 34, 'observation', '57676002', 'Joint pain', false),
 (36, 35, 'observation', '250087009', 'Joint deformity', false),
 (37, 36, 'observation', '129006008', 'Walking', true),
 (38, 37, 'observation', '248062006', 'Self-injurious behaviour', false),
 (39, 38, 'observation', '406556003', 'High risk of harm to others', false),
 (40, 39, 'observation', '24199005', 'Feeling agitated', true),
 (41, 40, 'observation', '7011001', 'Hallucinations', true),
 (42, 41, 'observation', '60032008', 'Unable to concentrate', true),
 (43, 42, 'observation', '398201009', 'Start time', true),
 (44, 43, 'observation', '301345002', 'Difficulty sleeping', true),
 (45, 44, 'observation', '404950004', 'Sleep behavior', true),
 (46, 45, 'observation', '298059007', 'Date of onset', true),
 (47, 46, 'observation', '163131000000108', 'Clinical observations and findings', true),
 (48, 47, 'observation', '165320004', 'Dementia test', false),
 (49, 46, 'observation', '196168001', 'Choking due to airways obstruction', false),
 (50, 46, 'observation', '288959006', 'Unable to swallow saliva', false),
 (51, 46, 'observation', '230145002', 'Difficulty breathing', false),
 (52, 46, 'observation', '162743000', 'Blue lips', false),
 (53, 46, 'observation', '367391008', 'Feels unwell', false),
 (54, 46, 'observation', '418107008', 'Unconscious', false),
 (55, 48, 'observation', '405738005', 'Blue', true),
 (56, 48, 'observation', '90998002', 'Yellow', true),
 (57, 48, 'observation', '371240000', 'Red', true),
 (58, 48, 'observation', '405739002', 'Green', true),
 (59, 49, 'observation', '35489007', 'Depressed', true),
 (60, 50, 'observation', '247753000', 'Loss of interest', true),
 (61, 51, 'observation', '49727002', 'Cough', true),
 (62, 52, 'observation', '261782000', 'End', true),
 (63, NULL, 'practiceCode', '394814009', 'General practice', true),
 (64, NULL, 'practiceCode', '394602003', 'Rehabilitation', true),
 (65, NULL, 'practiceCode', '394882004', 'Pain management', true),
 (66, NULL, 'practiceCode', '394733009', 'Medical specialty', true),
 (67, NULL, 'practiceCode', '394809005', 'Clinical neuro-physiology', true),
 (101, 101, 'observation', '271807003', 'Eruption of skin', false),
 (102, 102, 'observation', '21522001', 'Abdominal pain', false),
 (103, 103, 'observation', '281102003', 'Blood in vomit – symptom', false),
 (104, 104, 'observation', '240091000000105', 'Blurred vision', false),
 (105, 105, 'observation', '25064002', 'Headache', false),
 (106, 106, 'observation', '409668002', 'Photophobia', false),
 (107, 107, 'observation', '267060006', 'Diarrhoea symptom', false),
 (108, 108, 'observation', '267102003', 'Sore throat', false),
 (109, 109, 'observation', '161882006', 'Stiff neck', false),
 (110, 110, 'observation', '62476001', 'Disorientated', false),
 (111, 111, 'observation', '289154005', 'Drinking fluids normally', true),
 (112, 112, 'observation', '223552000', 'West African country', true),
 (113, 113, 'medication', '420848008', 'Antimalarial prophylaxis (procedure)', false),
 (114, 114, 'observation', '271807003', 'Eruption of skin', false),
 (115, 115, 'observation', '271594007', 'Fainting', false),
 (116, 116, 'observation', '409668002', 'Photophobia', false),
 (117, 117, 'observation', '267060006', 'Diarrhoea symptom', false),
 (118, 118, 'observation', '713514005', 'Muscle weakness in limb', false),
 (119, 119, 'observation', '82271004', 'Injury of head', false),
 (120, 120, 'observation', '422400008', 'Vomiting', true),
 (121, 121, 'observation', '422587007', 'Nausea', true),
 (122, 122, 'observation', '25064002', 'Headache', false),
 (123, 123, 'observation', '57676002', 'Joint pain', false),
 (124, 124, 'observation', '250087009', 'Joint deformity', false),
 (125, 125, 'observation', '129006008', 'Walking', true),
 (126, 126, 'immunization', '333641007', 'Adsorbed tetanus vaccine injection solution prefilled syringe', true),
 (127, 127, 'immunization', '333641007', 'Adsorbed tetanus vaccine injection solution prefilled syringe', false),
 (128, 128, 'observation', '309521004', 'Numbness of hand', false),
 (129, 129, 'observation', '122568004', 'Exudate specimen from wound', true);
 
INSERT INTO cdss_supplier.use_context
 (id, code, display, system) 
VALUES
 (1, '3', '3rd Party', 'CDSS'),
 (2, '111CH', '111 Call Handler', 'CDSS'),
 (3, '1', '1st Party', 'CDSS'),
 (4, '111CL', '111 Clinician', 'CDSS');
 
INSERT INTO cdss_supplier.service_definition_use_context
 (service_definition_id, use_context_id) 
VALUES
 (1, 1),
 (1, 2),
 (2, 2),
 (2, 3),
 (3, 2),
 (3, 3),
 (4, 2),
 (4, 3),
 (5, 2),
 (5, 3),
 (6, 2),
 (6, 3),
 (7, 3),
 (7, 4),
 (8, 3),
 (8, 4),
 (9, 3),
 (9, 4),
 (10, 3),
 (10, 4),
 (11, 1),
 (11, 2),
 (12, 2),
 (12, 3),
 (13, 2),
 (13, 3),
 (14, 2),
 (14, 3);
 
INSERT INTO cdss_supplier.trigger_definition 
 (service_definition_id, code, system) 
VALUES
 (1, '240091000000105', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (2, '271594007', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (3, '250087009', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (4, '309521004', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (5, '45326000', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (6, '57676002', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (7, '248062006', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (8, '288959006', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (9, '35489007', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (10, '49727002', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (11, '240091000000105', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (12, '271594007', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (13, '250087009', 'https://www.hl7.org/fhir/triggerdefinition.html'),
 (14, '309521004', 'https://www.hl7.org/fhir/triggerdefinition.html');
 
 INSERT INTO cdss_supplier.result
 (id, service_definition_id, result, coded_data_id)
VALUES
 (1, 1, 'Primary Care within 24 hours', 63),
 (2, 2, 'Emergency Ambulance/999', 64),
 (3, 3, 'Self-care', 65),
 (4, 4, 'Primary Care 6 hours', 63),
 (5, 5, 'ED', 66),
 (6, 6, 'Place holder for Care Advice', 66),
 (7, 7, 'Community mental health team home visit', 67),
 (8, 8, 'Self-care', 65),
 (9, 9, 'Community mental health team home visit', 67),
 (10, 10, 'Self-care', 65),
 (11, 11, 'Primary Care within 24 hours', 63),
 (12, 12, 'Emergency Ambulance/999', 64),
 (13, 13, 'Self-care', 65),
 (14, 14, 'Primary Care 6 hours', 63);
 