drop table if exists dw_users;
create table dw_users (
	id integer primary key autoincrement,
	g_id integer primary key ,
	user_name text not null,
	occupation text,
	org_type text,
	org_name text,
	org_title text,
	org_dept text,
	gender text not null,
	email text not null,
	image_url text
);

drop table if exists dw_providers;
create table dw_providers (
	id integer primary key autoincrement,
	g_id integer primary key ,
	route
);

