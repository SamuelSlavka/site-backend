
    alter table if exists article
       alter column deleted_at set data type timestamp(6);

    alter table if exists article
       alter column created_at set data type timestamp(6);

    alter table if exists category
       alter column deleted_at set data type timestamp(6);

    create table device (
        id varchar(255) not null,
        name varchar(255),
        primary key (id)
    );

    alter table if exists measurement
       alter column humidity set data type float(53);

    alter table if exists measurement
       alter column measured_at set data type timestamp(6);

    alter table if exists measurement
       alter column temperature set data type float(53);

    alter table if exists measurement
       add column device_id varchar(255);

    alter table if exists revision
       alter column deleted_at set data type timestamp(6);

    alter table if exists revision
       alter column created_at set data type timestamp(6);

    alter table if exists revision
       alter column text set data type TEXT;

    alter table if exists section
       alter column deleted_at set data type timestamp(6);

    alter table if exists section
       alter column created_at set data type timestamp(6);

    alter table if exists measurement
       add constraint FKfk341lu27m89eohc71wnwf8bt
       foreign key (device_id)
       references device;
