package me.drayshak.WorldInventories;

public class MultiInvImportHelper
{
    public static WIItemStack itemFromMIString(String sItem)
    {
        String[] sSplit = sItem.split(",");
        
        if (sSplit.length >= 4)
        {
            return new WIItemStack(Integer.parseInt(sSplit[0]), Integer.parseInt(sSplit[1]), Short.parseShort(sSplit[3]), Byte.parseByte(sSplit[2]));
        }
        else
        {
            return null;
        }
    }
    
    public static WIPlayerInventory playerInventoryFromMIString(String string)
    {
        String[] sSplit = string.split(";-;");
        
        WIItemStack[] playerItems = null;
        WIItemStack[] playerArmour = null;
        
        if (sSplit.length >= 3)
        {
            if (!sSplit[0].equals("!!!"))
            {
                playerItems = new WIItemStack[36];
                
                String[] itemsSplit = sSplit[0].split(";");
                
                int iMin = Math.min(36, itemsSplit.length);
                for(int i = 0; i < iMin; i++)
                {
                    playerItems[i] = itemFromMIString(itemsSplit[i]);
                }
            }

            if (!sSplit[1].equals("!!!"))
            {
                playerArmour = new WIItemStack[4];
                
                String[] armourSplit = sSplit[1].split(";");

                int iMin = Math.min(4, armourSplit.length);
                for(int i = 0; i < iMin; i++)
                {
                    playerArmour[i] = itemFromMIString(armourSplit[i]);
                }
            }
        }
        
        return new WIPlayerInventory(playerItems, playerArmour);
    }
}
