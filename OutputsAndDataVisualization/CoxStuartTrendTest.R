increasing = c()
decreasing = c()
cox.stuart.test =
function (x, n)
{
  method = "Cox-Stuart test for trend analysis"
  leng = length(x)
  apross = round(leng) %% 2
  if (apross == 1) {
    delete = (length(x)+1)/2
    x = x[ -delete ] 
  }
  half = length(x)/2
  x1 = x[1:half]
  x2 = x[(half+1):(length(x))]
  difference = x1-x2
  signs = sign(difference)
  signcorr = signs[signs != 0]
  pos = signs[signs>0]
  neg = signs[signs<0]
  if (length(pos) < length(neg)) {
    prop = pbinom(length(pos), length(signcorr), 0.5)
	print(prop)
    names(prop) = "Increasing trend, p-value"
    rval <- list(method = method, statistic = prop)
    class(rval) = "htest"
	increasing <<- c(increasing, paste("topic = ", n, "prop = ", prop))
    return(rval)
  }
  else {
    prop = pbinom(length(neg), length(signcorr), 0.5)
    names(prop) = "Decreasing trend, p-value"
    rval <- list(method = method, statistic = prop)
    class(rval) = "htest"
	decreasing <<- c(decreasing, paste("topic = ", n, "prop = ", prop))
    return(rval)
  }
}

yearMonth = c()
data = c()
dataNumeric = c()
index = 1;
noTopics <- 40

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
reqdData = m[1:length(yearMonth), 2:cols]
# reqdData = m[1:length(yearMonth), 37:cols]

# get all topic names
fileNameTopicNames <- "TopicNamesAnalysisFinal.txt"
connTopicNames <- file(fileNameTopicNames,open="r")
topicNames <- readLines(connTopicNames)
close(connTopicNames)
colnames(reqdData) <- c(topicNames[1:40])
# colnames(reqdData) <- c(topicNames[36:40])

df <- as.data.frame(reqdData, stringsAsFactors=FALSE)
for(i in 1 : noTopics) {
	print(paste0("Trend of ", colnames(df)[i]))
	print(cox.stuart.test(as.numeric(df[, colnames(df)[i]]), i))
}
print("Increasing Trends")
print(increasing)
print("Decreasing Trends")
print(decreasing)

