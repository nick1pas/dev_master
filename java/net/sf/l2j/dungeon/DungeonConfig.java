package net.sf.l2j.dungeon;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author Anarchy
 * @Modificador -= TioPatinhas =-
 */
public class DungeonConfig
{
    protected static final Logger _log = Logger.getLogger(DungeonConfig.class.getName());
    
    private static final String CONFIG_FILE = "./config/events/DungeonEvent.properties";
    
    public static boolean DUNGEON_EVENT_ENABLED;
    public static String[] DUNGEON_EVENT_INTERVAL;
    public static int DUNGEON_DURATION_MINUTES;
    public static String DUNGEON_TIME_MESSAGE;
    public static String VIP_REQUIRED_MESSAGE;
    public static int DUNGEON_COOLDOWN_HOURS;
    
    public static void init()
    {
        ExProperties settings = load(CONFIG_FILE);
        
        DUNGEON_EVENT_ENABLED = settings.getProperty("DungeonEventEnabled", false);
        DUNGEON_EVENT_INTERVAL = settings.getProperty("DungeonEventInterval", "14:00,18:00,22:00").split(",");
        DUNGEON_DURATION_MINUTES = settings.getProperty("DungeonDurationMinutes", 30);
        DUNGEON_TIME_MESSAGE = settings.getProperty("DungeonTimeMessage", "The dungeon is only available at specific times: %s (for %d minutes).");
        VIP_REQUIRED_MESSAGE = settings.getProperty("VipRequiredMessage", "Only VIP players can enter the dungeon.");
        DUNGEON_COOLDOWN_HOURS = settings.getProperty("DungeonCooldownHours", 12);
        
        for (int i = 0; i < DUNGEON_EVENT_INTERVAL.length; i++)
        {
            DUNGEON_EVENT_INTERVAL[i] = DUNGEON_EVENT_INTERVAL[i].trim();
        }
        
        _log.info("DungeonEventConfig: Loaded " + DUNGEON_EVENT_INTERVAL.length + " dungeon intervals. Duration: " + DUNGEON_DURATION_MINUTES + " minutes.");
    }
    
    public static boolean isDungeonTime()
    {
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        
        for (String interval : DUNGEON_EVENT_INTERVAL)
        {
            if (!interval.contains(":")) continue;
            
            String[] parts = interval.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            
            int scheduledTotalMinutes = hour * 60 + minute;
            int currentTotalMinutes = currentHour * 60 + currentMinute;
            
            if (currentTotalMinutes >= scheduledTotalMinutes && 
                currentTotalMinutes < scheduledTotalMinutes + DUNGEON_DURATION_MINUTES)
            {
                return true;
            }
        }
        return false;
    }
    
    public static String getFormattedHours()
    {
        List<String> hours = new ArrayList<>();
        for (String time : DUNGEON_EVENT_INTERVAL)
        {
            if (time.contains(":"))
            {
                String hour = time.split(":")[0];
                hours.add(hour + "h");
            }
        }
        return String.join(", ", hours);
    }
    
    public static ExProperties load(String filename)
    {
        return load(new File(filename));
    }
    
    public static ExProperties load(File file)
    {
        ExProperties result = new ExProperties();
        try
        {
            result.load(file);
        }
        catch (Exception e)
        {
            _log.warning("Error loading config: " + file.getName() + "!");
        }
        return result;
    }
}