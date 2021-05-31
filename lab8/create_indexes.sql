CREATE INDEX part_nyc_hand_index on part_nyc USING BTREE(on_hand);
CREATE INDEX part_nyc_supplier_index on part_nyc USING BTREE(supplier);
