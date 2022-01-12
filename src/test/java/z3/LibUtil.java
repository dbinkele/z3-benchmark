package z3;

import com.microsoft.z3.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LibUtil {

    public static void loadLib(){
        Path path = Paths.get(".").normalize().toAbsolutePath();  // System.load(path + "/libz3java.dylib");
        String value = path.toString()+ "/Lib";
        System.out.println("-----> Load Lib z3java " + value);
        try {

            System.setProperty("java.library.path", value);

            Method initLibraryPaths = ClassLoader.class.getDeclaredMethod("initLibraryPaths");
            initLibraryPaths.setAccessible(true);
            initLibraryPaths.invoke(null);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Context con = new Context();
        System.out.println("--------> Load Lib Ende");
    }
}
