package photoapp.main.windows.LoadImage;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;
import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import photoapp.main.Main;
import photoapp.main.graphicelements.MixOfImage;
import photoapp.main.storage.ImageData;
import photoapp.main.windows.ImageEdition.ImageEdition;
import photoapp.main.windows.MainImages.MainImages;

public class LoadImage {
    public static Thread thread = null;
    public static Integer numberOfLoadedImages = 0;
    public static Integer numberOfImagesExif = 0;
    public static Integer numberOfImagesLoaded = 0;
    public static Integer numberOfImagesLoadedForTIme = 0;

    public static Integer numberOfImagesToLoad = 0;

    public static Float progress = (float) 0;
    public static Float lastProgress = (float) 0;

    public static ArrayList<String> toLoad = new ArrayList<String>();
    public static ArrayList<String> toRemove = new ArrayList<String>();
    public static ArrayList<String> isLoading = new ArrayList<String>();

    public static Long time = (long) 0;

    public static void open() {
        Main.windowOpen = "LoadImage";

        Main.unLoadAll();
        numberOfImagesExif = 0;
        openFile();
    }

    public static void reload() {
    }

    public static void clear() {
    }

    public static void render() {

        if (numberOfLoadedImages != 0 && numberOfImagesToLoad.equals(numberOfLoadedImages)) {
            numberOfLoadedImages = 0;
            // time = TimeUtils.millis();

            Main.infoText = "All images imported";
            MixOfImage.LoadingList.clear();

            for (String imagePath : toLoad) {
                String[] nameList = imagePath.split("/");
                String imageName = nameList[nameList.length - 1];

                openImageExif(imageName, nameList[nameList.length - 2]);
                numberOfImagesExif += 1;
                Main.infoText = "exporting data of image : " + numberOfImagesExif + "/" + numberOfImagesToLoad
                        + "(if you import a lot of image it will lag, but just wait)";
            }
            ImageData.sortImageData(Main.imagesData);
            ImageData.saveImagesData();

        } else if (numberOfImagesExif != 0 && numberOfImagesExif > 0) {
            if (numberOfImagesExif.equals(numberOfImagesToLoad)) {
                Main.infoText = "All datas imported";

                numberOfImagesExif = -1;
                for (String imagePath : toLoad) {
                    String[] ListImageName = imagePath.split("/");
                    String fileName150 = ImageData.IMAGE_PATH + "/150/" + ListImageName[ListImageName.length - 1];
                    String fileName100 = ImageData.IMAGE_PATH + "/100/" + ListImageName[ListImageName.length - 1];
                    String fileName10 = ImageData.IMAGE_PATH + "/10/" + ListImageName[ListImageName.length - 1];

                    FileHandle fileHandle150 = new FileHandle(fileName150);
                    FileHandle fileHandle100 = new FileHandle(fileName100);
                    FileHandle fileHandle10 = new FileHandle(fileName10);

                    if (fileHandle150.exists() && fileHandle100.exists() && fileHandle10.exists()) {
                        toRemove.add(imagePath);
                        numberOfImagesLoaded += 1;
                    }

                }

                for (String imagePath : toRemove) {
                    toLoad.remove(imagePath);
                    // if (MixOfImage.manager.isLoaded(imagePath)) {
                    // MixOfImage.manager.unload(imagePath);
                    // }
                }

                for (String imagePath : toLoad) {

                    String[] nameList = imagePath.split("/");
                    String imageName = nameList[nameList.length - 1];
                    // TODO change because with a lot of image it crash (freeze)
                    FileHandle from = Gdx.files.absolute(imagePath);
                    byte[] data = from.readBytes();
                    FileHandle to = Gdx.files.absolute(ImageData.IMAGE_PATH + "/" + imageName);
                    if (!to.exists()) {
                        to.writeBytes(data, false);
                    }
                    FileHandle fromJson = Gdx.files.absolute(imagePath + ".json");
                    if (fromJson.exists()) {
                        byte[] dataJson = fromJson.readBytes();

                        FileHandle toJson = Gdx.files.absolute(ImageData.IMAGE_PATH + "/" + imageName + ".json");
                        toJson.writeBytes(dataJson, false);
                    }
                }
                if (numberOfImagesLoaded != 0) {

                    Main.infoText = "Creating images : " + numberOfImagesLoaded + "/" + numberOfImagesToLoad
                            + " sould be finish in : "
                            + (((TimeUtils.millis() - time) / numberOfImagesLoaded)
                                    * (numberOfImagesToLoad - numberOfImagesLoaded)) / 1000 / 60
                            + "min";
                }
                toRemove.clear();

            }

        } else if (numberOfImagesExif == -1) {
            progress = MixOfImage.manager.getProgress();
            if (progress != lastProgress) {
                lastProgress = progress;
                Integer max = Main.graphic.getInteger("image load at the same time");
                if (toLoad.size() < Main.graphic.getInteger("image load at the same time")) {
                    max = toLoad.size();
                }
                for (int i = 0; i < max; i++) {

                    if (isLoading.contains(toLoad.get(i)) && MixOfImage.manager.isLoaded(toLoad.get(i))) {
                        String imageName = Main.departurePathAndImageNameAndFolder(toLoad.get(i)).get(1);
                        String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
                        imageName = imageName.replaceAll(characterFilter, "");
                        // Main.departurePathAndImageNameAndFolder(toLoad.get(i));
                        MixOfImage.createAnImage(Main.departurePathAndImageNameAndFolder(toLoad.get(i)).get(0) + "/"
                                + Main.departurePathAndImageNameAndFolder(toLoad.get(i)).get(2),
                                ImageData.IMAGE_PATH + "/" + 150,
                                Main.departurePathAndImageNameAndFolder(toLoad.get(i)).get(1),
                                imageName,
                                150, false, false);
                        MixOfImage.createAnImage(Main.departurePathAndImageNameAndFolder(toLoad.get(i)).get(0) + "/"
                                + Main.departurePathAndImageNameAndFolder(toLoad.get(i)).get(2),
                                ImageData.IMAGE_PATH + "/" + 100,
                                Main.departurePathAndImageNameAndFolder(toLoad.get(i)).get(1),
                                imageName,
                                100, true, false);
                        MixOfImage.createAnImage(Main.departurePathAndImageNameAndFolder(toLoad.get(i)).get(0) + "/"
                                + Main.departurePathAndImageNameAndFolder(toLoad.get(i)).get(2),
                                ImageData.IMAGE_PATH + "/" + 10,
                                Main.departurePathAndImageNameAndFolder(toLoad.get(i)).get(1),
                                imageName,
                                10, true, false);

                        // setSizeAfterLoad(toLoad.get(i), 150, false);
                        // setSizeAfterLoad(toLoad.get(i), 100, true);
                        // setSizeAfterLoad(toLoad.get(i), 10, true);

                        toRemove.add(toLoad.get(i));
                        numberOfImagesLoaded += 1;
                        numberOfImagesLoadedForTIme += 1;

                    } else if (!isLoading.contains(toLoad.get(i))) {

                        MixOfImage.loadImage(toLoad.get(i), true, false);
                        isLoading.add(toLoad.get(i));
                    }
                }

                for (String imagePath : toRemove) {
                    if (MixOfImage.manager.isLoaded(imagePath)) {
                        MixOfImage.manager.unload(imagePath);
                    }
                    toLoad.remove(imagePath);
                }
                toRemove.clear();

                if (numberOfImagesLoadedForTIme != 0) {

                    Main.infoText = "Creating images : " + numberOfImagesLoaded + "/" + numberOfImagesToLoad
                            + " sould be finish in : "
                            + (((TimeUtils.millis() - time) / numberOfImagesLoadedForTIme)
                                    * (numberOfImagesToLoad - numberOfImagesLoadedForTIme)) / 1000 / 60
                            + "min";
                }
            }
            if (numberOfImagesLoaded.equals(numberOfImagesToLoad) && numberOfImagesLoaded != 0) {
                Main.infoText = "All is loaded opening Main images";
                LoadImage.clear();
                MainImages.open();

            }

        }

    }

