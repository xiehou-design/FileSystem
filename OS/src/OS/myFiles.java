package OS;
import java.io.File;
public class myFiles {
    private int blockName;//�����ļ���������
    private File myFile;//�����ļ�
    private String fileName;//�ļ�������
    double space;

    //���캯��
    public myFiles(File myFile, int blockName, double capacity){
        space = capacity;
        this.myFile = myFile;
        this.blockName = blockName;
        fileName = myFile.getName();//File�Ŀ⺯��
    }

    public String getFileName(){
    	//�����ļ�����
        return myFile.getName();
    }

    public String getFilePath(){
    	//�����ļ�·��
        return myFile.toString();
    }

    public boolean renameFile(String name){
    	//�������ļ�
        String c = myFile.getParent();
        File mm = new File(c + File.separator + name);
        if (myFile.renameTo(mm)){//�������ļ��Ŀ⺯��
            myFile = mm;
            fileName = name;
            return true;
        }else{
            return false;
        }
    }

    public File getMyFile(){
    	//����ļ�
        return myFile;
    }

    public int getBlockName() {
    	//����ļ��е�����
        return blockName;
    }

    public double getSpace() {
    	//����ļ��ռ��С
        return space;
    }

    @Override
    public String toString(){
    	//��д�������������
        return fileName;
    }
}
