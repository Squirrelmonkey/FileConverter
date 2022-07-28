import info.bioinfweb.commons.collections.NumberedStringsIterator;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.NoCharDefsNoSetsMatrixDataAdapter;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.SequenceTokensEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;

import java.io.IOException;
import java.util.Iterator;

class MatrixDataAdapterImpl extends NoCharDefsNoSetsMatrixDataAdapter implements MatrixDataAdapter {
    private ApplicationModel model;

    MatrixDataAdapterImpl(ApplicationModel model) {
        super ();
        this.model = model;
    }

    @Override
    public LinkedLabeledIDEvent getStartEvent(ReadWriteParameterMap parameters) {
        return new LinkedLabeledIDEvent (EventContentType.ALIGNMENT, "alignment", model.getLabel (), null);
    }

    @Override
    public long getSequenceCount(ReadWriteParameterMap parameters) {
        return model.size ();
    }

    @Override
    public long getColumnCount(ReadWriteParameterMap parameters) {
        return 0;
    }

    @Override
    public boolean containsLongTokens(ReadWriteParameterMap parameters) {
        return true;
    }

    @Override
    public Iterator<String> getSequenceIDIterator(ReadWriteParameterMap parameters) {
        return new NumberedStringsIterator (ReadWriteConstants.DEFAULT_SEQUENCE_ID_PREFIX, model.size ());
    }

    private int sequenceIndexByID(String sequenceID) {
        return NumberedStringsIterator.extractIntIndexFromString (sequenceID, ReadWriteConstants.DEFAULT_SEQUENCE_ID_PREFIX);
    }

    @Override
    public LinkedLabeledIDEvent getSequenceStartEvent(ReadWriteParameterMap parameters, String sequenceID) {
        return new LinkedLabeledIDEvent (EventContentType.SEQUENCE, sequenceID,
                model.getSequenceLabel (sequenceIndexByID (sequenceID)),
                null);
    }

    @Override
    public long getSequenceLength(ReadWriteParameterMap parameters, String sequenceID) throws IllegalArgumentException {
        return model.getSequenceTokens (sequenceIndexByID (sequenceID)).size ();
    }

    @Override
    public void writeSequencePartContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String sequenceID,
                                             long startColumn, long endColumn) throws IOException, IllegalArgumentException {
        receiver.add (new SequenceTokensEvent (
                model.getSequenceTokens (sequenceIndexByID (sequenceID)).subList ((int) startColumn, (int) endColumn)));

    }
}


