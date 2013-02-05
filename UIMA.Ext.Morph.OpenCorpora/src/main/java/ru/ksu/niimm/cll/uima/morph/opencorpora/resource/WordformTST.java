package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import org.opencorpora.cas.Wordform;

import java.io.Serializable;

public class WordformTST implements Serializable {
    private Node rootNode;

    private static int compareChars(char first, char second) {
        return first - second;
    }

    public void put(String key, Wordform wf) {
        getOrCreateNode(key).addData(wf);
    }

    private Node getOrCreateNode(String key) throws NullPointerException, IllegalArgumentException {
        if (key == null)
            throw new NullPointerException("attempt to get or create node with null key");
        if (key.length() == 0)
            throw new IllegalArgumentException("attempt to get or create node with key of zero length");
        if (rootNode == null)
            rootNode = new Node(key.charAt(key.length() - 1));

        Node currentNode = rootNode;
        int charIndex = key.length() - 1;
        while (true) {
            int charComp = compareChars(key.charAt(charIndex), currentNode.splitchar);

            if (charComp == 0) {
                charIndex--;
                if (charIndex < 0)
                    return currentNode;
                if (currentNode.getEqKid() == null)
                    currentNode.setEqKid(new Node(key.charAt(charIndex)));
                currentNode = currentNode.getEqKid();
            } else if (charComp < 0) {
                if (currentNode.getLoKid() == null)
                    currentNode.setLoKid(new Node(key.charAt(charIndex)));
                currentNode = currentNode.getLoKid();
            } else {
                // charComp must be greater than zero
                if (currentNode.getHiKid() == null)
                    currentNode.setHiKid(new Node(key.charAt(charIndex)));
                currentNode = currentNode.getHiKid();
            }
        }
    }

    private getNodeLongestPrefixMatchResult getNodeLongestPrefixMatch(String key) {
        if (key == null || rootNode == null || key.length() == 0)
            return null;
        Node currentNode = rootNode;
        Node resultNode = null;
        int matchLength = 0;
        int charIndex = key.length() - 1;

        while (true) {
            if (currentNode == null)
                return new getNodeLongestPrefixMatchResult(matchLength, resultNode);
            int charComp = compareChars(key.charAt(charIndex), currentNode.splitchar);

            if (charComp == 0) {
                charIndex--;
                if (charIndex < 0)
                    return new getNodeLongestPrefixMatchResult(key.length(), currentNode);
                resultNode = currentNode;
                matchLength = key.length() - charIndex - 1;
                currentNode = (Node) currentNode.getEqKid();
            } else if (charComp < 0) {
                currentNode = (Node) currentNode.getLoKid();
            } else {
                // charComp must be greater than zero
                currentNode = (Node) currentNode.getHiKid();
            }
        }
    }

    private static class Node implements Serializable {
        private char splitchar;
        private Wordform[] data;
        private Node LoKid;
        private Node EqKid;
        private Node HiKid;

        public Node(char splitchar) {
            this.splitchar = splitchar;
        }

        void addData(Wordform wf) {
            if (data == null) {
                data = new Wordform[1];
            } else {
                Wordform[] temp = data;
                data = new Wordform[data.length + 1];
                System.arraycopy(temp, 0, data, 0, temp.length);
            }
            data[data.length - 1] = wf;
        }

        Node getLoKid() {
            return LoKid;
        }

        void setLoKid(Node loKid) {
            LoKid = loKid;
        }

        Node getEqKid() {
            return EqKid;
        }

        void setEqKid(Node eqKid) {
            EqKid = eqKid;
        }

        Node getHiKid() {
            return HiKid;
        }

        void setHiKid(Node hiKid) {
            HiKid = hiKid;
        }
    }

    public class getNodeLongestPrefixMatchResult {
        private int matchLength;
        private Node resultNode;

        public getNodeLongestPrefixMatchResult(int matchLength, Node resultNode) {
            this.matchLength = matchLength;
            this.resultNode = resultNode;
        }

        public Node getResultNode() {
            return resultNode;
        }

        public void setResultNode(Node resultNode) {
            this.resultNode = resultNode;
        }

        public int getMatchLength() {
            return matchLength;
        }

        public void setMatchLength(int matchLength) {
            this.matchLength = matchLength;
        }
    }
}