    public static void openFile() {
        numberOfImagesToLoad = 0;

        Main.openFile(JFileChooser.FILES_AND_DIRECTORIES,
                (fileRessource) -> {
                    if (Main.isAnImage(fileRessource.toString())) {
                        numberOfImagesToLoad = 1;
                        time = TimeUtils.millis();
                        openImageInAFile((File) fileRessource);
                    } else {
                        time = TimeUtils.millis();
                        countImageToLoad((File) fileRessource);
                        openImageOfAFile((File) fileRessource);

                    }
                }, (fileRessource) -> {
                    // f.dispose();
                    clear();
                    Main.windowOpen = "MainImages";
                    Main.openWindow = true;
                    return;
                });
    }

    private static void countImageToLoad(File fileRessource) {
        File[] liste = fileRessource.listFiles();
        if (liste != null) {
            for (File item : liste) {

                if (item.isFile()) {
                    if (Main.isAnImage(item.getName())) {
                        numberOfImagesToLoad += 1;
                    }
                } else if (item.isDirectory()) {
                    countImageToLoad(item);
                }
            }
        }
    }

    protected static String normalizeUnicode(String str) {
        Normalizer.Form form = Normalizer.Form.NFD;
        if (!Normalizer.isNormalized(str, form)) {
            return Normalizer.normalize(str, form);
        }
        return str;
    }

