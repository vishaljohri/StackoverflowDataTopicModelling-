library("ggplot2")
data <- read.table("TechnologyTrends_AnalysisFinalMoreTags37.txt", comment.char = "")
colnames(data) <- c("Time", "Proportion", "Technology")
ggplot(data, aes(Time, Proportion, colour = Technology, group = Technology)) + geom_line(size = 1) + ggtitle("Version Control") + labs(x = "Time", y = "Technology Impact") + scale_x_discrete(breaks = unique(data$Time)[seq(1,24,3)], labels = breakPoints) + theme(plot.title = element_text(face="bold"))# + theme(axis.text.x = element_text(angle = 45, hjust = 1, size = 10))