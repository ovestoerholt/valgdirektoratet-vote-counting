-- Kjøres automatisk av Postgres-containeren FØRSTE gang den starter
-- (dvs. når datavolumet er tomt).
CREATE DATABASE opptelling OWNER valg;
CREATE DATABASE revisjon OWNER valg;