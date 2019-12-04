The following docker image was used:
vanessa/pytorch-dev:py2.7
 
(docker run --runtime=nvidia -v ~/code-authorship:/workspace/ca -it vanessa/pytorch-dev:py2.7 /bin/bash)
Any docker image with Python2.7 should work, though.

The following packages were installed:
pip install gensim scikit-learn pandas

Code from 
https://github.com/keishinkickback/Pytorch-RNN-text-classification
was used as the RNN.

First, populate res/authors by running select_author_subset.py. This randomly selects a list of authors. 
Next, run preprocessor_for_rnn.py. This creates train.tsv and test.tsv required for the RNN. These files should be moved into Pytorch-RNN-text-classification/data,
which is where the RNN expects them to be.

Next, from the Pytorch-RNN-text-classification directory:

To run without pretrained embeddings:
python main.py --embedding-size 128 --classes 250 

To run with the code2vec embeddings:
python main.py --glove code2vec.txt --embedding-size 128 --classes 250 


