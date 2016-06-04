LOAD DATA INFILE  "C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/Question_Topic_Prob_Document.txt" 
INTO TABLE stackoverflow.questiontopic
FIELDS TERMINATED BY ',' 
IGNORE 1 LINES;