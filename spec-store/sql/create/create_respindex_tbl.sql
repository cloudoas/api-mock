CREATE TABLE respindex_tbl (
            spec_id INT NOT NULL, 
            path_id INT NOT NULL, 
            content_type_id INT NOT NULL,
            request_method VARCHAR(10) NOT NULL,
            response_name VARCHAR(32) NOT NULL,
            resp_body_id INT NOT NULL,
            created_date DATE,
            lastUpdated_date DATE,
            
            UNIQUE(spec_id, path_id, request_method, response_name, content_type_id));