package OS;
import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
//窗口界面的构造函数
public class tableModel extends AbstractTableModel {
    private Vector content = null;
    private String[] title_name = { "文件名", "文件路径", "文件类型", "文件大小", "最近更改时间"};

    public tableModel(){
        content = new Vector();
    }

    //添加行的函数
    public void addRow(myFiles myFile){
        Vector v = new Vector();
        //文件大小，格式以整数后面两位小数构成
        DecimalFormat format=new DecimalFormat("#0.00");
        v.add(0, myFile.getFileName());//第一列文件的名字
        v.add(1, myFile.getFilePath());//第二列文件的路径
        if (myFile.getMyFile().isFile()){
            v.add(2, ".txt文本文件");//第三列文件的格式，固定位FILE，到时候可以修改
            v.add(3, format.format(myFile.getSpace()));//文件的格式大小显示
        }
        else{
            v.add(2, "文件夹");//第三列显示Directory，固定
            v.add(3, "-");//第四列显示“-”，表示没有
        }
        long time = myFile.getMyFile().lastModified();
        String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(time));//获得第五列的时间概述
        v.add(4, ctime);//第五列的时间显示
        content.add(v);//向该行向量中添加这一文件的属性
    }

    public void removeRow(String name) {
    	//文件删除时去除该行的值
        for (int i = 0; i < content.size(); i++){
            if (((Vector)content.get(i)).get(0).equals(name)){
                content.remove(i);
                break;
            }
        }
    }

    public void removeRows(int row, int count){
    	//去除该行元素
        for (int i = 0; i < count; i++){
            if (content.size() > row){
                content.remove(row);
            }
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int colIndex){
        ((Vector) content.get(rowIndex)).remove(colIndex);
        ((Vector) content.get(rowIndex)).add(colIndex, value);
        this.fireTableCellUpdated(rowIndex, colIndex);
    }

    public String getColumnName(int col) {
    	//获取列的名字
        return title_name[col];//title_name = { "File Name", "File Path", "File Type", "File Volume/KB", "Last Update"};
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        return false;
    }

    @Override
    public int getRowCount() {
    	//返回文件夹有多少文件
        return content.size();
    }

    @Override
    public int getColumnCount() {
    	//获得列的长度
        return title_name.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return ((Vector) content.get(rowIndex)).get(columnIndex);
    }
}