    public static void openImageOfAFile(File dir) {

        File[] liste = dir.listFiles();
        if (liste != null) {
            for (File item : liste) {
                if (item.isFile()) {
                    Main.infoText = "import of images : " + numberOfLoadedImages
                            + "/" + numberOfImagesToLoad;
                    String itemName = Main.departurePathAndImageNameAndFolder(item.getPath().replace("\\", "/")).get(1);
                    if (Main.isAnImage(itemName)) {

                        toLoad.add((dir + "/" + itemName).replace("\\", "/"));
                        numberOfLoadedImages += 1;
                    }
                } else if (item.isDirectory()) {
                    openImageOfAFile(item);
                }
            }
        }

    }

    public static void openImageInAFile(File dir) {
        FileHandle from = Gdx.files.absolute(dir.toString());
        byte[] data = from.readBytes();

        FileHandle to = Gdx.files.absolute(ImageData.IMAGE_PATH + "/" + dir.getName());
        to.writeBytes(data, false);

        FileHandle fromJson = Gdx.files.absolute(dir.toString() + ".json");
        if (fromJson.exists()) {
            byte[] dataJson = fromJson.readBytes();

            FileHandle toJson = Gdx.files.absolute(ImageData.IMAGE_PATH + "/" + dir.getName() + ".json");
            toJson.writeBytes(dataJson, false);
        }
        openImageExif(dir.getName(), dir.getParentFile().toString());
        MixOfImage.createAnImage(dir.getPath(), dir.getPath() + "/150", dir.getName(), dir.getName(), 150, false,
                false);
        MixOfImage.createAnImage(dir.getPath(), dir.getPath() + "/100", dir.getName(), dir.getName(), 100, true,
                false);
        MixOfImage.createAnImage(dir.getPath(), dir.getPath() + "/10", dir.getName(), dir.getName(), 10, true, false);

        MixOfImage.manager.finishLoading();
        clear();
        Main.windowOpen = "MainImages";
        Main.openWindow = true;
        return;

    }

    public static void createAllSize(String imageName) {
        MixOfImage.createAnImage(ImageData.IMAGE_PATH, ImageData.IMAGE_PATH + "/150", imageName, imageName, 150, false,
                false);
        System.out.println(3);

        MixOfImage.createAnImage(ImageData.IMAGE_PATH, ImageData.IMAGE_PATH + "/100", imageName, imageName, 100, true,
                false);
        MixOfImage.createAnImage(ImageData.IMAGE_PATH, ImageData.IMAGE_PATH + "/10", imageName, imageName, 10, true,
                false);
    }

    public static boolean needToBeCreated(String imageName) {
        FileHandle f10 = new FileHandle(ImageData.IMAGE_PATH + "/10/" + imageName);
        FileHandle f100 = new FileHandle(ImageData.IMAGE_PATH + "/100/" + imageName);
        FileHandle f150 = new FileHandle(ImageData.IMAGE_PATH + "/150/" + imageName);
        if (f10.exists() && f100.exists() && f150.exists()) {
            return true;
        }
        return false;

    }

