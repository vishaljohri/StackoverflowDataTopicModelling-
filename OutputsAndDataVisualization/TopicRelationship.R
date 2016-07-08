library("ggplot2")

# Multiple plot function
#
# ggplot objects can be passed in ..., or to plotlist (as a list of ggplot objects)
# - cols:   Number of columns in layout
# - layout: A matrix specifying the layout. If present, 'cols' is ignored.
#
# If the layout is something like matrix(c(1,2,3,3), nrow=2, byrow=TRUE),
# then plot 1 will go in the upper left, 2 will go in the upper right, and
# 3 will go all the way across the bottom.
#
multiplot <- function(..., plotlist=NULL, file, cols=1, layout=NULL) {
  library(grid)

  # Make a list from the ... arguments and plotlist
  plots <- c(list(...), plotlist)

  numPlots = length(plots)

  # If layout is NULL, then use 'cols' to determine layout
  if (is.null(layout)) {
    # Make the panel
    # ncol: Number of columns of plots
    # nrow: Number of rows needed, calculated from # of cols
    layout <- matrix(seq(1, cols * ceiling(numPlots/cols)),
                    ncol = cols, nrow = ceiling(numPlots/cols))
  }

 if (numPlots==1) {
    print(plots[[1]])

  } else {
    # Set up the page
    grid.newpage()
    pushViewport(viewport(layout = grid.layout(nrow(layout), ncol(layout))))

    # Make each plot, in the correct location
    for (i in 1:numPlots) {
      # Get the i,j matrix positions of the regions that contain this subplot
      matchidx <- as.data.frame(which(layout == i, arr.ind = TRUE))

      print(plots[[i]], vp = viewport(layout.pos.row = matchidx$row,
                                      layout.pos.col = matchidx$col))
    }
  }
}


data1 <- read.csv("TopicRelationship\\PerformanceOptimization.txt", header = F)
p1 <- ggplot(data1, aes(x=reorder(data1[[1]], -data1[[2]]), y=data1[[2]])) + 
geom_bar(stat="identity", fill="#548B54", width=0.7) + 
theme(plot.title = element_text(face="bold"), axis.text.x = element_text(angle = 45, hjust = 1)) + 
# scale_y_continuous(limits = c(0,8)) + 
ylab("Score") + 
xlab("Answer Topic") + 
# coord_flip() +
ggtitle("Performance/Optimization")

data2 <- read.csv("TopicRelationship\\ScriptingLanguage.txt", header = F)
p2 <- ggplot(data2, aes(x=reorder(data2[[1]], -data2[[2]]), y=data2[[2]])) + 
geom_bar(stat="identity", fill="#548B54", width=0.7) + 
theme(plot.title = element_text(face="bold"), axis.text.x = element_text(angle = 45, hjust = 1)) + 
# scale_y_continuous(limits = c(0,8)) + 
ylab("Score") + 
xlab("Answer Topic") + 
# coord_flip() +
ggtitle("Scripting Language")

data3 <- read.csv("TopicRelationship\\Testing.txt", header = F)
p3 <- ggplot(data3, aes(x=reorder(data3[[1]], -data3[[2]]), y=data3[[2]])) + 
geom_bar(stat="identity", fill="#548B54", width=0.7) + 
theme(plot.title = element_text(face="bold"), axis.text.x = element_text(angle = 45, hjust = 1)) + 
# scale_y_continuous(limits = c(0,8)) + 
ylab("Score") + 
xlab("Answer Topic") + 
# coord_flip() +
ggtitle("Testing")

data4 <- read.csv("TopicRelationship\\ThreadProcess.txt", header = F)
p4 <- ggplot(data4, aes(x=reorder(data4[[1]], -data4[[2]]), y=data4[[2]])) + 
geom_bar(stat="identity", fill="#548B54", width=0.7) + 
theme(plot.title = element_text(face="bold"), axis.text.x = element_text(angle = 45, hjust = 1)) + 
# scale_y_continuous(limits = c(0,8)) + 
ylab("Score") + 
xlab("Answer Topic") + 
# coord_flip() +
ggtitle("Thread/Process")

data5 <- read.csv("TopicRelationship\\VersionControl.txt", header = F)
p5 <- ggplot(data5, aes(x=reorder(data5[[1]], -data5[[2]]), y=data5[[2]])) + 
geom_bar(stat="identity", fill="#548B54", width=0.7) + 
theme(plot.title = element_text(face="bold"), axis.text.x = element_text(angle = 45, hjust = 1)) + 
# scale_y_continuous(limits = c(0,8)) + 
ylab("Score") + 
xlab("Answer Topic") + 
# coord_flip() +
ggtitle("Version Control")

data6 <- read.csv("TopicRelationship\\WebDevelopment.txt", header = F)
p6 <- ggplot(data6, aes(x=reorder(data6[[1]], -data6[[2]]), y=data6[[2]])) + 
geom_bar(stat="identity", fill="#548B54", width=0.7) + 
theme(plot.title = element_text(face="bold"), axis.text.x = element_text(angle = 45, hjust = 1)) + 
# scale_y_continuous(limits = c(0,8)) + 
ylab("Score") + 
xlab("Answer Topic") + 
# coord_flip() +
ggtitle("Web Development")

multiplot(p1, p2, p3, p4, p5, p6, cols=3)