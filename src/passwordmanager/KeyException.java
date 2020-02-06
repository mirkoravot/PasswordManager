/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

/**
 *
 * @author mirko.ravot
 */
public class KeyException extends Exception {

    /**
     * Creates a new instance of <code>KeyException</code> without detail
     * message.
     */
    public KeyException() {
    }

    /**
     * Constructs an instance of <code>KeyException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public KeyException(String msg) {
        super(msg);
    }
}