    public static void exportImages(@Nullable String fileName) {
        ImageData.openDataOfImages(fileName);

        Main.openFile(JFileChooser.DIRECTORIES_ONLY,
                (fileRessource) -> {
                    for (ImageData imageData : Main.imagesData) {
                        String imageName = imageData.getName();

                        FileHandle from = Gdx.files.absolute(ImageData.IMAGE_PATH + "/" + imageName);
                        byte[] data = from.readBytes();

                        FileHandle to = Gdx.files.absolute(fileRessource + "/" + imageName);
                        to.writeBytes(data, false);
                        Main.changeDate(ImageData.IMAGE_PATH + "/" + imageName, fileRessource + "/" + imageName);
                    }
                    Main.infoText = "export done";
                }, null);
    }

    public static void openImageExif(String imageName, String directory) {
        FileHandle file;
        try {
            file = Gdx.files.absolute(ImageData.IMAGE_PATH + "/" + imageName);
            FileHandle fileJson = Gdx.files.absolute(ImageData.IMAGE_PATH + "/" + imageName + ".json");
            ImageData imageData = ImageData.getImageDataIfExist(imageName);

            String coords = "";
            Integer rotation = 0;
            if (fileJson.exists()) {
                JsonValue root = new JsonReader().parse(fileJson);
                Boolean favorited = false;
                if (root.has("favorited")) {
                    favorited = root.getBoolean("favorited");
                }
                JsonValue photoTakenTime = root.get("photoTakenTime");
                Long photoTakenTime_timestamp = photoTakenTime.getLong("timestamp");
                JsonValue people = root.get("people");
                ArrayList<String> peoplesNames = new ArrayList<String>();
                if (people != null) {

                    people.forEach((child) -> {
                        peoplesNames.add(child.getString("name"));
                    });
                }
                JsonValue geoData = root.get("geoData");
                Float geoData_latitude = geoData.getFloat("latitude");
                Float geoData_longitude = geoData.getFloat("longitude");
                @SuppressWarnings("unused")
                Float geoData_altitude = geoData.getFloat("altitude");
                @SuppressWarnings("unused")
                Float geoData_latitudeSpan = geoData.getFloat("latitudeSpan");
                @SuppressWarnings("unused")
                Float geoData_longitudeSpan = geoData.getFloat("longitudeSpan");

                JsonValue geoDataExif = root.get("geoDataExif");
                Float geoDataExif_latitude = geoDataExif.getFloat("latitude");
                Float geoDataExif_longitude = geoDataExif.getFloat("longitude");
                @SuppressWarnings("unused")
                Float geoDataExif_altitude = geoDataExif.getFloat("altitude");
                @SuppressWarnings("unused")
                Float geoDataExif_latitudeSpan = geoDataExif.getFloat("latitudeSpan");
                @SuppressWarnings("unused")
                Float geoDataExif_longitudeSpan = geoDataExif.getFloat("longitudeSpan");

                if (geoData_latitude != 0.0 && geoData_longitude != 0.0) {
                    float[] listCoord = { geoData_latitude, geoData_longitude };
                    coords = processCoordinates(listCoord);
                } else if (geoDataExif_latitude != 0.0 && geoDataExif_longitude != 0.0) {
                    float[] listCoord = { geoDataExif_latitude, geoDataExif_longitude };
                    coords = processCoordinates(listCoord);

                }

                if (favorited) {
                    imageData.setLoved(true);
                }
                if (photoTakenTime_timestamp != null) {
                    imageData.setDate(Main.timestampToDate(photoTakenTime_timestamp));
                }
                if (!peoplesNames.isEmpty()) {
                    imageData.setPeoples(peoplesNames);
                }
                for (String p : peoplesNames) {
                    String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
                    p = p.replaceAll(characterFilter, "");
                    if (!Main.peopleData.containsKey(p)) {

                        Main.peopleData.put(p, 0);

                        File from = new File("images/no image people.png");

                        ImageEdition.movePeople(from, p, false);

                    }

                }

            }
            Metadata metadata = null;
            if (file.read() != null) {
                metadata = ImageMetadataReader.readMetadata(file.read());

            }
            if (metadata != null) {

                for (Directory dir : metadata.getDirectories()) {

                    if (dir != null && dir.getName() != null && dir.getName().equals("Exif SubIFD")) {
                        for (Tag tag : dir.getTags()) {
                            if (tag.getTagName().equals("Date/Time Original")) {
                                imageData.setDate(tag.getDescription());

                            }
                        }
                    } else if (dir != null && dir.getName() != null && dir.getName().equals("GPS")) {
                        String lat = "";
                        String lon = "";
                        String minusLat = "";
                        String minusLon = "";

                        for (Tag tag : dir.getTags()) {
                            if (tag.getTagName().equals("GPS Latitude")) {
                                lat = tag.getDescription();

                            } else if (tag.getTagName().equals("GPS Longitude")) {
                                lon = tag.getDescription();
                            } else if (tag.getTagName().equals("GPS Latitude Ref")) {

                                minusLat = tag.getDescription();

                            } else if (tag.getTagName().equals("GPS Longitude Ref")) {
                                minusLon = tag.getDescription();

                            }
                        }
                        if (lat == "" && lon == "" && minusLat == "" && minusLon == "") {
                            coords = "";
                        } else {
                            coords = lat + "_" + minusLat + ":" + lon + "_" + minusLon;
                        }
                    } else if (dir != null && dir.getName() != null && dir.getName().equals("Exif IFD0")) {
                        for (Tag tag : dir.getTags()) {
                            if (tag.getTagName().equals("Orientation")) {
                                if (tag.getDescription().contains("180")) {
                                    rotation = 180;
                                } else if (tag.getDescription().contains("270")) {
                                    rotation = 90;
                                } else if (tag.getDescription().contains("90")) {
                                    rotation = 270;
                                }
                            }
                        }

                    }
                }
            }

            if (imageData.getRotation() == 0) {
                imageData.setRotation(rotation);
            }
            if (imageData.getCoords() == null) {

                imageData.setCoords(coords);
            }

            if (imageData.getName() == "") {
                imageData.setName(imageName);
            }
            if (imageData.getLoved() == null || imageData.getLoved() == false) {
                imageData.setLoved(false);
            }
            if (imageData.getFiles() != null) {
                List<String> newList = new ArrayList<String>();

                for (String inList : imageData.getFiles()) {
                    newList.add(inList);
                }
                if (!newList.contains(directory)) {
                    newList.add(directory);
                }
                imageData.setFiles(newList);
            } else {
                imageData.setFiles(List.of(directory));
            }
            if (!Main.fileData.containsKey(directory)) {
                Main.fileData.put(directory, 0);
            }
            addImageData(imageData);

        } catch (Exception e) {
            Main.error("openImageExif", e);
            // for (StackTraceElement trace : e.getStackTrace()) {
            // Gdx.app.error("openImageExif", trace.toString());
            // }
            // Main.error("openImageExif", e);

        }
    }

