LOAD DATA INFILE  "C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/Answer_Topic_Prob_Document.txt" 
INTO TABLE stackoverflow.answertopic 
FIELDS TERMINATED BY ',' 
IGNORE 1 LINES;