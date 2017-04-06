import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Timer;  
import java.util.TimerTask;  

class myJFrame extends JFrame implements ActionListener{
    final int rows = 4;//行数  
    final int cols = 4;//列数
    final int sumNum = rows*cols;//总个数
    JButton ptJButton[] = new JButton[sumNum];//按钮
    BufferedImage backGroundImage;//背景图
    Icon nullIcon;//空白图
    int pt[][] = new int[rows][cols];//用此判断位置
    BufferedImage JButtonBGImage[] = new  BufferedImage[sumNum];//按钮的背景
    public myJFrame(){
        try{
            backGroundImage = ImageIO.read(new File("background.jpg"));
            for(int i = 0;i < rows;i++)
                for(int j = 0;j < cols;j++)
                    JButtonBGImage[i*cols+j] = backGroundImage.getSubimage(backGroundImage.getWidth()/cols*j,backGroundImage.getHeight()/rows*i,backGroundImage.getWidth()/cols,backGroundImage.getHeight()/rows);
            this.setSize(backGroundImage.getWidth()+10,backGroundImage.getHeight()+30);  //窗体大小
            this.setResizable(false);  //固定窗体大小,用户不可调整大小
            Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize(); // 获得显示器大小对象 
            Dimension frameSize = this.getSize();             // 获得窗口大小对象
            if(frameSize.width > displaySize.width)
                frameSize.width = displaySize.width;           // 窗口的宽度不能大于显示器的宽度  
            if(frameSize.height > displaySize.height)
                frameSize.height = displaySize.height;          // 窗口的高度不能大于显示器的高度  
            this.setLocation((displaySize.width - frameSize.width)/2,(displaySize.height - frameSize.height)/2); //设置窗口居中显示器显示 
            this.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));  //布局方式:流布局 中对其,按钮间水平像素0,按钮间垂直像素0
            for(int i = 0;i < sumNum;i ++){
                ptJButton[i] = new JButton();
                ptJButton[i].setMargin(new Insets(0,0,0,0));//按钮上文字距离上左下右边框的距离
                ptJButton[i].setPreferredSize(new Dimension(backGroundImage.getWidth()/cols,backGroundImage.getHeight()/rows));//按钮长宽
                ptJButton[i].setFocusPainted(false);//不绘制焦点(虚线框)
                //ptJButton[i].setBorderPainted(false);//无边框
                ptJButton[i].addActionListener(this);//添加监听
                ptJButton[i].setIcon(new ImageIcon((Image)JButtonBGImage[i]));
                this.add(ptJButton[i]);
            }

        }catch(IOException e){System.out.println("读取图片失败");}
        nullIcon = new ImageIcon("null.jpg");//将最后一个按钮背景为空白图
        ptJButton[sumNum - 1].setIcon(nullIcon);
        int k = 0;
        for(int i = 0;i < rows;i ++)//初始化pt
            for(int j = 0; j < cols;j ++){
                pt[i][j] = ++k;
            }
        pt[rows - 1][cols - 1] = 0;
    }
    public class SignXY{
        int x;
        int y;
        public int l(){
            return x*cols+y;
        }
    }
    public class QNode{
        int x;
        int y;  
        int parentNode;//父节点索引  
    }
    public void exchangeButton(int originalB,int changeB){          //后者为空
        ptJButton[changeB].setIcon(ptJButton[originalB].getIcon());
        ptJButton[originalB].setIcon(nullIcon);
    }
    public SignXY findXY(int x){    //找到对应的目标位置
        SignXY signXY = new SignXY();
        for(int i = 0;i < rows;i ++)
            for(int j = 0; j < cols;j ++){
                if(pt[i][j] == x){
                    signXY.x = i;signXY.y = j;
                    return signXY;
                }
            }
        return signXY;
    }
    public void movNullXY(int k,SignXY signXYNull){       //移动空
        if(k == 2){            //下
            exchangeButton(signXYNull.l()+cols,signXYNull.l());
            pt[signXYNull.x][signXYNull.y] = pt[++signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        else if(k== 8){        //上
            exchangeButton(signXYNull.l()-cols,signXYNull.l());
            pt[signXYNull.x][signXYNull.y] = pt[--signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        else if(k == 4){            //左
            exchangeButton(signXYNull.l() - 1,signXYNull.l());
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][--signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        else if(k == 6){                   //右
            exchangeButton(signXYNull.l() + 1,signXYNull.l());
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][++signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
    }
    public void findBestWay(SignXY signXYNull,SignXY goXYNull,boolean ptSignBoolBak[][]){  //广度优先搜索
        QNode queue[] = new QNode[sumNum];
        for(int i = 0 ;i < sumNum;i++){
        	queue[i] = new QNode();
        }
        queue[0].x = goXYNull.x;
        queue[0].y = goXYNull.y;  
        queue[0].parentNode = -1;//起始点没有父节点  
        int way[] = new int[sumNum];
        int p = 0;
        int dx[] = {0,0,-1,1};  //四种移动方向(左、右、上、下)对x、y坐标的影响
        int dy[] = {-1,1,0,0};   //x坐标：竖直方向，y坐标：水平方向
        int front = 0,rear = 1;//队列的头和尾  
        while(front != rear){//队列不为空  
            for (int i = 0;i < 4;++i){
                int nextX,nextY;//下一步的坐标  
                nextX = queue[front].x + dx[i];  
                nextY = queue[front].y + dy[i];
                if (nextX >= 0 && nextX < rows &&  nextY >= 0 && nextY < cols && ptSignBoolBak[nextX][nextY] == true){  //下一个节点可行 
                    if (nextX == signXYNull.x && nextY == signXYNull.y){   //寻找到目标点
                        int tempParentIndex = front;  
                        if(nextY < queue[tempParentIndex].y) //解析路径
                            way[p++] = 6;
                        else if(nextY > queue[tempParentIndex].y)
                            way[p++] = 4;
                        else if(nextX > queue[tempParentIndex].x)
                            way[p++] = 8;
                        else if(nextX < queue[tempParentIndex].x)
                            way[p++] = 2; 
                        while(tempParentIndex != -1){  
                            if(queue[tempParentIndex].parentNode != -1 )
                                if(queue[queue[tempParentIndex].parentNode].x < queue[tempParentIndex].x)
                                    way[p++] = 8;
                                else if(queue[queue[tempParentIndex].parentNode].x > queue[tempParentIndex].x)
                                    way[p++] = 2;
                                else if(queue[queue[tempParentIndex].parentNode].y < queue[tempParentIndex].y)
                                    way[p++] = 4;
                                else if(queue[queue[tempParentIndex].parentNode].y > queue[tempParentIndex].y)
                                    way[p++] = 6;
                            tempParentIndex = queue[tempParentIndex].parentNode;
                        }
                        for (int k = 0;k < sumNum;++k){
                            if(way[k] != 0){        //将空移动到目标位置
                                movNullXY(way[k],signXYNull);
                            }
                            else
                                return;
                        } 
                    }
                    
                    queue[rear].x = nextX;  //入栈
                    queue[rear].y = nextY;  
                    queue[rear].parentNode = front;
                    ++rear;
                    ptSignBoolBak[nextX][nextY] = false; //标记此点已被访问  
                }  
            }  
            ++front;  
        }
    }
    public boolean AIFun1(int GOX,int GOY,int sign,boolean ptSignBool[][]){//将sign移动到GOX，GOY
        boolean ptSignBoolBak[][] = new boolean[rows][cols];
        for(int i = 0;i < rows;i++)//备份
            System.arraycopy(ptSignBool[i],0,ptSignBoolBak[i],0,cols);
        SignXY signXYNum = new SignXY();//1目标所在的位置
        SignXY signXYNull = new SignXY();//0空所在的位置
        SignXY goXYNull = new SignXY();//空需要达到的位置
        SignXY goPositionXY = new SignXY();//目标需要达到的位置
        goPositionXY.x = GOX; goPositionXY.y = GOY;
        signXYNum =  findXY(sign);
        signXYNull = findXY(0);
        ptSignBoolBak[signXYNum.x][signXYNum.y] = false;
        
        goXYNull.x = signXYNum.x;//找到空需要达到的位置
        goXYNull.y = signXYNum.y;
        if(goPositionXY.x < signXYNum.x && ptSignBoolBak[signXYNum.x-1][signXYNum.y] == true)
            goXYNull.x = signXYNum.x - 1;
        else if(goPositionXY.x > signXYNum.x && ptSignBoolBak[signXYNum.x+1][signXYNum.y] == true)
            goXYNull.x = signXYNum.x + 1;
        else if(goPositionXY.y < signXYNum.y && ptSignBoolBak[signXYNum.x][signXYNum.y-1] == true)
            goXYNull.y = signXYNum.y - 1;
        else if(goPositionXY.y > signXYNum.y &&  ptSignBoolBak[signXYNum.x][signXYNum.y+1] == true)
            goXYNull.y = signXYNum.y + 1;
            
        if(goXYNull.x != signXYNum.x || goXYNull.y != signXYNum.y){
            findBestWay(signXYNull,goXYNull,ptSignBoolBak);//将 空 移动到 空需要达到的位置,并且不通过false
            exchangeButton(signXYNum.l(),signXYNull.l());
            pt[signXYNull.x][signXYNull.y] = pt[signXYNum.x][signXYNum.y];
            pt[signXYNum.x][signXYNum.y] = 0;
            return true;
        }
        return false;
    }
    public void AIFun2(int GOX,int GOY ,int sign,boolean ptSignBool[][]){
        SignXY signXYNum = new SignXY();            //4目标所在的位置
        signXYNum = findXY(sign);
        if(signXYNum.x == GOX && signXYNum.y == GOY)
            return;
        SignXY goXYNull = new SignXY();             //空需要达到的位置
        goXYNull.x = GOX+1;goXYNull.y = GOY-1;
        SignXY signXYNull = new SignXY();           //0空所在的位置
        signXYNull = findXY(0);
        if(signXYNull.x == GOX && signXYNull.y == GOY){             
            exchangeButton(signXYNull.l()+cols,signXYNull.l());             //向下
            pt[signXYNull.x][signXYNull.y] = pt[++signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
            exchangeButton(signXYNull.l() - 1,signXYNull.l());              //向左
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][--signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        signXYNum = findXY(sign);
        if(signXYNum.x == GOX && signXYNum.y == GOY)
            return;
        while(AIFun1(GOX+1,GOY,sign,ptSignBool)){;}
        signXYNull = findXY(0);
        if(signXYNull.x == GOX+1){      //将空移动到GOX+2,GOY位置
            exchangeButton(signXYNull.l()+cols,signXYNull.l());             //向下
            pt[signXYNull.x][signXYNull.y] = pt[++signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        while(signXYNull.y != GOY){     //防止特殊情况
            exchangeButton(signXYNull.l() + 1,signXYNull.l());          //向右
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][++signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        
        signXYNull = findXY(0);
        int way[] = {8,8,4,2,6,2,4,8,8,6,2};
        for(int i = 0;i < way.length;i++)
            movNullXY(way[i],signXYNull);
    }
    public void AIFun3(int GOX,int GOY ,int sign,boolean ptSignBool[][]){
        SignXY signXYNum = new SignXY();            //13目标所在的位置
        signXYNum = findXY(sign);
        if(signXYNum.x == GOX && signXYNum.y == GOY)
            return;
        SignXY goXYNull = new SignXY();             //空需要达到的位置
        goXYNull.x = GOX-1;goXYNull.y = GOY+1;
        SignXY signXYNull = new SignXY();           //0空所在的位置
        signXYNull = findXY(0);
        if(signXYNull.x == GOX && signXYNull.y == GOY){             
            exchangeButton(signXYNull.l() + 1,signXYNull.l());              //向右
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][++signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
            exchangeButton(signXYNull.l()-cols,signXYNull.l());             //向上
            pt[signXYNull.x][signXYNull.y] = pt[--signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        signXYNum = findXY(sign);
        if(signXYNum.x == GOX && signXYNum.y == GOY)
            return;
        while(AIFun1(GOX,GOY+1,sign,ptSignBool)){;}
        signXYNull = findXY(0);
        while(signXYNull.y > GOY+2){
            exchangeButton(signXYNull.l() - 1,signXYNull.l());              //向左
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][--signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        while(signXYNull.y < GOY+2){
            exchangeButton(signXYNull.l() + 1,signXYNull.l());              //向右
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][++signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        if(signXYNull.x < GOX){
            exchangeButton(signXYNull.l()+cols,signXYNull.l());             //向下
            pt[signXYNull.x][signXYNull.y] = pt[++signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        signXYNull = findXY(0);
        int way[] = {4,4,8,6,2,6,8,4,4,2,6};
        for(int i = 0;i < way.length;i++)
            movNullXY(way[i],signXYNull);
    }
    public void AIFun4(){
        SignXY signXYNull = new SignXY();           //0空所在的位置                     
        signXYNull = findXY(0);    
        if(signXYNull.x < rows-1){
            exchangeButton(signXYNull.l()+cols,signXYNull.l());             //向下
            pt[signXYNull.x][signXYNull.y] = pt[++signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        if(signXYNull.y < cols-1){
            exchangeButton(signXYNull.l() + 1,signXYNull.l());              //向右
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][++signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        SignXY signXYNum = new SignXY();            //sumNum-1目标所在的位置
        signXYNum = findXY(sumNum-1);   
        int way[] = {8,4,2,6};              //逆时针旋转一圈
        signXYNull = findXY(0); 
        while(signXYNum.x != rows-1 || signXYNum.y != cols-2){
            for(int i = 0;i < way.length;i++)
                movNullXY(way[i],signXYNull);
            signXYNum = findXY(sumNum-1);
        }
    }
    public void AI(){
        boolean ptSignBool[][] = new boolean[rows][cols];//用此判断空能否经过此地;true为能通过，false位不能通过
        for(int i = 0;i < rows;i ++)                //初始化ptSignBool
            for(int j = 0; j < cols;j ++)
                ptSignBool[i][j] = true;
        for(int i = 0;i < rows-2;i ++){
            for(int j = 0; j < cols-1;j ++){
                while(AIFun1(i,j,i*cols+j+1,ptSignBool)){;}ptSignBool[i][j] = false;
            }
            AIFun2(i,cols-1,(i+1)*cols,ptSignBool);ptSignBool[i][cols-1] = false;
        }
        for(int i = 0;i <cols-2;i++){
            while(AIFun1(rows-2,i,(rows-2)*cols+i+1,ptSignBool)){;}ptSignBool[rows-2][i] = false;
            AIFun3(rows-1,i,(rows-1)*cols+i+1,ptSignBool);ptSignBool[rows-1][i] = false;
        }
        AIFun4();
    }
    public void spaceLocation(int ptBack[][]){      //空格归位
        for(int i = 0;i < rows;i ++)
            for(int j = 0; j < cols;j ++){
                if(ptBack[i][j] == 0){
                    while(i < rows-1){
                        ptBack[i][j] = ptBack[++i][j];
                        ptBack[i][j] = 0;
                    }
                    while(j < cols-1){
                        ptBack[i][j] = ptBack[i][++j];
                        ptBack[i][j] = 0;
                    }
                return;
                }
            }
    }
    public boolean testSollution(){             //检测是否有解    逆序和判别
        int ptBack[][] = new int[rows][cols];//对其进行备份
        for(int i = 0;i < rows;i++)
            System.arraycopy(pt[i],0,ptBack[i],0,cols);
        spaceLocation(ptBack);
        int invertedSequenceNum = 0;
        int invSeq[] = new int[sumNum-1];
        for(int i = 0 ; i < sumNum-1; i++)
            invSeq[i] = ptBack[i/cols][i%cols];
        for(int i = 0 ; i < sumNum-1; i++)
            for(int j = i ; j < sumNum-1; j++)
                if(invSeq[i] > invSeq[j])
                    invertedSequenceNum++;
        if(invertedSequenceNum%2 == 0)
            return false;
        else
            return true;
    }
    public void randomPt(){              //随机一个拼图地图
        int randomNum = 0,x = 0,y = 0;
        do{ 
            for(int i = 0;i < rows;i ++)
                for(int j = 0; j < cols;j ++){
                    randomNum = (int)(Math.random()*sumNum);
                    x = randomNum/cols;                    //转化横列坐标
                    y = randomNum%cols;
                    randomNum = pt[x][y];  //顺便作交换中间变量
                    pt[x][y] = pt[i][j];
                    pt[i][j] = randomNum;
                }
            for(int i = 0;i < sumNum;i ++){
                if(pt[i/cols][i%cols] != 0)
                    ptJButton[i].setIcon(new ImageIcon((Image)JButtonBGImage[pt[i/cols][i%cols]-1]));
                else
                    ptJButton[i].setIcon(nullIcon);
            }
        }while(testSollution());
    }

    public void processKeyEvent(KeyEvent e){        //处理键盘事件
        int id = e.getID(); //或许按键信息返回整型
        if(id == 401){                              //401键盘按下
            int code = e.getKeyCode();
            if(code == 65){    //A
                AI();
            }
            else if(code == 82){        //R
                randomPt();
            }
        }
    }
    
    public void actionPerformed(ActionEvent e){
        int x = 0,y = 0;
        this.requestFocus();//返回焦点，以让窗体接受键盘信息
        for(int i = 0;i < sumNum;i ++){
            if(e.getSource() == ptJButton[i]){          //检测其上下左右是否有空白
                x = i/cols;                    //转化横列坐标
                y = i%cols;
                if(x > 0 && pt[x-1][y] == 0){                       //上
                    exchangeButton(i,i-cols);
                    pt[x-1][y] = pt[x][y];
                    pt[x][y] = 0;
                }
                else if(y > 0 && pt[x][y-1] == 0){             //左
                    exchangeButton(i,i-1);
                    pt[x][y-1] = pt[x][y];
                    pt[x][y] = 0;
                }
                else if(y < (cols -1) && pt[x][y+1] == 0){          //右
                    exchangeButton(i,i+1);
                    pt[x][y+1] = pt[x][y];
                    pt[x][y] = 0;
                }
                else if(x < (rows -1) && pt[x+1][y] == 0){             //下
                    exchangeButton(i,i+cols);
                    pt[x+1][y] = pt[x][y];
                    pt[x][y] = 0;
                }   
            }
        }
    }
}

public class pt{
    public static void main(String[] args){
        myJFrame windows = new myJFrame();
        windows.setTitle("拼图小游戏");
        String nowTitle = windows.getTitle();  //获得窗口标题名称
        //windows.getContentPane().requestFocus();
        Timer timer = new Timer();//计时
        long firstTime = System.currentTimeMillis();//获取从1970年开始至今的毫秒数
        timer.schedule(new TimerTask(){public void run(){
            long lastTime = System.currentTimeMillis();
            String nowTime= String.valueOf((lastTime - firstTime)/1000);
            windows.setTitle(nowTitle+"Time:"+nowTime);
        }},1000,1000);//执行的任务，开始的间隔时间，开始执行后间隔xx再次执行
        windows.setVisible(true);//显示窗口,放在最后
    }
}