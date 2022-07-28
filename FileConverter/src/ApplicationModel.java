import java.util.ArrayList;
import java.util.List;

class ApplicationModel {
    private static class Sequence {
        private String label;
        private List<String> tokenList;

        Sequence(String label) {
            super ();
            this.label = label;
            tokenList = new ArrayList<> ();
        }

        String getLabel() {
            return label;
        }

        List<String> getTokens() {
            return tokenList;
        }
    }

    private String label;
    private List<Sequence> sequences = new ArrayList<> ();

    String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }

    int addSequence(String sequenceLabel) {
        sequences.add (new Sequence (sequenceLabel));
        return sequences.size () - 1;
    }

    String getSequenceLabel(int index) {
        return sequences.get (index).getLabel ();
    }

    List<String> getSequenceTokens(int index) {
        return sequences.get (index).getTokens ();
    }

    int size() {
        return sequences.size ();
    }

    void clear() {
        label = null;
        sequences.clear ();
    }

    boolean isEmpty() {
        return (label == null) && sequences.isEmpty ();
    }
}
