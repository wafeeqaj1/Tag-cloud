import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This program will take a file as input and create a tagCloud that changes
 * font relative to the count of each word.
 *
 * @author jaleel.5, mccarthy.621 & Zhang.11341
 *
 */
public final class TagCloud10 {

    /**
     * Compare {@code String}s in lexicographic order.
     */
    private static class StringLT implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {

            return o1.compareToIgnoreCase(o2);
        }
    }

    /**
     * Compare {@code Integer}s in descending order.
     */
    private static class IntegerLT
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloud10() {
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param charSet
     *            the {@code Set} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    public static void generateElements(String str, Set<Character> charSet) {
        assert str != null : "Violation of: str is not null";
        assert charSet != null : "Violation of: charSet is not null";

        //clears any element present in the set
        charSet.clear();

        //Loops for every character in the given String str.
        for (int i = 0; i < str.length(); i++) {

            //Takes the character at position i of the String
            Character ch = str.charAt(i);

            //if the set does not contain the character then it will add it to
            //the set.
            if (!charSet.contains(ch)) {
                charSet.add(ch);
            }
        }
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    public static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        //Declaring a boolean and empty string.
        boolean check = false;
        String str1 = "";

        //Starts with the given position and loops until the end of text is reached
        int i = position;
        while (i < text.length() && !check) {

            //Takes the character at each position
            Character ch = text.charAt(i);

            //Checking whether separator set has the character
            if (separators.contains(ch)) {

                //if the position is equal to the starting point then the substring
                //end position will be increased by one
                if (position == i) {
                    str1 = text.substring(position, (i + 1));

                    //if not the i is used as the ending point
                } else {
                    str1 = text.substring(position, (i));
                }

                //true because the separator is read.
                check = true;

                //if not separator is present the substring will be until the
                //separator is encountered.
            } else {
                str1 = text.substring(position);
            }
            i++;
        }

        return str1;
    }

    /**
     * Outputs the opening HTML tags in the output file.
     *
     * @param out
     *            the output stream to HTML file
     * @param fileName
     *            the input file name
     * @updates out.content
     * @requires outputFolder is present, out.isOpen()
     * @ensures out.content = #out.content * [the HTML "opening" tags],
     *          generates a table for each word and its count
     *
     */

    public static void indexFile(PrintWriter out, String fileName) {

        assert out != null : "Violation of: out is not null";
        assert fileName != null : "Violation of: fileName is not null";

        //Printing header tags for the index html file
        out.println("<html>");
        out.println("<head>");
        out.println("<title> Words Counted in " + fileName + " </title>");
        out.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web-sw2"
                        + "/assignments/projects/tag-cloud-generator/data/tagcloud.css\" "
                        + "rel=\"stylesheet\" type=\"text/css\">");
        out.println(
                "<link href=\"tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");

        out.println("</head>");
        out.println("<body>");
        out.println("    <h2> Words Counted in " + fileName + " </h2>");
        out.println("    <hr/>");

        out.println("    <div class=\"cdiv\">");
        out.println("    <p class=\"cbox\">");

    }

    /**
     * Updates the word count in the map.
     *
     * @param str
     *            the given {@code String}
     * @param wordMap
     *            the {@code Set} to be replaced
     * @return total number of same word
     * @replaces charSet
     * @ensures charSet = entries(str)
     */

    public static int wordCount(String str,
            SortedMap<String, Integer> wordMap) {
        assert str != null : "Violation of: str is not null";
        assert wordMap != null : "Violation of: wordMap is not null";

        int num = 1;
        if (wordMap.containsKey(str)) {
            num = wordMap.get(str);
            num++;

        }
        return num;
    }

    /**
     * If a file is inputed, each word in it will be added to a queue(no
     * duplicates) and a map with the number of occurrence.
     *
     * @param input
     *            the input stream
     * @param wordMap
     *            map to add the words and the count
     * @updates q, terms
     * @requires <pre>
     * input.isOpen()
     * </pre>
     * @ensures <pre>
     * input.is_open and
     *
     * q={contains words from the file} and
     * terms=(words, count)
     * </pre>
     */
    public static void getWord(BufferedReader input,
            SortedMap<String, Integer> wordMap) {
        assert input != null : "Violation of: input is not null";
        assert wordMap != null : "Violation of: terms is not null";

        //Define separator characters for testing
        final String separatorStr = " \"\t\n\r,-.!?';:/()*_\\[]{}|<>~=";

        Set<Character> separatorSet = new HashSet<Character>();
        generateElements(separatorStr, separatorSet);

        //Checking whether the file end is not reached
        try {
            if (input.ready()) {

                int position = 0;

                //Takes each line of the file separately
                String line = "";

                try {
                    line = input.readLine();
                } catch (IOException e) {
                    System.err.println("Error reading file.");
                }

                if (line != null) {
                    //This loop until every word or separator of str is compared.
                    while (position < line.length()) {

                        //Takes a single word or separator from line
                        String word = nextWordOrSeparator(line, position,
                                separatorSet);

                        //Checking whether no separator present from str.
                        if (!separatorSet.contains(word.charAt(0))) {

                            String wordLower = word.toLowerCase();

                            //Gets the count for one specific word
                            int num = wordCount(wordLower, wordMap);

                            //Suppose the word has already been mapped it will
                            //replace the count value else the word will be added
                            //to the queue and the map
                            if (wordMap.containsKey(wordLower)) {
                                wordMap.replace(wordLower, num);
                            } else {
                                wordMap.put(wordLower, num);
                            }
                        }
                        //based on the length of word or separator position
                        //be increased so it will read from the next word.
                        position += word.length();
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error accessing file");
        }
    }

    /**
     * Returns the font size for each word relative to the minimum and maximum
     * count.
     *
     * @param max
     *            highest count
     * @param min
     *            lowest count
     * @param wordNum
     *            number of words to include
     * @return font size
     */
    public static int fontSize(int max, int min, int wordNum) {
        assert 0 <= max : "Violation of: 0 <= max";
        assert 0 <= min : "Violation of: 0 <= min";
        assert 0 <= wordNum : "Violation of: 0 <= wordNum";

        //defining the largest and smallest font size
        final int largeFont = 48;
        final int minFont = 11;
        double d = 0;
        int fontDiff = largeFont - minFont;

        //To ensure that neither denominator or numerator become 0,
        //this if-else statement is there.
        if (max != min && min != wordNum) {
            d = Math.ceil((fontDiff * (wordNum - min)) / (max - min));
        } else if (max == min && min != wordNum) {
            d = Math.ceil((fontDiff * (wordNum - min)));
        } else if (min == wordNum && max != min) {
            d = Math.ceil(fontDiff / (max - min));
        } else {
            d = fontDiff;
        }

        //changes double to int and adds minimum font so the font
        //size not below zero
        int i = (int) d;
        int f = i + minFont;

        return f;

    }

    /**
     * Outputs font tag and closing tags.
     *
     * @param out
     *            the output stream
     * @param map2
     *            sorted map of words
     * @param max
     *            highest count
     * @param min
     *            lowest count
     * @requires out.isOpen()
     */

    public static void outputTag(PrintWriter out,
            SortedMap<String, Integer> map2, int max, int min) {
        assert out != null : "Violation of: out is not null";
        assert map2 != null : "Violation of: map2 is not null";

        Set<Entry<String, Integer>> ent = map2.entrySet();
        //Takes the sorted list and writes the word in the specific frequency
        for (Entry<String, Integer> element : ent) {
            String word = element.getKey();
            int freq = element.getValue();
            int font = fontSize(max, min, freq);
            out.println(" <span style=\"cursor:default\" class=\"f" + font
                    + "\" title=\"count: " + freq + "\">" + word + "</span>");

        }

        //closing tags for the index html

        out.println("    </p>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Sorts the map in count descending order and gets the required number of
     * words. It will then sort the words in ascending order.
     *
     * @param wordMap
     *            the {@code Set} to be replaced
     * @param wordNum
     *            number of words to include
     * @param out
     *            the output stream
     * @updates wordMap
     *
     */

    public static void valueSort(SortedMap<String, Integer> wordMap,
            int wordNum, PrintWriter out) {

        assert out != null : "Violation of: out is not null";
        assert wordMap != null : "Violation of: wordMap is not null";
        assert 0 <= wordNum : "Violation of: 0 <= wordNum";

        Comparator<String> cs = new StringLT();
        //Creating sorting variable for words and their counts
        SortedMap<String, Integer> map2 = new TreeMap<String, Integer>(cs);

        Comparator<Map.Entry<String, Integer>> ci = new IntegerLT();

        List<Entry<String, Integer>> list = new ArrayList<>(wordMap.entrySet());
        Collections.sort(list, ci);
        int listSize = list.size();

        Map.Entry<String, Integer> maxPair = list.remove(0);
        int max = maxPair.getValue();
        map2.put(maxPair.getKey(), maxPair.getValue());

        //Checks if the requested number of words is more than the number
        //of words in the file. if so, it will loop until the required number
        // of elements are added to the new map. If not it will loop until
        //the number of words present and add it to the map
        if (listSize > wordNum) {
            for (int i = 0; i < wordNum - 2; i++) {
                Map.Entry<String, Integer> addPair = list.remove(0);
                map2.put(addPair.getKey(), addPair.getValue());
            }
        } else {
            for (int i = 0; i < listSize - 2; i++) {
                Map.Entry<String, Integer> addPair = list.remove(0);
                map2.put(addPair.getKey(), addPair.getValue());
            }
        }

        //Suppose only one word is in the file, the minimum and maximum
        //value will be the same. If not, the minimum will be last element
        //of the sorted list
        int min = max;
        if (list.size() > 0 && wordNum != 1) {
            Map.Entry<String, Integer> minPair = list.remove(0);
            min = minPair.getValue();
            map2.put(minPair.getKey(), minPair.getValue());
        }

        //this will sort the words in alphabetic order

        outputTag(out, map2, max, min);

    }

    /**
     * Requests valid integer input until valid input is entered where n is the
     * number of words to be generated in the word cloud, and is less than the
     * total number of different words input.
     *
     * @param in
     *            input stream
     * @requires in.IsOpen()
     * @return Integer n where n > 0 && n < max
     */
    private static int nCheck(BufferedReader in) {
        int n = -1;

        while (n < 0) {
            System.out.print(
                    "Please enter the number of words to be included in the "
                            + "generated tag cloud (please enter a positive "
                            + "integer): ");
            String nTemp = "";
            try {
                nTemp = in.readLine();
            } catch (Exception invalidReadLine) {
                System.err.print("Error accepting input.");
            }

            if (nTemp != null && nTemp.length() > 0) {
                boolean isDigit = true;
                for (int i = 0; i < nTemp.length(); i++) {
                    if (isDigit) {
                        isDigit = Character.isDigit(nTemp.charAt(i));
                    }
                }
                if (isDigit) {
                    int intTemp = Integer.parseInt(nTemp);
                    n = intTemp;
                } else {
                    System.out.println("");
                    System.out.println("Invalid input.");
                }
            } else {
                System.out.println("");
                System.out.println("Invalid input.");
            }

        }
        return n;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        /*
         * initiate input and output streams.
         */

        BufferedReader file;
        BufferedReader in;
        PrintWriter html;
        String fileInput;

        in = new BufferedReader(new InputStreamReader(System.in));

        try {
            //input file request and validation of path
            System.out.print("Please enter input file name: ");
            fileInput = in.readLine();
            file = new BufferedReader(new FileReader(fileInput));

            //out file request and validation of path
            System.out.print("Please enter output file name: ");
            String htmlOutput = in.readLine();
            html = new PrintWriter(
                    new BufferedWriter(new FileWriter(htmlOutput)));
        } catch (IOException e) {
            System.err.println("Error opening the input file");
            return;
        }

        /*
         * Get number of words to display.
         */

        //Declaring a new map variable
        SortedMap<String, Integer> wordMap = new TreeMap<String, Integer>();
        wordMap.clear();

        try {
            /*
             * Get words from input and add them to maps with their word as key
             * and count as values
             */
            while (file.ready()) {
                getWord(file, wordMap);
            }

        } catch (IOException e) {
            System.err.println("Error reading from the file");
        }

        //outputs the necessary tags for the index HTML file
        indexFile(html, fileInput);

        int n = nCheck(in);

        /*
         * Sort words into non-decreasing lexicographic order and output it to
         * the HTML file
         */
        valueSort(wordMap, n, html);

        try {
            /*
             * Close input and output streams
             */
            in.close();
            file.close();
            html.close();
        } catch (IOException e) {
            System.err.println("Error closing file");

        }

    }

}
