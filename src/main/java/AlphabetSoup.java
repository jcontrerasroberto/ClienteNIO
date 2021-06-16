import java.io.Serializable;
import java.util.ArrayList;

public class AlphabetSoup implements Serializable {

    private int rows = 16, columns = 16;
    private String[][] matrix = new String[rows][columns];
    private ArrayList<String> actualWords = new ArrayList<String>();
    private String category;
    private String linealMat;

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public String[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(String[][] matrix) {
        this.matrix = matrix;
    }

    public ArrayList<String> getActualWords() {
        return actualWords;
    }

    public void setActualWords(ArrayList<String> actualWords) {
        this.actualWords = actualWords;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLinealMat() {
        return linealMat;
    }

    public void setLinealMat(String linealMat) {
        this.linealMat = linealMat;
    }
}