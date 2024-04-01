
--Hay que cambiar los serials por sequences
CREATE TABLE IF NOT EXISTS media(
                                    id SERIAL PRIMARY KEY,
                                    bytes BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS users(
                      id SERIAL PRIMARY KEY,
                      username VARCHAR(50) UNIQUE NOT NULL,
                      email VARCHAR(50) UNIQUE NOT NULL,
                      password TEXT NOT NULL,
                      owner boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS community(
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(50) UNIQUE NOT NULL,
                          description TEXT NOT NULL,
                          portrait_id INT,
                          FOREIGN KEY (portrait_id) REFERENCES media(id)
);

CREATE TABLE IF NOT EXISTS post_categories(
                                              category TEXT PRIMARY KEY
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
                     category TEXT DEFAULT 'Miscellaneous',
                     FOREIGN KEY (author_id) REFERENCES users(id),
                     FOREIGN KEY (community_name) REFERENCES community(name),
                     FOREIGN KEY (media_id) REFERENCES media(id),
                     FOREIGN KEY (category) REFERENCES post_categories(category)
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
                               PRIMARY KEY (community_id, user_id)
);
