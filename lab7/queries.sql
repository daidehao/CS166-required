SELECT COUNT(on_hand)
From part_nyc
WHERE on_hand>70;

SELECT (A.Parts_Count+B.Parts_Count) AS total_count
FROM 
(SELECT COUNT(on_hand) AS Parts_Count FROM part_nyc, color WHERE part_nyc.color=color.color_id AND color.color_name='Red' GROUP BY part_nyc.color) AS A,
(SELECT COUNT(on_hand) AS Parts_Count FROM part_sfo, color WHERE part_sfo.color=color.color_id AND color.color_name='Red' GROUP BY part_sfo.color) AS B;

SELECT supplier.supplier_name
FROM
(SELECT part_nyc.supplier AS id, COUNT(on_hand) AS nyc_pcount FROM part_nyc GROUP BY part_nyc.supplier) AS A,
(SELECT part_sfo.supplier AS id, COUNT(on_hand) AS sfo_pcount FROM part_sfo GROUP BY part_sfo.supplier) AS B,
supplier
WHERE A.id=B.id AND B.id=supplier.supplier_id AND A.nyc_pcount>sfo_pcount;

SELECT DISTINCT supplier.supplier_name
FROM part_nyc, supplier
WHERE part_nyc.supplier=supplier.supplier_id
EXCEPT
SELECT DISTINCT supplier.supplier_name
FROM part_sfo, supplier
WHERE part_sfo.supplier=supplier.supplier_id;

UPDATE part_nyc SET on_hand=(on_hand-10);

DELETE FROM part_nyc
WHERE part_nyc.on_hand<30;


