CREATE TABLE section (
  deleted BOOLEAN,
   depth INTEGER,
   section_order INTEGER NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE,
   deleted_at TIMESTAMP WITHOUT TIME ZONE,
   article_id VARCHAR(255),
   created_by VARCHAR(255),
   id VARCHAR(255) NOT NULL,
   revision_id VARCHAR(255),
   super_section_id VARCHAR(255),
   CONSTRAINT section_pkey PRIMARY KEY (id)
);

CREATE TABLE article (
  deleted BOOLEAN,
   is_private BOOLEAN,
   is_public_editable BOOLEAN,
   created_at TIMESTAMP WITHOUT TIME ZONE,
   deleted_at TIMESTAMP WITHOUT TIME ZONE,
   created_by VARCHAR(255),
   id VARCHAR(255) NOT NULL,
   section_id VARCHAR(255),
   title VARCHAR(255),
   CONSTRAINT article_pkey PRIMARY KEY (id)
);

CREATE TABLE category (
  deleted BOOLEAN,
   deleted_at TIMESTAMP WITHOUT TIME ZONE,
   category_name VARCHAR(255),
   id VARCHAR(255) NOT NULL,
   CONSTRAINT category_pkey PRIMARY KEY (id)
);

CREATE TABLE section_revisions (
  revisions_id VARCHAR(255) NOT NULL,
   section_id VARCHAR(255) NOT NULL
);

ALTER TABLE section ADD CONSTRAINT section_revision_id_key UNIQUE (revision_id);

ALTER TABLE article ADD CONSTRAINT article_section_id_key UNIQUE (section_id);

ALTER TABLE category ADD CONSTRAINT category_unique UNIQUE (category_name);

ALTER TABLE section_revisions ADD CONSTRAINT section_revisions_revisions_id_key UNIQUE (revisions_id);

CREATE TABLE categories (
  article_id VARCHAR(255) NOT NULL,
   category_id VARCHAR(255) NOT NULL
);

CREATE TABLE revision (
  deleted BOOLEAN,
   created_at TIMESTAMP WITHOUT TIME ZONE,
   deleted_at TIMESTAMP WITHOUT TIME ZONE,
   created_by VARCHAR(255),
   id VARCHAR(255) NOT NULL,
   text TEXT,
   title VARCHAR(255),
   CONSTRAINT revision_pkey PRIMARY KEY (id)
);

ALTER TABLE section ADD CONSTRAINT fk3ilt9vt58gvan2u1hs2jeommj FOREIGN KEY (super_section_id) REFERENCES section (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE section_revisions ADD CONSTRAINT fk8qs952vfaq2pjp8rsmk29ydxf FOREIGN KEY (section_id) REFERENCES section (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE section_revisions ADD CONSTRAINT fkea3x0vy8kcx2jrshe1s83y6kt FOREIGN KEY (revisions_id) REFERENCES revision (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE section ADD CONSTRAINT fkhojowvxvqifrsqbwjmv9rokdf FOREIGN KEY (revision_id) REFERENCES revision (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE article ADD CONSTRAINT fkhwg3ffnvqnrchkaprvbtr9iqs FOREIGN KEY (section_id) REFERENCES section (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE section ADD CONSTRAINT fkkndgvqr4l6vj4fa2ieym3v8es FOREIGN KEY (article_id) REFERENCES article (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE categories ADD CONSTRAINT fkl2y3dics1a2ioji92if2wuxc4 FOREIGN KEY (category_id) REFERENCES category (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE categories ADD CONSTRAINT fklsy0rim7ih8xo7dycoalrmj8a FOREIGN KEY (article_id) REFERENCES article (id) ON UPDATE NO ACTION ON DELETE NO ACTION;