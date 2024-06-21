INSERT INTO users(id, username, password, email, owner, verified, locale) VALUES (10, 'Pedro', 'curti', 'pedro@curti.com', false, false, 'en');
INSERT INTO users(id, username, password, email, owner, verified, locale) VALUES (11, 'notMod', 'curti','notmod@hotmail.com',false, false, 'en');

INSERT INTO community(id, name, description, portrait_id,publisher,developer,release_date,rating_count,total_rating) VALUES (10, 'test', 'This is a test community', null,'falsepub','falsedeveloper','2024-05-05 20:30:00',0,0);
INSERT INTO community(id, name, description, portrait_id,publisher,developer,release_date) VALUES (11, 'other', 'This is a test community', null,'falsepub','falsedeveloper','2024-05-05 20:30:00');
INSERT INTO community(id, name, description, portrait_id,publisher,developer,release_date) VALUES (12, 'test2', 'This is a test community', null,'falsepub','falsedeveloper','2024-05-05 20:30:00');
INSERT INTO post(id, title, body, author_id, community_name, media, media_id, post_date,category, grooviness, deleted) VALUES (10, 'First post', 'This is my first post', 10, 'test', false, null, '2020-01-01 00:00:00','News',0, false);
INSERT INTO post(id, title, body, author_id, community_name, media, media_id, post_date,category, grooviness, deleted) VALUES (11, 'First post', 'This is my first post', 10, 'test', false, null, '2020-01-01 00:00:00','News',0, false);

-- id 1 is test
-- id 2 is other
-- id 3 is test2
INSERT INTO communities_categories(community_id, category) VALUES (12, 'Action');
INSERT INTO communities_categories(community_id, category) VALUES (10, 'Shooter');
INSERT INTO communities_categories(community_id, category) VALUES (10, 'RPG');
INSERT INTO communities_categories(community_id, category) VALUES (11, 'RPG');
INSERT INTO modders(user_id, community_id) VALUES (10, 10);
-- INSERT INTO modders(user_id, community_id) VALUES (10, 11);

INSERT INTO groovy_post_history(user_id, post_id, groovy_type) VALUES (10,10,true);

INSERT INTO community_user(community_id, user_id,community_role,community_name) VALUES (10, 10, 0,'test');

INSERT INTO comment ( id, post_id, author_id, parent_id, body, comment_date, grooviness,deleted)
VALUES (10, 10, 10, null, 'Comment 1', '2024-05-05 20:30:00', 0, false);

INSERT INTO comment (id, post_id, author_id, parent_id, body, comment_date, grooviness,deleted)
VALUES ( 11, 10, 10, null, 'Comment 2', CURRENT_TIMESTAMP, 0, false);

INSERT INTO comment (id, post_id, author_id, parent_id, body, comment_date, grooviness,deleted)
VALUES (12, 10, 10, null, 'Comment 3', CURRENT_TIMESTAMP, 0,false);

INSERT INTO token (value, user_id, type) values ('vaaa', 10, 'Validation');
INSERT INTO token (value, user_id, type) values ('raaa', 10, 'ResetPass');

INSERT INTO media (id,bytes) values (10, HEXTORAW('e04fd020ea3a6910a2d808002b30309d'));

INSERT INTO ratings (user_id, community_id, rating) VALUES (10, 10, 4.5);