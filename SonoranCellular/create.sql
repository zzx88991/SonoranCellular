/* Assignment 8 -create.sql
 * CSc 460
 * Fall 2014
 * Carla Bustos and Zixiang Zhou
 */


CREATE TABLE Account (
   AccountNumber NUMBER(10) NOT NULL PRIMARY KEY,
   Name VARCHAR(25) NOT NULL UNIQUE
   );

CREATE TABLE Phone (
   IMEI INTEGER NOT NULL PRIMARY KEY,
   MobileNumber INTEGER NOT NULL UNIQUE,
   Manufacturer VARCHAR(25) NOT NULL,
   Model VARCHAR(10) NOT NULL
   );

CREATE TABLE Plan (
   PlanName VARCHAR(25) NOT NULL PRIMARY KEY,
   PriceMonth FLOAT NOT NULL,
   AllowedDataUsage INTEGER NOT NULL, 
   PlanType VARCHAR(25) NOT NULL
   );

CREATE TABLE Owns (
   MasterAccountNumber NUMBER(10) NOT NULL,
   DependantAccountNumber NUMBER(10) NOT NULL,
   PRIMARY KEY (MasterAccountNumber, DependantAccountNumber),
   FOREIGN KEY (MasterAccountNumber) REFERENCES Account,
   FOREIGN KEY (DependantAccountNumber) REFERENCES Account
   );

CREATE TABLE Address (
   AddressNumber INTEGER NOT NULL,
   AccountNumber NUMBER(10) NOT NULL,
   Street VARCHAR(25) NOT NULL,
   Zip INTEGER NOT NULL,
   IsPrimary CHAR(1) CHECK (IsPrimary in ('Y','N')),
   PRIMARY KEY (AddressNumber),
   FOREIGN KEY (AccountNumber) REFERENCES Account
   );

CREATE TABLE Bill (
   AccountNumber NUMBER(10) NOT NULL,
   EndDate DATE NOT NULL,
   StartDate DATE NOT NULL,
   DueDate DATE NOT NULL,
   PRIMARY KEY (AccountNumber, EndDate),
   FOREIGN KEY (AccountNumber) REFERENCES Account
   );

CREATE TABLE Item (
   AccountNumber NUMBER(10) NOT NULL,
   EndDate DATE NOT NULL,
   ItemNumber INTEGER NOT NULL,
   Amount FLOAT NOT NULL,
   PRIMARY KEY (AccountNumber, EndDate, ItemNumber),
   FOREIGN KEY (AccountNumber, EndDate) REFERENCES Bill
   ); 

CREATE TABLE Subscribe (
   IMEI INTEGER NOT NULL UNIQUE,
   AccountNumber NUMBER(10) NOT NULL,
   PlanName VARCHAR(25) NOT NULL,
   PRIMARY KEY (AccountNumber, IMEI, PlanName),
   FOREIGN KEY (AccountNumber) REFERENCES Account,
   FOREIGN KEY (IMEI) REFERENCES Phone,
   FOREIGN KEY (PlanName) REFERENCES Plan	
   );

CREATE TABLE IsAssociatedWith (
   IMEI INTEGER NOT NULL,
   ItemNumber INTEGER NOT NULL,
   EndDate DATE NOT NULL,
   AccountNumber NUMBER(10) NOT NULL,
   PlanName VARCHAR(25) NOT NULL, 
   ActualDataUsage FLOAT NOT NULL,
   PRIMARY KEY (IMEI, PlanName, AccountNumber, EndDate, ItemNumber),
   FOREIGN KEY (IMEI) REFERENCES Phone,
   FOREIGN KEY (PlanName) REFERENCES Plan,
   FOREIGN KEY (AccountNumber, EndDate, ItemNumber) REFERENCES Item 
   );

CREATE TABLE PrePaidPlan(
   PlanName VARCHAR(25) NOT NULL,
   OutgoingMinutes INTEGER NOT NULL,
   PRIMARY KEY (PlanName), 
   FOREIGN KEY (PlanName) REFERENCES Plan
);

CREATE TABLE BringYourOwnDevicePlan(
   NumContractYears INTEGER NOT NULL,
   RecurringPlanName VARCHAR(25) NOT NULL,
   NumIntlOutgoingMinutesPerMonth INTEGER NOT NULL,
   PRIMARY KEY (NumContractYears) ,
   FOREIGN KEY (RecurringPlanName) REFERENCES Plan
);

CREATE TABLE BuyNewDevicePlan(
   NumInstallments INTEGER NOT NULL,
   NumRoamingMinutesPerMonth INTEGER NOT NULL,
   RecurringPlanName VARCHAR(25) NOT NULL,
   PRIMARY KEY (NumInstallments),
   FOREIGN KEY (RecurringPlanName) REFERENCES Plan
);



