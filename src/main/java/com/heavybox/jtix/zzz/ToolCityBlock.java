package com.heavybox.jtix.zzz;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.math.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ToolCityBlock extends Tool {

    private static final Array<Combination> combinations = new Array<>(true, 10);
    static {
//        try {
//            File file = new File("assets/app-combinations/castles.xml");
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document doc = builder.parse(file);
//            doc.getDocumentElement().normalize();
//
//            NodeList combinationList = doc.getElementsByTagName("combination");
//            for (int i = 0; i < combinationList.getLength(); i++) {
//                Element combinationElement = (Element) combinationList.item(i);
//                NodeList blocks = combinationElement.getElementsByTagName("object");
//                Combination combination = new Combination();
//                combination.blockUnits = new BlockUnit[blocks.getLength()];
//                for (int j = 0; j < blocks.getLength(); j++) {
//                    Element block = (Element) blocks.item(j);
//                    MapTokenHouseCity2.Direction direction = MapTokenHouseCity2.Direction.values()[Integer.parseInt(block.getAttribute("dir"))];
//                    MapTokenHouseCity2.Size size = MapTokenHouseCity2.Size.values()[Integer.parseInt(block.getAttribute("size"))];
//                    MapTokenHouseCity2.Look look = MapTokenHouseCity2.Look.values()[Integer.parseInt(block.getAttribute("look"))];
//                    float x = Float.parseFloat(block.getAttribute("x"));
//                    float y = Float.parseFloat(block.getAttribute("y"));
//                    combination.blockUnits[j] = new BlockUnit();
//                    combination.blockUnits[j].direction = direction;
//                    combination.blockUnits[j].size = size;
//                    combination.blockUnits[j].look = look;
//                    combination.blockUnits[j].offsetX = x;
//                    combination.blockUnits[j].offsetY = y;
//                }
//                combinations.add(combination);
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }

    }

    public MapToken lastCreated;

    private final TexturePack props;

    public float scale = 0.5f;
    //public float scale = 1;
    public MapTokenHouseCity2.Direction currentDirection = MapTokenHouseCity2.Direction.values()[0];
    public MapTokenHouseCity2.Size currentSize = MapTokenHouseCity2.Size.values()[0];
    public MapTokenHouseCity2.Look currentLook = MapTokenHouseCity2.Look.values()[0];
    public int baseIndex = 0;
    public int comboIndex = MathUtils.randomUniformInt(0, 5);

    public TextureRegion regionFoundation;
    public TextureRegion regionFoundationOverlay;
    public TextureRegion regionRoof;
    public TextureRegion regionRoofOverlay;

    public Mode mode = Mode.SINGLES;

    public ToolCityBlock(TexturePack props) {
        this.props = props;
        updateTextureRegions();
    }

    public void selectNextDirection() {
        int nextIndex = (currentDirection.ordinal() + 1) % MapTokenHouseCity2.Direction.values().length;
        currentDirection = MapTokenHouseCity2.Direction.values()[nextIndex];
        updateTextureRegions();
    }

    public void selectNextSize() {
        int nextIndex = (currentSize.ordinal() + 1) % MapTokenHouseCity2.Size.values().length;
        currentSize = MapTokenHouseCity2.Size.values()[nextIndex];
        updateTextureRegions();
    }

    public void selectNextLook() {
        int nextIndex = (currentLook.ordinal() + 1) % MapTokenHouseCity2.Look.values().length;
        currentLook = MapTokenHouseCity2.Look.values()[nextIndex];
        updateTextureRegions();
    }

    private void updateTextureRegions() {
        regionFoundation = props.getRegion(MapTokenHouseCity2.getRegionFoundation(currentDirection, currentSize));
        regionFoundationOverlay = props.getRegion(MapTokenHouseCity2.getRegionFoundationOverlay(currentDirection, currentSize, 0));
        regionRoof = props.getRegion(MapTokenHouseCity2.getRegionRoof(currentDirection, currentSize, currentLook));
        regionRoofOverlay = props.getRegion(MapTokenHouseCity2.getRegionRoofOverlay(currentDirection, currentSize,0));
    }

    public void toggleMode() {
        if (mode == Mode.SINGLES) mode = Mode.COMBOS;
        else mode = Mode.SINGLES;
    }

    public BlockUnit[] getCombination() {
        Combination combination = combinations.get(comboIndex);
        return combination.blockUnits;
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (mode == Mode.SINGLES) {
            float realSclX = scale;
            if (currentDirection == MapTokenHouseCity2.Direction.RIGHT) realSclX *= -1;
            renderer2D.setColor(1, 1, 1, 0.5f);
            renderer2D.drawTextureRegion(regionRoof, x, y, deg, realSclX, scale);
            renderer2D.drawTextureRegion(regionRoofOverlay, x, y, deg, realSclX, scale);
            renderer2D.drawTextureRegion(regionFoundation, x, y, deg, realSclX, scale);
            renderer2D.drawTextureRegion(regionFoundationOverlay, x, y, deg, realSclX, scale);
            renderer2D.setColor(1, 1, 1, 1);
        } else {
//            Combination combination = combinations.get(comboIndex);
//            BlockUnit[] blocks = combination.blockUnits;
//            renderer2D.setColor(1,1,1,0.5f);
//            for (BlockUnit b : blocks) {
//                TextureRegion blockRegion = MapTokenCastleBlock.BlockType.getRegion(props, b.type, 0);
//                float worldX = x + b.offsetX;
//                float worldY = y + b.offsetY;
//                float realSclX = scale;
//                if (b.type.isRight()) realSclX *= -1;
//                renderer2D.drawTextureRegion(blockRegion, worldX, worldY, deg, realSclX, scale);
//            }
//            renderer2D.setColor(1,1,1,1);
        }
    }

    public static final class BlockUnit {

        MapTokenHouseCity2.Direction direction;
        MapTokenHouseCity2.Size size;
        MapTokenHouseCity2.Look look;
        float offsetX;
        float offsetY;

    }

    public static final class Combination {

        BlockUnit[] blockUnits;

    }

    public enum Mode {
        SINGLES,
        COMBOS,
        ;
    }

}
