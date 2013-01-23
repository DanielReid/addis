CREATE TYPE activity_type AS ENUM ('SCREENING', 'RANDOMIZATION', 'WASH_OUT', 'FOLLOW_UP', 'TREATMENT', 'OTHER');
CREATE TYPE allocation_type AS ENUM ('UNKNOWN', 'RANDOMIZED', 'NONRANDOMIZED');
CREATE TYPE blinding_type AS ENUM ('OPEN', 'SINGLE_BLIND', 'DOUBLE_BLIND', 'TRIPLE_BLIND', 'UNKNOWN');
CREATE TYPE source AS ENUM ('MANUAL', 'CLINICALTRIALS');
CREATE TYPE status AS ENUM ('NOT_YET_RECRUITING', 'RECRUITING', 'ENROLLING', 'ACTIVE', 'COMPLETED', 'SUSPENDED', 'TERMINATED', 'WITHDRAWN', 'UNKNOWN');
CREATE TYPE direction as ENUM ('HIGHER_IS_BETTER', 'LOWER_IS_BETTER'); 
CREATE TYPE measurement_type as ENUM ('CONTINUOUS', 'RATE', 'CATEGORICAL'); 
CREATE TYPE variable_type as ENUM ('PopulationCharacteristic', 'Endpoint', 'AdverseEvent'); 
CREATE TYPE epoch_offset as ENUM ('FROM_EPOCH_START', 'BEFORE_EPOCH_END');

CREATE TABLE "projects" ( 
  "id" bigserial,
  "name" varchar, 
  "description" text,
  PRIMARY KEY ("id")
);

CREATE TABLE "project_variables" ( 
  "project_id" bigint,
  "variable_id" bigint,
  PRIMARY KEY ("project_id", "variable_id")
);

CREATE TABLE "variable_map" ( 
  "sub" bigint,
  "super" bigint,
  PRIMARY KEY ("sub", "super")
);

CREATE TABLE "treatment_dosings" (
  "treatment_id" bigint,
  "planned_time" interval,
  "min_dose" varchar,
  "max_dose" varchar,
  "scale_modifier" varchar,
  "unit" varchar,
  PRIMARY KEY ("treatment_id", "planned_time")
);
CREATE INDEX ON "treatment_dosings" ("treatment_id") WHERE "planned_time" IS NULL;

CREATE TABLE "treatments" (
  "id" bigserial,
  "study_id" bigint,
  "activity_name" varchar,
  "drug_name" varchar,
  "periodicity" interval DEFAULT 'P0D',
  PRIMARY KEY ("id"),
  UNIQUE("study_id", "activity_name", "drug_name")
);

CREATE TABLE "activities" (
  "study_id" bigint,
  "name" varchar,
  "type" activity_type,
  PRIMARY KEY ("study_id", "name") 
);

CREATE TABLE "designs" (
  "study_id" bigint,
  "arm_name" varchar,
  "epoch_name" varchar,
  "activity_name" varchar,
  PRIMARY KEY ("study_id", "arm_name", "epoch_name") 
);

CREATE TABLE "epochs" (
  "study_id" bigint,
  "name" varchar,
  "duration" interval DEFAULT 'P0D',
  "note_hook" bigint,
  PRIMARY KEY ("study_id", "name") 
);

CREATE TABLE "arms" (
  "study_id" bigint,
  "name" varchar,
  "arm_size" varchar,
  "note_hook" bigint,
  PRIMARY KEY ("study_id", "name") 
);
COMMENT ON COLUMN "arms"."name" IS 'Empty string indicates "total population"';

CREATE TABLE "drugs" (
  "name" varchar, 
  "code" varchar,
  "code_system" varchar,
  PRIMARY KEY ("name")
);
CREATE UNIQUE INDEX ON "drugs" ("code", "code_system");

CREATE TABLE "units" ( 
  "name" varchar,
  "symbol" varchar, 
  "ucum" varchar, 
  PRIMARY key ("name")
);
CREATE INDEX ON "units" ("ucum");

