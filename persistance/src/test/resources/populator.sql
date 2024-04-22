INSERT INTO users(username, password, email, owner) VALUES ('Pedro', 'curti', 'pedro@curti.com', false);
INSERT INTO users(username, password, email, owner) VALUES ('notMod', 'curti','notmod@hotmail.com',false);
INSERT INTO community(name, description, portrait_id) VALUES ('test', 'This is a test community', null);
INSERT INTO community(name, description, portrait_id) VALUES ('other', 'This is a test community', null);
INSERT INTO community(name, description, portrait_id) VALUES ('test2', 'This is a test community', null);
INSERT INTO post(title, body, author_id, community_name, media, media_id, post_date,category) VALUES ('First post', 'This is my first post', 1, 'test', false, null, '2020-01-01 00:00:00','News');
-- id 1 is test
-- id 2 is other
-- id 3 is test2
INSERT INTO communities_categories(community_id, category) VALUES (3, 'Action');
INSERT INTO communities_categories(community_id, category) VALUES (1, 'Shooter');
INSERT INTO communities_categories(community_id, category) VALUES (1, 'RPG');
INSERT INTO communities_categories(community_id, category) VALUES (2, 'RPG');
INSERT INTO modders(user_id, community_id) VALUES (1, 1);