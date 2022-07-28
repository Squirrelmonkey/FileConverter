import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.implementations.ListBasedDocumentDataAdapter;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Application {
    private ApplicationModel model = new ApplicationModel ();
    private JPhyloIOReaderWriterFactory factory = new JPhyloIOReaderWriterFactory ();

    private static void frameUpload(Application application) {
        JFrame upload = new JFrame ("File Upload");
        upload.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        final JFileChooser fileChooser = new JFileChooser ();
        fileChooser.setCurrentDirectory (new java.io.File ("C:/Users"));
        fileChooser.setMultiSelectionEnabled (false);
        fileChooser.setFileSelectionMode (JFileChooser.FILES_ONLY);

        JButton b1 = new JButton ("Upload File");
        b1.setBounds (90, 50, 100, 50);
        b1.addActionListener (e -> {
            if (fileChooser.showOpenDialog (null) == JFileChooser.APPROVE_OPTION) {
                File filePath = fileChooser.getSelectedFile ();
                File file = new File (filePath.getPath ());

                application.read (file);
                application.display ();
                upload.dispose ();

                frameDownload (application);

            }
        });
        upload.setSize (300, 200);
        upload.setLayout (null);
        upload.add (b1);
        upload.setVisible (true);
    }

    private void read(File file) {
        try {
            JPhyloIOEventReader eventReader = factory.guessReader (file, new ReadWriteParameterMap ());
            if (eventReader != null) {
                try {
                    new AlignmentReader ().read (eventReader, model);
                } finally {
                    eventReader.close ();
                }
            } else {
                System.out.println ("The format of the file \"" + file.getAbsolutePath () + "\" is not supported.");
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    private void display() {
        if (model.isEmpty ()) {
            System.out.println ("File was empty or data was not a sequence.");
        } else {
            if (model.getLabel () != null) {
                System.out.println (model.getLabel () + ":");
            } else {
                System.out.println ("Unnamed alignment:");
            }
            for (int i = 0; i < model.size (); i++) {
                System.out.print (model.getSequenceLabel (i) + ":  ");
                for (String t : model.getSequenceTokens (i)) {
                    System.out.print (t + " ");
                }
                System.out.println ();
            }
        }
    }


    private static void frameDownload(Application application) {
        JRadioButton r1, r2, r3, r4;
        JButton b1, b2;

        JFrame convert = new JFrame ("File Converter");
        convert.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        final JFileChooser fileChooser = new JFileChooser ();
        fileChooser.setCurrentDirectory (new java.io.File ("C:/Users"));
        fileChooser.setMultiSelectionEnabled (false);
        fileChooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);

        r1 = new JRadioButton ("NEXML");
        r1.setBounds (100, 25, 100, 30);
        r2 = new JRadioButton ("PHYLIP");
        r2.setBounds (100, 75, 100, 30);
        r3 = new JRadioButton ("NEXUS");
        r3.setBounds (100, 125, 100, 30);
        r4 = new JRadioButton ("FASTA");
        r4.setBounds (100, 175, 100, 30);
        ButtonGroup bg = new ButtonGroup ();
        bg.add (r1);
        bg.add (r2);
        bg.add (r3);
        bg.add (r4);

        b1 = new JButton ("Convert File");
        b1.setBounds (85, 225, 110, 50);
        b1.addActionListener (e -> {
            if (fileChooser.showSaveDialog (null) == JFileChooser.APPROVE_OPTION) {
                String fType = null;
                String fID = null;
                if (r1.isSelected ()) {
                    fID = JPhyloIOFormatIDs.NEXML_FORMAT_ID;
                    fType = "Converted.xml";
                } else if (r2.isSelected ()) {
                    fID = JPhyloIOFormatIDs.PHYLIP_FORMAT_ID;
                    fType = "Converted.phy";
                } else if (r3.isSelected ()) {
                    fID = JPhyloIOFormatIDs.NEXUS_FORMAT_ID;
                    fType = "Converted.nex";
                } else if (r4.isSelected ()) {
                    fID = JPhyloIOFormatIDs.FASTA_FORMAT_ID;
                    fType = "Converted.fasta";
                }
                File folderPath = fileChooser.getCurrentDirectory ();
                File file = new File (folderPath + File.separator + fType);
                application.write (new File (String.valueOf (file)), fID);
            }
        });

        b2 = new JButton ("Close");
        b2.setBounds (99, 300, 80, 40);
        b2.addActionListener (e -> convert.dispose ());

        convert.add (r1);
        convert.add (r2);
        convert.add (r3);
        convert.add (r4);
        convert.add (b1);
        convert.add (b2);
        convert.setSize (300, 400);
        convert.setLayout (null);
        convert.setVisible (true);
    }

    private void write(File file, String fID) {
        ListBasedDocumentDataAdapter document = new ListBasedDocumentDataAdapter ();

        document.getMatrices ().add (new MatrixDataAdapterImpl (model));
        JPhyloIOEventWriter writer = factory.getWriter (fID);
        try {
            writer.writeDocument (document, file, new ReadWriteParameterMap ());
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public static void main(String[] args) {
        Application application = new Application ();
        frameUpload (application);
    }
}
