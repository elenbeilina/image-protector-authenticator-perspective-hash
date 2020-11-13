package frames;

import actions.*;
import components.ImagePanel;
import exceptions.DecodeException;
import exceptions.EncodeException;
import methods.Descriptor;
import methods.Encryptor;
import methods.ImageComparison;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import static constants.Parameters.*;
import static utils.Util.*;


public class MainFrame extends JFrame {

    private ImagePanel coverImagePanel;
    private JFileChooser fileChooser;
    private File toHide;

    public MainFrame() {
        setSize(MAINFRAME_WIDTH, MAINFRAME_HEIGHT);
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(scrSize.width / 2 - MAINFRAME_WIDTH / 2, scrSize.height / 2 - MAINFRAME_HEIGHT / 2);
        setTitle(PROGRAM_NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowEventsHandler());

        createComponents();
    }

    public void openCoverImage() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadImageFile(fileChooser.getSelectedFile(), coverImagePanel);
        }
    }

    public void encryptImage(Encryptor encryptor) {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                encryptor.hideTheMessage(coverImagePanel.getImage(), "00000000000000000000000100000000000000000000000010000000000000000000000");
                loadImage();
                showInformationMessage(this, MESSAGE_ENCRYPTION_COMPLETED);
            } catch (EncodeException e) {
                showErrorMessage(this, MESSAGE_ENCRYPTION_ERROR, e.getMessage());
            } catch (NullPointerException e) {
                showErrorMessage(this, MESSAGE_UNEXPECTED_ERROR, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadImage() throws IOException {
        File file = FileFilter.addExtension(fileChooser.getSelectedFile());

        String name = file.getName();
        String extension = name.substring(name.lastIndexOf('.') + 1);
        try {
            ImageIO.write(coverImagePanel.getImage(), extension, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        coverImagePanel.loadImage(file);
    }

    public void decryptImage(Descriptor descriptor) {
        try {
            String phash = descriptor.decodeTheImage(coverImagePanel.getImage());
            showInformationMessage(this, phash);
        } catch (DecodeException e) {
            showErrorMessage(this, MESSAGE_DECRYPTION_ERROR, e.getMessage());
        } catch (NullPointerException e) {
            showErrorMessage(this, MESSAGE_UNEXPECTED_ERROR, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compareImage(ImageComparison comparison){

        JOptionPane.showInputDialog(this, MESSAGE_SIMILARITIES_COMPLETED, PROGRAM_NAME, JOptionPane.INFORMATION_MESSAGE, null, null, null);
    }

    public void exit() {
        this.dispose();
    }

    private void createComponents() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new OpenCoverImageAction(this));
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(this));

        JMenu encryptionMenu = new JMenu("Encryption");
        encryptionMenu.add(new LSBMethodEncryptionAction(this));

        JMenu decryptionMenu = new JMenu("Decryption");
        decryptionMenu.add(new LSBMethodDecryptionAction(this));

        JMenu comparisonMenu = new JMenu("Comparison");
        comparisonMenu.add(new ImageComparisonActon(this));

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new AboutAction(this));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(encryptionMenu);
        menuBar.add(decryptionMenu);
        menuBar.add(comparisonMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.addChoosableFileFilter(new FileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);

        coverImagePanel = new ImagePanel("Cover image: ");

        this.add(coverImagePanel, BorderLayout.WEST);
    }

    private void loadImageFile(File f, ImagePanel panel) {
        try {
            panel.loadImage(f);
        } catch (IOException e) {
            showErrorMessage(this, MESSAGE_IO_ERROR, e.getMessage());
        } catch (Exception e) {
            showErrorMessage(this, MESSAGE_UNEXPECTED_ERROR, e.getMessage());
        }

    }


    private class WindowEventsHandler extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            MainFrame.this.exit();
        }
    }
}
