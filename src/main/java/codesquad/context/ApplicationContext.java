package codesquad.context;

import codesquad.util.scan.Solo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ApplicationContext {

    private final Logger logger = LoggerFactory.getLogger(ApplicationContext.class);
    private final Set<Class<?>> waitingSolos = new HashSet<>();
    private final Map<Class<?>, Object> soloObject = new HashMap<>();

    public <T> T getSoloObject(Class<T> clazz) {
        return clazz.cast(soloObject.get(clazz));
    }

    /**
     * basePackageName 아래에 존재하는 컴포넌트를 스캔하여 객체를 생성합니다.
     *
     * @param basePackageName
     */
    public void scan(String basePackageName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = basePackageName.replace('.', '/');

        List<Class<?>> classes = new ArrayList<>();

        try {
            List<File> files = new ArrayList<>();
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("jar")) {
                    String jarPath = URLDecoder.decode(resource.getPath().substring(5, resource.getPath().indexOf("!")), StandardCharsets.UTF_8);
                    scanJar(basePackageName, jarPath, classes);
                } else {
                    files.add(new File(resource.getFile()));
                }
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, basePackageName));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // init solo
        for (Class<?> clazz : classes) {
            Solo annotation = clazz.getAnnotation(Solo.class);
            if (annotation != null) {
                initSolos(clazz);
            }
        }
        // make solo object and register
        for (Class<?> clazz : waitingSolos) {
            makeSolos(clazz);
        }
        waitingSolos.clear();
    }

    private void scanJar(String basePackageName, String jarPath, List<Class<?>> classes) throws IOException {
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith(basePackageName.replace('.', '/')) && entryName.endsWith(".class")) {
                    String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                try {
                    classes.add(Class.forName(className, false, classLoader));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return classes;
    }

    /**
     * 컨텍스트에 등록할 객체의 타입을 초기화합니다. 컨텍스트에 등록한 객체에 한해서 의존성을 주입하기 위한 작업입니다.
     *
     * @param clazz 등록할 싱글톤 객체의 타입
     */
    private void initSolos(Class<?> clazz) {
        waitingSolos.add(clazz);
    }

    /**
     * 싱글톤 객체를 생성합니다. 만약 이미 등록된 객체 타입이라면 등록된 객체를 반환합니다.
     *
     * @param clazz 싱글톤 객체의 타입
     * @return 생성된 싱글톤 객체
     */
    private Object makeSolos(Class<?> clazz) {
        if (soloObject.containsKey(clazz)) {
            return soloObject.get(clazz);
        }
        if (waitingSolos.isEmpty() || !waitingSolos.contains(clazz)) {
            throw new RuntimeException(clazz.getName() + "은 initSolos를 통해서 생성 대기열에 추가가 필요합니다.");
        }
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length != 1) {
            throw new RuntimeException("싱글톤 객체는 단 하나의 생성자가 존재해야합니다.");
        }
        Constructor<?> constructor = constructors[0];
        Parameter[] args = constructor.getParameters();

        List<Object> parameters = new ArrayList<>();
        for (Parameter arg : args) {
            Object object = makeSolos(arg.getType());
            parameters.add(object);
        }

        try {
            Object solo = constructor.newInstance(parameters.toArray());
            registerSingleTon(solo);
            return solo;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error(constructor.getName() + " 생성 중 오류가 발생했습니다.");
            throw new RuntimeException(e);
        }
    }

    /**
     * 싱글톤 객체를 등록합니다.
     *
     * @param solo 싱글톤 객체
     */
    private void registerSingleTon(Object solo) {
        soloObject.putIfAbsent(solo.getClass(), solo);
    }
}
