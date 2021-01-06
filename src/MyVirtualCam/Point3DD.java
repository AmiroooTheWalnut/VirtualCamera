/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyVirtualCam;

/**
 *
 * @author user
 */
public class Point3DD {
    public double x;
    public double y;
    public double z;
    public Point3DD(double passed_x,double passed_y,double passed_z){
        x=passed_x;
        y=passed_y;
        z=passed_z;
    }
    
    public Point3DD crossProduct(Point3DD secondPoint){
        Point3DD  output=new Point3DD(y*secondPoint.z-z*secondPoint.y,z*secondPoint.x-x*secondPoint.z,x*secondPoint.y-y*secondPoint.x);
        return output;
    }
    
    public double size(){
        return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2));
    }
}
