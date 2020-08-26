package OS;
import java.io.File;
public class myFiles {
    private int blockName;//定义文件名，整型
    private File myFile;//定义文件
    private String fileName;//文件的名称
    double space;

    //构造函数
    public myFiles(File myFile, int blockName, double capacity){
        space = capacity;
        this.myFile = myFile;
        this.blockName = blockName;
        fileName = myFile.getName();//File的库函数
    }

    public String getFileName(){
    	//返回文件名称
        return myFile.getName();
    }

    public String getFilePath(){
    	//返回文件路径
        return myFile.toString();
    }

    public boolean renameFile(String name){
    	//重命名文件
        String c = myFile.getParent();
        File mm = new File(c + File.separator + name);
        if (myFile.renameTo(mm)){//重命名文件的库函数
            myFile = mm;
            fileName = name;
            return true;
        }else{
            return false;
        }
    }

    public File getMyFile(){
    	//获得文件
        return myFile;
    }

    public int getBlockName() {
    	//获得文件夹的名字
        return blockName;
    }

    public double getSpace() {
    	//获得文件空间大小
        return space;
    }

    @Override
    public String toString(){
    	//重写函数，获得名称
        return fileName;
    }
}
