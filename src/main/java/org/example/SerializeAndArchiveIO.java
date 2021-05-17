package org.example;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.Deflater;

public class SerializeAndArchiveIO {
    public static void main(String[] args) {
        try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("data/o.dat"))) {
            StudentSerialize student = new StudentSerialize("Janka", 555777, "VKL_1410");
            System.out.println(student);
            output.writeObject(student);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class StudentSerialize implements Serializable {
    static String faculty = "MMF";
    private String name;
    private int id;
    private transient String password;
    private static final long serialVersionUID = 2L;
    public StudentSerialize(String name, int id, String password) {
        this.name = name;
        this.id = id;
        this.password = password;
    }
    @Override
    public String toString() {
        return new StringJoiner(", ", StudentSerialize.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("id=" + id)
                .add("password='" + password + "'")
                .toString();
    }
}

class DeSerializationMain {
    public static void main(String[] args) {
        StudentSerialize.faculty ="GEO";
        try(ObjectInputStream input = new ObjectInputStream(
                new FileInputStream("data/o.dat"))) {
            StudentSerialize student = (StudentSerialize)input.readObject();
            System.out.println(student);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(); }
    }
}

class XmlSerializeMain {
    public static void main(String[] args) {
        try (XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(
                new FileOutputStream("data/serial.xml")))) {
            StudentXml student = new StudentXml("Janka", 555777, "VKL_1410");
            xmlEncoder.writeObject(student);
            xmlEncoder.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try (XMLDecoder xmlDecoder = new XMLDecoder(new BufferedInputStream(
                new FileInputStream("data/serial.xml")))) {
            StudentXml student = (StudentXml) xmlDecoder.readObject();
            System.out.println(student);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class ScannerMain {
    public static void main(String[] args) {
        String filename = "data/scan.txt";
        try(Scanner scan = new Scanner(new FileReader(filename))) {
            while (scan.hasNext()) {
                if (scan.hasNextInt()) {
                    System.out.println(scan.nextInt() + " :int");
                } else if (scan.hasNextBoolean()) {
                    System.out.println(scan.nextBoolean() + " :boolean");
                } else if (scan.hasNextDouble()) {
                    System.out.println(scan.nextDouble() + " :double");
                } else {
                    System.out.println(scan.next() + " :String");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }
}

class ScannerDelimiterMain {
    public static void main(String[] args) {
        double sum = 0.0;
        String numbersStr = "1,3;2,0; 8,5; 4,8;9,0; 1; 10;";
        Scanner scan = new Scanner(numbersStr)
                .useLocale(Locale.FRANCE) // change to Locale.US
                .useDelimiter(";\\s*");
        while (scan.hasNext()) {
            if (scan.hasNextDouble()) {
                sum += scan.nextDouble();
            } else {
                System.out.println(scan.next());
            }
        }
        System.out.printf("Sum = " + sum);
        scan.close();
    }
}

class PackMain {
    public static void main(String[] args) {
        String dirName = "data";
        try {
            PackJar packJar = new PackJar("example.jar");
            packJar.pack(dirName);
        } catch (FileNotFoundException e) {
        e.printStackTrace();
        }
    }
}

class PackJar {
    private String jarFileName;
    public final static int BUFFER = 2_048;
    public PackJar(String jarFileName) throws FileNotFoundException {
        if(!jarFileName.endsWith(".jar")) {
            throw new FileNotFoundException(jarFileName + " incorrect archive name");
        }
        this.jarFileName = jarFileName;
    }
    public void pack(String dirName) throws FileNotFoundException {
        Path dirPath = Paths.get(dirName);
        if (Files.notExists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new FileNotFoundException(dirPath + " not found");
        }
        List<Path> listFilesToJar = null;
        try {
            listFilesToJar = Files
                    .walk(dirPath, 10)
                    .filter(f -> !Files.isDirectory(f))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path[] temp = {};
        Path[] filesToJar = listFilesToJar.toArray(temp);
        // actually archiving
        try (FileOutputStream outputStream = new FileOutputStream(jarFileName);
             JarOutputStream jarOutputStream = new JarOutputStream(outputStream)) {
            byte[] buffer = new byte[BUFFER];
            jarOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
            for (int i = 0; i < filesToJar.length; i++) {
                String file = filesToJar[i].toString();
                jarOutputStream.putNextEntry(new JarEntry(file));
                try (FileInputStream in = new FileInputStream(file)) {
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        jarOutputStream.write(buffer, 0, len);
                    }
                    jarOutputStream.closeEntry();
                } catch (FileNotFoundException e) {
                    System.err.println("File not found " + e);
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("incorrect data " + e);
        } catch (IOException e) {
            System.err.println("I/O error " + e);
        }
    }
}

class UnPackMain {
    public static void main(String[] args) {
        // location and archive name
        String nameJar = "example.jar";
        // directory to which the files will be unpacked
        String destinationPath = "tmp";
        new UnPackJar().unpack(destinationPath, nameJar);
    }
}

class UnPackJar {
    private Path destinationPath;
    // buffer size when unpacking
    public static final int BUFFER = 2_048;
    public void unpack(String destinationDirectory, String nameJar) {
        try (JarFile jarFile = new JarFile(nameJar)) {
            jarFile.stream().forEach(entry -> {
                String entryname = entry.getName();
                System.out.println("Extracting: " + entry);
                destinationPath = Paths.get(destinationDirectory, entryname);
                // create directory structure
                destinationPath.getParent().toFile().mkdirs();
                // unpack the record, if it is not a directory
                if (!entry.isDirectory()) {
                    writeFile(jarFile, entry);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void writeFile(JarFile jar, JarEntry entry) {
        int currentByte;
        byte data[] = new byte[BUFFER];
        try (BufferedInputStream bufferedInput = new BufferedInputStream(jar.getInputStream(entry));
             FileOutputStream fileOutput = new FileOutputStream(destinationPath.toString());
             BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput, BUFFER)) {
            // write the file to disk
            while ((currentByte = bufferedInput.read(data, 0, BUFFER)) > 0) {
                bufferedOutput.write(data, 0, currentByte);
            }
            bufferedOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}