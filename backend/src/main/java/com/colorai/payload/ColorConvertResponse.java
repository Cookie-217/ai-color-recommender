package com.colorai.payload;

public class ColorConvertResponse {
    private String hex;
    private Rgb rgb;
    private Hsl hsl;
    private Hsv hsv;
    private Cmyk cmyk;
    private Lab lab;
    private String bestTextColor;
    private double luminance;

    public static class Rgb { public int r, g, b; public Rgb(int r, int g, int b){this.r=r;this.g=g;this.b=b;} }
    public static class Hsl { public double h, s, l; public Hsl(double h, double s, double l){this.h=h;this.s=s;this.l=l;} }
    public static class Hsv { public double h, s, v; public Hsv(double h, double s, double v){this.h=h;this.s=s;this.v=v;} }
    public static class Cmyk { public double c, m, y, k; public Cmyk(double c, double m, double y, double k){this.c=c;this.m=m;this.y=y;this.k=k;} }
    public static class Lab { public double l, a, b; public Lab(double l, double a, double b){this.l=l;this.a=a;this.b=b;} }

    public String getHex() { return hex; }
    public void setHex(String hex) { this.hex = hex; }
    public Rgb getRgb() { return rgb; }
    public void setRgb(Rgb rgb) { this.rgb = rgb; }
    public Hsl getHsl() { return hsl; }
    public void setHsl(Hsl hsl) { this.hsl = hsl; }
    public Hsv getHsv() { return hsv; }
    public void setHsv(Hsv hsv) { this.hsv = hsv; }
    public Cmyk getCmyk() { return cmyk; }
    public void setCmyk(Cmyk cmyk) { this.cmyk = cmyk; }
    public Lab getLab() { return lab; }
    public void setLab(Lab lab) { this.lab = lab; }
    public String getBestTextColor() { return bestTextColor; }
    public void setBestTextColor(String bestTextColor) { this.bestTextColor = bestTextColor; }
    public double getLuminance() { return luminance; }
    public void setLuminance(double luminance) { this.luminance = luminance; }
}