    public static void addImageData(ImageData imgd) {
        if (Main.imagesData == null) {
            Main.imagesData = new ArrayList<>();
        }
        if (Main.imagesData != null && imgd != null && !Main.imagesData.contains(imgd)) {
            Main.imagesData.add(imgd);
        }
    }

    public static void setSize(String departurePath, String imageName, Integer size, String type, Boolean force,
            boolean isSquare) {
        // String[] ImageSplit = imagePath.split("/");
        FileHandle handlebis = Gdx.files.absolute(ImageData.IMAGE_PATH + "/" + size + "/" + imageName);

        if (!handlebis.exists()) {
            if (force) {
                // MixOfImage.isInImageData(imagePath, false, "force");
                MixOfImage.isInImageData(imageName,
                        true,
                        false, isSquare);
            } else {
                // MixOfImage.isInImageData(imagePath, false, "firstloading");
                MixOfImage.isInImageData(imageName,
                        false,
                        true, isSquare);
            }

        } else {
            numberOfLoadedImages += 1;
        }
    }

    private static String processCoordinates(float[] coordinates) {
        String[] ORIENTATIONS = "N/S/E/W".split("/");
        String converted0 = decimalToDMS(coordinates[1]);

        final String dmsLat = coordinates[0] > 0 ? ORIENTATIONS[0] : ORIENTATIONS[1];
        converted0 = converted0.concat("_").concat(dmsLat);

        String converted1 = decimalToDMS(coordinates[0]);
        final String dmsLng = coordinates[1] > 0 ? ORIENTATIONS[2] : ORIENTATIONS[3];
        converted1 = converted1.concat("_").concat(dmsLng);

        return converted0.concat(":").concat(converted1);
    }

