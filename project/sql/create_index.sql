CREATE INDEX Patient_index on Patient USING BTREE(patient_ID);
CREATE INDEX Hospital_index on Hospital USING BTREE(hospital_ID);
CREATE INDEX Department_index on Department USING BTREE(dept_ID);
CREATE INDEX Staff_index on Staff USING BTREE(staff_ID);
CREATE INDEX Doctor_index on Doctor USING BTREE(doctor_ID);
CREATE INDEX Appointment_index on Appointment USING BTREE(appnt_ID);
