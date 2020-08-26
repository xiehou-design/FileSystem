
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
    private File rootFile;//根文件
    private File readMe;//读文件
    private FileWriter readMeWrite;//写文件
    private Block block1;//十个文件目录，文件夹
    //下横条显示帮助信息
    private ArrayList<Block> blocks = new ArrayList<Block>();
    private JLabel blockName = new JLabel("文件夹个数：");
    private JLabel nameField = new JLabel();
    private JLabel haveUsed = new JLabel("空间已使用大小：");
    private JLabel usedField = new JLabel();
    private JLabel freeYet = new JLabel("剩余空间大小：");
    private JLabel freeField = new JLabel();
    private JLabel fileNum = new JLabel("文本文件数量：");
    private JLabel fileNumField = new JLabel();
    private JTextField searchLine = new JTextField();
    // 删除一个文件目录
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

    //获得文件大小
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

    // 更新文件夹信息
    public void upDateBlock(Block currentBlock){
        fileNumField.setText(String.valueOf(currentBlock.getFileNum()));
        usedField.setText(String.valueOf(currentBlock.getSpace()) + " KB");
        freeField.setText(String.valueOf(1024 - currentBlock.getSpace()) + "KB");
    }

    // 搜索框搜索文件
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
                	//没有找到则弹框发出警告，则返回true结束
                    JOptionPane.showMessageDialog(null, myFile.getPath() + "抱歉，有些东西出错了！！！", "没有成功打开文件",
                            JOptionPane.ERROR_MESSAGE);
                    return true;
                }
            }
            //文件是在目录下面，并且文件是可读的
            if (myFile.isDirectory() && myFile.canRead()){
                if(searchFile(fileName, myFile)){
                    return true;
                }
            }
        }
        return false;
    }
     //界面效果
    public UI() throws IOException {
        setTitle("File System");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 运行后得首页，选择文件夹作为初始化文件位置
        String path = File.listRoots()[0].getPath();//定义路径，方便后面使用
        String rootPath = new String();//定义根目录
        chooser = new JFileChooser(path);
        chooser.setDialogTitle("请选择一个文件夹作为文件系统的根文件：");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setPreferredSize(new Dimension(800, 600));//设置窗口大小
        int result = chooser.showOpenDialog(this);
        if (result == chooser.APPROVE_OPTION){
            System.out.println(chooser.getSelectedFile().getAbsolutePath());
            rootPath = chooser.getSelectedFile().getPath();
        }
        // 创建一个工作的空间
        rootFile = new File(rootPath + File.separator + "FileSystem");
        readMe = new File(rootPath + File.separator + "FileSystem" + File.separator + "ReadMe.txt");

        boolean flag = true;
        
        // 目录 的创建
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(new myFiles(rootFile, 0, 10240));
        
         if (!rootFile.exists()) {
            flag = false;
            try {
                rootFile.mkdir();
                readMe.createNewFile();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "这个地方不支持创建文件！！！", "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
	
        //初始化文件夹
        block1 = new Block("user", new File(rootFile.getPath() + File.separator + "user"), flag);
        blocks.add(block1);
        //定义文件的大小，并将其添加到左边的状态栏上，目录
        root.add(new DefaultMutableTreeNode(new myFiles(block1.getBlockFile(), 1, 1024.0)));
        model.addRow(new myFiles(block1.getBlockFile(), 1, 1024.0));
        ((DefaultMutableTreeNode)root.getChildAt(0)).add(new DefaultMutableTreeNode("temp"));
        fileTable = new JTable(model);
        fileTable.getTableHeader().setFont(new Font(Font.DIALOG,Font.CENTER_BASELINE,24));//定义字体
        fileTable.setSelectionBackground(Color.RED);//被选中的哪一行颜色

        fileTable.updateUI();//刷新界面的颜色

        final DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.putClientProperty("Jtree.lineStyle",  "Horizontal");
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        //添加鼠标监听器，当左边的目录被点击时
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


        // 鼠标双击打开一个文件
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
                        JOptionPane.showMessageDialog(null, "抱歉，打开错误！！!", "打开错误",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    JOptionPane.showMessageDialog(null, "File Name: " + fileName + "\n File Path: " + filePath, "content",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // 文件栏的初始化
        final JPopupMenu myMenu = new JPopupMenu();
        myMenu.setPreferredSize(new Dimension(300, 200));

        //创建一个文件
        JMenuItem createFileItem = new JMenuItem("创建一个文本文件");
        createFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);

                String inputValue;
                double capacity;
                //弹窗显示，输入文件的名字
                JOptionPane inputPane = new JOptionPane();
                inputPane.setPreferredSize(new Dimension(600, 600));
                inputPane.setInputValue(JOptionPane.showInputDialog("文件名："));
                if (inputPane.getInputValue() == null) {
                    return;
                }
                //输入文件的大小
                inputValue = inputPane.getInputValue().toString();
                inputPane.setInputValue(JOptionPane.showInputDialog("文件大小(KB):"));
                if (inputPane.getInputValue() == null) {
                    return;
                }
                capacity = Double.parseDouble(inputPane.getInputValue().toString());

                File newFile = new File(temp.getFilePath() + File.separator + inputValue + ".txt");//为文件创建路径
                //弹窗显示创建的情况,失败或者成功
                if (!newFile.exists() && !inputValue.equals(null)){
                    try {
                        if (currentBlock.createFile(newFile, capacity)) {
                            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new myFiles(newFile, blokName, capacity));
                            model.removeRows(0, model.getRowCount());
                            model.addRow(new myFiles(newFile, blokName, capacity));
                            fileTable.updateUI();
                            upDateBlock(currentBlock);
                            JOptionPane.showMessageDialog(null, "创建成功，请重新打开这个文件去查看效果！！!", "成功", JOptionPane.DEFAULT_OPTION);
                        }
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "创建失败!!!", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        myMenu.add(createFileItem);

        //创建一个文件夹
        JMenuItem createDirItem = new JMenuItem("创建一个文件夹");
        createDirItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);
                String inputValue = JOptionPane.showInputDialog("文件夹的名称");
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
                    JOptionPane.showMessageDialog(null, "创建成功，请重新打开这个文件夹才能看见创建效果", "创建", JOptionPane.DEFAULT_OPTION);
                }catch (Exception E){
                    JOptionPane.showMessageDialog(null, "创建失败!!!", "失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        myMenu.add(createDirItem);

        //删除一个文件或者文件夹
        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);
                int choose = JOptionPane.showConfirmDialog(null, "你确认再删除这个文件吗?", "确认", JOptionPane.YES_NO_OPTION);
                if (choose == 0){
                    if (currentBlock.deleteFile(temp.getMyFile(), temp.getSpace())){
                        try {
                            currentBlock.rewriteBitMap();
                            currentBlock.rewriteRecoverWriter();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        upDateBlock(currentBlock);
                        JOptionPane.showMessageDialog(null, "删除成功!", "成功", JOptionPane.DEFAULT_OPTION);
                        JOptionPane.showMessageDialog(null, "删除成功，请重新打开这个文件夹才能看见删除效果", "删除", JOptionPane.DEFAULT_OPTION);
                    }else{
                        JOptionPane.showMessageDialog(null, "删除失败!!!", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        myMenu.add(deleteItem);

        //格式化一个文件夹
        JMenuItem formatItem = new JMenuItem("格式化");
        formatItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);
                int choose = JOptionPane.showConfirmDialog(null, "你想要清空这个这个文件夹？", "确认", JOptionPane.YES_NO_OPTION);
                if (choose == 0){
                    try{
                    if (temp.getMyFile().isDirectory()) {
                        for (File myfile : temp.getMyFile().listFiles()) {
                            currentBlock.deleteFile(myfile, getSpace(myfile));
                        }
                        upDateBlock(currentBlock);
                        JOptionPane.showMessageDialog(null, "格式化成功，请重新打开这个文件夹看更新效果！！！", "成功", JOptionPane.DEFAULT_OPTION);
                        currentBlock.rewriteBitMap();
                    }
                    }catch (Exception E1){
                        JOptionPane.showMessageDialog(null, "格式化失败!!!", "失败", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        myMenu.add(formatItem);

        //重命名文件名字或者文件夹名字
        JMenuItem renameItem = new JMenuItem("重命名");
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                myFiles temp = (myFiles)node.getUserObject();
                int blokName = temp.getBlockName();
                Block currentBlock = blocks.get(blokName - 1);

                String inputValue = null;
                JOptionPane inputPane = new JOptionPane();
                inputPane.setInputValue(JOptionPane.showInputDialog("新的文件名称："));
                if (inputPane.getInputValue() == null) {
                    return;
                }
                inputValue = inputPane.getInputValue().toString();
                try {
                    currentBlock.renameFile(temp.getMyFile(), inputValue, temp.getSpace());
                    JOptionPane.showMessageDialog(null, "重命名成功，请重新打开问价夹，查看效果！！!", "成功", JOptionPane.DEFAULT_OPTION);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "重命名失败!!!", "失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        myMenu.add(renameItem);

        // 底部文件信息的显示
        JPanel panel = new JPanel();
        panel.setBackground(Color.YELLOW);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel tips = new JLabel("文件操作帮助:选中左侧文件之后右键 打开文件:双击右侧表格内文件");
        panel.add(tips);
        panel.add(blockName);
        nameField.setForeground(Color.RED);//将下面的关键信息已红色的字体显示
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


        // 搜索栏的初始化
        JPanel searchPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JLabel searchLabel = new JLabel("请输入一个文件名：");
        searchPane.add(searchLabel);
        searchLine.setPreferredSize(new Dimension(500, 30));
        searchPane.add(searchLine);
        JButton searchButton = new JButton("查找");
        //按钮被点击触发的事件
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = searchLine.getText();
                if(!searchFile(fileName, rootFile)){
                    JOptionPane.showMessageDialog(null, "没有查找到文件!", "失败!", JOptionPane.WARNING_MESSAGE);
                }
                searchLine.setText("");
            }
        });
        searchPane.add(searchButton);
        add(searchPane, BorderLayout.NORTH);

        // 鼠标的监听事件，查看树是否被点击
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3){
                    myMenu.show(e.getComponent(), e.getX(), e.getY());

                }
            }
        });       
        setSize(1200, 600);//设置窗口大小
        setVisible(true);
    }

    public static void main(String args[]) throws IOException {
        new UI();
    }
}
