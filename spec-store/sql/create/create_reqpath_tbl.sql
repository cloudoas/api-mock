CREATE TABLE reqpath_tbl (
            id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, 
            path VARCHAR(128) NOT NULL,
            created_date DATE,
            lastUpdated_date DATE);