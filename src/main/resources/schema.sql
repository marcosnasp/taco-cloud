create table if not exists Taco_Order (
  id bigint primary key,
  delivery_Name varchar(50) not null,
  delivery_Street varchar(50) not null,
  delivery_City varchar(50) not null,
  delivery_State varchar(2) not null,
  delivery_Zip varchar(10) not null,
  cc_number varchar(16) not null,
  cc_expiration varchar(5) not null,
  cc_cvv varchar(3) not null,
  placed_at timestamp
);

create table if not exists Taco (
  id bigint primary key,
  name varchar(50) not null,
  taco_order bigint not null,
  taco_order_key bigint not null,
  created_at timestamp not null,
  constraint taco_order_fk foreign key (taco_order) references Taco_Order(id)
);

create table if not exists Ingredient (
  id varchar(4) primary key,
  name varchar(25) not null,
  type varchar(10) not null
);

create table if not exists Ingredient_Ref (
  ingredient varchar(4) primary key,
  taco bigint not null,
  taco_key bigint not null,
  constraint ingredient_fk foreign key (ingredient) references Ingredient(id)
);