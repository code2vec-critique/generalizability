import torch.nn as nn
import torch.nn.functional as F
import torch.optim
import pickle
import torch
import sys
from sklearn.metrics import precision_recall_curve
from sklearn.metrics import average_precision_score


from torch.utils.data import DataLoader
import torch.utils.data as utils
from IPython import embed

class Authorship(nn.Module):

    def __init__(self, authors_count):

        super(Authorship, self).__init__()

        self.authors_count = int(authors_count)

        # Hidden layers
        self.hidden = nn.ModuleList()

        layer = nn.LSTM(1000, 1024, 2, dropout=0.6)

        #self.hidden.append(layer)

        # for k in range(2):
        #     self.hidden.append(nn.LSTM(1024, 1024, 2, dropout = 0.6))

        for k in range(1):
            layer = nn.Sequential(
                nn.Linear(256, 512),
                nn.ReLU(inplace=True)
            )
            self.hidden.append(layer)
        
        # Output layer
        self.out = nn.Linear(512, 400)
        self.out2 = nn.Linear(400, self.authors_count)

    def forward(self, x):

        for layer in self.hidden:
            x = layer(x)
            if isinstance(x, tuple):
                x = x[0]
        final_embeddings = self.out(x)

        hmm = self.out2(final_embeddings)
        # print('shape', hmm.shape)

        # output = F.softmax(hmm, dim=0)
        # print('output', output.shape)

        return hmm


def main():
    with open('res/code2vec.pickle', 'rb') as f:
        tfidf = pickle.load(f)

    # tfidf_tensor = torch.from_numpy(tfidf)

    use_sparse = False
    with open('res/code2vec_authors.pickle', 'rb') as f:
        target = pickle.load(f)

    with open('res/code2vec.pickle.test', 'rb') as f:
        tfidf_test = pickle.load(f)

        # tfidf_tensor = torch.from_numpy(tfidf)

    with open('res/code2vec_authors.pickle.test', 'rb') as f:
        target_test = pickle.load(f)
    # target_tensor = torch.from_numpy(target)

    # uniq_authors = set(target)

    # print(max(target[0]))
    net = Authorship(max(target) + 1).double()
    print(net)

    params = list(net.parameters())
    # print(len(params))
    # print(params[0].size())  # conv1's .weight
    criterion = nn.CrossEntropyLoss()
    optimizer = torch.optim.Adam(net.parameters(), lr=0.01)

    # embed()
    # print(tfidf[0])
    if use_sparse:
        tensor_x = torch.stack([torch.from_numpy(i.todense()) for i in tfidf])  # transform to torch tensors
    else:
        tensor_x = torch.stack([torch.from_numpy(i) for i in tfidf])  # transform to torch tensors

    tensor_y = torch.from_numpy(target)

    if use_sparse:
        tensor_x_test = torch.stack([torch.from_numpy(i.todense()) for i in tfidf_test])  # transform to torch tensors
    else:
        tensor_x_test = torch.stack([torch.from_numpy(i) for i in tfidf_test])  # transform to torch tensors


    tensor_y_test = torch.from_numpy(target_test)

    print('tensor y', tensor_y)
    author_dataset = utils.TensorDataset(tensor_x, tensor_y)
    author_dataloader = utils.DataLoader(author_dataset, batch_size=128)
    if sys.argv[1] != "test":

        # trainloader = DataLoader(dataset=dataset, batch_size=128)

        num_training_steps = 800
        for trg_step in range(num_training_steps):
            print('training step', trg_step)
            running_loss = 0.0
            for input, target in author_dataloader:

                # input = torch.randn(1, 1, 32, 32)
                # print('input', input.shape)

                optimizer.zero_grad()

                out = net(input)

                # print('target', target)
                # print('target shape', target.shape)
                # print('output', out.squeeze(1))
                # print('output shape', out.squeeze(1).shape)
                loss = criterion(out.squeeze(1), target)
                running_loss += loss.item()
                loss.backward()

                optimizer.step()
                if trg_step % 50 == 0:
                    torch.save(net.state_dict(), './model.checkpoint.' + str(trg_step))
            print(running_loss)
    else:
        net.load_state_dict(torch.load(sys.argv[2]))


    author_test_dataset = utils.TensorDataset(tensor_x_test, tensor_y_test)
    author_test_dataloader = utils.DataLoader(author_test_dataset, batch_size=128)

    torch.save(net.state_dict(), './model.saved')
    print('.... out ....')
    # set toe evaluation mode to turn off dropout etc..
    net.eval()

    guesses = []

    for input, target in author_test_dataloader:
        out = net(input)
        # print(out.shape)

        _, predicted = torch.max(out.squeeze(1), 1)

        print('predicted', predicted)
        for prediction in predicted:
            guesses.append(prediction)
    print(tensor_y_test)

    # evalulation
    correct = 0
    for i, guess in enumerate(guesses):
        if guess == tensor_y_test[i]:
            correct += 1


    print('Average precision score, micro-averaged over all classes: {0:0.2f}'.format(correct / len(guesses)))

    

if __name__ == "__main__":
    main()
