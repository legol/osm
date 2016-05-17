DROP INDEX IF EXISTS "relation_member_relation_ref5";
DROP INDEX IF EXISTS "relation_tag_relation_ref5";
DROP INDEX IF EXISTS "relation_member_type5";
DROP INDEX IF EXISTS "relation_member_type_ref5";
DROP TABLE IF EXISTS "relation_tag5";
DROP TABLE IF EXISTS "relation_member5";
DROP TABLE IF EXISTS "relation5";


DROP INDEX IF EXISTS "way_tag_way_ref5";
DROP INDEX IF EXISTS "way_nd_way_ref5";
DROP INDEX IF EXISTS "way_nd_nd_ref5";
DROP TABLE IF EXISTS "way_tag5";
DROP TABLE IF EXISTS "way_nd5";
DROP TABLE IF EXISTS "way5";

DROP INDEX IF EXISTS "node_tag_nd_ref5";
DROP TABLE IF EXISTS "node_tag5";
DROP TABLE IF EXISTS "node5";

DROP TABLE IF EXISTS "relation_bounding_box5";
DROP TABLE IF EXISTS "way_bounding_box5";
DROP TABLE IF EXISTS "relation_top_level5";


CREATE TABLE "node5"(
	"id" bigint NOT NULL,
	"visible" boolean,
	"version" bigint,
	"changeset" bigint,
	"timestamp" timestamp with time zone,
	"user" varchar(100),
	"uid" bigint,
	"wgs84long_lat" geometry NOT NULL,	
	PRIMARY KEY("id")
);

CREATE TABLE "node_tag5"(
	"nd_ref" bigint NOT NULL,
	"k" varchar(256),
	"v" varchar(256),
	FOREIGN KEY ("nd_ref") REFERENCES "node5"("id") ON DELETE CASCADE
);

CREATE INDEX "node_tag_nd_ref5" ON "node_tag5"("nd_ref");



CREATE TABLE "way5"(
	"id" bigint NOT NULL,
	"visible" boolean,
	"version" bigint,
	"changeset" bigint,
	"timestamp" timestamp with time zone,
	"user" varchar(100),
	"uid" bigint,
	PRIMARY KEY("id")
);

CREATE TABLE "way_nd5"(
	"way_ref" bigint NOT NULL,
	"nd_ref" bigint NOT NULL,
	FOREIGN KEY ("way_ref") REFERENCES "way5"("id") ON DELETE CASCADE,
	FOREIGN KEY ("nd_ref") REFERENCES "node5"("id") ON DELETE CASCADE
);
CREATE INDEX "way_nd_way_ref5" ON "way_nd5"("way_ref");
CREATE INDEX "way_nd_nd_ref5" ON "way_nd5"("nd_ref");



CREATE TABLE "way_tag5"(
	"way_ref" bigint NOT NULL,
	"k" varchar(256),
	"v" varchar(256),
	FOREIGN KEY ("way_ref") REFERENCES "way5"("id") ON DELETE CASCADE
);
CREATE INDEX "way_tag_way_ref5" ON "way_tag5"("way_ref");






CREATE TABLE "relation5"(
	"id" bigint NOT NULL,
	"visible" boolean,
	"version" bigint,
	"changeset" bigint,
	"timestamp" timestamp with time zone,
	"user" varchar(100),
	"uid" bigint,
	PRIMARY KEY("id")
);

CREATE TABLE "relation_member5"(
	"relation_ref" bigint NOT NULL,
	"type" varchar(10) NOT NULL, 
	"ref" bigint NOT NULL,
	"role" varchar(256)
);
CREATE INDEX "relation_member_relation_ref5" ON "relation_member5"("relation_ref");
CREATE INDEX "relation_member_type5" ON "relation_member5"("type");
CREATE INDEX "relation_member_type_ref5" ON "relation_member5"("type", "ref");


CREATE TABLE "relation_tag5"(
	"relation_ref" bigint NOT NULL,
	"k" varchar(256),
	"v" varchar(256),
	FOREIGN KEY ("relation_ref") REFERENCES "relation5"("id") ON DELETE CASCADE
);
CREATE INDEX "relation_tag_relation_ref5" ON "relation_tag5"("relation_ref");


CREATE TABLE "way_bounding_box5"(
	"way_ref" bigint NOT NULL,
	"minlat" float(32),
	"minlon" float(32),
	"maxlat" float(32),
	"maxlon" float(32),
	PRIMARY KEY("way_ref"),
	FOREIGN KEY ("way_ref") REFERENCES "way5"("id") ON DELETE CASCADE
);


CREATE TABLE "relation_bounding_box5"(
	"relation_ref" bigint NOT NULL,
	"minlat" float(32),
	"minlon" float(32),
	"maxlat" float(32),
	"maxlon" float(32),
	PRIMARY KEY("relation_ref"),
	FOREIGN KEY ("relation_ref") REFERENCES "relation5"("id") ON DELETE CASCADE
);


CREATE TABLE "relation_bounding_box5"(
	"relation_ref" bigint NOT NULL,
	"minlat" float(32),
	"minlon" float(32),
	"maxlat" float(32),
	"maxlon" float(32),
	PRIMARY KEY("relation_ref"),
	FOREIGN KEY ("relation_ref") REFERENCES "relation5"("id") ON DELETE CASCADE
);


CREATE TABLE "top_level_relation5"(
	"relation_ref" bigint NOT NULL,
	PRIMARY KEY("relation_ref"),
	FOREIGN KEY ("relation_ref") REFERENCES "relation5"("id") ON DELETE CASCADE
);



