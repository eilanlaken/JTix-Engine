package com.heavybox.jtix.zzz;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.math.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;

public class ToolCastleGenerator extends Tool {

    private static final Array<Combination> combinations = new Array<>(true, 10);
    static {

        Combination a1 = new Combination();

        try {
            File file = new File("assets/app-castles-combinations/combinations.xml");
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
                    MapTokenCastleBlock.BlockType type = MapTokenCastleBlock.BlockType.values()[Integer.parseInt(block.getAttribute("type"))];
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
            System.out.println(e.getMessage());
        }

    }

    public MapTokenCastleBlock lastCreated;

    private static final MapTokenCastleBlock.BlockType[] allTypes = MapTokenCastleBlock.BlockType.values();
    private final TexturePack props;

    //public float scale = 0.25f;
    public float scale = 1;
    public MapTokenCastleBlock.BlockType currentType = allTypes[0]; // TOWER_TALL, TOWER_SHORT, BUILDING_TALL_LEFT, ...
    public int baseIndex = 0;
    public int comboIndex = MathUtils.randomUniformInt(0, combinations.size);
    public TextureRegion region;

    public Mode mode = Mode.SINGLES;

    public ToolCastleGenerator(TexturePack props) {
        this.props = props;
        region = MapTokenCastleBlock.BlockType.getRegion(props, currentType, baseIndex);

        comboIndex = 0;

    }

    public void selectNext() {
        if (mode == Mode.SINGLES) {
            int nextIndex = (currentType.ordinal() + 1) % allTypes.length;
            currentType = allTypes[nextIndex];
            region = MapTokenCastleBlock.BlockType.getRegion(props, currentType, baseIndex);
        } else {
            System.out.println(comboIndex);
            comboIndex++;
            comboIndex %= combinations.size;
        }
    }

    public void toggleMode() {
        if (mode == Mode.SINGLES) mode = Mode.COMBOS;
        else mode = Mode.SINGLES;
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (mode == Mode.SINGLES) {
            float realSclX = scale;
            if (currentType.isRight()) realSclX *= -1;
            renderer2D.setColor(1, 1, 1, 0.5f);
            renderer2D.drawTextureRegion(region, x, y, deg, realSclX, scale);
            renderer2D.setColor(1, 1, 1, 1);
        } else {
            Combination combination = combinations.get(comboIndex);
            BlockUnit[] blocks = combination.blockUnits;
            renderer2D.setColor(1,1,1,0.5f);
            System.out.println(blocks.length);
            for (BlockUnit b : blocks) {
                TextureRegion blockRegion = MapTokenCastleBlock.BlockType.getRegion(props, b.type, 0);
                float worldX = x + b.offsetX;
                float worldY = y + b.offsetY;
                float realSclX = scale;
                if (currentType.isRight()) realSclX *= -1;
                renderer2D.drawTextureRegion(blockRegion, worldX, worldY, deg, realSclX, scale);
            }
            renderer2D.setColor(1,1,1,1);
        }
    }

    private static final class BlockUnit {

        MapTokenCastleBlock.BlockType type;
        float offsetX;
        float offsetY;

    }

    private static final class Combination {

        BlockUnit[] blockUnits;

    }

    public enum Mode {
        SINGLES,
        COMBOS,
        ;
    }

}
