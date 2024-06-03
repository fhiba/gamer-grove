CREATE TABLE IF NOT EXISTS users(
                      id SERIAL PRIMARY KEY,
                      username VARCHAR(50) UNIQUE NOT NULL,
                      email VARCHAR(50) UNIQUE NOT NULL,
                      password TEXT NOT NULL,
                      owner boolean NOT null default false,
                      portrait_id INT DEFAULT NULL,
    CONSTRAINT fk_users_portrait_id FOREIGN KEY (portrait_id) REFERENCES media(id)
);

CREATE TABLE IF NOT EXISTS media(
                      id SERIAL PRIMARY KEY,
                      bytes bytea NOT NULL
);

CREATE TABLE IF NOT EXISTS community(
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(50) UNIQUE NOT NULL,
                          description TEXT NOT NULL,
                          portrait_id INT,
                          FOREIGN KEY (portrait_id) REFERENCES media(id)
);

CREATE TABLE IF NOT EXISTS post(
                     id SERIAL PRIMARY KEY,
                     title VARCHAR(50) NOT NULL,
                     body TEXT,
                     author_id INT NOT NULL,
                     community_name TEXT NOT NULL,
                     media boolean NOT NULL,
                     media_id INT,
                     post_date TIMESTAMP NOT NULL,
                     grooviness INT NOT NULL DEFAULT 0,
                     FOREIGN KEY (author_id) REFERENCES users(id),
                     FOREIGN KEY (community_name) REFERENCES community(name),
                     FOREIGN KEY (media_id) REFERENCES media(id)
);

CREATE TABLE IF NOT EXISTS comment(
                        id SERIAL PRIMARY KEY,
                        body VARCHAR(500) NOT NULL,
                        author_id INT NOT NULL,
                        post_id INT NOT NULL,
                        parent_id INT,
                        comment_date TIMESTAMP NOT NULL,
                        grooviness INT NOT NULL DEFAULT 0,
                        FOREIGN KEY (parent_id) REFERENCES comment(id),
                        FOREIGN KEY (post_id) REFERENCES post(id),
                        FOREIGN KEY (author_id) REFERENCES users(id)

);

CREATE TABLE IF NOT EXISTS community_user(
                               community_id INT NOT NULL,
                               user_id INT NOT NULL,
                               community_role INT NOT NULL,
                               community_name TEXT NOT NULL,
                               PRIMARY KEY (community_id, user_id),
                               foreign key(community_id) references community(id),
                               foreign key(user_id) references users(id)
);

CREATE TABLE IF NOT EXISTS post_categories(
                                              category TEXT PRIMARY KEY
);
INSERT INTO post_categories (category) VALUES ('Miscellaneous');
INSERT INTO post_categories (category) VALUES('Help');
INSERT INTO post_categories (category) VALUES('Review');
INSERT INTO post_categories (category) VALUES('Recommendation');
INSERT INTO post_categories (category) VALUES('Guide');
INSERT INTO post_categories (category) VALUES('News');

ALTER TABLE post ADD COLUMN IF NOT EXISTS category TEXT DEFAULT 'Miscellaneous';
ALTER TABLE post ADD FOREIGN KEY (category) REFERENCES post_categories(category);

DROP TABLE IF EXISTS groovy_history;

create table if not exists groovy_post_history(
                                             user_id INT not null,
                                             post_id int not null,
                                             groovy_type boolean not null,
                                             primary key(user_id, post_id),
                                             foreign key(user_id) references users(id),
                                             foreign key(post_id) references post(id)
);

create table if not exists groovy_comment_history(
                                                  user_id INT not null,
                                                  post_id int not null,
                                                  comment_id int not null,
                                                  groovy_type boolean not null,
                                                  primary key(user_id, post_id,comment_id),
                                                  foreign key(user_id) references users(id),
                                                  foreign key(post_id) references post(id),
                                                  foreign key(comment_id) references comment(id)
);

create table if not exists communities_categories(
    community_id INT NOT NULL,
    category VARCHAR(50) NOT NULL,
    PRIMARY KEY (community_id, category),
    FOREIGN KEY (community_id) REFERENCES community(id)
);

UPDATE post_categories SET category = 'Miscellaneous' WHERE category = 'Discussion';

INSERT INTO post_categories (category) VALUES ('Discussion');

UPDATE post SET category = 'Discussion' WHERE category = 'Miscellaneous';

DELETE FROM post_categories WHERE category = 'Miscellaneous';


create table if not exists modders(
                                   user_id int not null,
                                   community_id int not null,
                                   primary key(user_id, community_id),
                                   foreign key(user_id) references users(id),
                                   foreign key(community_id) references community(id)
);

ALTER TABLE post ADD COLUMN IF NOT EXISTS deleted boolean DEFAULT false NOT NULL;
ALTER TABLE comment ADD COLUMN IF NOT EXISTS deleted boolean DEFAULT false NOT NULL;

ALTER TABLE community ADD COLUMN IF NOT EXISTS publisher text DEFAULT 'Not specified' NOT NULL;
ALTER TABLE community ADD COLUMN IF NOT EXISTS developer text DEFAULT 'Not specified' NOT NULL;
ALTER TABLE community ADD COLUMN IF NOT EXISTS release_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;

CREATE TABLE IF NOT EXISTS post_images (
                                           post_id INT NOT NULL ,
                                           image_id INT NOT NULL ,
                                           PRIMARY KEY (post_id, image_id),
                                           FOREIGN KEY (post_id) REFERENCES post(id),
                                           FOREIGN KEY (image_id) REFERENCES media(id)
);

--ALTER TABLE users ADD COLUMN IF NOT EXISTS portrait_id int DEFAULT null;

--ALTER TABLE users ADD CONSTRAINT  fk_users_portrait_id FOREIGN KEY  (portrait_id)  REFERENCES media(id);

CREATE TABLE IF NOT EXISTS token(
    value CHAR(50) NOT NULL,
    user_id INT NOT NULL,
    type CHAR(10) CHECK (type IN ('Validation', 'ResetPass')) NOT NULL,
    PRIMARY KEY (value),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

--- Sprint 3

ALTER TABLE users ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS locale VARCHAR(2) DEFAULT 'en' NOT NULL;


--- Sprint 5
ALTER TABLE modders ADD COLUMN since_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;

---- Sprint 5

ALTER TABLE community ADD COLUMN total_rating FLOAT NOT NULL DEFAULT 0.0;
ALTER TABLE community ADD COLUMN rating_count INT NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS ratings (
                                       user_id INT NOT NULL,
                                       community_id INT NOT NULL,
                                       rating FLOAT NOT NULL,
                                       PRIMARY KEY (user_id, community_id),
                                       FOREIGN KEY (user_id) REFERENCES users(id),
                                       FOREIGN KEY (community_id) REFERENCES community(id)
);
