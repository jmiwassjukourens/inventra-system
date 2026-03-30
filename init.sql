CREATE DATABASE catalog_db;
CREATE DATABASE suppliers_db;
CREATE DATABASE purchases_db;
CREATE DATABASE sales_db;
CREATE DATABASE accounts_db;
CREATE DATABASE IF NOT EXISTS keycloak_db;

GRANT ALL PRIVILEGES ON catalog_db.* TO 'inventra'@'%';
GRANT ALL PRIVILEGES ON suppliers_db.* TO 'inventra'@'%';
GRANT ALL PRIVILEGES ON purchases_db.* TO 'inventra'@'%';
GRANT ALL PRIVILEGES ON sales_db.* TO 'inventra'@'%';
GRANT ALL PRIVILEGES ON accounts_db.* TO 'inventra'@'%';

FLUSH PRIVILEGES;