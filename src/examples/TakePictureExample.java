
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;


/**
 * Example of how to take single picture.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class TakePictureExample {

	public static void main(String[] args) throws IOException {

//		// get default webcam and open it
//		Webcam webcam = Webcam.getDefault();
//		webcam.open();
//
//		// get image
//		BufferedImage image = webcam.getImage();
//
//		// save image to PNG file
//		ImageIO.write(image, "PNG", new File("test.png"));
            Webcam webcam1 = Webcam.getWebcams().get(0);
            Webcam webcam2 = Webcam.getWebcams().get(1);
            webcam1.open();
            webcam2.open();
            BufferedImage image1 = webcam1.getImage();
            BufferedImage image2 = webcam2.getImage();
            ImageIO.write(image1, "PNG", new File("test1.png"));
            ImageIO.write(image2, "PNG", new File("test2.png"));
	}
}
