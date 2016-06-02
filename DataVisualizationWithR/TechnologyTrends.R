library("ggplot2")
data <- read.table("TechnologyTrends8.txt", comment.char = "")
colnames(data) <- c("Time", "Proportion", "Technology")
ggplot(data, aes(Time, Proportion, colour = Technology, group = Technology)) + geom_line() + ggtitle("Programming Languages Trends") + labs(x = "Time", y = "Technology Impact")