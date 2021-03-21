import java.io.*;
import java.net.Socket;

public class ClientSopaLetras {



    public ClientSopaLetras() throws IOException, ClassNotFoundException {



        /*categories = (String[]) ois.readObject();
        for (String category :
                categories) {
            System.out.println(category);
        }*/
    }

    /*public String[] getCategories() throws IOException, ClassNotFoundException {
        String[] categories = null;
        sendMessage("getCategories");
        categories = (String[]) ois.readObject();
        return categories;
    }

    public void sendMessage(String toSend) throws IOException {
        oos.writeUTF(toSend);
        oos.flush();
    }*/

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Tablero();
    }
}
