DROP TABLE ComenziContinut
DROP TABLE Comenzi
DROP TABLE Produse
DROP VIEW CmdFilter
DROP VIEW infoView

CREATE TABLE Produse(
Nume VARCHAR(15) PRIMARY KEY,
Categorie VARCHAR(60),
Pret FLOAT)
 
CREATE TABLE Comenzi(
Id INT IDENTITY(1,1) PRIMARY KEY,
Client VARCHAR(15) NOT NULL,
DataOrdin DATETIME DEFAULT GETDATE())

CREATE TABLE ComenziContinut(
Comanda INT FOREIGN KEY REFERENCES Comenzi(Id),
Produs VARCHAR(15) FOREIGN KEY REFERENCES Produse(Nume),
Cantitate INT NOT NULL,
Pret FLOAT NOT NULL,
CONSTRAINT PK_CmdCont PRIMARY KEY (Comanda,Produs))

INSERT INTO PRODUSE(Nume,Categorie,Pret) VALUES ('Aspirina','Calmant',14.55)
INSERT INTO PRODUSE(Nume,Categorie,Pret) VALUES ('Paracetamol','Calmant',25.21)
INSERT INTO PRODUSE(Nume,Categorie,Pret) VALUES ('Nurofen','Antibiotic,Antiinflamator',58.63)
INSERT INTO PRODUSE(Nume,Categorie,Pret) VALUES ('VitaMAX','Supliment,Vitamina',32.99)

BEGIN TRANSACTION
INSERT INTO Comenzi(Client) VALUES ('Dona') 
DECLARE @Id INT
Set @Id =  SCOPE_IDENTITY() 
INSERT INTO ComenziContinut(Comanda,Produs,Cantitate,Pret) 
	SELECT @Id,'Aspirina',13,Pret FROM Produse WHERE Nume = 'Aspirina'
INSERT INTO ComenziContinut(Comanda,Produs,Cantitate,Pret) 
	SELECT @Id,'VitaMax',12,Pret FROM Produse WHERE Nume = 'Paracetamol'
IF(@@ERROR > 0)
BEGIN
    Rollback Transaction
END
ELSE
BEGIN
   Commit Transaction
END

SELECT * FROM ComenziContinut
GO

CREATE VIEW CmdFilter AS 
	SELECT * FROM Comenzi 
		WHERE Client = 'Dona' AND MONTH(DataOrdin) = 11 AND YEAR(DataOrdin) = YEAR(GETDATE());
GO		

CREATE VIEW infoView AS
SELECT Comenzi.Id,Client,Produs,Cantitate,ComenziContinut.Pret,DataOrdin,Categorie FROM Comenzi
  JOIN ComenziContinut  ON Comenzi.Id = ComenziContinut.Comanda
  JOIN Produse ON Nume = Produs
GROUP BY Id,Client,Produs,Cantitate,ComenziContinut.Pret,DataOrdin,Categorie
GO

SELECT	COUNT( DISTINCT Id) AS 'Numar Comenzi',
		SUM( Pret * Cantitate) AS 'Suma totala',
		SUM( Pret * Cantitate)/COUNT( DISTINCT Id) AS 'Media'
		FROM infoView 
		WHERE Client = 'Dona' AND MONTH(DataOrdin) = 11 AND YEAR(DataOrdin) = YEAR(GETDATE());

SELECT COUNT(Id) AS 'Numar comenzi antibiotice' FROM infoView
		WHERE YEAR(GETDATE()) = YEAR(DataOrdin) AND
		Client = 'Vlad' AND
		Categorie LIKE '%Antibiotic%'

GO
CREATE OR ALTER VIEW totalView AS SELECT Client,SUM( Pret * Cantitate) AS Total FROM infoView
GROUP BY Id,Client
GO
SELECT Client,Total FROM totalView
WHERE Total = (SELECT MAX(TOTAL) FROM totalView)

SELECT * FROM totalView

SELECT * FROM infoView



		


