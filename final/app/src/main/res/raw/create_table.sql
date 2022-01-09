create table Profiles(
	ID int primary key,
	backgroundImg int not null,
	avatar int not null,
	name varchar(50) not null,
	gmail varchar (50) not null,
	password varchar(50) not null
)

create table Posts(
	ID int primary key,
	ownerId int references Profiles (ID),
	status date,
	text varchar(200),
	image int
)

create table Notifications(
	ID int primary key,
	postId int references Posts (ID),
	senderId int references Profiles (ID),
	type char not null check( type in ('L', 'C', 'S')),
	status date,
	comment varchar(200)
)

create table ReceivedNotifications(
	receiverId int references Profiles (ID),
	notificationId int references Notifications (ID)
)

create table Exercises(
    ID int primary key,
    name varchar(50) not null,
    url varchar(200) not null,
    instruction varchar(500),
    time int not null
)





