data <- read.csv("InterpretedTopicSpreadPerDocument.txt", header = F)

ggplot(data, aes(x=reorder(data[[1]], -data[[2]]), y=data[[2]])) + 
geom_bar(stat="identity", fill="steelblue") + 
theme(axis.text.x = element_text(angle = 60, hjust = 1)) + 
# scale_y_continuous(limits = c(0,8)) + 
ylab("Percentage Popularity") + 
xlab("Topic Name") + 
ggtitle("Topic Popularity Per Document")