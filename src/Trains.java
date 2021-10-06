import java.util.List;

public class Trains {

    public static void main(String[] args) {

        String filepathFirst = "railway_depot.xml";
        String filepathSecond = "train_info.xml";
        List<Carriage> langList = Methods.readXML(filepathFirst);
        Methods.createXML(filepathSecond, langList);
    }
}