create table userinfo (
    email varchar(255) not null,
    name varchar(255),
    primary key (email)
);

create table project (
    key varchar(255) not null,
    active boolean not null,
    name varchar(255) not null,
    primary key (key)
);

create table project_estimations (
    project_key varchar(255) not null,
    estimations integer not null
);
alter table project_estimations add constraint FK81ch991kndi2hegjkiddgle2u foreign key (project_key) references project;

create table project_history_entry (
    id bigint generated by default as identity,
    new_value varchar(255),
    old_value varchar(255),
    timestamp timestamp(6) with time zone not null,
    type varchar(255) check (type in ('CREATED','USER_ADDED','USER_REMOVED','USER_ROLE_CHANGED','NAME_CHANGED','ACTIVATE_CHANGED')),
    affected_user_email varchar(255),
    project_key varchar(255) not null,
    user_email varchar(255) not null,
    primary key (id)
);
alter table project_history_entry add constraint FKj5m0r1gb6f0iqym6wmr5ra7uo foreign key (affected_user_email) references userinfo;
alter table project_history_entry add constraint FKrh42pebviaphx6sv1bkmabjv6 foreign key (project_key) references project;
alter table project_history_entry add constraint FKgaj3gyxh113ml6ewi594lcskq foreign key (user_email) references userinfo;

create table project_role (
    id bigint generated by default as identity,
    type varchar(255) check (type in ('ADMIN','DEVELOPER','STAKEHOLDER')),
    project_key varchar(255) not null,
    user_email varchar(255) not null,
    primary key (id)
);
alter table project_role add constraint FKm4igfl9fhd53acnw8gy0j9ofx foreign key (project_key) references project;
alter table project_role add constraint FKqdqyed36764ks4ri6w41klqgw foreign key (user_email) references userinfo;

create table next_id (
    name varchar(255) not null,
    next_id integer not null,
    primary key (name)
);

create table task (
    key varchar(255) not null,
    project_key varchar(255) not null,
    deleted boolean not null,
    description text not null,
    estimation integer,
    status varchar(255) check (status in ('FUNNEL','READY','BACKLOG','TODO','PROGRESS','REVIEW','DONE','CANCELLED')),
    title varchar(255) not null,
    assignee_email varchar(255),
    creator_email varchar(255) not null,
    primary key (key)
);
alter table task add constraint FK72pb3f3200ut883ymblc0vb23 foreign key (assignee_email) references userinfo;
alter table task add constraint FKeo02rvf33wjuy2jwror72rn46 foreign key (creator_email) references userinfo;
alter table task add constraint FKj5m0r1gb6f0iqym6wmr517342 foreign key (project_key) references project;

create table task_history_entry (
    id bigint generated by default as identity,
    field varchar(255),
    new_value varchar(255),
    old_value varchar(255),
    timestamp timestamp(6) with time zone not null,
    type varchar(255) check (type in ('CREATED','COMMENTED','EDITED','DELETED')),
    task_key varchar(255) not null,
    user_email varchar(255) not null,
    primary key (id)
);
alter table task_history_entry add constraint FKdqrjtquw8p3f8agrps5dk7c0w foreign key (task_key) references task;
alter table task_history_entry add constraint FKk9u2donv941a25yf8fepgkein foreign key (user_email) references userinfo;

create table comment (
    id bigint generated by default as identity,
    text text not null,
    timestamp timestamp(6) with time zone not null,
    task_key varchar(255) not null,
    user_email varchar(255) not null,
    primary key (id)
);
alter table comment add constraint FKtddtns6av4ubcsn0cormgk0y5 foreign key (task_key) references task;
alter table comment add constraint FKi5q8w18ae2jmcp4hsn7l0ocb2 foreign key (user_email) references userinfo;
