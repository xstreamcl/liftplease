drop table if exists lp_user;
drop table if exists lp_provider;
drop table if exists lp_subscriber;
drop table if exists lp_match;
drop table if exists lp_route;
drop table if exists lp_geo;

create table lp_user (\
    lp_uid integer primary key,\
    device_id text not null,\
    g_id text not null,\
    app_id text not null,\
    phone text ,\
    display_name text ,\
    gender text ,\
    email text ,\
    image_url blob,\
    about_me blob,\
    org_name text,\
    org_title text)
;

create table lp_provider (\
    lp_uid integer primary key,\
    trip_creation_time text not null,\
    routeid text not null,\
    encroute blob not null)
;

create table lp_subscriber (\
    lp_uid integer primary key,\
    trip_creation_time text not null,\
    routeid text not null,\
    encroute blob not null)
;

create table lp_match (\
    matchid integer primary key,\
    p_lp_uid integer not null,\
    s_lp_uid integer not null,\
    p_routeid text,\
    s_routeid text,\
    s_req_time text,\
    status integer not null)
;

create table lp_route (
    routeid integer primary key,
    lp_uid integer not null,
    start_lat real not null,
    start_lng real not null,
    end_lat real not null,
    end_lng real not null,
    encroute blob not null)
;

create table lp_geo (
    geoid integer primary key,
    lat real not null,
    lng real not null)
;

-- tail
