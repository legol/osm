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