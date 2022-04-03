package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Io {
    public static void main(String[] args) {
        FileInputStream input = null;
        try {
            input = new FileInputStream("data/t.txt");
            int code = input.read();
            System.out.println(code + " char = " + (char)code);
            byte[] arr = new byte[16];
            int numberBytes = input.read(arr);
            System.out.println("numberBytes = " + numberBytes);
            System.out.println(Arrays.toString(arr));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path path = FileSystems.getDefault().getPath("src");
        if (Files.exists(path) && Files.isDirectory(path)) {
            int maxDepth = 5;
            try (Stream<Path> streamDir = Files.find(path, maxDepth, (p, a) -> String.valueOf(p).endsWith(".java"))) {
                long counter = streamDir
                        .map(String::valueOf)
                        .peek(System.out::println)
                        .count();
                System.out.println("found: " + counter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path start = Paths.get("src");
        int maxDepth = 5;
        try (Stream<Path> pathStream = Files.walk(start, maxDepth)) {
            pathStream.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class OutMain {
    public static void main(String[] args) {
        try (FileOutputStream output = new FileOutputStream("data/out.txt", true)) {
            output.write(48);
            byte[] value = {65, 67, 100};
            output.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class FileMain {
    public static void main(String[] args) {
        File file = new File("data" + File.separator + "info.txt");
        if (file.exists() && file.isFile()) {
            System.out.println("Path " + file.getPath());
            System.out.println("Absolute Path " + file.getAbsolutePath());
            System.out.println("Size " + file.length());
            System.out.println("Dir " + file.getParent());
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File dir = new File("data");
        if (dir.exists() && dir.isDirectory()) {
            for (File current : dir.listFiles()) {
                long millis = current.lastModified();
                Instant date = Instant.ofEpochMilli(millis);
                System.out.println(current.getPath() + "\t" + current.length() + "\t" + date);
            }
            File root = File.listRoots()[0];
            System.out.printf("\n%s %,d from %,d free bytes", root.getPath(),
                    root.getUsableSpace(), root.getTotalSpace());
        }
    }
}

class ReadStringMain {
    public static void main(String[] args) {
        String stringLines = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("data/res.txt"))) {
            String tmp;
            while ((tmp = reader.readLine()) != null) { //java 2
                stringLines += tmp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }System.out.println(stringLines);
    }
}

class ReadStringMain1 {
    public static void main(String[] args) {
        String dirName = "data";
        String filename = "res.txt";
        Path path = FileSystems.getDefault().getPath(dirName, filename);
        try {//java7
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            System.out.println(lines.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ReadStringMain2 {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/res.txt"));
             Stream<String> stream = reader.lines()) { // java 8
                String lines = stream.collect(Collectors.joining());
                System.out.println(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ReadStringMain3 {
    public static void main(String[] args) {
        Path path = Paths.get("data/res.txt");
        try (Stream<String> stream = Files.newBufferedReader(path).lines()) {
            stream.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ReadStringMain4 {
    public static void main(String[] args) {
        Path path = Paths.get("data/res.txt");
        try(Stream<String> streamLines = Files.lines(path)) {
            String result = streamLines.collect(Collectors.joining());
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class PrintMain {
    public static void main(String[] args) {
        double[] values1 = {1.10, 1.2};
        char a = '1';
        System.out.println((byte)a);
        try(PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("data/r.txt")))) {
            for (double value: values1) {
                writer.printf("Java %.2g%n", value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        double[] values2 = {14.10, 17};
        try(PrintStream stream = new PrintStream(new FileOutputStream("data/res.txt"))) {
            for (double value: values2) {
                stream.printf("Java %.2g%n", value);
                System.setOut(stream);
                System.out.printf("%.2g%n", value);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
class Panda {
    int age;

    public static void main(String[] args) throws IOException {

        Panda p1 = new Panda();
        p1.age = 1;
        check(p1, p -> p.age < 5);

        Path path1 = Paths.get("data/res.txt");
        BasicFileAttributeView view = Files.getFileAttributeView(path1, BasicFileAttributeView.class);
        BasicFileAttributes attributes = view.readAttributes();
        System.out.println(attributes.lastModifiedTime());
        System.out.println(FileSystems.getDefault().supportedFileAttributeViews());

        Path p2 = Paths.get("cat","..","dog");
        System.out.println(p2);
        System.out.println(Paths.get("..").toRealPath().getParent());
        System.out.println(Paths.get("..").toAbsolutePath());
        System.out.println(Paths.get("..").getFileName());
        Path path2 = path1.normalize().relativize(Paths.get("lion"));
        System.out.println(path1.normalize() + " "+ path2);
    }

    private static void check(Panda panda, Predicate<Panda> pred) {
        String result = pred.test(panda) ? "match" : "not match";
        System.out.println(result);
    }
}