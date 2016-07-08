library("ggplot2")
data <- read.csv("InterpretedTopicShare_AnalysisFinal.txt", header = F)

ggplot(data, aes(x=reorder(data[[1]], data[[2]]), y=data[[2]])) + 
geom_bar(stat="identity", fill="steelblue") + 
# theme(axis.text.x = element_text(angle = 60, hjust = 1)) + 
theme(plot.title = element_text(face="bold")) +
# scale_y_continuous(limits = c(0,8)) + 
ylab("Percentage Impact") + 
xlab("Topic Name") + 
coord_flip() +
ggtitle("Topic Impact")