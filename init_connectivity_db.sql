DROP TABLE IF EXISTS "connectivity";
DROP INDEX IF EXISTS "connectivity_nd_ref1";
DROP INDEX IF EXISTS "connectivity_nd_ref2";


CREATE TABLE "connectivity"(
	"nd_ref1" bigint NOT NULL,
	"nd_ref2" bigint NOT NULL,
	"way_ref" bigint NOT NULL, -- because of which way, these 2 nodes are connected.
	"nd1_wgs84long_lat" geometry NOT NULL, -- redundant. just for speeding things up
	"nd2_wgs84long_lat" geometry NOT NULL, -- redundant. just for speeding things up
	FOREIGN KEY ("nd_ref1") REFERENCES "node"("id") ON DELETE CASCADE,
	FOREIGN KEY ("nd_ref2") REFERENCES "node"("id") ON DELETE CASCADE,
	FOREIGN KEY ("way_ref") REFERENCES "way"("id") ON DELETE CASCADE
);
CREATE INDEX "connectivity_nd_ref1" ON "connectivity"("nd_ref1");
CREATE INDEX "connectivity_nd_ref2" ON "connectivity"("nd_ref2");


run osmParser with command line: calc_connectivity

