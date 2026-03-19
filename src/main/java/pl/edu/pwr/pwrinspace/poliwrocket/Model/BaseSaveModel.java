package pl.edu.pwr.pwrinspace.poliwrocket.Model;

public abstract class BaseSaveModel {

    private final String path;
    private final String fileName;
    private final String tempFileName;
    private final String persistPrefix;

    public BaseSaveModel(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
        this.tempFileName = fileName.replace(".yaml", "") + "_temp" + ".yaml";
        this.persistPrefix = "BAD_";
    }

    public BaseSaveModel(String path, String fileName, String persistPrefix, String tempPath) {
        this.path = path;
        this.fileName = fileName;
        this.persistPrefix = persistPrefix;
        this.tempFileName = tempPath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getTempFileName() {
        return tempFileName;
    }

    public String getPath() {
        return path;
    }

    public String getPersistPrefix() {
        return persistPrefix;
    }
}
