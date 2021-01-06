/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyVirtualCam;

import java.awt.Polygon;

/**
 *
 * @author user
 */
public class Optimizer {
    
    
    
    public Solution initSolution(Camera camera) {
        Solution solution = new Solution();
        for (int i = 0; i < 4; i++) {
            int x = (int) (Math.random() * camera.image.getWidth());
            int y = (int) (Math.random() * camera.image.getHeight());
            solution.points[i] = new Point2DI(x, y);
        }
        return solution;
    }
    
    public Solution getCornerPoints(Camera camera, Solution solution) {
        Solution bestSolution = optimize(camera, solution, 2d);
        return bestSolution;
    }
    
    public Solution optimize(Camera camera, Solution solution, double initialTemperature) {
        Solution bestSolution = solution;
        double[] bestFitness = fitness(camera, bestSolution);
        double teperature = initialTemperature;
        for (int i = 0; i < 400; i++) {
            teperature = teperature / 1.01d;
            Solution newSolution = perturbate(camera, bestSolution, teperature);
            double[] objValues = fitness(camera, newSolution);
            if (objValues[0] + objValues[1] < bestFitness[0] + bestFitness[1]) {
                bestFitness = objValues;
                bestSolution = newSolution;
            }
//            printSolution(newSolution);
//            System.out.println("***********");
//            System.out.println("TEMPERATURE: " + teperature);
//            System.out.println("FITNESS: " + (objValues[0] + objValues[1]));
//            drawDebuggingSolutionPoints(bestSolution);
        }
        
        Solution output = new Solution();
        for (int i = 0; i < 4; i++) {
            output.points[i] = new Point2DI(bestSolution.points[i].x, bestSolution.points[i].y);
        }
//        System.out.println("BEST FITNESS: " + (bestFitness[0] + bestFitness[1]));
        return output;
    }
    
    public Solution perturbate(Camera camera, Solution solution, double temp) {
        Solution output = new Solution();
        for (int i = 0; i < 4; i++) {
            if (Math.random() < 0.5d) {
                int newX = (int) (solution.points[i].x + (Math.random() - 0.5d) * camera.image.getWidth() * temp);
                if (newX < 0) {
                    newX = 0;
                } else if (newX >= camera.image.getWidth()) {
                    newX = camera.image.getWidth() - 1;
                }
                int newY = (int) (solution.points[i].y + (Math.random() - 0.5d) * camera.image.getHeight() * temp);
                if (newY < 0) {
                    newY = 0;
                } else if (newY >= camera.image.getHeight()) {
                    newY = camera.image.getHeight() - 1;
                }
                output.points[i] = new Point2DI(newX, newY);
            } else {
                output.points[i] = new Point2DI(solution.points[i].x, solution.points[i].y);
            }
        }
        return output;
    }
    
    
    public double[] fitness(Camera camera, Solution solution) {
        double objValues[] = new double[2];
        int[] s1Xs = new int[4];
        int[] s1Ys = new int[4];
        for (int i = 0; i < 4; i++) {
            s1Xs[i] = solution.points[i].x;
            s1Ys[i] = solution.points[i].y;
        }
        Polygon s1Poly = new Polygon(s1Xs, s1Ys, 4);

        double sol1Surf = 0;
        Point2DI selectedPixel1 = new Point2DI(-1, -1);
        for (int i = 0; i < camera.image.getHeight(); i++) {
            for (int j = 0; j < camera.image.getWidth(); j++) {
                selectedPixel1.x = i;
                selectedPixel1.y = j;
                int color = camera.image.getRGB(i, j);
                int red = (color >> 16) & 0xFF;
                int green = (color >> 8) & 0xFF;
                int blue = color & 0xFF;
                if (s1Poly.contains(i, j) == true) {
                    sol1Surf = sol1Surf + 1;
                    objValues[0] = objValues[0] + Math.abs(red - green) + Math.abs(red - blue) + Math.abs(green - blue) - Math.max(red, Math.max(green, blue)) + Math.min(red, Math.min(green, blue));
                } else {
                    objValues[0] = objValues[0] - Math.abs(red - green) - Math.abs(red - blue) - Math.abs(green - blue) + Math.max(red, Math.max(green, blue)) - Math.min(red, Math.min(green, blue));
                }
            }
        }
        objValues[0] = objValues[0] - sol1Surf * 8;
        if (sol1Surf < 400) {
            objValues[0] = objValues[0] + 1000000 / sol1Surf;
        }
        objValues[1] = 0;
        return objValues;
    }
    
    
}
