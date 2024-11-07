package analyzer;

import analyzer.controller.FileTypeController;
import analyzer.view.FileTypeView;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Main <folder_path> <patterns_file>");
            return;
        }

        String folderPath = args[0];
        String patternsFile = args[1];

        FileTypeView view = new FileTypeView();
        FileTypeController controller = new FileTypeController(view, patternsFile);
        controller.checkFileTypes(folderPath);
    }
}