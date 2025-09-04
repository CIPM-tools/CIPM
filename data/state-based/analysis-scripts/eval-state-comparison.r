library(tseries)
library(rcompanion)
library(jsonlite)
source("location-and-scales.r")
source("non-parametric-cis.r")
source("normal-eval-functions.r")
source("generic-eval.r")

out <- evaluateMeasurements("./ms3.csv")
write_json(out$results, "eval-results.json", pretty = TRUE)

readRawData <- read.csv("ms3-all.csv", sep = ",", header = FALSE)
allRawData <- c()
for (idx in 1:length(readRawData)) {
  allRawData <- c(allRawData, readRawData[[idx]])
}
yMin <- min(allRawData)
yMax <- max(allRawData)
pdf(file="all-in-one-scatter.pdf")
plot(allRawData)
dev.off()

g1 <- c("teammates-48b67b-48b67b", "teammates-648425-648425", "teammates-ce4463-ce4463", "teammates-83f518-83f518", "teammates-f33d0b-f33d0b")

g2 <- c("teammates-48b67b-648425", "teammates-83f518-f33d0b", "teammates-ce4463-f33d0b", "teammates-648425-48b67b", "teammates-f33d0b-ce4463", "teammates-48b67b-83f518", "teammates-f33d0b-83f518", "teammates-83f518-48b67b")

g3 <- c("cwa-server-7e1b61-7e1b61", "cwa-server-6e9702-6e9702", "cwa-server-3977e6-3977e6", "cwa-server-206e8c-206e8c", "cwa-server-c22f93-c22f93", "cwa-server-94bca6-94bca6", "cwa-server-9323b8-9323b8", "cwa-server-33d1c9-33d1c9")

g4 <- c("cwa-server-3977e6-94bca6", "cwa-server-94bca6-3977e6", "cwa-server-c22f93-33d1c9", "cwa-server-33d1c9-c22f93", "cwa-server-c22f93-6e9702", "cwa-server-9323b8-33d1c9", "cwa-server-206e8c-3977e6", "cwa-server-7e1b61-6e9702", "cwa-server-206e8c-9323b8", "cwa-server-6e9702-c22f93", "cwa-server-6e9702-7e1b61", "cwa-server-9323b8-206e8c", "cwa-server-33d1c9-9323b8", "cwa-server-3977e6-206e8c")

groups <- list(g1=g1, g2=g2, g3=g3, g4=g4)
groupColors <- list(g1="grey", g2="red", g3="green", g4="blue")
groupPointers <- list(g1="+", g2=NULL, g3="-", g4="x")

pdf(file="all-groups.pdf")
plot(c(), c(), ylim=c(1800, 10900), xlim=c(1, 100), xlab="# Measurement", ylab="Execution Time (ms)")
title("Measurement Points")
for (gName in names(groups)) {
  for (indName in groups[[gName]]) {
    points(out$allSeries[[indName]], col=groupColors[[gName]],pch=groupPointers[[gName]])
  }
}
dev.off()
