import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Locale;

public class Tablero extends JFrame {

    private final int port = 9876;
    private final String dir = "localhost";
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private int rows = 16, columns = 16;
    private JLabel titulo;
    private JPanel table, info;
    private String[][] matrix = new String[rows][columns];
    private JButton[][] buttons = new JButton[rows][columns];
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
        matrix = (String[][]) ois.readObject();
        fillTablero();
    }

    public void sendMessage(String toSend) throws IOException {
        oos.writeUTF(toSend);
        oos.flush();
    }

}
