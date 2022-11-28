package task2;

import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Клас для читання JSON-файлів з інформацією про штрафи та виводу підсумків по кожному штрафу за весь час
 * (відсортованими за спаданням).
 */
public class FineReader {
    private static final String FOLDER = "src/main/resources/task2/input/"; //Path of input files
    private static HashMap<String, Double> fineMap; //Map with all fines with its total amount

    static {
        fineMap = new HashMap<>(); //Initialize map on the start of program
    }

    public static void readJsonObject() {

        //Create array of all files we have
        File[] folder = new File(FOLDER).listFiles();

        //If folder is empty - finish the program
        if (folder == null) {
            System.out.println("Folder is empty");
            return;
        }

        //Create file reader and scanner
        FileReader fr = null;
        Scanner scanner = null;

        //Read each file in folder
        for (File file : folder) {

            try {

                //Initialize file reader and scanner by file path
                fr = new FileReader(file);
                scanner = new Scanner(fr);

                //While file is not finished to read - read all file
                while (true) {

                    //Get each JSON object as string
                    String jsonString = readJsonObject(scanner);

                    //If file was ended - break loop
                    if (jsonString.equals("")) {
                        break;
                    }

                    //Read object and get fine with its amount
                    getFineWithAmount(jsonString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {

                    //Close streams
                    if (scanner != null) {
                        scanner.close();
                    }
                    if (fr != null) {
                        fr.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Write statistics in the XML-file
        writeStatistics();
    }

    //Function to get JSON-object as string
    private static String readJsonObject(Scanner scanner) {

        //Create string builder for JSON string
            StringBuilder jsonString = new StringBuilder();

            //Read one JSON string
            while (scanner.hasNext()) {

                //Save line of JSON
                String nextLine = scanner.nextLine();

                //Skip if line is beginning of end of the file
                if (nextLine.equals("[") || nextLine.equals("]")) {
                    continue;
                }

                //Add line to JSON string
                jsonString.append(nextLine);

                //Break loop if JSON object was ended
                if (nextLine.contains("},") || nextLine.equals("}")) {
                    break;
                }
            }

            //Return JSON string
            return String.valueOf(jsonString);
    }

    //Function to read JSON object and get info about fine
    private static void getFineWithAmount(String jsonString) {
        //Get JSON object
        JSONObject jsonObject = new JSONObject(jsonString);

        String type = jsonObject.getString("type"); //Get type of fine
        double amount = jsonObject.getDouble("fine_amount"); //Get amount of fine

        //Create temporary amount
        double currentAmount = 0.0;

        //If map is not empty and have such type of fine - get total amount of fine
        if ((!fineMap.isEmpty()) && fineMap.containsKey(type)) {
            currentAmount = fineMap.get(type); //Get current total amount of fine
        }

        //Write fine and its total amount in the map
        fineMap.put(type, currentAmount + amount);
    }

    //Function to write fines statistics in XML-file
    private static void writeStatistics() {

        //Create file writer
        FileWriter fw = null;

        try {

            //Initialize file writer by the path of output file
            fw = new FileWriter("src/main/resources/task2/statistics.xml");

            //Sort map with fines by total amount
            List<Map.Entry<String, Double>> keys = fineMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .toList();

            //Write open tag
            fw.write("<fines>\n");

            //Write each fine with its total amount
            for (Map.Entry<String, Double> entry : keys) {
                writeXMLString(fw, entry);
            }

            //Write end tag
            fw.write("</fines>");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {

                    //Close streams
                    fw.flush();
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Function to write all fines with total amounts
    private static void writeXMLString(FileWriter fw, Map.Entry<String, Double> entry) throws IOException {
        fw.write("\t<fine>\n\t\t<type>");
        fw.write(entry.getKey());
        fw.write("</type>\n");
        fw.write("\t\t<total_amount>");
        fw.write(String.valueOf(entry.getValue()));
        fw.write("</total_amount>\n\t</fine>\n");
        fw.flush();
    }

}
