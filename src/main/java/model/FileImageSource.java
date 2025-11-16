package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FileImageSource implements ImageSource {

    private final BufferedImage image;

    public FileImageSource(String path) throws IOException {
        this.image = ImageIO.read(new File(path));
    }

    @Override
    public BufferedImage image() {
        return image;
    }
}
