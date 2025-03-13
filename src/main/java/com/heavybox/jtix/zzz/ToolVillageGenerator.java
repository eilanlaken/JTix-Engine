package com.heavybox.jtix.zzz;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ToolVillageGenerator extends Tool {

    private static final Array<Combination> combinations = new Array<>(true, 10);
    static {
        try {
            File file = new File("assets/app-combinations/castles.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList combinationList = doc.getElementsByTagName("combination");
            for (int i = 0; i < combinationList.getLength(); i++) {
                Element combinationElement = (Element) combinationList.item(i);
                NodeList blocks = combinationElement.getElementsByTagName("object");
                Combination combination = new Combination();
                combination.blockUnits = new BlockUnit[blocks.getLength()];
                for (int j = 0; j < blocks.getLength(); j++) {
                    Element block = (Element) blocks.item(j);
                    MapTokenHouseVillage.HouseType type = MapTokenHouseVillage.HouseType.values()[Integer.parseInt(block.getAttribute("type"))];
                    float x = Float.parseFloat(block.getAttribute("x"));
                    float y = Float.parseFloat(block.getAttribute("y"));
                    combination.blockUnits[j] = new BlockUnit();
                    combination.blockUnits[j].type = type;
                    combination.blockUnits[j].offsetX = x;
                    combination.blockUnits[j].offsetY = y;
                }
                combinations.add(combination);
            }
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }

    }

    public MapToken lastCreated;

    private static final MapTokenHouseVillage.HouseType[] allTypes = MapTokenHouseVillage.HouseType.values();
    private final TexturePack props;

    public float scale = 0.5f;
    //public float scale = 1;
    public MapTokenHouseVillage.HouseType currentHouseType = allTypes[0];
    public int currentPropType = 0;
    public int baseIndex = 0;
    public int comboIndex = 0;//MathUtils.randomUniformInt(0, combinations.size);
    public TextureRegion region;

    public Mode mode = Mode.SINGLES_HOUSES;

    public ToolVillageGenerator(TexturePack props) {
        this.props = props;
        region = MapTokenHouseVillage.HouseType.getRegion(props, currentHouseType, baseIndex);

        comboIndex = 0;

    }

    public void selectNext() {
        if (mode == Mode.SINGLES_HOUSES) {
            int nextIndex = (currentHouseType.ordinal() + 1) % allTypes.length;
            currentHouseType = allTypes[nextIndex];
            region = MapTokenHouseVillage.HouseType.getRegion(props, currentHouseType, baseIndex);
        } else if (mode == Mode.SINGLES_PROPS) {
            currentPropType++;
            region = props.getRegion(MapTokenPropVillage.regions[currentPropType % MapTokenPropVillage.regions.length]);
        } else {
            comboIndex++;
            comboIndex %= combinations.size;
        }
    }

    public void toggleMode() {
        if (mode == Mode.SINGLES_HOUSES) {
            mode = Mode.SINGLES_PROPS;
            region = props.getRegion(MapTokenPropVillage.regions[currentPropType % MapTokenPropVillage.regions.length]);
        } else if (mode == Mode.SINGLES_PROPS) {
            mode = Mode.SINGLES_HOUSES;
            region = MapTokenHouseVillage.HouseType.getRegion(props, currentHouseType, baseIndex);
        }


        if (true) return; // TODO: this should be the one.
        if (mode == Mode.SINGLES_HOUSES) mode = Mode.SINGLES_PROPS;
        else if (mode == Mode.SINGLES_PROPS) mode = Mode.COMBOS;
        else mode = Mode.SINGLES_HOUSES;
    }

    public BlockUnit[] getCombination() {
        Combination combination = combinations.get(comboIndex);
        return combination.blockUnits;
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (mode == Mode.SINGLES_HOUSES) {
            float realSclX = scale;
            if (currentHouseType.isRight()) realSclX *= -1;
            renderer2D.setColor(1, 1, 1, 0.5f);
            renderer2D.drawTextureRegion(region, x, y, deg, realSclX, scale);
            renderer2D.setColor(1, 1, 1, 1);
        } else if (mode == Mode.SINGLES_PROPS) {
            renderer2D.setColor(1, 1, 1, 0.5f);
            renderer2D.drawTextureRegion(region, x, y, deg, scale, scale);
            renderer2D.setColor(1, 1, 1, 1);
        } else {
            Combination combination = combinations.get(comboIndex);
            BlockUnit[] blocks = combination.blockUnits;
            renderer2D.setColor(1,1,1,0.5f);
            for (BlockUnit b : blocks) {
                TextureRegion blockRegion = MapTokenHouseVillage.HouseType.getRegion(props, b.type, 0);
                float worldX = x + b.offsetX;
                float worldY = y + b.offsetY;
                float realSclX = scale;
                if (b.type.isRight()) realSclX *= -1;
                renderer2D.drawTextureRegion(blockRegion, worldX, worldY, deg, realSclX, scale);
            }
            renderer2D.setColor(1,1,1,1);
        }
    }

    public static final class BlockUnit {

        MapTokenHouseVillage.HouseType type;
        float offsetX;
        float offsetY;

    }

    public static final class Combination {

        BlockUnit[] blockUnits;

    }

    public enum Mode {
        SINGLES_HOUSES,
        SINGLES_PROPS,
        COMBOS,
        ;
    }

}
