drop table if exists "user";
drop table if exists "accounts";

create table "user"(
	id INTEGER PRIMARY KEY,
	username text, 
	password text
);

CREATE TABLE "accounts"(
	id INTEGER PRIMARY KEY,
	user_id int NOT NULL,
	balance double(18, 2),
	FOREIGN KEY (user_id) REFERENCES user(id)
);

insert into "user" values ('admin', 1234);