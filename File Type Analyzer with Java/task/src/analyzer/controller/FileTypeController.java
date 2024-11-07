package analyzer.controller;

import analyzer.domain.PatternEntry;
import analyzer.model.FileTypeModel;
import analyzer.model.PatternReader;
import analyzer.view.FileTypeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileTypeController {
    private FileTypeView view;
    private List<PatternEntry> patterns;

    public FileTypeController(FileTypeView view, String patternsFile) {
        this.view = view;
        this.patterns = PatternReader.readPatterns(patternsFile);
    }

    public void checkFileTypes(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files == null) {
            view.displayError("Invalid folder path or empty folder.");
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<String>> futures = new ArrayList<>();

        for (File file : files) {
            if (file.isFile()) {
                FileTypeModel model = new FileTypeModel(file.getPath(), patterns);
                 futures.add(executor.submit(model));
            }
        }

        for (Future<String> future : futures) {
            try {
                String message = future.get();
                view.displayMessage(message);
            } catch (Exception e) {
                view.displayError("An error occurred: " + e.getMessage());
            }
        }

        executor.shutdown();
    }


}