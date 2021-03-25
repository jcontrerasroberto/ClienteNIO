import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class Tablero extends JFrame {

    private final int port = 9876;
    private final String dir = "localhost";
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private int rows = 16, columns = 16;
    private JLabel titulo;
    private JPanel table, info, words;
    private String[][] matrix = new String[rows][columns];
    private JButton[][] buttons = new JButton[rows][columns];
    private ArrayList<String> actualWords = new ArrayList<String>();
    private ArrayList<JLabel> wordsLabel = new ArrayList<JLabel>();
    private JComboBox<String> comboCategories = new JComboBox<String>();
    private String category;

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
                buttons[x][y].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        System.out.println(actionEvent.getActionCommand());
                    }
                });
                table.add(buttons[x][y]);
            }
        }
    }

    public void createInfo() throws IOException, ClassNotFoundException {
        info = new JPanel();
        titulo = new JLabel("Categoria: ", SwingConstants.CENTER);
        comboCategories.setPreferredSize(new Dimension(180, 30));
        comboCategories.addItem("");
        comboCategories.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String itemSel = (String) comboCategories.getSelectedItem();
                try {
                    if(!itemSel.equals(""))
                        changeCategory(itemSel);
                } catch (IOException | ClassNotFoundException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        info.add(titulo);
        info.add(comboCategories);
        words = new JPanel();
        words.setLayout(new GridLayout(15, 0));
        wordsLabel.clear();
        for(int i=0; i<15; i++){
            wordsLabel.add(new JLabel(""));
            words.add(wordsLabel.get(i), SwingConstants.CENTER);
        }

        info.add(words);

        info.setPreferredSize(new Dimension(320, 480));
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
