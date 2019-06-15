package models;

import java.util.*;

import com.google.common.collect.Maps;
import indexbased.EmbeddingsComparison;
import utility.Util;
import indexbased.SearchManager;

public class CloneValidator implements IListener, Runnable {

    @Override
    public void run() {
        try {
            CandidatePair candidatePair = SearchManager.verifyCandidateQueue.remove();

            this.validate(candidatePair);
        } catch (NoSuchElementException e) {
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void validate(CandidatePair candidatePair)
            throws InterruptedException {
        if (candidatePair.candidateTokens != null && candidatePair.candidateTokens.trim().length() > 0) {

            int similarity = this.updateSimilarity(candidatePair.queryBlock,
                    candidatePair.candidateTokens,
                    candidatePair.computedThreshold,
                    candidatePair.candidateSize, candidatePair.simInfo);
            if (similarity > 0) {
                ClonePair cp = new ClonePair(candidatePair.queryBlock.getId(), candidatePair.candidateId);
                System.out.println(System.nanoTime() + " : GOT found " + candidatePair.queryBlock.getId() + ", " + candidatePair.candidateId);
                SearchManager.reportCloneQueue.put(cp);
            } else {
                System.out.println(System.nanoTime() + " : NOT found " + candidatePair.queryBlock.getId() + ", " + candidatePair.candidateId);
            }

            candidatePair.queryBlock = null;
            candidatePair.simInfo = null;

        } else {
            System.out.println("tokens not found for document");
        }
    }

    private int updateSimilarity(QueryBlock queryBlock, String tokens,
            int computedThreshold, int candidateSize, CandidateSimInfo simInfo) {

        try {
            Map<String, Integer> tokenStringSet = new HashMap<String, Integer>();
            Map<String, Integer> queryStringSet = new HashMap<String, Integer>();

            for (Map.Entry<String, TokenInfo> entry : queryBlock.getPrefixMap().entrySet()) {
                if (!EmbeddingsComparison.hasToken(entry.getKey().toLowerCase())) {
                    continue;
                }
                queryStringSet.put(entry.getKey().toLowerCase().intern(), entry.getValue().getFrequency());
            }
            for (Map.Entry<String, TokenInfo> entry : queryBlock.getSuffixMap().entrySet()) {
                if (!EmbeddingsComparison.hasToken(entry.getKey().toLowerCase())) {
                    continue;
                }
                queryStringSet.put(entry.getKey().toLowerCase().intern(), entry.getValue().getFrequency());
            }


            for (String tokenfreqFrame : tokens.split("::")) {
                String[] tokenFreqInfo = tokenfreqFrame.split(":");
                if (!EmbeddingsComparison.hasToken(tokenFreqInfo[0].toLowerCase())) {
                    continue;
                }

                tokenStringSet.put(tokenFreqInfo[0].toLowerCase().intern(), Integer.valueOf(tokenFreqInfo[1]));
            }


            if (EmbeddingsComparison.cosineSimilarityExceedsThreshold(tokenStringSet.entrySet(), queryStringSet.entrySet(), 900)) {
//                System.err.println("found " + System.nanoTime());
                return 1;
            }  else {
//                System.err.println("NOT found ");
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("possible error in the format. tokens: "
                    + tokens);
        }
        return -1;
    }

    private int updateSimilarityHelper(
            TokenInfo tokenInfo, int similarity, int candidatesTokenFreq) {
        similarity += Math.min(tokenInfo.getFrequency(), candidatesTokenFreq);
        // System.out.println("similarity: "+ similarity);
        return similarity;
    }
}
