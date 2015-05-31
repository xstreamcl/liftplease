drop table if exists lp_user;
drop table if exists lp_provider;
drop table if exists lp_subscriber;
drop table if exists lp_match;
drop table if exists lp_route;
drop table if exists lp_geo;


create table lp_user (
    lpuid integer primary key autoincrement,
    user_name text not null,
    gtoken blob not null
);


create table lp_provider (
    lpuid integer primary key,
    departtime datetime not null,
    routeid integer not null
);


create table lp_subscriber (
    lpuid integer primary key,
    departtime datetime not null,
    sgeoid integer not null,
    dgeoid integer not null
);


create table lp_match (
    matchid integer primary key,
    plpid integer not null,
    slpid integer not null,
    dropoffset real not null
);


create table lp_route (
    routeid integer primary key,
    encroute blob not null
);


create table lp_geo (
    geoid integer primary key,
    lat real not null,
    lng real not null
);

-- tail
