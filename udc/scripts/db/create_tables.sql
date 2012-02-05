drop table if exists usagedata_profile;
create table usagedata_profile (
	id int unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, 
	userId char(40) NOT NULL,
	workspaceId char(40) NOT NULL
) Engine=MyISAM;

drop table if exists usagedata_upload;
create table usagedata_upload (
	id int unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
	profileId int unsigned NOT NULL,
	time timestamp DEFAULT CURRENT_TIMESTAMP
) Engine=MyISAM;
CREATE INDEX IDX_profileId ON usagedata_upload(profileId);

drop table if exists usagedata_record;
create table usagedata_record (
	id int unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
	uploadId int unsigned NOT NULL,
	monitorId varchar(256),
	attribute1 varchar(256),
	attribute2 varchar(256),
	attribute3 varchar(256),
	attribute4 varchar(256),
	time bigint unsigned NOT NULL
)  Engine=MyISAM;
CREATE INDEX IDX_uploadId ON usagedata_record(uploadId);
CREATE INDEX IDX_monitorId ON usagedata_record(monitorId);
