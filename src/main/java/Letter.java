import java.util.Comparator;

public class Letter{

    private Integer xpos;
    private Integer ypos;
    private String value;

    public static Comparator<Letter> xposOrder = new Comparator<Letter>() {

        public int compare(Letter l1, Letter l2) {

            int xpos1 = l1.getXpos();
            int xpos2 = l2.getXpos();

            /*For ascending order*/
            return xpos1-xpos2;

        }};

    public static Comparator<Letter> yposOrder = new Comparator<Letter>() {

        public int compare(Letter l1, Letter l2) {

            int ypos1 = l1.getYpos();
            int ypos2 = l2.getYpos();

            /*For ascending order*/
            return ypos1-ypos2;

        }};


    public Integer getXpos() {
        return xpos;
    }

    public void setXpos(Integer xpos) {
        this.xpos = xpos;
    }

    public Integer getYpos() {
        return ypos;
    }

    public void setYpos(Integer ypos) {
        this.ypos = ypos;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Letter(Integer xpos, Integer ypos, String value) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.value = value;
    }
}
