SELECT suppliers.sname AS Suppliers_Name, COUNT(*) AS Parts_Count
FROM catalog, suppliers
WHERE suppliers.sid=catalog.sid
GROUP BY suppliers.sid;

SELECT A.sname AS Suppliers_Name, A.Parts_Count AS Parts_Count_3MORE
FROM (SELECT suppliers.sname, COUNT(*) AS parts_count FROM catalog, suppliers WHERE suppliers.sid=catalog.sid GROUP BY suppliers.sid) AS A
WHERE A.parts_count>=3;

SELECT suppliers.sname AS Suppliers_Name, COUNT(*) AS Parts_Count
FROM catalog, suppliers, parts
WHERE suppliers.sid=catalog.sid AND catalog.pid=parts.pid AND parts.color='Green'
GROUP BY suppliers.sid
EXCEPT
SELECT suppliers.sname AS Suppliers_Name, COUNT(*) AS Parts_Count
FROM catalog, suppliers, parts
WHERE suppliers.sid=catalog.sid AND catalog.pid=parts.pid AND parts.color!='Green'
GROUP BY suppliers.sid;

SELECT suppliers.sname AS Supplier_Name, MAX(catalog.cost) AS MAX_PRICE
FROM catalog, suppliers
WHERE suppliers.sid=catalog.sid AND suppliers.sid IN (
	SELECT suppliers.sid
	FROM catalog, suppliers, parts
	WHERE suppliers.sid=catalog.sid AND catalog.pid=parts.pid AND parts.color='Green'
	INTERSECT
	SELECT suppliers.sid
	FROM catalog, suppliers, parts
	WHERE suppliers.sid=catalog.sid AND catalog.pid=parts.pid AND parts.color='Red'
	)
GROUP BY suppliers.sname