CREATE TABLE "studies" (
  "id" bigserial,
  "name" varchar,
  "title" text,
  "indication" varchar,
  "objective" text,
  "allocation_type" allocation_type,
  "blinding_type" blinding_type,
  "number_of_centers" int2,
  "created_at" date,
  "source" source,
  "exclusion" text,
  "inclusion" text,
  "status" status,
  "start_date" date,
  "end_date" date,
  "note_hook" bigint,
  "blinding_type_note_hook" bigint,
  "title_note_hook" bigint,
  "allocation_type_note_hook" bigint,
  PRIMARY KEY ("id")
);
CREATE INDEX ON "studies" ("name");
CREATE INDEX ON "studies" ("indication");

CREATE TABLE "study_references" (
  "study_id" bigint,
  "id" varchar, 
  "repostitory" text DEFAULT 'PubMed',
  PRIMARY KEY ("study_id", "id")
);

CREATE TABLE "variables" (
  "id" bigserial,
  "name" varchar,
  "description" text,
  "direction" direction,
  "measurement_type" measurement_type,
  "unit" varchar,
  "code" varchar,
  "code_system" varchar,
  PRIMARY KEY ("id")
);
CREATE INDEX variable_id_idx ON "variables" ("id");
CREATE UNIQUE INDEX variables_code_idx ON "variables" ("name", "code", "code_system");

CREATE TABLE "study_variables" ( 
  "study_id" bigint,
  "variable_id" bigint,
  "is_primary" bool,
  "variable_type" variable_type,
  "note_hook" bigint,
  PRIMARY KEY ("variable_id"),
  UNIQUE ("study_id", "variable_id")
);
CREATE INDEX study_variables_idx ON ("study_id");

CREATE TABLE "variable_categories" (
  "variable_id" bigint,
  "category_name" varchar,
  PRIMARY KEY ("variable_id", "category_name") 
);

CREATE TABLE "measurements" (
  "study_id" bigint,
  "variable_id" bigint,
  "measurement_moment_name" varchar,
  "arm_name" varchar,
  "attribute" varchar, 
  "integer_value" bigint,
  "real_value" float, 
  PRIMARY KEY ("variable_id", "measurement_moment_name", "arm_name", "attribute") 
);
COMMENT ON COLUMN "measurements"."variable_id" IS 'Uniquely identifies the study';

CREATE TABLE "measurement_moments" ( 
  "study_id" bigint,
  "name" varchar,
  "epoch_name" varchar,
  "is_primary" bool,
  "offset_from_epoch" interval,
  "before_epoch" epoch_offset,
  "note_hook" bigint,
  PRIMARY KEY ("study_id", "name"),
  UNIQUE ("study_id", "epoch_name", "offset_from_epoch", "before_epoch")
);

CREATE TABLE "indications" (
  "name" varchar,
  "code" varchar,
  "code_system" varchar,
  PRIMARY KEY ("name") 
);
CREATE UNIQUE INDEX indications_code_idx ON "indications" ("code", "code_system");

CREATE TABLE "code_systems" (
  "code_system" varchar,
  "code_system_name" varchar,
  PRIMARY KEY ("code_system") 
);

CREATE TABLE "note_hooks" (
  "id" bigserial,
  PRIMARY KEY ("id") 
);

CREATE TABLE "notes" (
  "id" bigserial,
  "note_hook_id" bigint,
  "text" text,
  "source" source,
  PRIMARY KEY ("id", "note_hook_id") 
);

ALTER TABLE "project_variables" ADD CONSTRAINT "projects_variables__project_fkey" FOREIGN KEY ("project_id") REFERENCES "projects" ("id");
ALTER TABLE "project_variables" ADD CONSTRAINT "projects_variables_variable_fkey" FOREIGN KEY ("variable_id") REFERENCES "variables" ("id");
ALTER TABLE "variable_map" ADD CONSTRAINT "variable_subtype_map_fkey" FOREIGN KEY ("sub") REFERENCES "variables" ("id"); 
ALTER TABLE "variable_map" ADD CONSTRAINT "variable_supertype_map_fkey" FOREIGN KEY ("super") REFERENCES "variables" ("id"); 
ALTER TABLE "variables" ADD CONSTRAINT "variable_unit_fkey" FOREIGN KEY ("unit") REFERENCES "units" ("name");
ALTER TABLE "variables" ADD CONSTRAINT "variable_code_system_fkey" FOREIGN KEY ("code_system") REFERENCES "code_systems" ("code_system");
ALTER TABLE "variable_categories" ADD CONSTRAINT "variable_category_fkey" FOREIGN KEY ("variable_id") REFERENCES "variables" ("id");
ALTER TABLE "study_variables" ADD CONSTRAINT "study_variable_fkey" FOREIGN KEY ("variable_id") REFERENCES "variables" ("id");
ALTER TABLE "study_variables" ADD CONSTRAINT "study_variable_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");

