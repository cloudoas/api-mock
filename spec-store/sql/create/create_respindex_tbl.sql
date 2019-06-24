CREATE TABLE respindex_tbl (
            spec_id INT NOT NULL, 
            path_id INT NOT NULL, 
            status_code VARCHAR(4) NOT NULL,
            content_type_id INT NOT NULL,
            created_date DATE,
            lastUpdated_date DATE,
            
            UNIQUE(spec_id, path_id, status_code, content_type_id));