package photoapp.main.windows;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import photoapp.main.CommonFunction;
import photoapp.main.Main;
import photoapp.main.graphicelements.MixOfImage;
import photoapp.main.storage.ImageData;

public class BigPreview {
    static Table BigPreviewTable;
    static public Boolean imageWithGoodQuality = false;

    public static void create() {
        BigPreviewTable = new Table();
        BigPreviewTable.setSize(
                Gdx.graphics.getWidth() - Main.preferences.getInteger("border") * 2,
                Gdx.graphics.getHeight() - Main.preferences.getInteger("border") * 2);
        BigPreviewTable.setPosition(
                Main.preferences.getInteger("border"),
                Main.preferences.getInteger("border"));

    }

    public static void open(String imageName) {
        Main.windowOpen = "BigPreview";
        ImageEdition.bigPreview = true;
        Main.mainStage.addActor(BigPreviewTable);
        clear();

        ImageEdition.theCurrentImagePath = imageName;

        Main.placeImage(List.of(ImageData.IMAGE_PATH + "/" + imageName),
                "big preview",
                new Vector2(0, 0),
                Main.mainStage,
                (o) -> {
                    // System.err.println("click");
                    CommonFunction.back();
                }, null, null, false, true, false, BigPreviewTable, false, "");
        imageWithGoodQuality = false;

    }

    public static void reload() {

    }

    public static void clear() {

        BigPreviewTable.clear();

    }

    public static void load() {

        open(ImageEdition.theCurrentImagePath);

    }

    public static void close() {
        ImageEdition.bigPreview = false;

        clear();
        ImageEdition.open(ImageEdition.theCurrentImagePath, true);

    }

    public static void previousImage(String theCurrentImagePath) {

        ImageData previous = null;
        for (ImageData imageData : Main.imagesData) {
            if ((imageData.getName())
                    .equals(ImageEdition.theCurrentImagePath)) {

                if (previous == null) {
                    open(
                            Main.imagesData.get(Main.imagesData.size() - 1).getName());
                } else {
                    open(previous.getName());
                    Integer i = 4;
                    if (Main.imagesData.indexOf(previous) + 4 >= Main.imagesData.size()) {
                        i = i - Main.imagesData.size();
                    }

                }

            }
            previous = imageData;

        }
        MainImages.imageI = Main.getImageDataIndex(ImageEdition.theCurrentImagePath);

        Main.checkToUnload(null);
    }

    public static void nextImage(String theCurrentImagePath) {

        boolean next = false;
        for (ImageData imageData : Main.imagesData) {
            if ((imageData.getName())
                    .equals(ImageEdition.theCurrentImagePath)) {
                next = true;
            } else if (next) {

                open(imageData.getName());

                Integer i = -4;

                if (Main.imagesData.indexOf(imageData) + i < 0) {
                    i = Main.imagesData.size() + i;
                }

                next = false;
            }
        }
        if (next) {
            open(Main.imagesData.get(0).getName());

        }
        MainImages.imageI = Main.getImageDataIndex(ImageEdition.theCurrentImagePath);
        Main.checkToUnload(null);
    }

    public static void render() {
        if (!imageWithGoodQuality
                && MixOfImage.manager.isLoaded(ImageData.IMAGE_PATH + "/" + ImageEdition.theCurrentImagePath)) {
            clear();
            BigPreview.open(ImageEdition.theCurrentImagePath);
            imageWithGoodQuality = true;
        }
    }

}
