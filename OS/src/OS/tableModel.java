package OS;
import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
//���ڽ���Ĺ��캯��
public class tableModel extends AbstractTableModel {
    private Vector content = null;
    private String[] title_name = { "�ļ���", "�ļ�·��", "�ļ�����", "�ļ���С", "�������ʱ��"};

    public tableModel(){
        content = new Vector();
    }

    //����еĺ���
    public void addRow(myFiles myFile){
        Vector v = new Vector();
        //�ļ���С����ʽ������������λС������
        DecimalFormat format=new DecimalFormat("#0.00");
        v.add(0, myFile.getFileName());//��һ���ļ�������
        v.add(1, myFile.getFilePath());//�ڶ����ļ���·��
        if (myFile.getMyFile().isFile()){
            v.add(2, ".txt�ı��ļ�");//�������ļ��ĸ�ʽ���̶�λFILE����ʱ������޸�
            v.add(3, format.format(myFile.getSpace()));//�ļ��ĸ�ʽ��С��ʾ
        }
        else{
            v.add(2, "�ļ���");//��������ʾDirectory���̶�
            v.add(3, "-");//��������ʾ��-������ʾû��
        }
        long time = myFile.getMyFile().lastModified();
        String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(time));//��õ����е�ʱ�����
        v.add(4, ctime);//�����е�ʱ����ʾ
        content.add(v);//����������������һ�ļ�������
    }

    public void removeRow(String name) {
    	//�ļ�ɾ��ʱȥ�����е�ֵ
        for (int i = 0; i < content.size(); i++){
            if (((Vector)content.get(i)).get(0).equals(name)){
                content.remove(i);
                break;
            }
        }
    }

    public void removeRows(int row, int count){
    	//ȥ������Ԫ��
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
    	//��ȡ�е�����
        return title_name[col];//title_name = { "File Name", "File Path", "File Type", "File Volume/KB", "Last Update"};
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        return false;
    }

    @Override
    public int getRowCount() {
    	//�����ļ����ж����ļ�
        return content.size();
    }

    @Override
    public int getColumnCount() {
    	//����еĳ���
        return title_name.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return ((Vector) content.get(rowIndex)).get(columnIndex);
    }
}
