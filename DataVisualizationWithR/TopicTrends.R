library("ggplot2")
yearMonth = c()
data = c()
dataNumeric = c()
index = 1;

# read contents of input file
fileName <- "CombinedTopicTrends.txt"
conn <- file(fileName, open = "r")
line <- readLines(conn)
for(i in 1 : length(line)) {
	if(grepl("YearMonth", line[i])) {
		s = (trimws(line[i]))
		yearMonth[index] = substr(s, 12, nchar(s))
		i = i + 1
		data[index] = strsplit(trimws(line[i]), "\\s+")
		index = index + 1
	}
}
close(conn)

# put data in matrix form
m = matrix(nrow = length(yearMonth), ncol = length(data[[1]]) + 1)
m[,1] = yearMonth
cols = length(data[[1]]) + 1
for(i in 1 : length(yearMonth)) {
	m[i, 2:cols] = as.numeric(data[[i]])
}
# reqdData = m[1:length(yearMonth), 2:cols]
reqdData = m[1:length(yearMonth), 37:cols]

# get all topic names
fileNameTopicNames <- "TopicNames.txt"
connTopicNames <- file(fileNameTopicNames,open="r")
topicNames <- readLines(connTopicNames)
close(connTopicNames)
# colnames(reqdData) <- c(topicNames[1:40])
colnames(reqdData) <- c(topicNames[36:40])

df <- as.data.frame(reqdData, stringsAsFactors=FALSE)
dfs <- stack(df)
dfs[,3] = yearMonth
colnames(dfs) <- c("Proportion", "Topic", "Time")
f <- factor(dfs[,1])
f <- as.numeric(as.character(f))
dfs[,1] <- f
ggplot(dfs, aes(Time, Proportion, colour = Topic, group = Topic)) + geom_line() +  ggtitle("Trend Analysis") + labs(x = "Time", y = "Topic Impact") 
