drop user if exists everkeep;

create user everkeep with password 'everkeep';
create schema everkeep authorization everkeep;

grant usage on schema everkeep to everkeep;

alter default privileges for user everkeep in schema everkeep grant select,insert,update,delete,truncate on tables to everkeep;
alter default privileges for user everkeep in schema everkeep grant usage on sequences to everkeep;
alter default privileges for user everkeep in schema everkeep grant execute on functions to everkeep;