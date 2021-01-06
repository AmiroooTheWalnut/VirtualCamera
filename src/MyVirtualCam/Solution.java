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
public class Solution {
    public Point2DI[] points;
    
    public Solution(){
        points=new Point2DI[4];
        for(int i=0;i<4;i++){
            points[i]=new Point2DI(-1,-1);
        }
    }
    
    public Solution(Point2DI[] passed_points){
        points=passed_points;
    }
}
