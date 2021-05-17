import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;

class Model {
    ArrayList<Integer> currFeatures;
    ArrayList<String> allFeatures;
    ArrayList<Boolean> selected;
    public double brow_offset;
    public double eye_scale;
    public Paint hairColor;
    public Paint bgColor;

    Model() {
        hairColor = Color.BLACK;
        bgColor = Color.WHITE;

        brow_offset = 0;
        eye_scale = 1;
        currFeatures = new ArrayList<>();
        allFeatures = new ArrayList<>();
        selected = new ArrayList<>();

        allFeatures.add("./resources/skin/skin_lighter.png");//0
        allFeatures.add("./resources/skin/skin_light.png");//1
        allFeatures.add("./resources/skin/skin_brown.png");//2

        allFeatures.add("./resources/brows/brows_angry.png");//3
        allFeatures.add("./resources/brows/brows_default.png");//4
        allFeatures.add("./resources/brows/brows_sad.png");//5

        allFeatures.add("./resources/eyes/eyes_closed.png");//6
        allFeatures.add("./resources/eyes/eyes_default.png");//7
        allFeatures.add("./resources/eyes/eyes_wide.png");//8

        allFeatures.add("./resources/mouth/mouth_default.png");//9
        allFeatures.add("./resources/mouth/mouth_sad.png");//10
        allFeatures.add("./resources/mouth/mouth_serious.png");//11

        allFeatures.add("./resources/hair/hair_short.svg");//12
        allFeatures.add("./resources/hair/hair_wavy.svg");//13
        allFeatures.add("./resources/hair/hair_curly.svg");//14
        allFeatures.add("./resources/hair/hair_long.svg");//15

        allFeatures.add("./resources/nose_default.png");//16
        allFeatures.add("./resources/clothes.svg");//17

        currFeatures.add(-1);//background,0
        currFeatures.add(1);//skin,1
        currFeatures.add(4);//brows,2
        currFeatures.add(7);//eyes,3
        currFeatures.add(9);//mouth,4
        currFeatures.add(16);//nose,5
        currFeatures.add(13);//hair,6
        currFeatures.add(17);//clothes,7

        for(int i = 0; i < 12; ++i) {
            selected.add(false);
        }
    }

    public void updateFeature(int feature, int i) {
        int pos = ((feature-1)*3)+i;
        if(6==feature) pos-=3;
//        System.out.println(feature);
//        System.out.println(pos);
        currFeatures.set(feature, pos);
    }

    public String getFeature(int feature) {
        return allFeatures.get(currFeatures.get(feature));
    }

    public void Selected(int index) {
        for(int i = 0; i < 12; ++i) {
            selected.set(i, (index==i));
        }
    }

    public int theSelected() {
        int i = 0;
        for(boolean b : selected) {
            if(b) break;
            i+=1;
        }
        return i;
    }
}
