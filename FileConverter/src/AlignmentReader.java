import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.utils.JPhyloIOReadingUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AlignmentReader {
    private JPhyloIOEventReader reader;
    private ApplicationModel model;
    private Map<String, Integer> sequenceIDMap = new HashMap<> ();

    void read(JPhyloIOEventReader reader, ApplicationModel model)
            throws IOException {
        this.reader = reader;
        this.model = model;
        sequenceIDMap.clear ();
        while (reader.hasNextEvent ()) {
            JPhyloIOEvent event = reader.next ();
            switch (event.getType ().getContentType ()) {
                case DOCUMENT:
                    if (event.getType ().getTopologyType ().equals (EventTopologyType.START)) {
                        model.clear ();
                    }
                    break;
                case ALIGNMENT:
                    if (event.getType ().getTopologyType ().equals (EventTopologyType.START)) {
                        if (model.isEmpty ()) {
                            readAlignment (event.asLinkedLabeledIDEvent ());
                        } else {
                            System.out.println ("Since this application does not support multiple alignments, the alignment with the ID "
                                    + event.asLinkedLabeledIDEvent ().getID () + " was skipped.");
                        }
                    }
                    break;

                default:
                    JPhyloIOReadingUtils.reachElementEnd (reader);
                    break;
            }
        }
    }

    private void readAlignment(LinkedLabeledIDEvent alignmentStartEvent) throws IOException {
        model.setLabel (alignmentStartEvent.getLabel ());

        JPhyloIOEvent event = reader.next ();
        while ((!event.getType ().getTopologyType ().equals (EventTopologyType.END))) {
            if (event.getType ().getContentType ().equals (EventContentType.SEQUENCE)) {
                readSequencePart (event.asLinkedLabeledIDEvent ());
            } else {
                JPhyloIOReadingUtils.reachElementEnd (reader);
            }
            event = reader.next ();
        }
    }

    private void readSequencePart(LinkedLabeledIDEvent sequencePartStartEvent) throws IOException {
        List<String> sequence = getTokenList (sequencePartStartEvent);

        JPhyloIOEvent event = reader.next ();
        while ((!event.getType ().getTopologyType ().equals (EventTopologyType.END))) {
            switch (event.getType ().getContentType ()) {
                case SEQUENCE_TOKENS:
                    sequence.addAll (event.asSequenceTokensEvent ().getTokens ());
                    break;

                case SINGLE_SEQUENCE_TOKEN:
                    sequence.add (event.asSingleSequenceTokenEvent ().getToken ());
                    JPhyloIOReadingUtils.reachElementEnd (reader);
                    break;

                default:
                    JPhyloIOReadingUtils.reachElementEnd (reader);
                    break;
            }
            event = reader.next ();
        }
    }

    private List<String> getTokenList(LinkedLabeledIDEvent sequencePartStartEvent) {
        Integer index = sequenceIDMap.get (sequencePartStartEvent.getID ());
        if (index == null) {
            index = model.addSequence (sequencePartStartEvent.getLabel ());
            sequenceIDMap.put (sequencePartStartEvent.getID (), index);
        }
        return model.getSequenceTokens (index);
    }
}
