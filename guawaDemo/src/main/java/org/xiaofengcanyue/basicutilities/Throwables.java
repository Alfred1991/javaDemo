package org.xiaofengcanyue.basicutilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class Throwables {

    public static void main(String[] args) throws Exception{

        try {
            FileInputStream fis = new FileInputStream(new File(""));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Throwable t){
            com.google.common.base.Throwables.propagateIfPossible(t, IOException.class);
            com.google.common.base.Throwables.propagateIfPossible(t, SQLException.class);

            com.google.common.base.Throwables.throwIfUnchecked(t);

        }
    }


}
