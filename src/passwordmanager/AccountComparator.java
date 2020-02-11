/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.util.Comparator;
import org.w3c.dom.Element;

/**
 *
 * @author mirko.ravot
 */
public class AccountComparator implements Comparator<Element>  {
    
    @Override
    public int compare(Element o1, Element o2) {
        return o2.getAttribute("dae").compareTo(o1.getAttribute("date"));
            
    }
    
}
