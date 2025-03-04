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

import static com.heavybox.jtix.zzz.MapTokenCastleBlock.regionsBuildingTallMiddle;
import static com.heavybox.jtix.zzz.MapTokenCastleBlock.regionsTowerTall;

public class ToolCastleGenerator extends Tool {

    private static final Combination[] combinations = new Combination[100];
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
                Element combination = (Element) combinationList.item(i);
                NodeList blocks = combination.getElementsByTagName("object");
                for (int j = 0; j < blocks.getLength(); j++) {
                    Element block = (Element) blocks.item(j);
                    System.out.println(block.getAttribute("type"));
                }
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

    @Deprecated private final Array<BlockUnit> middleBlocks = new Array<>();
    public int comboIndex = MathUtils.randomUniformInt(0, combinations.length);

    public TextureRegion region;

    public ToolCastleGenerator(TexturePack props) {
        this.props = props;
        region = MapTokenCastleBlock.BlockType.getRegion(props, currentType, baseIndex);



    }

    public void selectNext() {
        int nextIndex = (currentType.ordinal() + 1) % allTypes.length;
        currentType = allTypes[nextIndex];
        region = MapTokenCastleBlock.BlockType.getRegion(props, currentType, baseIndex);
        System.out.println(currentType);
    }

    public void regenerate() {
        comboIndex++;
        comboIndex %= combinations.length;
        // generate back

        // generate middle
        middleBlocks.clear();
        BlockUnit blockUnit = new BlockUnit();
        blockUnit.type = MapTokenCastleBlock.BlockType.TOWER_TALL;
        blockUnit.index = MathUtils.randomUniformInt(0, regionsTowerTall.length);
        blockUnit.region = props.getRegion(regionsTowerTall[blockUnit.index]);
        blockUnit.offsetX = 0;
        blockUnit.offsetY = 0;
        middleBlocks.add(blockUnit);

        BlockUnit blockUnit2 = new BlockUnit();
        blockUnit2.type = MapTokenCastleBlock.BlockType.BUILDING_TALL_MIDDLE;
        blockUnit2.index = MathUtils.randomUniformInt(0, regionsBuildingTallMiddle.length);
        blockUnit2.region = props.getRegion(regionsBuildingTallMiddle[blockUnit2.index]);
        blockUnit2.offsetX = blockUnit.region.packedWidthHalf;
        blockUnit2.offsetY = 0;
        middleBlocks.add(blockUnit2);

        // generate front
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        float realSclX = scale;
        if (currentType.isRight()) realSclX *= -1;
        renderer2D.drawTextureRegion(region, x, y, deg, realSclX, scale);

        if (true) return;
        // TODO this is the real one.
        // render the current selected combination layout + species.
        renderer2D.setColor(1,1,1,0.5f);
        // render back walls
        // render middle
        for (BlockUnit unit : middleBlocks) {
            renderer2D.drawTextureRegion(unit.region, x + unit.offsetX, y + unit.offsetY, 0, scale, scale);
        }
        // render front
        renderer2D.setColor(1,1,1,1);
    }

    private static final class BlockUnit {

        MapTokenCastleBlock.BlockType type;
        TextureRegion region;
        int index;
        float offsetX;
        float offsetY;

    }

    private static final class Combination {

        BlockUnit[] blockUnits;

    }

}
