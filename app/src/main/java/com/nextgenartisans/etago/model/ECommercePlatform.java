package com.nextgenartisans.etago.model;

public class ECommercePlatform {
    private String name;
    private int logoDrawable;
    private String packageName;  // Package name to open the app

    public ECommercePlatform(String name, int logoDrawable, String packageName) {
        this.name = name;
        this.logoDrawable = logoDrawable;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public int getLogoDrawable() {
        return logoDrawable;
    }

    public String getPackageName() {
        return packageName;
    }
}


