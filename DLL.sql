create table conversation_members
(
	RelationID int auto_increment
		primary key,
	ConversationID int null,
	UserID int not null,
	constraint conversation_members_ID_uindex
		unique (RelationID),
	constraint conversation_members_conversations_ConversationID_fk
		foreign key (ConversationID) references csci201.conversations (ConversationID)
)
;

create index conversation_members_conversations_ID_fk
	on conversation_members (UserID)
;

create index conversation_members_conversations_ConversationID_fk
	on conversation_members (ConversationID)
;

create table conversations
(
	ConversationID int auto_increment
		primary key,
	Active tinyint(1) default '1' not null,
	Topic varchar(255) null,
	constraint conversations_ID_uindex
		unique (ConversationID)
)
;

create table files
(
	FileID int auto_increment
		primary key,
	ConversationID int not null,
	MessageID int not null,
	UserID int not null,
	FileContents mediumblob not null,
	constraint files_MessageID_uindex
		unique (MessageID),
	constraint files_conversations_ID_fk
		foreign key (ConversationID) references csci201.conversations (ConversationID),
	constraint files_messages_ID_fk
		foreign key (MessageID) references csci201.messages (MessageID),
	constraint files_users_ID_fk
		foreign key (UserID) references csci201.users (UserID)
)
;

create index files_conversations_ID_fk
	on files (ConversationID)
;

create index files_users_ID_fk
	on files (UserID)
;

create table messages
(
	MessageID int auto_increment
		primary key,
	ConversationID int not null,
	UserID int null,
	Message text null,
	File tinyint(1) default '0' not null,
	constraint messages_ID_uindex
		unique (MessageID),
	constraint messages_conversations_ID_fk
		foreign key (ConversationID) references csci201.conversations (ConversationID),
	constraint messages_users_ID_fk
		foreign key (UserID) references csci201.users (UserID)
)
;

create index messages_conversations_ID_fk
	on messages (ConversationID)
;

create index messages_users_ID_fk
	on messages (UserID)
;

create table users
(
	UserID int auto_increment
		primary key,
	FirstName varchar(255) null,
	LastName varchar(255) null,
	Email varchar(255) null,
	Password varchar(255) null,
	PhoneNumber varchar(255) null,
	Biography text null,
	Interests text null,
	Username varchar(255) null,
	Guest tinyint(1) default '0' not null
)
;
