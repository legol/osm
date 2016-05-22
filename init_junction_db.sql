DROP TABLE IF EXISTS "nd_junction";
DROP INDEX IF EXISTS "nd_junction_nd_ref";


CREATE TABLE "nd_junction"(
	"nd_ref" bigint NOT NULL,
	"wgs84long_lat" geometry NOT NULL,
	FOREIGN KEY ("nd_ref") REFERENCES "node"("id") ON DELETE CASCADE
);
CREATE INDEX "nd_junction_nd_ref" ON "nd_junction"("nd_ref");



--- populate this table with
insert into nd_junction 
	(select nd_ref from (select nd_ref, count(distinct way_ref) as way_ref_count from way_nd group by nd_ref) as nd_junction_count where nd_junction_count.way_ref_count > 1)

-- get lon,lat of each junction node
select nd_ref, ST_X(node.wgs84long_lat) as lon, ST_Y(node.wgs84long_lat) as lat from nd_junction left join node on node.id=nd_junction.nd_ref;

