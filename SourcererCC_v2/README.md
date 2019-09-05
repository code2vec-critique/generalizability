Use SourcererCC V2: https://github.com/Mondego/SourcererCC/tree/v2.0

Follow the instructions on that page to tokenize the files using TXL.

Next, index the files using java -jar dist/indexbased.SearchManager.jar index 8.
And then run the search using java -jar dist/indexbased.SearchManager.jar search 8 .
Note that the parameter to  `search` is a dummy value. 
The actual value was set within the source code: This is set in src/model/CloneValidator.java as the third parameter to  `cosineSimilarityExceedsThreshold`.

This threshold can be tuned (manually) accordingly until both code2vec and glove vectors match on a similar number of code clones. The path to the code vectors can be set in src/indexbased/EmbeddingsComparison.java
