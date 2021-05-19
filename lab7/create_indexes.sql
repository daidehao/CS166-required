DROP INDEX part_nyc_hand_index;
DROP INDEX part_nyc_supplier_index;
DROP INDEX part_sfo_hand_index;
DROP INDEX part_sfo_supplier_index;
DROP INDEX supplier_id_index;
DROP INDEX color_name_index;

CREATE INDEX part_nyc_hand_index on part_nyc USING BTREE(on_hand);
CREATE INDEX part_nyc_supplier_index on part_nyc USING BTREE(supplier);
CREATE INDEX part_sfo_hand_index on part_sfo USING BTREE(on_hand);
CREATE INDEX part_sfo_supplier_index on part_sfo USING BTREE(supplier);
CREATE INDEX supplier_id_index on supplier USING BTREE(supplier_id);
CREATE INDEX color_name_index on color USING BTREE(color_name);