    public static Pixmap textureToPixmap(Texture in) {
        if (!in.getTextureData().isPrepared()) {
            in.getTextureData().prepare();
        }
        return in.getTextureData().consumePixmap();
    }

    public static Pixmap resize(Pixmap inPm, int outWidth, int outheight, boolean cut) {
        Pixmap outPm = new Pixmap(outWidth, outheight, Pixmap.Format.RGBA8888);
        int srcWidth;
        int srcHeigth;
        int srcx = 0;
        int srcy = 0;
        if (cut) {
            int size = Math.min(inPm.getWidth(), inPm.getHeight());

            srcWidth = size;
            srcHeigth = size;
            srcx = (inPm.getWidth() - size) / 2;
            srcy = (inPm.getHeight() - size) / 2;

        } else {
            srcWidth = inPm.getWidth();
            srcHeigth = inPm.getHeight();
        }
        outPm.drawPixmap(inPm, srcx, srcy, srcWidth, srcHeigth, 0, 0, outWidth, outheight);
        inPm.dispose();
        return outPm;
    }

    private static String decimalToDMS(float coord) {

        float mod = coord % 1;
        int intPart = (int) coord;
        String degrees = String.valueOf(intPart);
        coord = mod * 60;
        mod = coord % 1;
        intPart = (int) coord;
        if (intPart < 0)
            intPart *= -1;
        String minutes = String.valueOf(intPart);
        coord = mod * 60;
        intPart = (int) coord;
        if (intPart < 0)
            intPart *= -1;
        String seconds = String.valueOf(intPart);
        String output = Math.abs(Integer.parseInt(degrees)) + "° " + minutes + "' " + seconds + "\" ";
        // .println("fist coords : " + output);
        return output;
    }

    /**
     * Vérifie l'existence d'un fichier à l'emplacement spécifié.
     *
     * <p>
     * Cette méthode vérifie d'abord si le fichier existe dans les fichiers internes
     * de l'application.
     * Si ce n'est pas le cas, elle vérifie si le fichier existe dans le système de
     * fichiers en utilisant
     * un {@link FileHandle}.
     * </p>
     *
     * @param filePath le chemin d'accès complet au fichier, utilisant des barres
     *                 obliques (/) ou des barres obliques inverses (\) comme
     *                 séparateurs de dossier
     * @return {@code true} si le fichier existe, {@code false} sinon
     */
    public static boolean fileExist(String filePath) {
        if (!Gdx.files.internal(filePath).exists()) {
            return new FileHandle(filePath).exists();
        } else {
            return true;
        }
    }

    /**
     * Charge une texture à partir d'une image si elle existe à l'emplacement
     * spécifié.
     *
     * <p>
     * Cette méthode vérifie d'abord si le fichier image existe dans le système de
     * fichiers en utilisant
     * un {@link FileHandle}, puis dans les fichiers internes de l'application avec
     * {@link Gdx#files}.
     * Si le fichier image existe, il est chargé en tant que texture à l'aide du
     * gestionnaire d'images.
     * </p>
     *
     * @param imagePath le chemin d'accès complet à l'image, utilisant des barres
     *                  obliques (/) ou des barres obliques inverses (\) comme
     *                  séparateurs de dossier
     * @return {@code true} si l'image a été chargée avec succès, {@code false}
     *         sinon
     */
    public static boolean loadIfExist(String imagePath) {
        if (new FileHandle(imagePath).exists() || Gdx.files.internal(imagePath).exists()) {
            MixOfImage.manager.load(imagePath, Texture.class);
            return true;
        }
        return false;
    }

