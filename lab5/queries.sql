SELECT pid 
FROM catalog 
WHERE cost<10;

SELECT parts.pname
FROM parts, catalog
WHERE catalog.cost<10 AND catalog.pid=parts.pid;

SELECT suppliers.address
FROM suppliers, parts, catalog
WHERE parts.pname like '%Fire Hydrant Cap%'AND suppliers.sid=catalog.sid AND catalog.pid=parts.pid;

SELECT suppliers.sname
FROM suppliers, parts, catalog
WHERE (parts.color='green' OR parts.color='Green') AND suppliers.sid=catalog.sid AND catalog.pid=parts.pid;

SELECT suppliers.sname, parts.pname
FROM suppliers, parts, catalog
WHERE suppliers.sid=catalog.sid AND catalog.pid=parts.pid;

