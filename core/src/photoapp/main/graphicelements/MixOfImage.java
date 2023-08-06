package photoapp.main.graphicelements;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.TimeUtils;

import photoapp.main.Main;
import photoapp.main.storage.ImageData;

public class MixOfImage extends Group {
    public static AssetManager manager = new AssetManager();
    public static boolean isLoading = false;
    public static long lastTime = 0;

    // public void mixOfImage() {
    // System.out.println("new HASH MAP ------------------------");
    // imagesData = new HashMap<>();
    // }
    public static void clearImagesTextureData() {
        Main.imagesTextureData = new OrderedMap<>();
    }

    public static void loadImage(String lookingFor) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (Main.infoText == " " || Main.infoText == "done") {
                    Main.infoTextSet("loading .");
                    // System.out.println(".");
                }
                if (Main.infoText == "loading ." && TimeUtils.millis() - lastTime >= 500) {
                    Main.infoTextSet("loading ..");
                    // System.out.println("..");
                    lastTime = TimeUtils.millis();

                } else if (Main.infoText == "loading .." && TimeUtils.millis() - lastTime >= 500) {
                    Main.infoTextSet("loading ...");

                    // System.out.println("...");

                    lastTime = TimeUtils.millis();

                } else if (Main.infoText == "loading ..." && TimeUtils.millis() - lastTime >= 500) {
                    Main.infoTextSet("loading .");

                    lastTime = TimeUtils.millis();
                }

                isLoading = true;
                manager.load(lookingFor, Texture.class);
                // System.out.println(lookingFor + " : looking for ");

            }
        }).start();
        // new Thread(new Runnable() {
        // @Override
        // public void run() {

        // }
        // }).start();

        // System.out.println(lookingFor + " is now loaded");
    }

    public static Texture isInImageData(String lookingFor, boolean wait) {
        Texture texture;

        if (!manager.isLoaded(lookingFor)) {
            // System.out.println("to load : " + lookingFor);
            loadImage(lookingFor);
            // System.out.println("loading ...");
            // System.out.println(lookingFor + " : lookingfor");
            if (lookingFor.startsWith("images/") || wait) {
                // System.out.println("waiting");
                // manager.
                while (!manager.isLoaded(lookingFor)) {
                    manager.update();

                }

            }
            // else {
            // Main.toReloadList = Main.addToList(Main.toReloadList, lookingFor);
            // // System.out.println(Main.toReloadList);
            // }
        }
        if (manager.isLoaded(lookingFor)) {
            // System.out.println("is loaded : " + lookingFor);

            texture = manager.get(lookingFor, Texture.class);
        } else {

            String[] ListImageName = lookingFor.split("/");
            String fileName;
            System.out.println(lookingFor.split("/")[ListImageName.length - 2] + "--------------" + lookingFor);
            if (!lookingFor.split("/")[ListImageName.length - 2].equals("images")
                    && !lookingFor.split("/")[ListImageName.length - 2].equals("peoples")
                    && !lookingFor.split("/")[ListImageName.length - 2].equals("places")
                    && !lookingFor.split("/")[ListImageName.length - 2].equals("150")) {
                // System.out.println("wait");
                fileName = ImageData.IMAGE_PATH + "/150/" + ListImageName[ListImageName.length - 1];
                manager.load(fileName, Texture.class);
                manager.finishLoadingAsset(fileName);
                // return isInImageData(fileName, true);
                // manager.finishLoading();
                manager.update();
            } else {
                fileName = lookingFor;
            }

            if (!manager.isLoaded(fileName)) {
                // System.out.println("not loaded");

                // manager.load(fileName, Texture.class);
                return new Texture("images/error.png");
                // return isInImageData(fileName, false);
            } else {
                return manager.get(fileName, Texture.class);
            }

        }

        // imagesTextureData.put(lookingFor, texture);

        // System.out.println(lookingFor + "----------" + texture);
        // imagesData.keySet(lookingFor).entrySet(texture);
        // System.out.println(imagesData);

        return texture;
    }

    public MixOfImage(List<String> imageNames, boolean isSquare) {

        for (String imageName : imageNames) {
            // System.out.println(ImageData.IMAGE_PATH + imageName + "\n" +
            // Gdx.files.internal(imageName));
            if (!Gdx.files.internal(ImageData.IMAGE_PATH + imageName).exists()
                    && !Gdx.files.internal(imageName).exists()) {
                imageName = "images/error.png";
            }
            // long startTimePlaceImageOfPeoples = TimeUtils.millis();
            Texture texture = isInImageData(imageName, false);
            // long stopTimePlaceImageOfPeoples = TimeUtils.millis();
            // System.out.println(
            // "-----------------" + ImageData.IMAGE_PATH + imageName + "create mix of image
            // done in : ");
            // if (stopTimePlaceImageOfPeoples - startTimePlaceImageOfPeoples >= 50) {
            // System.out.println("TO MUCH LAG !!!");
            // }
            // System.out.println(stopTimePlaceImageOfPeoples -
            // startTimePlaceImageOfPeoples);

            Image image = new Image(texture);
            if (imageName.endsWith("outline.png")) {
                // image.setSize(lastTime, lastTime);
                image.setName("outline");
            } else {
                image.setName("image");
            }

            addActor(image);
        }
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);// super : celle de mon parents ici group
        for (Actor actor : getChildren()) {
            if (actor.getName().equals("image") && width == Main.preferences.getInteger("size of main images button")
                    && height == Main.preferences.getInteger("size of main images button")) {
                actor.setSize(Main.preferences.getInteger("size of main images button") - 10,
                        Main.preferences.getInteger("size of main images button") - 10);
                actor.setPosition(10 / 2, 10 / 2);
                // actor.setPosition(125 / 4, 125 / 4, Align.center);
            } else {
                actor.setSize(width, height);

            }

            // System.out.println(actor.getName());
        }

    }
}