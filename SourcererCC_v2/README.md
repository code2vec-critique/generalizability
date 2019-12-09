Use SourcererCC V2: https://github.com/Mondego/SourcererCC/tree/v2.0

`git clone git@github.com:Mondego/SourcererCC.git`
`cd SourcererCC`
`git checkout v2.0`
`ant clean cdi` can be run in the subdirectory clone-dectector
 
Use Java 8. Running `java -version` should show something similar to the following:
```
$ java -version
java version "1.8.0_181"
Java(TM) SE Runtime Environment (build 1.8.0_181-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)
```

Follow the instructions on Sourcerer's CC github to tokenize the files using TXL.
Run the tokenizer as described in SourcererCC's readme. Note that this must be run on a Linux machine (as mentioned in SourcerCC's readme). 
```
java -jar InputBuilderClassic.jar ~/Downloads/bcb_reduced/  parser_output/tokens.file parser_output_header/headers.file functions java 0 0 10 0 false false false 8
```


Next, modify the source code of SourcererCC by replacing the files in clone-detector/src/indexbased/ and clone-detector/src/models in this repository.
Afterwards, run `ant clean cdi` to recreate the indexbased.SearchManager.jar file.

Next, index the files using java -jar dist/indexbased.SearchManager.jar index 7/8.
And then run the search using java -jar dist/indexbased.SearchManager.jar search 7/8 .
Note that the parameter to  `search` is a dummy value. 
The actual value was set within the source code: This is set in src/model/CloneValidator.java as the third parameter to  `cosineSimilarityExceedsThreshold`.

This threshold can be tuned (manually) accordingly until both code2vec and glove vectors match on a similar number of code clones. The path to the code vectors can be set in src/indexbased/EmbeddingsComparison.java


Dataset:

BigCloneBench can be accessed from https://github.com/jeffsvajlenko/BigCloneEval (from Jeffrey Svajlenko and Chanchal K. Roy, “Evaluating Clone Detection Tools with BigCloneBench”, In Proceedings of the 31st International Conference on Software Maintenance and Evolution (ICSME 2015))
https://jeffsvajlenko.weebly.com/bigcloneeval.html -> "IJaDatsaet: download (2016-06-20)"

OJClone can be accessed from https://sites.google.com/site/treebasedcnn/ (from Mou, Lili, et al. "Convolutional neural networks over tree structures for programming language processing." Thirtieth AAAI Conference on Artificial Intelligence. 2016.)
