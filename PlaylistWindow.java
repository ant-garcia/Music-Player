import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.util.Scanner;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlaylistWindow extends JPanel implements ActionListener{
    private String audioFilePath;
    private String audioFileName;
    private String lastOpenPath;
    protected ArrayList <String> audioFileNameList = new ArrayList<String>();
    protected ArrayList <String> audioPathNameList = new ArrayList<String>();
    protected File list = new File("list.txt");
    static protected BufferedWriter fw;
    protected FileReader flr;
    protected BufferedReader listReader;
    protected JButton addButton;
    protected JButton delButton;
    private JScrollPane scrollSongList;
    private JTextArea SongList;

    public PlaylistWindow(){
        initComponents();
    }

    @SuppressWarnings("unchecked")
    
    private void initComponents(){
        
        scrollSongList = new JScrollPane();
        SongList = new JTextArea();
        addButton = new JButton();
        delButton = new JButton();

        setPreferredSize(new Dimension(400, 400));

        scrollSongList.setEnabled(true);

        SongList.setColumns(20);
        SongList.setRows(5);
        SongList.setEditable(false);
        scrollSongList.setViewportView(SongList);

        addButton.setText("");
        addButton.addActionListener(this);
        delButton.setText("");
        delButton.addActionListener(this);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(scrollSongList, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(delButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrollSongList, GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(delButton)))
        );

        try{
            loadPlaylist();
            setSongList();
        }   
        catch(IOException i)
        {}
    }

    @Override
    public void actionPerformed(ActionEvent event){
        Object source = event.getSource();
        if (source instanceof JButton){
            JButton button = (JButton) source;
            if (button == addButton)
                openFile();
            else if (button == delButton){
                String fileName = JOptionPane.showInputDialog("Which song do you want to remove?");
                deleteFile(fileName);
            } 
        }
    }

    private void openFile(){
        JFileChooser fileChooser = null;
        if (lastOpenPath != null && !lastOpenPath.equals("")){
            fileChooser = new JFileChooser(lastOpenPath);
        }else{
            fileChooser = new JFileChooser();
        }
        
        FileFilter wavFilter = new FileFilter() {
            @Override
            public String getDescription() {
                return "Sound file (*.WAV)";
            }

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }else{
                    return file.getName().toLowerCase().endsWith(".wav");
                }
            }
        };

        
        fileChooser.setFileFilter(wavFilter);
        fileChooser.setDialogTitle("Open Audio File");
        fileChooser.setAcceptAllFileFilterUsed(false);

        int userChoice = fileChooser.showOpenDialog(this);
        if (userChoice == JFileChooser.APPROVE_OPTION){
            audioFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            audioFileName = fileChooser.getSelectedFile().getName();
            lastOpenPath = fileChooser.getSelectedFile().getParent();
            try{
                writePlaylist(audioFilePath);
                loadPlaylist();

                int index = 0;
                SongList.setText("");

                for(String s : audioFileNameList){
                    s = s.substring(s.lastIndexOf(File.separator) + 1);
                    SongList.append(++index + ". " + s + "\n");
                }
            }
            catch(IOException i){
                System.out.println("Something happened");
            }
        }
    }

    private void deleteFile(String fileName){
        for(int i = 0; i < audioFileNameList.size(); i++)
            if((fileName + ".wav").equals(audioFileNameList.get(i))){
                    audioFileNameList.remove(i);
                    audioPathNameList.remove(i);
                }

        SongList.setText("");

        for(int i = 0; i < audioFileNameList.size(); i++)
            SongList.append((i + 1) + ". " + audioFileNameList.get(i) + "\n");

    }

    public void writePlaylist(String file) throws IOException{
        if(fw == null)
            fw = new BufferedWriter(new FileWriter(list, true));

        fw.append(file);
        fw.append("\r\n");
        fw.flush();
        fw.close();
    }

    public void loadPlaylist()throws IOException{
        String s;
        Scanner in = new Scanner(list);

        if(audioPathNameList != null){
            audioPathNameList.clear();
            audioFileNameList.clear();
        }

        while(in.hasNext()){
            s = in.nextLine();
            audioPathNameList.add(s);
            audioFileNameList.add(s.substring(s.lastIndexOf(File.separator) + 1, s.lastIndexOf('.')));
        }
        in.close();
    }

    public void setSongList(){
        int index = 0;
        SongList.setText("");
        for(String s : audioFileNameList){
            s = s.substring(s.lastIndexOf(File.separator) + 1);
            SongList.append(++index + ". " + s + "\n");
        }
    }
}
