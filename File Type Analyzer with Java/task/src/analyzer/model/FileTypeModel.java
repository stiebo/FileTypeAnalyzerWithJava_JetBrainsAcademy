package analyzer.model;

import analyzer.domain.PatternEntry;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public class FileTypeModel implements Callable<String> {
    private String filePath;
    private List<PatternEntry> patterns;
    private static final int BASE = 256; // Number of characters in the input alphabet
    private static final int MOD = 101; // A prime number for hashing
    private static final int BUFFER_SIZE = 4096; // Size of the byte buffer

    public FileTypeModel(String filePath, List<PatternEntry> patterns) {
        this.filePath = filePath;
        this.patterns = patterns;
    }

    @Override
    public String call() throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            return "File '" + filePath + "' not found.";
        }

        StringBuilder result = new StringBuilder(file.getName() + ": Unknown file type");
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                for (PatternEntry patternEntry : patterns) {
                    if (rabinKarpSearch(buffer, bytesRead, patternEntry.getPatternBytes(), BASE, MOD)) {
                        result = new StringBuilder(file.getName() + ": " + patternEntry.getResult());
                        return result.toString(); // Return once a match is found
                    }
                }
            }
        }
        return result.toString();
    }

    // https://www.geeksforgeeks.org/java-program-for-rabin-karp-algorithm-for-pattern-searching/
    static boolean rabinKarpSearch(byte[] txt, int txt_length, byte[] pat, int d, int q) {
        int M = pat.length;
        int N = txt_length;
        int i, j;
        int p = 0; // hash value for pattern
        int t = 0; // hash value for txt
        int h = 1;

        // The value of h would be "pow(d, M-1)%q"
        for (i = 0; i < M - 1; i++)
            h = (h * d) % q;

        // Calculate the hash value of pattern and first
        // window of text
        for (i = 0; i < M; i++) {
            p = (d * p + pat[i]) % q;
            t = (d * t + txt[i]) % q;
        }

        // Slide the pattern over text one by one
        for (i = 0; i <= N - M; i++) {

            // Check the hash values of current window of text
            // and pattern. If the hash values match then only
            // check for characters one by one
            if (p == t) {
                /* Check for characters one by one */
                for (j = 0; j < M; j++) {
                    if (txt[i + j] != pat[j])
                        break;
                }

                // if p == t and pat[0...M-1] = txt[i, i+1, ...i+M-1]
                if (j == M)
                    return true;
            }

            // Calculate hash value for next window of text: Remove
            // leading digit, add trailing digit
            if (i < N - M) {
                t = (d * (t - txt[i] * h) + txt[i + M]) % q;

                // We might get negative value of t, converting it
                // to positive
                if (t < 0)
                    t = (t + q);
            }
        }
        return false;
    }
}