    /**
     * Extrait le nom du dossier parent à partir d'un chemin d'accès d'image donné.
     *
     * <p>
     * Cette méthode remplace les séparateurs de dossier spécifiques au système
     * d'exploitation
     * (barres obliques inverses) par des barres obliques, puis divise le chemin en
     * une liste de segments.
     * Le nom du dossier parent est supposé être l'avant-dernier segment de la
     * liste.
     * </p>
     *
     * @param imagePath le chemin d'accès complet à l'image, utilisant des barres
     *                  obliques (/) ou des barres obliques inverses (\) comme
     *                  séparateurs de dossier
     * @return le nom du dossier parent de l'image, ou {@code null} si le chemin ne
     *         contient pas suffisamment de segments pour identifier un dossier
     *         parent
     */
    public static String getFileName(String pathToTheFile) {
        String[] pathList = pathToTheFile.replace("\\", "/").split("/");
        if (pathList.length >= 2) {
            return pathList[pathList.length - 2];
        }
        Gdx.app.log("getFileName", "can't get the file name ");

        return null;
    }

    /**
     * Extrait un segment spécifique d'un chemin d'accès de fichier.
     *
     * <p>
     * Cette méthode remplace les séparateurs de dossier spécifiques au système
     * d'exploitation
     * (barres obliques inverses) par des barres obliques, puis divise le chemin en
     * une liste de segments.
     * Elle retourne un segment spécifique en partant de la fin, basé sur le numéro
     * de fichier fourni.
     * </p>
     *
     * @param imagePath  le chemin d'accès complet au fichier, utilisant des barres
     *                   obliques (/) ou des barres obliques inverses (\) comme
     *                   séparateurs de dossier
     * @param fileNumber le numéro du segment à récupérer en partant de la fin (0
     *                   pour le dernier segment, 1 pour l'avant-dernier, etc.)
     * @return le segment spécifié du chemin d'accès, ou {@code null} si le chemin
     *         ne contient pas suffisamment de segments
     */
    public static String getFile(String imagePath, Integer fileNumber) {
        String[] imageList = imagePath.replace("\\", "/").split("/");
        if (imageList.length >= 2 + fileNumber) {
            return imageList[imageList.length - 2 - fileNumber];
        } else {
            Gdx.app.log("getImageFile",
                    "can't get file number " + fileNumber + " because the path doesn't contains that much files");

        }
        Gdx.app.log("getFile", "can't get the file number " + fileNumber);

        return null;
    }

    /**
     * Extrait le chemin avant un segment spécifique d'un chemin d'accès de fichier.
     *
     * <p>
     * Cette méthode remplace les séparateurs de dossier spécifiques au système
     * d'exploitation
     * (barres obliques inverses) par des barres obliques, puis divise le chemin en
     * une liste de segments.
     * Elle retourne la partie du chemin située avant un segment spécifique, basé
     * sur le numéro de fichier fourni.
     * </p>
     *
     * @param imagePath  le chemin d'accès complet au fichier, utilisant des barres
     *                   obliques (/) ou des barres obliques inverses (\) comme
     *                   séparateurs de dossier
     * @param fileNumber le numéro du segment avant lequel le chemin doit être
     *                   extrait (0 pour le dernier segment, 1 pour l'avant-dernier,
     *                   etc.)
     * @return la partie du chemin d'accès avant le segment spécifié, ou
     *         {@code null} si le chemin ne contient pas suffisamment de segments
     */
    public static String getPathBefore(String imagePath, Integer fileNumber) {
        String[] imageList = imagePath.replace("\\", "/").split("/");
        if (imageList.length >= 2 + fileNumber) {
            String path = "";

            for (int i = 0; i < imageList.length - 2 - fileNumber; i++) {
                path += imageList[i];
            }
            return path;
        } else {
            Gdx.app.log("getImageFile",
                    "can't get path before file number " + fileNumber
                            + " because the path doesn't contains that much files");

        }
        Gdx.app.log("getPathBefore", "can't get path before file number " + fileNumber);

        return null;
    }

}
