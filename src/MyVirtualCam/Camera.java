/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyVirtualCam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author user
 */
public class Camera {
    public Webcam webcam;
    private BufferedImage dummyImage;
    public BufferedImage image;
    public BufferedImage originalImage;
    public Solution currentSolution;
    public boolean isDebugging=false;
    public int reducedImageSize=100;
    
    public Camera(int webcamIndex){
        webcam = Webcam.getWebcams().get(webcamIndex);
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();
    }
    
    public Camera(String dummyImagePath, int webcamIndex, boolean passed_isDebugging){
        isDebugging=passed_isDebugging;
        try {
            dummyImage = ImageIO.read(new File(dummyImagePath));
        } catch (IOException ex) {
            Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, null, ex);
        }
        webcam = Webcam.getWebcams().get(webcamIndex);
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();
    }
    
    public void readImages() {
        if (isDebugging == true) {
            image = deepCopyBufferedImage(dummyImage);
            originalImage=deepCopyBufferedImage(image);
            image=resize(image,reducedImageSize,reducedImageSize);
        } else {
            if (webcam.isImageNew()) {
                image = webcam.getImage();
                originalImage=deepCopyBufferedImage(image);
                image=resize(image,reducedImageSize,reducedImageSize);
            }
        }
    }
    
    public BufferedImage deepCopyBufferedImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    
    public void printSolution(Point2DI[] solution) {
        for (int i = 0; i < 4; i++) {
            System.out.println("[" + i + "]x: " + solution[i].x + " y: " + solution[i].y);
        }
    }
    
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
    

    public void deepTest(Optimizer optimizer) {
        Solution solution = new Solution();

//        solution[0] = new Point2DI(25, 30);
//        solution[1] = new Point2DI(75, 30);
//        solution[2] = new Point2DI(75, 70);
//        solution[3] = new Point2DI(25, 70);
        solution.points[0] = new Point2DI((int) (Math.random() * 100), (int) (Math.random() * 100));
        solution.points[1] = new Point2DI((int) (Math.random() * 100), (int) (Math.random() * 100));
        solution.points[2] = new Point2DI((int) (Math.random() * 100), (int) (Math.random() * 100));
        solution.points[3] = new Point2DI((int) (Math.random() * 100), (int) (Math.random() * 100));
        solution.points[4] = new Point2DI(0, 0);
        solution.points[5] = new Point2DI(0, 0);
        solution.points[6] = new Point2DI(0, 0);
        solution.points[7] = new Point2DI(0, 0);
        Point3DD vec11 = new Point3DD(solution.points[0].x - solution.points[1].x, solution.points[0].y - solution.points[1].y, 1);
        Point3DD vec12 = new Point3DD(solution.points[1].x - solution.points[2].x, solution.points[1].y - solution.points[2].y, 1);
        double sol1Surf = vec11.crossProduct(vec12).size() / 2;

        Point3DD vec13 = new Point3DD(solution.points[0].x - solution.points[3].x, solution.points[0].y - solution.points[3].y, 1);
        Point3DD vec14 = new Point3DD(solution.points[2].x - solution.points[3].x, solution.points[2].y - solution.points[3].y, 1);
        sol1Surf = sol1Surf + vec13.crossProduct(vec14).size() / 2;
        int[] s1Xs = new int[4];
        int[] s2Xs = new int[4];
        int[] s1Ys = new int[4];
        int[] s2Ys = new int[4];
        for (int i = 0; i < 4; i++) {
            s1Xs[i] = solution.points[i].x;
            s1Ys[i] = solution.points[i].y;
            s2Xs[i] = solution.points[4 + i].x;
            s2Ys[i] = solution.points[4 + i].y;
        }
        Polygon s1Poly = new Polygon(s1Xs, s1Ys, 4);
        Polygon s2Poly = new Polygon(s2Xs, s2Ys, 4);
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (i == 50 && j == 50) {
                    System.out.println();
                }
                if (s1Poly.contains(i, j) == true) {//isInside(new Point2DI(i, j), solution, sol1Surf, true) == true) {
                    int r = 0;
                    int g = 0;
                    int b = 255;
                    int rgb = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
                    image.setRGB(i, j, rgb);
                } else {

                }
            }
        }
        drawDebuggingSolutionPoints(solution);
        double[] objVals = optimizer.fitness(this, solution);
        System.out.println(objVals[0]);
    }
    
    public void drawDebuggingSolutionPoints(Solution solution) {
        double yDiff=(double)originalImage.getHeight()/(double)reducedImageSize;
        double xDiff=(double)originalImage.getWidth()/(double)reducedImageSize;
        int[] s1Xs = new int[4];
        int[] s1Ys = new int[4];
        for (int i = 0; i < 4; i++) {
            s1Xs[i] = (int)(Math.round(solution.points[i].x*xDiff));
            s1Ys[i] = (int)(Math.round(solution.points[i].y*yDiff));
        }
        Polygon s1Poly = new Polygon(s1Xs, s1Ys, 4);
        for (int i = 1; i < originalImage.getWidth()-1; i++) {
            for (int j = 1; j < originalImage.getHeight()-1; j++) {
                if (s1Poly.contains(i, j) == true) {
                    int r = 0;
                    int g = 0;
                    int b = 255;
                    int rgb = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
                    try{
                    originalImage.setRGB(i, j, rgb);
                    }catch(Exception ex){
                        System.out.println("!!!");
                    }
                }
            }
        }
        
        
        int r = 255;
        int g = 0;
        int b = 0;
        int rgb = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
        for (int i = 0; i < 4; i++) {
            for (int k = -4; k <= 4; k++) {
                for (int h = -4; h <= 4; h++) {
                    if ((int)(solution.points[i].x*xDiff) + k > 0 && (int)(solution.points[i].x*xDiff) + k < originalImage.getWidth() && (int)(solution.points[i].y*yDiff) + h > 0 && (int)(solution.points[i].y*yDiff) + h < originalImage.getHeight()) {
                        originalImage.setRGB((int)(solution.points[i].x*xDiff) + k, (int)(solution.points[i].y*yDiff) + h, rgb);
                    }
                }
            }
        }
    }

    
    