ALTER TABLE "measurement_moments" ADD CONSTRAINT "epoch_study_measurement_fkey" FOREIGN KEY ("study_id", "epoch_name") REFERENCES "epochs" ("study_id", "name");
ALTER TABLE "measurement_moments" ADD CONSTRAINT "study_measurement_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "measurements" ADD CONSTRAINT "variable_measurement_fkey" FOREIGN KEY ("study_id", "variable_id") REFERENCES "study_variables" ("study_id", "variable_id");
ALTER TABLE "measurements" ADD CONSTRAINT "arm_measurement_fkey" FOREIGN KEY ("study_id", "arm_name") REFERENCES "arms" ("study_id", "name");
ALTER TABLE "measurements" ADD CONSTRAINT "measurement_moments" FOREIGN KEY ("study_id", "measurement_moment_name") REFERENCES "measurement_moments" ("study_id", "name");
ALTER TABLE "arms" ADD CONSTRAINT "study_arm_fkey" FOREIGN KEY ("study_id") REFERENCES "studies" ("id");
ALTER TABLE "arms" ADD CONSTRAINT "study_arms_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "epochs" ADD CONSTRAINT "study_epoch_fkey" FOREIGN KEY ("study_id") REFERENCES "studies" ("id");
ALTER TABLE "epochs" ADD CONSTRAINT "study_epochs_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "designs" ADD CONSTRAINT "design_arm_fkey" FOREIGN KEY ("study_id", "arm_name") REFERENCES "arms" ("study_id", "name");
ALTER TABLE "designs" ADD CONSTRAINT "design_epoch_fkey" FOREIGN KEY ("study_id", "epoch_name") REFERENCES "epochs" ("study_id", "name");
ALTER TABLE "designs" ADD CONSTRAINT "design_activity_fkey" FOREIGN KEY ("study_id", "activity_name") REFERENCES "activities" ("study_id", "name");
ALTER TABLE "treatments" ADD CONSTRAINT "treatments_drug_fkey" FOREIGN KEY ("drug_name") REFERENCES "drugs" ("name");
ALTER TABLE "treatments" ADD CONSTRAINT "treatment_activity_fkey" FOREIGN KEY ("study_id", "activity_name") REFERENCES "activities" ("study_id", "name");
ALTER TABLE "treatment_dosings" ADD CONSTRAINT "treatment_dosings_unit_fkey" FOREIGN KEY ("unit") REFERENCES "units" ("name");
ALTER TABLE "treatment_dosings" ADD CONSTRAINT "treatment_dosings_fkey" FOREIGN KEY ("treatment_id") REFERENCES "treatments" ("id");

ALTER TABLE "indications" ADD CONSTRAINT "indication_code_system_fkey" FOREIGN KEY ("code_system") REFERENCES "code_systems" ("code_system");
ALTER TABLE "notes" ADD CONSTRAINT "note_note_hooks" FOREIGN KEY ("note_hook_id") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "study_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "study_indication_fkey" FOREIGN KEY ("indication") REFERENCES "indications" ("name");
ALTER TABLE "studies" ADD CONSTRAINT "study_blinding_type_note_hook_fkey" FOREIGN KEY ("blinding_type_note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "study_title_note_hook_fkey" FOREIGN KEY ("title_note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "allocation_type_note_hook_fkey" FOREIGN KEY ("allocation_type_note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "study_references" ADD CONSTRAINT "study_references_fkey" FOREIGN KEY ("study_id") REFERENCES "studies" ("id");
ALTER TABLE "drugs" ADD CONSTRAINT "drugs_code_system_fkey" FOREIGN KEY ("code_system") REFERENCES "code_systems" ("code_system");
