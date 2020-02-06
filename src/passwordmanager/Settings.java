/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 *
 * @author mirko.ravot
 */
public class Settings extends Properties {
    
    private static Settings internal;
    public static final String FILENAME = "settings.properties";
    public static final String K_LAST_FILE = "last.file";
    
    private String filePath;
    private String fileDir;
   
    
    
    private Settings() {
        
        fileDir = System.getProperty("user.home") + System.getProperty("file.separator") + "PasswordManager"  + System.getProperty("file.separator");
        File fd = new File(fileDir);
        if (fd.exists() == false) {
            fd.mkdirs();
        }
        filePath = fileDir + FILENAME;
        try {
            this.load(new FileInputStream(filePath));
        } catch (Exception ex) {
            
        }
        
        
    }

    public static Settings getSettings() {
        
        if (internal == null) {
            internal = new Settings();
        }
        return internal;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }
   
    
    
    
    public void save() throws FileNotFoundException {
        this.save(new FileOutputStream(filePath), "");
        
    }
    
    
    
}
