start transaction;
delete from access_control as AC where AC.allowed=false;
end transaction;