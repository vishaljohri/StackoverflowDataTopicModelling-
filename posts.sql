create database stackoverflow DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

use stackoverflow;

CREATE TABLE posts (
    Id INT NOT NULL PRIMARY KEY,
    PostTypeId SMALLINT,
    AcceptedAnswerId INT,
    ParentId INT,
    Score INT NULL,
    ViewCount INT NULL,
    Body text NULL,
    OwnerUserId INT NOT NULL,
    LastEditorUserId INT,
	LastEditorDisplayName VARCHAR(256),
    LastEditDate DATETIME,
    LastActivityDate DATETIME,
    Title varchar(256) NOT NULL,
    Tags VARCHAR(256),
    AnswerCount INT NOT NULL DEFAULT 0,
    CommentCount INT NOT NULL DEFAULT 0,
    FavoriteCount INT NOT NULL DEFAULT 0,
    CreationDate DATETIME,
	CommunityOwnedDate DATETIME,
	ClosedDate DATETIME
);

load xml local infile 'G:\\StackoverflowData\\stackoverflow.com-Posts\\Posts.xml'
into table stackoverflow.posts
rows identified by '<row>';


create index posts_idx_1 on posts(AcceptedAnswerId);
create index posts_idx_2 on posts(ParentId);
create index posts_idx_3 on posts(OwnerUserId);
create index posts_idx_4 on posts(LastEditorUserId);

