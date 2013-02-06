package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import com.google.common.collect.AbstractIterator;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
        if (key == null || key.length() == 0)
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
                currentNode = currentNode.getEqKid();
            } else if (charComp < 0) {
                currentNode = currentNode.getLoKid();
            } else {
                // charComp must be greater than zero
                currentNode = currentNode.getHiKid();
            }
        }
    }

    public WordformTSTSearchResult getLongestPrefixMatch(String key) {
        getNodeLongestPrefixMatchResult nodeLongestPrefixMatchResult = getNodeLongestPrefixMatch(key);
        if (nodeLongestPrefixMatchResult == null)
            return null;
        // if match exact return iterator over wordforms in just result node
        if (nodeLongestPrefixMatchResult.getMatchLength() == key.length())
            return new WordformTSTSearchResult(key.length(), nodeLongestPrefixMatchResult.getResultNode().iterator());
        // otherwise, iterate over wordforms in subtree with root in result node
        else
            return new WordformTSTSearchResult(nodeLongestPrefixMatchResult.getMatchLength(),
                new SubtreeIterator(nodeLongestPrefixMatchResult.getResultNode()));
    }

    private static class Node implements Serializable, Iterable<Wordform> {
        private char splitchar;

        private Wordform[] data;

        private Node LoKid;
        private Node EqKid;
        private Node HiKid;
        private Node(char splitchar) {
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

        @Override
        public Iterator<Wordform> iterator() {
            return new Iterator<Wordform>() {
                private int i = 0;

                @Override
                public boolean hasNext() {
                    return (i < data.length);
                }

                @Override
                public Wordform next() {
                    return data[i++];
                }

                @Override
                public void remove() {
                    Wordform[] n = new Wordform[data.length - 1];
                    System.arraycopy(data, 0, n, 0, i );
                    System.arraycopy(data, i + 1, n, i, data.length - i - 1);
                    data = n;
                }
            };
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

    private class SubtreeIterator extends AbstractIterator<Wordform> {
        private Node rootNode;
        private Iterator<Wordform> currentNodeIterator;
        private Deque<Node> nodeStack = new ArrayDeque<Node>();

        private SubtreeIterator(Node rootNode) {
            this.rootNode = rootNode;
            nodeStack.addFirst(rootNode);
        }

        @Override
        protected Wordform computeNext() {
            if (rootNode == null)
                return endOfData();
            while (!currentNodeIterator.hasNext()) {
                boolean hasNextNode = goToNextNode();
                if (!hasNextNode) {
                    return endOfData();
                }
            }
            return currentNodeIterator.next();
        }

        boolean goToNextNode() {
            Node nextNode;
            try {
                nextNode = nodeStack.removeFirst();
            } catch (NoSuchElementException e) {
                return false;
            }
            currentNodeIterator = nextNode.iterator();
            // We don't need side nodes for root node because they overwrite it, because of TST structure.
            if (nextNode.getLoKid() != null && nextNode != rootNode)
                nodeStack.addFirst(nextNode.getLoKid());
            if (nextNode.getEqKid() != null)
                nodeStack.addFirst(nextNode.getEqKid());
            if (nextNode.getHiKid() != null && nextNode != rootNode)
                nodeStack.addFirst(nextNode.getHiKid());
            return true;
        }
    }

    private class getNodeLongestPrefixMatchResult {
        private int matchLength;
        private Node resultNode;

        private getNodeLongestPrefixMatchResult(int matchLength, Node resultNode) {
            this.matchLength = matchLength;
            this.resultNode = resultNode;
        }

        public Node getResultNode() {
            return resultNode;
        }

        public int getMatchLength() {
            return matchLength;
        }
    }
}
