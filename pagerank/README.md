Copyright 2015

Ioannis Antoniadis

<antoniii@auth.gr>

Aristotle University of Thessaloniki, Greece

Department of Electrical and Computer Engineering

# pagerank
Implementation of the google-pagerank algorithm

## Algorithm
Internet can be modelled as a graph with nodes that correspond to webpages and links between them.
The pagerank algorithm calculates the probability of a user being on a random webpage at a future time.
The final values of the probabilities are used by search engines to rank each webpage in the final results list. 

## Implementation
The current implementation includes both a sequential version as well as a parallel version of the algorithm using p-threads. Running the parallel version in a multi-threaded environment results in better timings, compared to the sequential version.

## Input
Input graphs as .txt files can be found [here](https://www.dropbox.com/sh/yq6rh093rqyf9uv/AABgMBf-fxEujsKsVclrZOmTa?dl=0).
More info at [http://snap.stanford.edu/data](http://snap.stanford.edu/data) at Web-Graphs section.
