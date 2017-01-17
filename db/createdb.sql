

CREATE DATABASE IF NOT EXISTS fish DEFAULT CHARSET utf8 COLLATE utf8_general_ci ;
create user 'fish'@'localhost' identified by 'aabb123';
grant all on fish.* to 'fish'@'localhost'; 
flush privileges;

use fish;

