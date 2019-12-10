Use SourcererCC V2: https://github.com/Mondego/SourcererCC/tree/v2.0

`git clone git@github.com:Mondego/SourcererCC.git`
`cd SourcererCC`
`git checkout v2.0`
`ant clean cdi` can be run in the subdirectory clone-dectector
 
Use Java 8. Check this by running `java -version`.

Follow the instructions on Sourcerer's CC github to tokenize the files using TXL.
Run the tokenizer as described in SourcererCC's readme. Note that this must be run on a Linux machine (as mentioned in SourcerCC's readme). 
```
java -jar InputBuilderClassic.jar ~/Downloads/bcb_reduced/  parser_output/tokens.file parser_output_header/headers.file functions java 0 0 10 0 false false false 8
```

Next, modify the source code of SourcererCC by replacing the files in clone-detector/src/indexbased/ and clone-detector/src/models with the ones in this repository.
Two values have to changed: the similarity threshold and the path to the code vectors.

Both these values are set within the source code: This is set in src/model/CloneValidator.java as the third parameter to  `cosineSimilarityExceedsThreshold`.
This similarity threshold can be tuned (manually) accordingly until both code2vec and glove vectors match on a similar number of code clones. 
The path to the code vectors can be set in src/indexbased/EmbeddingsComparison.java

Afterwards, run `ant clean cdi` to recreate `indexbased.SearchManager.jar`.
Modify the file `clone-detector/sourcerer-cc.properties` and change the value of `QUERY_DIR_PATH` and `DATASET_DIR_PATH` to be `parser/java/parser_output` (the output of the tokenization step)

Next, index the files using java -jar dist/indexbased.SearchManager.jar index 7/8.
And then run the search using java -jar dist/indexbased.SearchManager.jar search 7/8 .
Note that the parameter to  `search` is a dummy value. 
The actual value was set within the source code in src/model/CloneValidator.java as the third parameter to  `cosineSimilarityExceedsThreshold` (as mentioned above).

The output of SourcererCC is written to a file in output7.0/output8.0. `convert.py` is provided as a helper script to convert this to a format that can be imported into BigCloneBench. (https://github.com/jeffsvajlenko/BigCloneEval).
`convert.py` takes the output file of SourcererCC as input, and writes to standard output. `python convert.py output7.0/blocksclones_index_WITH_FILTER.txt > output.csv` 


Briefly, on BigCloneBench (download/clone from https://github.com/jeffsvajlenko/BigCloneEval), run `init`, `registerTool`, `listTools`,`importClones`, and `evaluate`. 
e.g. `./init`
e.g. `./registerTool -n "EmbeddingsSourcerer" -d "Embeddings"`
e.g. `./listTools` will print out the tool ID that was just registered
e.g. `./importClones -t <tool ID>`
e.g. `./evaluate -t <tool ID> -st both -m Coverage "CoverageMatcher 0.7" -mil 6 -mit 50`
 


Dataset:

BigCloneBench can be accessed from https://github.com/jeffsvajlenko/BigCloneEval (from Jeffrey Svajlenko and Chanchal K. Roy, “Evaluating Clone Detection Tools with BigCloneBench”, In Proceedings of the 31st International Conference on Software Maintenance and Evolution (ICSME 2015))
https://jeffsvajlenko.weebly.com/bigcloneeval.html -> "IJaDatsaet: download (2016-06-20)"

OJClone can be accessed from https://sites.google.com/site/treebasedcnn/ (from Mou, Lili, et al. "Convolutional neural networks over tree structures for programming language processing." Thirtieth AAAI Conference on Artificial Intelligence. 2016.)
