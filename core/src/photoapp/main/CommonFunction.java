package photoapp.main;

import photoapp.main.windows.BigPreview;
import photoapp.main.windows.EnterValue;
import photoapp.main.windows.FileChooser;
import photoapp.main.windows.ImageEdition;
import photoapp.main.windows.MainImages;
import photoapp.main.windows.Parameter;

public class CommonFunction {
    public static void back() {
        if (Main.windowOpen.equals("ImageEdition")) {
            if (ImageEdition.plusTableOpen) {
                ImageEdition.plusTable.clear();
                ImageEdition.plusPeople = false;
                ImageEdition.plusPlace = false;
                ImageEdition.bigPreview = false;

                ImageEdition.open(ImageEdition.theCurrentImagePath, false);
                ImageEdition.plusTableOpen = false;
            } else {
                ImageEdition.clear();
                MainImages.open();
            }
        } else if (Main.windowOpen.equals("MainImages")) {
            if (MainImages.selectModeIsOn) {
                MainImages.selectModeIsOn = false;
                MainImages.reload();
            } else {
                MainImages.clear();
                FileChooser.open();

            }
        } else if (Main.windowOpen.equals("BigPreview")) {
            ImageEdition.bigPreview = false;
            BigPreview.clear();
            ImageEdition.open(ImageEdition.theCurrentImagePath, true);
        } else if (Main.windowOpen.equals("Parameter")) {
            Parameter.clear();
            FileChooser.open();
        } else if (Main.windowOpen.equals("EnterValue")) {
            EnterValue.clear();
            Main.openWindow = true;
            Main.windowOpen = "MainImages";
        }
    }
}
