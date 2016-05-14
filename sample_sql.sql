-- select all node that belong to ways of a relation
select distinct way_nd.nd_ref as nd_ref from way_nd where way_nd.way_ref in (select relation_member.ref as way_ref from relation_member where relation_member.type='way' and relation_member.relation_ref=27700)

-- select all nodes that are referenced by a relation
select relation_member.ref as nd_ref from relation_member where relation_member.type='node' and relation_member.relation_ref=27700

-- select longtitude and latitude of all the nodes that belong to ways of a relation
select ST_X(node.wgs84long_lat) as lon, ST_Y(node.wgs84long_lat) as lat from node 
		right join 
				(select distinct way_nd.nd_ref as nd_ref from way_nd where way_nd.way_ref in 
					(select relation_member.ref as way_ref from relation_member where relation_member.type='way' and relation_member.relation_ref=27700)) as nodes 
		on nodes.nd_ref=node.id

-- select longtitude and latitude of all nodes that are referenced by a relation
select ST_X(node.wgs84long_lat) as lon, ST_Y(node.wgs84long_lat) as lat from node 
	right join 
		(select relation_member.ref as nd_ref from relation_member where relation_member.type='node' and relation_member.relation_ref=27700) as nodes
	on nodes.nd_ref=node.id
	

-- select all relation, aka relation_ref2, that are referenced by this relation
select relation_member.ref as relation_ref2 from relation_member where relation_member.type='relation' and relation_member.relation_ref=1350622;

latitude is y
longitude is x

-- select all non top level relations
select distinct ref as relation_ref2 from relation_member where type='relation' 

-- select all top level relations
select relation.id as relation_ref from relation where relation.id not in (select distinct relation_member.ref as relation_ref2 from relation_member where relation_member.type='relation' )

-- insert into top level relation table
insert into top_level_relation(relation_ref) select relation.id as relation_ref from relation where relation.id not in (select distinct relation_member.ref as relation_ref2 from relation_member where relation_member.type='relation' )
 

-- select all relation that intersects with boundingBox
select * from relation_bounding_box where not(boundingBox.maxlat < minlat or boundingBox.maxlon < minlon or maxlat < boundingBox.minlat maxlon < boundingBox.minlon)

-- select all top level relations whose bounding box intersects with boundingBox
select relation_ref from relation_bounding_box where not(boundingBox.maxlat < minlat or boundingBox.maxlon < minlon or maxlat < boundingBox.minlat or maxlon < boundingBox.minlon) and
 relation_ref in (select relation_ref from top_level_relation)

select relation_ref from relation_bounding_box where not(40.112 < minlat or 116.344 < minlon or maxlat < 40.086 or maxlon < 116.268) and  relation_ref in (select relation_ref from top_level_relation)

-- select all ways whose bounding box intersects with boundingBox
select way_ref from way_bounding_box where not(boundingBox.maxlat < minlat or boundingBox.maxlon < minlon or maxlat < boundingBox.minlat or maxlon < boundingBox.minlon)

