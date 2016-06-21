library("ggplot2")
data <- read.table("TechnologyTrends8.txt", comment.char = "")
colnames(data) <- c("Time", "Proportion", "Technology")
ggplot(data, aes(Time, Proportion, colour = Technology, group = Technology)) + geom_line(size = 1) + ggtitle("Programming Language Trends") + labs(x = "Time", y = "Technology Impact")