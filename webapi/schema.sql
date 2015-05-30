drop table if exists dw_users;
create table dw_users (
	id integer primary key autoincrement,
	g_id integer primary key ,
	displayName text not null,
	gender text
	image_url text
	aboutMe
	occupation text,
	org_type text,
	org_name text,
	org_title text,
	org_dept text,
	email text not null,
	
);

drop table if exists dw_providers;
create table dw_providers (
	id integer primary key autoincrement,
	g_id integer primary key ,
	route
);

