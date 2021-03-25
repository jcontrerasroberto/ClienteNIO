import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.css.RGBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class Tablero extends JFrame {

    private final int port = 9876;
    private final String dir = "localhost";
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private int rows = 16, columns = 16;
    private JLabel titulo, time;
    private JPanel table, info, words;
    private String[][] matrix = new String[rows][columns];
    private JButton[][] buttons = new JButton[rows][columns];
    private ArrayList<String> actualWords = new ArrayList<String>();
    private ArrayList<JLabel> wordsLabel = new ArrayList<JLabel>();
    private JComboBox<String> comboCategories = new JComboBox<String>();
    private String category;
    private String tryWord;
    private ArrayList<JButton> selectedButtons  = new ArrayList<JButton>();
    private Integer pendienteok;
    private JButton start, giveup;
    private JPanel controlpanel, categoriespanel, timePanel;
    private boolean gamestatus = false;
    private long startTime, endTime;

    public Tablero() throws IOException, ClassNotFoundException {

        createGrid();
        createInfo();
        this.setLayout(new BorderLayout());
        this.add(table, BorderLayout.WEST);
        this.add(info, BorderLayout.EAST);
        this.setBounds(10,10,800,480);
        this.setVisible(true);
        this.setTitle("Sopa de letras");
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        System.out.println("Iniciando el cliente");
        Socket socketcon = new Socket(dir, port);
        System.out.println("Conexion establecida con el servidor");
        oos = new ObjectOutputStream(socketcon.getOutputStream());
        oos.flush();
        ois = new ObjectInputStream(socketcon.getInputStream());
        fillCategories();

    }

    public void createGrid(){
        GridLayout experimentLayout = new GridLayout(rows,columns);
        table = new JPanel();
        table.setLayout(experimentLayout);
        table.setPreferredSize(new Dimension(480, 480));
        for(int x=0; x<rows; x++){
            for (int y=0; y<columns; y++){
                buttons[x][y] = new JButton(" ");
                buttons[x][y].setMargin(new Insets(0,0,0,0));
                buttons[x][y].setBackground(Color.WHITE);
                buttons[x][y].setPreferredSize(new Dimension(30, 30));
                buttons[x][y].putClientProperty("xpos", y);
                buttons[x][y].putClientProperty("ypos", x);
                buttons[x][y].putClientProperty("result", false);
                buttons[x][y].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        System.out.println(actionEvent.getActionCommand());
                        if(!gamestatus) return;
                         JButton actioner = (JButton) actionEvent.getSource();
                        if(!selectedButtons.contains(actioner)){
                            selectedButtons.add(actioner);
                            actioner.setBackground(Color.YELLOW);
                            System.out.println(actioner.getClientProperty("xpos") + " . " + actioner.getClientProperty("ypos"));
                        }else{
                            selectedButtons.remove(actioner);
                            actioner.setBackground(Color.WHITE);
                            if((boolean) actioner.getClientProperty("result")){
                                actioner.setBackground(Color.GREEN);
                            }
                        }
                        if (checkIfOk()){
                            System.out.println("Checar si es alguna palabra");
                            String word = getWordInOrder();
                            if(actualWords.contains(word.toLowerCase(Locale.ROOT)) || actualWords.contains(StringUtils.reverse(word.toLowerCase(Locale.ROOT)))){
                                System.out.println("Encontraste la palabra :  " + word);
                                disableButtons();
                                markInPanel(word);
                                //actualWords.get();
                                int posInOrder = actualWords.indexOf(word.toLowerCase(Locale.ROOT));
                                int posInDisorder = actualWords.indexOf(StringUtils.reverse(word.toLowerCase(Locale.ROOT)));
                                if(posInOrder>=0) actualWords.remove(posInOrder);
                                else{
                                    actualWords.remove(posInDisorder);
                                }
                                if(actualWords.size()==0){
                                    endGame();
                                }
                            }
                        }
                    }
                });
                table.add(buttons[x][y]);
            }
        }
    }

    public void markInPanel(String word){
        for (JLabel tlabel : wordsLabel){
            System.out.println("Palabra del label :  " + tlabel.getText());
            if(tlabel.getText().equals(word.toUpperCase(Locale.ROOT)) || tlabel.getText().equals(StringUtils.reverse(word).toUpperCase(Locale.ROOT))) tlabel.setForeground(Color.RED);
        }
    }

    public void disableButtons(){
        for (int i = 0; i < selectedButtons.size(); i++){
            selectedButtons.get(i).setBackground(Color.GREEN);
            selectedButtons.get(i).putClientProperty("result", true);
            //selectedButtons.get(i).setEnabled(false);
        }
        selectedButtons.clear();
    }

    public String getWordInOrder(){
        String word = "";
        ArrayList<Letter> arrLetras = new ArrayList<Letter>();
        for(JButton sel : selectedButtons){
            Letter temp = new Letter((int) sel.getClientProperty("xpos"), (int) sel.getClientProperty("ypos"), sel.getText());
            arrLetras.add(temp);
        }
        if(pendienteok==0){
            System.out.println("Ordenando en base a x");
            Collections.sort(arrLetras, Letter.xposOrder);
        }
        if(pendienteok==180){
            System.out.println("Ordenando en base a y");
            Collections.sort(arrLetras, Letter. yposOrder);
        }
        if(pendienteok==45 || pendienteok==-45){
            System.out.println("Ordenando en base a y o x, no importa");
            Collections.sort(arrLetras, Letter. xposOrder);
        }

        for(Letter tl : arrLetras){
            word = word + tl.getValue();
        }

        return word;

    }

    public boolean checkIfOk(){
        ArrayList<Integer> pendientes = new ArrayList<Integer>();
        for (int i = 0; i<selectedButtons.size()-1; i++) {
            int restax = ((int) selectedButtons.get(i+1).getClientProperty("xpos") - (int)selectedButtons.get(i).getClientProperty("xpos"));
            if(restax==0){
                pendientes.add(180);
            }else{
                int pendientetemp = ((int) selectedButtons.get(i+1).getClientProperty("ypos") - (int)selectedButtons.get(i).getClientProperty("ypos")) / restax;
                System.out.println("La pend de este punto es :  " + pendientetemp);
                if (pendientetemp==1) pendientes.add(45);
                if (pendientetemp==0) pendientes.add(0);
                if (pendientetemp==-1) pendientes.add(-45);
            }
        }
        if(pendientes.size()==0) return false;

        boolean samePend = true;
        int inicial = pendientes.get(0);
        for (int p: pendientes){
            System.out.println(p + " == " + inicial);
            if (p==inicial) samePend=true;
            else{
                samePend=false;
                break;
            }
        }

        if(samePend){
            System.out.println("Misma pendiente: " + inicial);
            if (checkOrder(inicial)){
                System.out.println("Estan posicionados");
                pendienteok = inicial;
                return true;
            }else{
                System.out.println("NO estan posicionados");
                return false;
            }
        }else{
            System.out.println("Diferente pendiente");
            return false;
        }
    }

    public boolean checkOrder(Integer pendiente){
        boolean positioned = true;
        if (pendiente==0){
            ArrayList<Integer> xposs = new ArrayList<Integer>();
            for(JButton t: selectedButtons){
                xposs.add((int) t.getClientProperty("xpos"));
            }
            if(xposs.size()==0) return false;
            Collections.sort(xposs);
            for (int i = 0; i<xposs.size()-1; i++){
                if(xposs.get(i) == xposs.get(i+1) - 1) positioned=true;
                else{
                    positioned=false;
                    break;
                }
            }
            return positioned;
        }
        if(pendiente==180){
            ArrayList<Integer> yposs = new ArrayList<Integer>();
            for(JButton t: selectedButtons){
                yposs.add((int) t.getClientProperty("ypos"));
            }
            if(yposs.size()==0) return false;
            Collections.sort(yposs);
            for (int i = 0; i<yposs.size()-1; i++){
                if(yposs.get(i) == yposs.get(i+1) - 1) positioned=true;
                else{
                    positioned=false;
                    break;
                }
            }
            return positioned;
        }
        if(pendiente==45 || pendiente==-45){
            ArrayList<Integer> xposs = new ArrayList<Integer>();
            ArrayList<Integer> yposs = new ArrayList<Integer>();
            for(JButton t: selectedButtons){
                xposs.add((int) t.getClientProperty("xpos"));
                yposs.add((int) t.getClientProperty("ypos"));
            }
            if(yposs.size()==0 || xposs.size()==0) return false;
            Collections.sort(xposs);
            Collections.sort(yposs);
            for (int i = 0; i<yposs.size()-1; i++){
                if(yposs.get(i) == (yposs.get(i+1) - 1) && xposs.get(i) == (xposs.get(i+1) -1)) positioned=true;
                else{
                    positioned=false;
                    break;
                }
            }
            return positioned;
        }
        else{
            return false;
        }
    }

    public void createInfo() throws IOException, ClassNotFoundException {
        info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        categoriespanel = new JPanel();
        titulo = new JLabel("Categoria: ", SwingConstants.CENTER);
        comboCategories.setPreferredSize(new Dimension(180, 30));
        comboCategories.addItem("");
        comboCategories.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String itemSel = (String) comboCategories.getSelectedItem();
                try {
                    if(!itemSel.equals(""))
                        restarStyles();
                        changeCategory(itemSel);
                } catch (IOException | ClassNotFoundException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        categoriespanel.add(titulo);
        categoriespanel.add(comboCategories);

        words = new JPanel();
        words.setLayout(new GridLayout(15, 0));
        wordsLabel.clear();
        for(int i=0; i<15; i++){
            wordsLabel.add(new JLabel(""));
            words.add(wordsLabel.get(i), SwingConstants.CENTER);
        }

        controlpanel = new JPanel();
        controlpanel.setLayout(new GridLayout(0, 2));
        start = new JButton("START");
        start.setBackground(Color.BLUE);
        start.setForeground(Color.WHITE);
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Iniciando juego");
                startGame();
            }
        });
        giveup = new JButton("GIVE UP");
        giveup.setBackground(Color.RED);
        giveup.setForeground(Color.WHITE);
        giveup.setEnabled(gamestatus);
        giveup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                endGame();
            }
        });
        controlpanel.add(start);
        controlpanel.add(giveup);

        timePanel = new JPanel();
        time = new JLabel("Time: ");
        timePanel.add(time);

        info.add(categoriespanel);
        info.add(words);
        info.add(controlpanel);
        info.add(timePanel);

        info.setPreferredSize(new Dimension(320, 480));
    }

    public void restarStyles(){
        for(int x=0; x<rows; x++){
            for (int y=0; y<columns; y++){
                buttons[x][y].setBackground(Color.WHITE);
            }
        }

        for(JLabel t: wordsLabel){
            t.setForeground(Color.BLACK);
        }

        time.setText("Time: ");
    }

    public void startGame(){
        gamestatus = true;
        startTime = System.currentTimeMillis();
        giveup.setEnabled(gamestatus);
        start.setEnabled(!gamestatus);
        comboCategories.setEnabled(!gamestatus);
    }

    public void endGame(){
        gamestatus = false;
        endTime = System.currentTimeMillis();
        giveup.setEnabled(gamestatus);
        start.setEnabled(!gamestatus);
        actualWords.clear();
        selectedButtons.clear();
        long totalTime = endTime - startTime;
        long minutes = (totalTime / 1000) / 60;
        comboCategories.setEnabled(!gamestatus);
        // formula for conversion for
        // miliseconds to seconds
        long seconds = (totalTime/ 1000) % 60;
        time.setText("Time: " + minutes + "min " + seconds + "s");
    }

    public void fillCategories() throws IOException, ClassNotFoundException {
        String[] categories = getCategories();
        for (String cat:
             categories) {
            comboCategories.addItem(cat.toUpperCase(Locale.ROOT));
        }
    }

    public void fillTablero(){
        for(int x=0; x<rows; x++){
            for (int y=0; y<columns; y++){
                buttons[x][y].setText(matrix[x][y]);
            }
        }
    }

    public String[] getCategories() throws IOException, ClassNotFoundException {
        String[] categories = null;
        sendMessage("getCategories");
        categories = (String[]) ois.readObject();
        return categories;
    }

    private void changeCategory(String sel) throws IOException, ClassNotFoundException {
        sendMessage("category:" + sel.toLowerCase(Locale.ROOT));
        category = sel.toLowerCase(Locale.ROOT);
        AlphabetSoup temp = (AlphabetSoup) ois.readObject();
        String[][] matrixrec = (String[][]) ois.readObject();
        String linealMat = ois.readUTF();
        System.out.println(temp.getActualWords());
        matrix = temp.getMatrix().clone();
        printMatrix(matrixrec);
        System.out.println(linealMat);
        fillMatrix(linealMat);
        actualWords.clear();
        actualWords.addAll(temp.getActualWords());
        fillTablero();
        displayWords();
    }

    public void fillMatrix(String lineal){
        int cont=0;
        for(int x=0; x<rows; x++){
            for (int y=0; y<columns; y++){
                matrix[x][y] = String.valueOf(lineal.charAt(cont));
                cont++;
            }
        }
    }

    public void printMatrix(String[][] rec){
        for(int x=0; x<rows; x++){
            for (int y=0; y<columns; y++){
                System.out.print(rec[x][y] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void displayWords(){
        System.out.println("Llenando palabras");
        int i = 0;
        for (String word: actualWords) {
            wordsLabel.get(i).setText(word.toUpperCase(Locale.ROOT));
            i++;
        }
    }

    public void sendMessage(String toSend) throws IOException {
        oos.writeUTF(toSend);
        oos.flush();
    }

}
