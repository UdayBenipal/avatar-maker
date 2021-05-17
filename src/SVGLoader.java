import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SVGLoader {
    public Group loadSVG(String svgFileName){
        Group container = new Group();

        for(String path : getPaths(svgFileName)){
            if(path.contains("translate")){
                Pattern p = Pattern.compile("[-]?[0-9]+[.]?[0-9]*");
                Matcher matcher = p.matcher(path);
                if(matcher.find())
                    container.setTranslateX(Float.parseFloat(matcher.group()));
                if(matcher.find())
                    container.setTranslateY(Float.parseFloat(matcher.group()));
            }
            else {
                SVGPath svgPath = new SVGPath();
                svgPath.setContent(path);
                svgPath.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                container.getChildren().add(svgPath);
            }
        }

        return container;
    }

    private String[] getPaths(String svgFilename){
        ArrayList<String> paths = new ArrayList<>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            InputStream is = getClass().getClassLoader().getResourceAsStream(svgFilename);
            Document document = db.parse(is);
            NodeList nodeList = document.getElementsByTagName("path");
            for(int x=0,size= nodeList.getLength(); x<size; x++) {
                paths.add(nodeList.item(x).getAttributes().getNamedItem("d").getNodeValue().toString());
            }

            nodeList = document.getElementsByTagName("g");
            for(int x=0,size= nodeList.getLength(); x<size; x++) {
                paths.add(nodeList.item(x).getAttributes().getNamedItem("transform").getNodeValue().toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths.toArray(new String[0]);
    }
}
