CREATE TABLE respindex_tbl (
            spec_id INT NOT NULL, 
            path_id INT NOT NULL, 
            content_type_id INT NOT NULL,
            name VARCHAR(4) NOT NULL,
            resp_body_id INT NOT NULL,
            created_date DATE,
            lastUpdated_date DATE,
            
            UNIQUE(spec_id, path_id, content_type_id, name));