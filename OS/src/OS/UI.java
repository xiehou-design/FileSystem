
package OS;
import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
public class UI extends JFrame {
    private JTree tree;
    private JScrollPane treePane;
    private JScrollPane tablePane;
    private tableModel model = new tableModel();
    private JTable fileTable;
    private JPopupMenu myMenu = new JPopupMenu();
    private JFileChooser chooser;
    private File rootFile;//���ļ�
    private File readMe;//���ļ�
    private FileWriter readMeWrite;//д�ļ�
    private Block block1;//ʮ���ļ�Ŀ¼���ļ���
    //�º�����ʾ������Ϣ
    private ArrayList<Block> blocks = new ArrayList<Block>();
    private JLabel blockName = new JLabel("�ļ��и�����");
    private JLabel nameField = new JLabel();
    private JLabel haveUsed = new JLabel("�ռ���ʹ�ô�С��");
    private JLabel usedField = new JLabel();
    private JLabel freeYet = new JLabel("ʣ��ռ��С��");
    private JLabel freeField = new JLabel();
    private JLabel fileNum = new JLabel("�ı��ļ�������");
    private JLabel fileNumField = new JLabel();
    private JTextField searchLine = new JTextField();
    // ɾ��һ���ļ�Ŀ¼
    public static void deleteDirectory(String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            return;
        }
        if(file.isFile()){
            file.delete();
        }else if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File myfile : files) {
                deleteDirectory(filePath + File.separator + myfile.getName());
            }
            file.delete();
        }
    }

    //����ļ���С
    public double getSpace(File file){
        double space = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine();
            space = Double.parseDouble(reader.readLine());
            if (space > 1024){
                space = 0.0;
            }
            reader.close();
        } catch (Exception e){};
        return space;
    }

    // �����ļ�����Ϣ
    public void upDateBlock(Block currentBlock){
        fileNumField.setText(String.valueOf(currentBlock.getFileNum()));
        usedField.setText(String.valueOf(currentBlock.getSpace()) + " KB");
        freeField.setText(String.valueOf(1024 - currentBlock.getSpace()) + "KB");
    }

    // �����������ļ�
    public boolean searchFile(String fileName, File parent){
        File [] files = parent.listFiles();
        for (File myFile:files){
            if (myFile.getName().equals(fileName)){
                try {
                    if(Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(myFile);
                        return true;
                    }
                } catch (IOException e1) {
                	//û���ҵ��򵯿򷢳����棬�򷵻�true����
                    JOptionPane.showMessageDialog(null, myFile.getPath() + "��Ǹ����Щ���������ˣ�����", "û�гɹ����ļ�",
                            JOptionPane.ERROR_MESSAGE);
                    return true;
                }
            }
            //�ļ�����Ŀ¼���棬�����ļ��ǿɶ���
            if (myFile.isDirectory() && myFile.canRead()){
                if(searchFile(fileName, myFile)){
                    return true;
                }
            }
        }
        return false;
    }
     //����Ч��
    public UI() throws IOException {
        setTitle("File System");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ���к����ҳ��ѡ���ļ�����Ϊ��ʼ���ļ�λ��
        String path = File.listRoots()[0].getPath();//����·�����������ʹ��
        String rootPath = new String();//�����Ŀ¼
        chooser = new JFileChooser(path);
        chooser.setDialogTitle("��ѡ��һ���ļ�����Ϊ�ļ�ϵͳ�ĸ��ļ���");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setPreferredSize(new Dimension(800, 600));//���ô��ڴ�С
        int result = chooser.showOpenDialog(this);
        if (result == chooser.APPROVE_OPTION){
            System.out.println(chooser.getSelectedFile().getAbsolutePath());
            rootPath = chooser.getSelectedFile().getPath();
        }
        // ����һ�������Ŀռ�
        rootFile = new File(rootPath + File.separator + "FileSystem");
        readMe = new File(rootPath + File.separator + "FileSystem" + File.separator + "ReadMe.txt");

        boolean flag = true;
        
        // Ŀ¼ �Ĵ���
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(new myFiles(rootFile, 0, 10240));
        
         if (!rootFile.exists()) {
            flag = false;
            try {
                rootFile.mkdir();
                readMe.createNewFile();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "����ط���֧�ִ����ļ�������", "����", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
	
        //��ʼ���ļ���
        block1 = new Block("user", new File(rootFile.getPath() + File.separator + "user"), flag);
        blocks.add(block1);
        //�����ļ��Ĵ�С����������ӵ���ߵ�״̬���ϣ�Ŀ¼
        root.add(new DefaultMutableTreeNode(new myFiles(block1.getBlockFile(), 1, 1024.0)));
        model.addRow(new myFiles(block1.getBlockFile(), 1, 1024.0));
        ((DefaultMutableTreeNode)root.getChildAt(0)).add(new DefaultMutableTreeNode("temp"));
        fileTable = new JTable(model);
        fileTable.getTableHeader().setFont(new Font(Font.DIALOG,Font.CENTER_BASELINE,24));//��������
        fileTable.setSelectionBackground(Color.RED);//��ѡ�е���һ����ɫ

        fileTable.updateUI();//ˢ�½������ɫ

        final DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.putClientProperty("Jtree.lineStyle",  "Horizontal");
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        //�����������������ߵ�Ŀ¼�����ʱ
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode parent = null;
                TreePath parentPath = e.getPath();
                if (parentPath == null){
                    parent = root;
                }else{
                    parent = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
                }
                int blokName = ((myFiles)parent.getUserObject()).getBlockName();
                Block currentBlock = blocks.get(blokName - 1);
                if (parentPath == null){
                    parent = root;
                }else{
                    parent = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
                }

                nameField.setText(String.valueOf(blokName));
                upDateBlock(currentBlock);

                model.removeRows(0, model.getRowCount());
                File rootFile = new File(((myFiles)parent.getUserObject()).getFilePath());
                if (parent.getChildCount() > 0) {
                    File[] childFiles = rootFile.listFiles();

                    for (File file : childFiles) {
                        model.addRow(new myFiles(file, blokName, getSpace(file)));
                    }
                }
                else{
                    model.addRow(new myFiles(rootFile, blokName, getSpace(rootFile)));
                }
                fileTable.updateUI();

            }
        });
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                DefaultMutableTreeNode parent = null;
                TreePath parentPath = event.getPath();
                if (parentPath == null){
                    parent = root;
                }else{
                    parent = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
                }

                int blokName = ((myFiles)parent.getUserObject()).getBlockName();

                File rootFile = new File(((myFiles)parent.getUserObject()).getFilePath());
                File [] childFiles = rootFile.listFiles();

                model.removeRows(0, model.getRowCount());
                for (File myFile : childFiles){
                    DefaultMutableTreeNode node = null;
                    node = new DefaultMutableTreeNode(new myFiles(myFile, blokName, getSpace(myFile)));
                    if (myFile.isDirectory() && myFile.canRead()) {
                        node.add(new DefaultMutableTreeNode("temp"));
                    }

                    treeModel.insertNodeInto(node, parent,parent.getChildCount());
                    model.addRow(new myFiles(myFile, blokName, getSpace(myFile)));
                }
                if (parent.getChildAt(0).toString().equals("temp") && parent.getChildCount() != 1)
                    treeModel.removeNodeFromParent((MutableTreeNode) parent.getChildAt(0));
                fileTable.updateUI();
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                DefaultMutableTreeNode parent = null;
                TreePath parentPath = event.getPath();
                if (parentPath == null){
                    parent = root;
                }else{
                    parent = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
                }
                if (parent.getChildCount() > 0) {
                    int count = parent.getChildCount();
                    for (int i = count - 1; i >= 0; i--){
                        treeModel.removeNodeFromParent((MutableTreeNode) parent.getChildAt(i));
                    }
                    treeModel.insertNodeInto(new DefaultMutableTreeNode("temp"), parent,parent.getChildCount());
                }
                model.removeRows(0, model.getRowCount());
                fileTable.updateUI();
            }
        });
        treePane = new JScrollPane(tree);
        treePane.setPreferredSize(new Dimension(200, 400));
        add(treePane, BorderLayout.WEST);

        tablePane = new JScrollPane(fileTable);
        add(tablePane, BorderLayout.CENTER);


        // ���˫����һ���ļ�
        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1){
                    String fileName = ((String) model.getValueAt(fileTable.getSelectedRow(), 0));
                    String filePath = ((String) model.getValueAt(fileTable.getSelectedRow(), 1));
                    try {
                        if(Desktop.isDesktopSupported()) {
                            Desktop desktop = Desktop.getDesktop();
                            desktop.open(new File(filePath));
                        }
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "��Ǹ���򿪴��󣡣�!", "�򿪴���",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    JOptionPane.showMessageDialog(null, "File Name: " + fileName + "\n File Path: " + filePath, "content",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // �ļ����ĳ�ʼ��
        final JPopupMenu myMenu = new JPopupMenu();
        myMenu.setPreferredSize(new Dimension(300, 200));

        //����һ���ļ�
        JMenuItem createFileItem = new JMenuItem("����һ���ı��ļ�");
        createFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);

                String inputValue;
                double capacity;
                //������ʾ�������ļ�������
                JOptionPane inputPane = new JOptionPane();
                inputPane.setPreferredSize(new Dimension(600, 600));
                inputPane.setInputValue(JOptionPane.showInputDialog("�ļ�����"));
                if (inputPane.getInputValue() == null) {
                    return;
                }
                //�����ļ��Ĵ�С
                inputValue = inputPane.getInputValue().toString();
                inputPane.setInputValue(JOptionPane.showInputDialog("�ļ���С(KB):"));
                if (inputPane.getInputValue() == null) {
                    return;
                }
                capacity = Double.parseDouble(inputPane.getInputValue().toString());

                File newFile = new File(temp.getFilePath() + File.separator + inputValue + ".txt");//Ϊ�ļ�����·��
                //������ʾ���������,ʧ�ܻ��߳ɹ�
                if (!newFile.exists() && !inputValue.equals(null)){
                    try {
                        if (currentBlock.createFile(newFile, capacity)) {
                            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new myFiles(newFile, blokName, capacity));
                            model.removeRows(0, model.getRowCount());
                            model.addRow(new myFiles(newFile, blokName, capacity));
                            fileTable.updateUI();
                            upDateBlock(currentBlock);
                            JOptionPane.showMessageDialog(null, "�����ɹ��������´�����ļ�ȥ�鿴Ч������!", "�ɹ�", JOptionPane.DEFAULT_OPTION);
                        }
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "����ʧ��!!!", "����", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        myMenu.add(createFileItem);

        //����һ���ļ���
        JMenuItem createDirItem = new JMenuItem("����һ���ļ���");
        createDirItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);
                String inputValue = JOptionPane.showInputDialog("�ļ��е�����");
                if (inputValue == null) {
                    return;
                }
                File newDir = new File(temp.getFilePath() + File.separator + inputValue);
                if (newDir.exists())
                    deleteDirectory(newDir.getPath());
                try{
                    newDir.mkdir();
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new myFiles(newDir, blokName, 0));
                    newNode.add(new DefaultMutableTreeNode("temp"));
                    model.removeRows(0, model.getRowCount());
                    model.addRow(new myFiles(newDir, blokName, 0));
                    fileTable.updateUI();
                    upDateBlock(currentBlock);
                    JOptionPane.showMessageDialog(null, "�����ɹ��������´�����ļ��в��ܿ�������Ч��", "����", JOptionPane.DEFAULT_OPTION);
                }catch (Exception E){
                    JOptionPane.showMessageDialog(null, "����ʧ��!!!", "ʧ��", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        myMenu.add(createDirItem);

        //ɾ��һ���ļ������ļ���
        JMenuItem deleteItem = new JMenuItem("ɾ��");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);
                int choose = JOptionPane.showConfirmDialog(null, "��ȷ����ɾ������ļ���?", "ȷ��", JOptionPane.YES_NO_OPTION);
                if (choose == 0){
                    if (currentBlock.deleteFile(temp.getMyFile(), temp.getSpace())){
                        try {
                            currentBlock.rewriteBitMap();
                            currentBlock.rewriteRecoverWriter();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        upDateBlock(currentBlock);
                        JOptionPane.showMessageDialog(null, "ɾ���ɹ�!", "�ɹ�", JOptionPane.DEFAULT_OPTION);
                        JOptionPane.showMessageDialog(null, "ɾ���ɹ��������´�����ļ��в��ܿ���ɾ��Ч��", "ɾ��", JOptionPane.DEFAULT_OPTION);
                    }else{
                        JOptionPane.showMessageDialog(null, "ɾ��ʧ��!!!", "����", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        myMenu.add(deleteItem);

        //��ʽ��һ���ļ���
        JMenuItem formatItem = new JMenuItem("��ʽ��");
        formatItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);
                int choose = JOptionPane.showConfirmDialog(null, "����Ҫ����������ļ��У�", "ȷ��", JOptionPane.YES_NO_OPTION);
                if (choose == 0){
                    try{
                    if (temp.getMyFile().isDirectory()) {
                        for (File myfile : temp.getMyFile().listFiles()) {
                            currentBlock.deleteFile(myfile, getSpace(myfile));
                        }
                        upDateBlock(currentBlock);
                        JOptionPane.showMessageDialog(null, "��ʽ���ɹ��������´�����ļ��п�����Ч��������", "�ɹ�", JOptionPane.DEFAULT_OPTION);
                        currentBlock.rewriteBitMap();
                    }
                    }catch (Exception E1){
                        JOptionPane.showMessageDialog(null, "��ʽ��ʧ��!!!", "ʧ��", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        myMenu.add(formatItem);

        //�������ļ����ֻ����ļ�������
        JMenuItem renameItem = new JMenuItem("������");
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);

                String inputValue = null;
                JOptionPane inputPane = new JOptionPane();
                inputPane.setInputValue(JOptionPane.showInputDialog("�µ��ļ����ƣ�"));
                if (inputPane.getInputValue() == null) {
                    return;
                }
                inputValue = inputPane.getInputValue().toString();
                try {
                    currentBlock.renameFile(temp.getMyFile(), inputValue, temp.getSpace());
                    JOptionPane.showMessageDialog(null, "�������ɹ��������´��ʼۼУ��鿴Ч������!", "�ɹ�", JOptionPane.DEFAULT_OPTION);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "������ʧ��!!!", "ʧ��", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        myMenu.add(renameItem);

        // �ײ��ļ���Ϣ����ʾ
        JPanel panel = new JPanel();
        panel.setBackground(Color.YELLOW);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel tips = new JLabel("�ļ���������:ѡ������ļ�֮���Ҽ� ���ļ�:˫���Ҳ������ļ�");
        panel.add(tips);
        panel.add(blockName);
        nameField.setForeground(Color.RED);//������Ĺؼ���Ϣ�Ѻ�ɫ��������ʾ
        panel.add(nameField);
        panel.add(new JLabel("  "));
        panel.add(haveUsed);
        usedField.setForeground(Color.RED);
        panel.add(usedField);
        panel.add(new JLabel("  "));
        panel.add(freeYet);
        freeField.setForeground(Color.RED);
        panel.add(freeField);
        panel.add(new JLabel("  "));
        panel.add(fileNum);
        fileNumField.setForeground(Color.RED);
        panel.add(fileNumField);
        add(panel, BorderLayout.SOUTH);


        // �������ĳ�ʼ��
        JPanel searchPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JLabel searchLabel = new JLabel("������һ���ļ�����");
        searchPane.add(searchLabel);
        searchLine.setPreferredSize(new Dimension(500, 30));
        searchPane.add(searchLine);
        JButton searchButton = new JButton("����");
        //��ť������������¼�
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = searchLine.getText();
                if(!searchFile(fileName, rootFile)){
                    JOptionPane.showMessageDialog(null, "û�в��ҵ��ļ�!", "ʧ��!", JOptionPane.WARNING_MESSAGE);
                }
                searchLine.setText("");
            }
        });
        searchPane.add(searchButton);
        add(searchPane, BorderLayout.NORTH);

        // ���ļ����¼����鿴���Ƿ񱻵��
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3){
                    myMenu.show(e.getComponent(), e.getX(), e.getY());

                }
            }
        });       
        setSize(1200, 600);//���ô��ڴ�С
        setVisible(true);
    }

    public static void main(String args[]) throws IOException {
        new UI();
    }
}
