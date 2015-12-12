-- Create Users table
CREATE TABLE sdev_users (
  user_id INTEGER PRIMARY KEY,
  email  VARCHAR(75) NOT NULL UNIQUE,
  firstname VARCHAR(50) NOT NULL,
  lastname VARCHAR(75) NOT NULL,
  city VARCHAR(75),
  State CHAR(2),
  zip VARCHAR(10) 
);

-- Roles table
CREATE TABLE roles (
  role_id INTEGER PRIMARY KEY,
  role varchar(20) NOT NULL UNIQUE
)

-- user-info
CREATE TABLE user_info (
  user_id INTEGER Primary Key, 
  password  VARCHAR(40)  NOT NULL,
  CONSTRAINT fk_wu2 Foreign Key (user_id) 
  references  sdev_users(user_id) on delete cascade   
);


-- User roles
CREATE TABLE user_roles (
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  Constraint PKUR primary key (user_id, role_id),
  Constraint fk_ur1 Foreign Key (user_id) references  
   sdev_users(user_id) on delete cascade,   
  Constraint fk_ur2 Foreign Key (role_id) references  
   roles(role_id) on delete cascade    
);

-- Account data
CREATE TABLE CustomerAccount (
  account_id INTEGER Primary Key,
  user_id INTEGER NOT NULL references sdev_users (user_id),
  Cardholdername VARCHAR(75) NOT NULL,
  CardType VARCHAR(20) NOT NULL,
  CardNumber VARCHAR(30) NOT NULL,
  CAV_CCV2 INTEGER NOT NULL,  
  expiredate date NOT NULL
);

-- Insert records
insert into sdev_users (user_id, email, firstname, lastname,
city, state, zip)
values (1,'james.robertson@umuc.edu','Jim', 'Robertson','Adelphi',
'MD','20706');

--Insert user_info
insert into user_info (user_id, password) 
values (1,'mypassword');


-- Insert roles
insert into roles (role_id, role)
values (1,'Customer');

insert into roles (role_id, role)
values (2,'Admin');


-- Inseer user_roles
insert into user_roles (user_id, role_id)
values (1,1);

insert into user_roles (user_id, role_id)
values (1,2);

insert into user_roles (user_id, role_id)
values (2,2);

insert into user_roles (user_id, role_id)
values (3,1);


-- Insert CustomerAccount
insert into CustomerAccount (account_id, user_id,
CardType, CardNumber, CAV_CCV2, Cardholdername, expiredate) values (1,1,'MasterCard','1111111111111',321,'James Robertson','02/23/2016');