//    public boolean isInside(Point2DI input, Point2DI[] solution, double solSurface, boolean isFirstProblem) {
//        if (isFirstProblem == true) {
//            Point3DD topLeftVec = new Point3DD(input.x - solution[0].x, input.y - solution[0].y, 0);
//            Point3DD topRightVec = new Point3DD(input.x - solution[1].x, input.y - solution[1].y, 0);
//            Point3DD downRightVec = new Point3DD(input.x - solution[2].x, input.y - solution[2].y, 0);
//            Point3DD downLeftVec = new Point3DD(input.x - solution[3].x, input.y - solution[3].y, 0);
//            double surfUp = topLeftVec.crossProduct(topRightVec).size() / 2d;
//            double surfRight = topRightVec.crossProduct(downRightVec).size() / 2d;
//            double surfDown = downRightVec.crossProduct(downLeftVec).size() / 2d;
//            double surfLeft = downLeftVec.crossProduct(topLeftVec).size() / 2d;
//            double calcSurf = surfUp + surfRight + surfDown + surfLeft - 0.001d;
//            if (calcSurf >= solSurface) {
//                return false;
//            } else {
//                return true;
//            }
//        } else {
//            Point3DD topLeftVec = new Point3DD(input.x - solution[4].x, input.y - solution[4].y, 1);
//            Point3DD topRightVec = new Point3DD(input.x - solution[5].x, input.y - solution[5].y, 1);
//            Point3DD downRightVec = new Point3DD(input.x - solution[6].x, input.y - solution[6].y, 1);
//            Point3DD downLeftVec = new Point3DD(input.x - solution[7].x, input.y - solution[7].y, 1);
//            double surfUp = topLeftVec.crossProduct(topRightVec).size();
//            double surfRight = topRightVec.crossProduct(downRightVec).size();
//            double surfDown = downRightVec.crossProduct(downLeftVec).size();
//            double surfLeft = downLeftVec.crossProduct(topLeftVec).size();
//            double calcSurf = surfUp + surfRight + surfDown + surfLeft - 12d;
//            if (calcSurf >= solSurface) {
//                return true;
//            } else {
//                return false;
//            }
//        }
//    }
    
    
}
