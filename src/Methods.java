import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;

public class Methods {

    public static List<Carriage> readXML(String filepath) {
        File xmlFile = new File(filepath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        List<Carriage> langList = new ArrayList<>();
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // получаем узлы с именем Carriage
            // теперь XML полностью загружен в память в виде объекта Document
            NodeList nodeList = document.getElementsByTagName("Carriage");

            // создаём из него список объектов Carriage
            for (int i = 0; i < nodeList.getLength(); i++) {
                langList.add(getCarriage(nodeList.item(i)));
            }

        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return langList;
    }


    // создаем из узла документа объект Carriage
    private static Carriage getCarriage(Node node) {
        Carriage lang = new Carriage();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            lang.setName(getTagValue("name", element));
            lang.setNumber(Integer.parseInt(getTagValue("number", element)));
        }
        return lang;
    }

    // получаем значение элемента по указанному тегу
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

    public static void createXML(String filepath, List<Carriage> langList) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();

            // создаем пустой объект Document, в котором будем создавать XML-файл
            Document document = builder.newDocument();
            // создаем корневой элемент
            Element rootElement =
                    document.createElementNS("", "Trains");
            // добавляем корневой элемент в объект Document
            document.appendChild(rootElement);

            //считаем грузоподъёмность и/или число пассажиров
            Map<String, Integer> numberCarriage = new HashMap<>();
            Map<String, Integer> weight = new HashMap<>();
            int numberPassenger = 0;
            int numberCommodity = 0;
            int weightPassenger = 0;
            int weightCommodity = 0;
            for (int i = 0; i < langList.size(); i++) {
                if (langList.get(i).getName().equals("locomotive")) {
                    numberCarriage.put("locomotive", 1);
                    weight.put("locomotive", 0);
                }
                if (langList.get(i).getName().equals("passenger")) {
                    numberPassenger++;
                    weightPassenger = weightPassenger + langList.get(i).getNumber();
                    numberCarriage.put("passenger", numberPassenger);
                    weight.put("passenger", weightPassenger);
                }
                if (langList.get(i).getName().equals("commodity")) {
                    numberCommodity++;
                    weightCommodity = weightCommodity + langList.get(i).getNumber();
                    numberCarriage.put("commodity", numberCommodity);
                    weight.put("commodity", weightCommodity);
                }
            }

            // добавляем элементы в Document
            for (String key : numberCarriage.keySet()) {
                rootElement.appendChild(getCarriage(document, key, String.valueOf(numberCarriage.get(key)), String.valueOf(weight.get(key))));
            }

           //создаем объект TransformerFactory для записи
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            // для красивой записи
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);

            StreamResult file = new StreamResult(new File(filepath));

            //записываем данные
            transformer.transform(source, file);
            System.out.println("XML-файл создан!");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // метод для создания нового узла XML-файла
    private static Node getCarriage(Document doc, String name, String number, String weight) {
        Element train = doc.createElement("Train");

        // создаем элемент name
        train.appendChild(getCarriageElements(doc, "carriage", name));

        // создаем элемент age
        train.appendChild(getCarriageElements(doc, "number", number));

        // создаем элемент weight
        train.appendChild(getCarriageElements(doc, "weight", weight));

        return train;
    }


    // метод для создание нового узла XML-файла
    private static Node getCarriageElements(Document doc, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
}
