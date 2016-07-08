library("ggplot2")
yearMonth = c()
data = c()
dataNumeric = c()
index = 1;

# read contents of input file
fileName <- "CombinedTopicTrends_AnalysisFinal.txt"
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
# reqdData = m[1:length(yearMonth), 37:cols]

# give topicNo + 1 in c(...)
reqdData = m[1:length(yearMonth), c(5, 10, 16, 18, 39)]

# get all topic names
fileNameTopicNames <- "TopicNamesAnalysisFinal.txt"
connTopicNames <- file(fileNameTopicNames,open="r")
topicNames <- readLines(connTopicNames)
close(connTopicNames)
# colnames(reqdData) <- c(topicNames[1:40])
# colnames(reqdData) <- c(topicNames[36:40])

# give topic numbers in c(topicNames[i])
colnames(reqdData) <- c(topicNames[4], topicNames[9], topicNames[15], topicNames[17], topicNames[38])

df <- as.data.frame(reqdData, stringsAsFactors=FALSE)
dfs <- stack(df)
dfs[,3] = yearMonth
colnames(dfs) <- c("Proportion", "Topic", "Time")
f <- factor(dfs[,1])
f <- as.numeric(as.character(f))
dfs[,1] <- f
# breakPoints <- c("Jan 2014", "", "", "Apr 2014", "", "", "Jul 2014", "", "", "Oct 2014", "", "", "Jan 2015", "", "", "Apr 2015", "", "", "Jul 2015", "", "", "Oct 2015", "", "")
breakPoints <- c("Jan 2014", "Apr 2014", "Jul 2014", "Oct 2014", "Jan 2015", "Apr 2015", "Jul 2015", "Oct 2015")
ggplot(dfs, aes(Time, Proportion, colour = Topic, group = Topic)) + geom_line(size = 1) +  ggtitle("Top Decreasing Topic Trends") + labs(x = "Time", y = "Topic Impact") + scale_x_discrete(breaks = unique(dfs$Time)[seq(1,24,3)], labels = breakPoints) + theme(plot.title = element_text(face="bold")) # + scale_y_continuous(limits = c(0.022, 0.026)) # + theme(axis.text.x = element_text(angle = 45, hjust = 1, size = 10))
