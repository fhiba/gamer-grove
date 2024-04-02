INSERT INTO users(username, password, email, owner) VALUES ('Pedro', 'curti', 'pedro@curti.com', false);
INSERT INTO community(name, description, portrait_id) VALUES ('test', 'This is a test community', null);
INSERT INTO post_categories(category) VALUES ('Miscellaneous');
INSERT INTO post(title, body, author_id, community_name, media, media_id, post_date,category) VALUES ('First post', 'This is my first post', 1, 'test', false, null, '2020-01-01 00:00:00','Miscellaneous');