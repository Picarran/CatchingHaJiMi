package org.picarran.mycathome.entity;

public class Point {
    private int r;
    private int c;

    public Point(){ }
    public Point(int r, int c){ this.r=r; this.c=c; }

    public int getR() { return r; }
    public void setR(int r) { this.r = r; }
    public int getC() { return c; }
    public void setC(int c) { this.c = c; }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(o==null||getClass()!=o.getClass()) return false;
        Point p = (Point)o;
        return r==p.r && c==p.c;
    }

    @Override
    public int hashCode(){ return r*31 + c; }
}
