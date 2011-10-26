package me.drayshak.WorldInventories;

import java.io.Serializable;

public class WIPlayerStats implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int health;
    private int foodlevel;
    private float exhausation;
    private float saturation;    
    
    public WIPlayerStats(int health, int foodlevel, float exhaustion, float saturation)
    {
        this.health = health;
        this.foodlevel = foodlevel;
        this.exhausation = exhaustion;
        this.saturation = saturation;
    }
    
    public int getHealth()
    {
        return this.health;
    }
    
    public void setHealth(int health)
    {
        this.health = health;
    }
    
    public int getFoodLevel()
    {
        return this.foodlevel;
    }
    
    public void setFoodLevel(int foodlevel)
    {
        this.foodlevel = foodlevel;
    }   
    
    public float getExhaustion()
    {
        return this.exhausation;
    }
    
    public void setExhaustion(float exhaustion)
    {
        this.exhausation = exhaustion;
    }
    
    public float getSaturation()
    {
        return this.saturation;
    }
    
    public void setSaturation(float saturation)
    {
        this.saturation = saturation;
    }
}