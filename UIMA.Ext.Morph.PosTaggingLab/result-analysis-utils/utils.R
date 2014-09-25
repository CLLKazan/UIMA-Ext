read.input.tsv <- function(input.tsv.file) {
  df.tmp <- read.delim(input.tsv.file, quote="", nrow=1)
  # check for GOLD
  if (!("GOLD" %in% names(df.tmp))) stop ("No GOLD column")
  # calc col classes
  df.input.col.classes <- rep("character", ncol(df.tmp))
  if("ID" %in% names(df.tmp)) {
    df.input.col.classes[match("ID", names(df.tmp))] <- "numeric"
  }
  df.input <- read.delim(input.tsv.file, quote="", fill=FALSE,
                         colClasses=df.input.col.classes)
  tagger.names <- as.list(names(df.tmp))
  tagger.names[tagger.names %in% c("GOLD", "ID", "TEXT")] <- NULL
  tagger.names <- unlist(tagger.names)
  
  # prepare tagset factor levels
  tagset.all <- sort(unique(
                       unlist(
                         df.input[c("GOLD", tagger.names)])))
  for(tagger.name in c("GOLD", tagger.names)) {
    df.input[[tagger.name]] <- factor(df.input[[tagger.name]], levels=tagset.all)
  }
  # rename empty tag to "null"
  tagset.all[tagset.all == ""] <- "null"
  for(tagger.name in c("GOLD", tagger.names)) {
    levels(df.input[[tagger.name]]) <- tagset.all
  }
  attr(df.input, "tagger.names") <- tagger.names
  df.input
}

align.factors <- function(df.arg, col.names) {
  factor.labels <- sort(unique(
                          unlist(
                            df.arg[col.names])))
  for(col.name in col.names) {
    df.arg[[col.name]] <- factor(df.arg[[col.name]], levels=factor.labels)
  }
  df.arg
}

# test statistic
acc.diff <- function(df.input, base.tagger, test.tagger) {
  gold <- df.input$GOLD
  base.output <- df.input[[base.tagger]]
  test.output <- df.input[[test.tagger]]
  calc.acc(gold, test.output) - calc.acc(gold, base.output)
}

calc.acc <- function(gold, system) {
  if(length(gold) != length(system)) stop("sequences of different size")
  sum(gold == system) / length(gold)
}

permut.output <- function(swap.vec, df.input, base.tagger, test.tagger) {
  if(nrow(df.input) != length(swap.vec)) stop("sequences of different size")
  swap.vec <- as.logical(swap.vec)
  #
  base.out.new <- df.input[[base.tagger]]
  test.out.new <- df.input[[test.tagger]]
  base.out.new[swap.vec] <- test.out.new[swap.vec]
  test.out.new[swap.vec] <- df.input[[base.tagger]][swap.vec]
  #
  # df.input[[base.tagger]] <- rep(base.out.new[1], nrow(df.input))
  df.input[[base.tagger]] <- base.out.new
  df.input[[test.tagger]] <- test.out.new
  df.input
}

rand.on.acc.distr <- function(df.arg, base.tagger, test.tagger) {
  swap.vec <- rbinom(nrow(df.arg), 1, .5)
  df.new <- permut.output(swap.vec, df.arg, base.tagger, test.tagger)
  abs(acc.diff(df.new, base.tagger, test.tagger))
}

approx.rand.pvalue <- function(df.arg, base.tagger, test.tagger, R=1000) {
  # observed difference in accuracy between test tagger and baseline tagger
  obs.diff <- abs(acc.diff(df.src, base.tagger, test.tagger))
  acc.diff.dist <- replicate(R, rand.on.acc.distr(df.arg, base.tagger, test.tagger))
  (sum(acc.diff.dist > obs.diff) + 1) / (length(acc.diff.dist) + 1)
}

posGrams <- c("NOUN","ADJF","ADJS","COMP","VERB","INFN","PRTF","PRTS","GRND","NUMR","ADVB","NPRO","PRED","PREP","CONJ","PRCL","INTJ","RNC_INIT","Prnt","Apro","Anum")

# calc.relative.accuracy per POS
calc.rel.acc <- function(df.input, tagger) {
  df.trimmed <- df.input[,c("GOLD", tagger)]
  df.trimmed$POS <- factor(trimToGramCat(as.character(df.trimmed$GOLD), posGrams))
  sapply(levels(df.trimmed$POS), function(x){
    df.rel <- subset(df.trimmed, POS == x, select=c("GOLD", tagger))
    calc.acc(df.rel$GOLD, df.rel[,tagger]) * 100
  })
}

# tags - input, character
trimToGramCat <- function(tags, cat.grams){
  grams.list <- strsplit(tags, "&")
  sapply(grams.list, function(grs) {
    result <- intersect(grs, cat.grams)
    if(length(result) == 0) "null"
    else paste(result, collapse="&")
  })
}

df.trimToGramCat <- function(df, cat.grams){
  for(taggerName in colnames(df)){
    df[[taggerName]] <- factor(trimToGramCat(as.character(df[[taggerName]]), cat.grams))
  }
  df <- align.factors(df, colnames(df))
  df
}
