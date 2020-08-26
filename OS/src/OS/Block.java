package OS;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//�ļ����ƿ飬Ҳ���Ƕ����ļ���
public class Block {
    private String blockName;//����1~10
    private File blockFile;
    private File blockBitMap;
    private File recover;
    private FileWriter bitWriter;
    private FileWriter recoverWriter;
    private int fileNum;
    private double space;
    public int [][] bitmap = new int[32][32];//�����ά����32X32
    private Map<String, int[][] > filesBit = new HashMap<String, int[][]>();//�ļ��Ĺ�ϣɢ��ͼ
    private ArrayList<File> files = new ArrayList<File>();//�����ļ��б�
    //���幹�캯��Block
    public Block(String name, File file, boolean rec) throws IOException {
        blockName = name;//int�͵��ļ����־
        blockFile = file;//�ļ���ֵ
        blockBitMap = new File(blockFile.getPath() + File.separator  + "UserData.txt");//ָ���ļ����ڵ�λ��  File.separatorΪ��/��
        recover = new File(blockFile.getPath() + File.separator + "recover.txt");//ָ���ļ����ڵ�λ��  File.separatorΪ��/��
        if (!rec) {
            space = 0;
            fileNum = 0;
            blockFile.mkdir();//�����е��ļ����´����µ��ļ���
            blockBitMap.createNewFile();
            bitWriter = new FileWriter(blockBitMap);//��ָ��λ�ô����ļ�
            for (int i = 0; i < 32; i++) {
                for (int k = 0; k < 32; k++) {
                    bitmap[i][k] = 0;
                    bitWriter.write("0");
                }
                bitWriter.write("\r\n");//д��س�����
            }
            bitWriter.flush();//���þ�����ջ�����������ļ�д�������

            recover.createNewFile();
            recoverWriter = new FileWriter(recover);
            recoverWriter.write(String.valueOf(space) + "\r\n");
            recoverWriter.write(String.valueOf(fileNum) + "\r\n");
            for (int i = 0; i < 32; i++) {
                for (int k = 0; k < 32; k++) {
                    if (bitmap[i][k] == 0) {
                        recoverWriter.write("0\r\n");//д��0�س�����
                    } else {
                        recoverWriter.write("1\r\n");////д��1�س�����
                    }
                }
            }
            recoverWriter.flush();//���þ�����ջ�����������ļ�д�������
        }else{
        	//�׳��ļ������쳣
            try {
            	//�ָ��ĵ�����ʼ
                BufferedReader reader = new BufferedReader(new FileReader(recover));
                space = Double.parseDouble(reader.readLine());
                fileNum = Integer.parseInt(reader.readLine());
                for (int i = 0; i < 32; i++) {
                    for (int k = 0; k < 32; k++) {
                        if (Integer.parseInt(reader.readLine()) == 0) {
                            bitmap[i][k] = 0;
                        } else {
                            bitmap[i][k] = 1;
                        }
                    }
                }
                String temp;
                while ((temp = reader.readLine()) != null) {
                    File myFile = new File(blockFile.getPath() + File.separator + temp);
                    files.add(myFile);
                    int[][] tempBit = new int[32][32];
                    for (int i = 0; i < 32; i++) {
                        for (int k = 0; k < 32; k++) {
                            if (Integer.parseInt(reader.readLine()) == 0) {
                                tempBit[i][k] = 0;
                            } else {
                                tempBit[i][k] = 1;
                            }
                        }
                    }
                    filesBit.put(myFile.getName(), tempBit);
                }
                reader.close();
            }catch (Exception e){
            	 JOptionPane.showMessageDialog(null, "����ļ��Ѿ������ٴ����ˡ������ѡ������һ���ط�ȥ��������ɾ�����е��ļ�������","Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    public File getBlockFile(){
        return blockFile;
    }

    public void putFCB(File file, double capacity) throws IOException {
    	//�ļ�����
    	//�Լ�д���ļ������ݣ�����ʱ��Ȳ���
    	FileWriter newFileWriter = new FileWriter(file);
        newFileWriter.write("File\r\n");
        newFileWriter.write(String.valueOf(capacity) + "\r\n");
        newFileWriter.write("Name: " + file.getName() + "\r\n");
        newFileWriter.write("Path: " + file.getPath() + "\r\n");
        String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(file.lastModified()));
        newFileWriter.write("Date last updated: " + ctime + "\r\n");
        newFileWriter.close();
    }


    public void rewriteBitMap() throws IOException {
    	//�ָ��ļ�re
    	//����¼д��BitMap&&Fat.txt��
    	
        bitWriter = new FileWriter(blockBitMap);
        bitWriter.write("");
        for (int i = 0; i < 32;i++){
            for (int k = 0; k < 32; k++){
                if (bitmap[i][k] == 0){
                    bitWriter.write("0");
                }else{
                    bitWriter.write("1");
                }
            }
            bitWriter.write("\r\n");
        }
        for (int i = 0; i < files.size(); i++){
            bitWriter.write(files.get(i).getName() + ":");
            for (int k = 0; k < 32; k++){
                for (int j = 0; j < 32; j++){
                    try {
                        if (filesBit.get(files.get(i).getName())[k][j] == 1) {
                            bitWriter.write(String.valueOf(k * 32 + j) + " ");
                        }
                    }catch (Exception e){
                        System.out.println("wrong");
                    }
                }
            }
            bitWriter.write("\r\n");
        }
        bitWriter.flush();
    }

    public void rewriteRecoverWriter() throws IOException{
    	//�ָ�recover�ļ�
    	
        recoverWriter = new FileWriter(recover);
        recoverWriter.write("");

        recoverWriter.write(String.valueOf(space) + "\r\n");
        recoverWriter.write(String.valueOf(fileNum) + "\r\n");
        for (int i = 0; i < 32; i++){
            for (int k = 0; k < 32; k++){
                if (bitmap[i][k] == 0){
                    recoverWriter.write("0\r\n");
                }else{
                    recoverWriter.write("1\r\n");
                }
            }
        }
        for (int i = 0; i < files.size(); i++){
            recoverWriter.write(files.get(i).getName() + "\r\n");
            int [][] bitTemp = filesBit.get(files.get(i).getName());
            for (int k = 0; k < 32; k++){
                for (int j = 0; j < 32; j++){
                    if (bitTemp[k][j] == 0){
                        recoverWriter.write("0\r\n");
                    }else {
                        recoverWriter.write("1\r\n");
                    }
                }
            }
        }
        recoverWriter.flush();
    }

    public boolean createFile(File file, double capacity) throws IOException {
    	//�����ļ�
        files.add(file);
        file.createNewFile();
        int cap[][] = new int[32][32];
        for (int i = 0; i < 32; i++){
            for (int k = 0; k < 32; k++)
                cap[i][k] = 0;
        }
        BufferedReader in = new BufferedReader(new FileReader(blockBitMap));
        int count = (int) capacity;
        for (int i = 0; i < 32; i++){
            String line  = in.readLine();
            for (int k = 0; k < 32; k++){
                if (count > 0) {
                    if (line.charAt(k) == '0') {
                        count--;
                        cap[i][k] = 1;
                        bitmap[i][k] = 1;
                    }
                }
            }
        }
        if (count > 0){
            JOptionPane.showMessageDialog(null, "�洢�ռ��Ѿ������ˣ�����", "ʧ��", JOptionPane.ERROR_MESSAGE);
            file.delete();
            for (int i = 0; i < 32; i++){
                for (int k = 0; k < 32; k++){
                    if (cap[i][k] == 1){
                        bitmap[i][k] = 0;
                    }
                }
            }
            return false;
        }else{
            fileNum++;
            space += capacity;
            filesBit.put(file.getName(), cap);
            rewriteBitMap();
            rewriteRecoverWriter();
            // Put FCB
            putFCB(file, capacity);

            return true;
        }
    }

    public boolean deleteFile(File file, double capacity){
    	//ɾ���ļ����ļ��ı������ͣ���Щ�ļ���Ȩ�޲���ɾ��
        if (file.getName().equals("user") || file.getName().equals("UserData.txt")|| file.getName().equals("recover.txt")){
            JOptionPane.showMessageDialog(null, "����ļ��Ǳ����ģ�����", "ɾ��ʧ��", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try{
            if (file.isFile()){
                try {
                    file.delete();
                }catch (Exception e){
                    e.printStackTrace();
                }
                space -= capacity;
                fileNum--;
                int[][] fileStore = filesBit.get(file.getName());
                for (int i = 0; i < 32; i++){
                    for (int k = 0; k < 32; k++){
                        if (bitmap[i][k] == 1 && fileStore[i][k] == 1){
                            bitmap[i][k] = 0;
                        }
                    }
                }
                filesBit.remove(file.getName());
                for (int i = 0; i < files.size(); i++){
                    if (files.get(i).getName().equals(file.getName())){
                        files.remove(i);
                        break;
                    }
                }
            }else{
                File [] files = file.listFiles();
                for(File myFile : files){
                    deleteFile(myFile, capacity);
                }
                while(file.exists()) {
                    file.delete();
                }
            }
            return true;
        }catch (Exception e){
            System.out.println("fail");
            return false;
        }
    }

    public boolean renameFile(File file, String name, double capacity) throws IOException {
    	//�������ļ�����Щ�ļ���Ȩ�޲���������
        String oldName = file.getName();
        int[][] tempBit = filesBit.get(oldName);
        String c = file.getParent();
        File mm;
        if(file.isFile()) {
            mm = new File(c + File.separator + name + ".txt");
            if (file.renameTo(mm)){
                file = mm;
                filesBit.remove(oldName);
                filesBit.put(file.getName(), tempBit);
                // Put FCB
                putFCB(file, capacity);
                for (int i = 0; i < files.size(); i++){
                    if (files.get(i).getName().equals(oldName)){
                        files.remove(i);
                        files.add(file);
                        break;
                    }
                }
                rewriteBitMap();
                rewriteRecoverWriter();
                return true;
            }else{
                return false;
            }
        }
        else {
            mm = new File(c + File.separator + name);
            file.renameTo(mm);
            return true;
        }
    }

    public int getFileNum() {
    	//����ļ�������
        return fileNum;
    }

    public double getSpace() {
    	//����ļ��Ŀռ�
        return space;
    }
}
