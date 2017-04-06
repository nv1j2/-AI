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
    final int rows = 4;//����  
    final int cols = 4;//����
    final int sumNum = rows*cols;//�ܸ���
    JButton ptJButton[] = new JButton[sumNum];//��ť
    BufferedImage backGroundImage;//����ͼ
    Icon nullIcon;//�հ�ͼ
    int pt[][] = new int[rows][cols];//�ô��ж�λ��
    BufferedImage JButtonBGImage[] = new  BufferedImage[sumNum];//��ť�ı���
    public myJFrame(){
        try{
            backGroundImage = ImageIO.read(new File("background.jpg"));
            for(int i = 0;i < rows;i++)
                for(int j = 0;j < cols;j++)
                    JButtonBGImage[i*cols+j] = backGroundImage.getSubimage(backGroundImage.getWidth()/cols*j,backGroundImage.getHeight()/rows*i,backGroundImage.getWidth()/cols,backGroundImage.getHeight()/rows);
            this.setSize(backGroundImage.getWidth()+10,backGroundImage.getHeight()+30);  //�����С
            this.setResizable(false);  //�̶������С,�û����ɵ�����С
            Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize(); // �����ʾ����С���� 
            Dimension frameSize = this.getSize();             // ��ô��ڴ�С����
            if(frameSize.width > displaySize.width)
                frameSize.width = displaySize.width;           // ���ڵĿ�Ȳ��ܴ�����ʾ���Ŀ��  
            if(frameSize.height > displaySize.height)
                frameSize.height = displaySize.height;          // ���ڵĸ߶Ȳ��ܴ�����ʾ���ĸ߶�  
            this.setLocation((displaySize.width - frameSize.width)/2,(displaySize.height - frameSize.height)/2); //���ô��ھ�����ʾ����ʾ 
            this.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));  //���ַ�ʽ:������ �ж���,��ť��ˮƽ����0,��ť�䴹ֱ����0
            for(int i = 0;i < sumNum;i ++){
                ptJButton[i] = new JButton();
                ptJButton[i].setMargin(new Insets(0,0,0,0));//��ť�����־����������ұ߿�ľ���
                ptJButton[i].setPreferredSize(new Dimension(backGroundImage.getWidth()/cols,backGroundImage.getHeight()/rows));//��ť����
                ptJButton[i].setFocusPainted(false);//�����ƽ���(���߿�)
                //ptJButton[i].setBorderPainted(false);//�ޱ߿�
                ptJButton[i].addActionListener(this);//��Ӽ���
                ptJButton[i].setIcon(new ImageIcon((Image)JButtonBGImage[i]));
                this.add(ptJButton[i]);
            }

        }catch(IOException e){System.out.println("��ȡͼƬʧ��");}
        nullIcon = new ImageIcon("null.jpg");//�����һ����ť����Ϊ�հ�ͼ
        ptJButton[sumNum - 1].setIcon(nullIcon);
        int k = 0;
        for(int i = 0;i < rows;i ++)//��ʼ��pt
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
        int parentNode;//���ڵ�����  
    }
    public void exchangeButton(int originalB,int changeB){          //����Ϊ��
        ptJButton[changeB].setIcon(ptJButton[originalB].getIcon());
        ptJButton[originalB].setIcon(nullIcon);
    }
    public SignXY findXY(int x){    //�ҵ���Ӧ��Ŀ��λ��
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
    public void movNullXY(int k,SignXY signXYNull){       //�ƶ���
        if(k == 2){            //��
            exchangeButton(signXYNull.l()+cols,signXYNull.l());
            pt[signXYNull.x][signXYNull.y] = pt[++signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        else if(k== 8){        //��
            exchangeButton(signXYNull.l()-cols,signXYNull.l());
            pt[signXYNull.x][signXYNull.y] = pt[--signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        else if(k == 4){            //��
            exchangeButton(signXYNull.l() - 1,signXYNull.l());
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][--signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        else if(k == 6){                   //��
            exchangeButton(signXYNull.l() + 1,signXYNull.l());
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][++signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
    }
    public void findBestWay(SignXY signXYNull,SignXY goXYNull,boolean ptSignBoolBak[][]){  //�����������
        QNode queue[] = new QNode[sumNum];
        for(int i = 0 ;i < sumNum;i++){
        	queue[i] = new QNode();
        }
        queue[0].x = goXYNull.x;
        queue[0].y = goXYNull.y;  
        queue[0].parentNode = -1;//��ʼ��û�и��ڵ�  
        int way[] = new int[sumNum];
        int p = 0;
        int dx[] = {0,0,-1,1};  //�����ƶ�����(���ҡ��ϡ���)��x��y�����Ӱ��
        int dy[] = {-1,1,0,0};   //x���꣺��ֱ����y���꣺ˮƽ����
        int front = 0,rear = 1;//���е�ͷ��β  
        while(front != rear){//���в�Ϊ��  
            for (int i = 0;i < 4;++i){
                int nextX,nextY;//��һ��������  
                nextX = queue[front].x + dx[i];  
                nextY = queue[front].y + dy[i];
                if (nextX >= 0 && nextX < rows &&  nextY >= 0 && nextY < cols && ptSignBoolBak[nextX][nextY] == true){  //��һ���ڵ���� 
                    if (nextX == signXYNull.x && nextY == signXYNull.y){   //Ѱ�ҵ�Ŀ���
                        int tempParentIndex = front;  
                        if(nextY < queue[tempParentIndex].y) //����·��
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
                            if(way[k] != 0){        //�����ƶ���Ŀ��λ��
                                movNullXY(way[k],signXYNull);
                            }
                            else
                                return;
                        } 
                    }
                    
                    queue[rear].x = nextX;  //��ջ
                    queue[rear].y = nextY;  
                    queue[rear].parentNode = front;
                    ++rear;
                    ptSignBoolBak[nextX][nextY] = false; //��Ǵ˵��ѱ�����  
                }  
            }  
            ++front;  
        }
    }
    public boolean AIFun1(int GOX,int GOY,int sign,boolean ptSignBool[][]){//��sign�ƶ���GOX��GOY
        boolean ptSignBoolBak[][] = new boolean[rows][cols];
        for(int i = 0;i < rows;i++)//����
            System.arraycopy(ptSignBool[i],0,ptSignBoolBak[i],0,cols);
        SignXY signXYNum = new SignXY();//1Ŀ�����ڵ�λ��
        SignXY signXYNull = new SignXY();//0�����ڵ�λ��
        SignXY goXYNull = new SignXY();//����Ҫ�ﵽ��λ��
        SignXY goPositionXY = new SignXY();//Ŀ����Ҫ�ﵽ��λ��
        goPositionXY.x = GOX; goPositionXY.y = GOY;
        signXYNum =  findXY(sign);
        signXYNull = findXY(0);
        ptSignBoolBak[signXYNum.x][signXYNum.y] = false;
        
        goXYNull.x = signXYNum.x;//�ҵ�����Ҫ�ﵽ��λ��
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
            findBestWay(signXYNull,goXYNull,ptSignBoolBak);//�� �� �ƶ��� ����Ҫ�ﵽ��λ��,���Ҳ�ͨ��false
            exchangeButton(signXYNum.l(),signXYNull.l());
            pt[signXYNull.x][signXYNull.y] = pt[signXYNum.x][signXYNum.y];
            pt[signXYNum.x][signXYNum.y] = 0;
            return true;
        }
        return false;
    }
    public void AIFun2(int GOX,int GOY ,int sign,boolean ptSignBool[][]){
        SignXY signXYNum = new SignXY();            //4Ŀ�����ڵ�λ��
        signXYNum = findXY(sign);
        if(signXYNum.x == GOX && signXYNum.y == GOY)
            return;
        SignXY goXYNull = new SignXY();             //����Ҫ�ﵽ��λ��
        goXYNull.x = GOX+1;goXYNull.y = GOY-1;
        SignXY signXYNull = new SignXY();           //0�����ڵ�λ��
        signXYNull = findXY(0);
        if(signXYNull.x == GOX && signXYNull.y == GOY){             
            exchangeButton(signXYNull.l()+cols,signXYNull.l());             //����
            pt[signXYNull.x][signXYNull.y] = pt[++signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
            exchangeButton(signXYNull.l() - 1,signXYNull.l());              //����
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][--signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        signXYNum = findXY(sign);
        if(signXYNum.x == GOX && signXYNum.y == GOY)
            return;
        while(AIFun1(GOX+1,GOY,sign,ptSignBool)){;}
        signXYNull = findXY(0);
        if(signXYNull.x == GOX+1){      //�����ƶ���GOX+2,GOYλ��
            exchangeButton(signXYNull.l()+cols,signXYNull.l());             //����
            pt[signXYNull.x][signXYNull.y] = pt[++signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        while(signXYNull.y != GOY){     //��ֹ�������
            exchangeButton(signXYNull.l() + 1,signXYNull.l());          //����
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][++signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        
        signXYNull = findXY(0);
        int way[] = {8,8,4,2,6,2,4,8,8,6,2};
        for(int i = 0;i < way.length;i++)
            movNullXY(way[i],signXYNull);
    }
    public void AIFun3(int GOX,int GOY ,int sign,boolean ptSignBool[][]){
        SignXY signXYNum = new SignXY();            //13Ŀ�����ڵ�λ��
        signXYNum = findXY(sign);
        if(signXYNum.x == GOX && signXYNum.y == GOY)
            return;
        SignXY goXYNull = new SignXY();             //����Ҫ�ﵽ��λ��
        goXYNull.x = GOX-1;goXYNull.y = GOY+1;
        SignXY signXYNull = new SignXY();           //0�����ڵ�λ��
        signXYNull = findXY(0);
        if(signXYNull.x == GOX && signXYNull.y == GOY){             
            exchangeButton(signXYNull.l() + 1,signXYNull.l());              //����
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][++signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
            exchangeButton(signXYNull.l()-cols,signXYNull.l());             //����
            pt[signXYNull.x][signXYNull.y] = pt[--signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        signXYNum = findXY(sign);
        if(signXYNum.x == GOX && signXYNum.y == GOY)
            return;
        while(AIFun1(GOX,GOY+1,sign,ptSignBool)){;}
        signXYNull = findXY(0);
        while(signXYNull.y > GOY+2){
            exchangeButton(signXYNull.l() - 1,signXYNull.l());              //����
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][--signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        while(signXYNull.y < GOY+2){
            exchangeButton(signXYNull.l() + 1,signXYNull.l());              //����
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][++signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        if(signXYNull.x < GOX){
            exchangeButton(signXYNull.l()+cols,signXYNull.l());             //����
            pt[signXYNull.x][signXYNull.y] = pt[++signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        signXYNull = findXY(0);
        int way[] = {4,4,8,6,2,6,8,4,4,2,6};
        for(int i = 0;i < way.length;i++)
            movNullXY(way[i],signXYNull);
    }
    public void AIFun4(){
        SignXY signXYNull = new SignXY();           //0�����ڵ�λ��                     
        signXYNull = findXY(0);    
        if(signXYNull.x < rows-1){
            exchangeButton(signXYNull.l()+cols,signXYNull.l());             //����
            pt[signXYNull.x][signXYNull.y] = pt[++signXYNull.x][signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        if(signXYNull.y < cols-1){
            exchangeButton(signXYNull.l() + 1,signXYNull.l());              //����
            pt[signXYNull.x][signXYNull.y] = pt[signXYNull.x][++signXYNull.y];
            pt[signXYNull.x][signXYNull.y] = 0;
        }
        SignXY signXYNum = new SignXY();            //sumNum-1Ŀ�����ڵ�λ��
        signXYNum = findXY(sumNum-1);   
        int way[] = {8,4,2,6};              //��ʱ����תһȦ
        signXYNull = findXY(0); 
        while(signXYNum.x != rows-1 || signXYNum.y != cols-2){
            for(int i = 0;i < way.length;i++)
                movNullXY(way[i],signXYNull);
            signXYNum = findXY(sumNum-1);
        }
    }
    public void AI(){
        boolean ptSignBool[][] = new boolean[rows][cols];//�ô��жϿ��ܷ񾭹��˵�;trueΪ��ͨ����falseλ����ͨ��
        for(int i = 0;i < rows;i ++)                //��ʼ��ptSignBool
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
    public void spaceLocation(int ptBack[][]){      //�ո��λ
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
    public boolean testSollution(){             //����Ƿ��н�    ������б�
        int ptBack[][] = new int[rows][cols];//������б���
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
    public void randomPt(){              //���һ��ƴͼ��ͼ
        int randomNum = 0,x = 0,y = 0;
        do{ 
            for(int i = 0;i < rows;i ++)
                for(int j = 0; j < cols;j ++){
                    randomNum = (int)(Math.random()*sumNum);
                    x = randomNum/cols;                    //ת����������
                    y = randomNum%cols;
                    randomNum = pt[x][y];  //˳���������м����
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

    public void processKeyEvent(KeyEvent e){        //��������¼�
        int id = e.getID(); //��������Ϣ��������
        if(id == 401){                              //401���̰���
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
        this.requestFocus();//���ؽ��㣬���ô�����ܼ�����Ϣ
        for(int i = 0;i < sumNum;i ++){
            if(e.getSource() == ptJButton[i]){          //��������������Ƿ��пհ�
                x = i/cols;                    //ת����������
                y = i%cols;
                if(x > 0 && pt[x-1][y] == 0){                       //��
                    exchangeButton(i,i-cols);
                    pt[x-1][y] = pt[x][y];
                    pt[x][y] = 0;
                }
                else if(y > 0 && pt[x][y-1] == 0){             //��
                    exchangeButton(i,i-1);
                    pt[x][y-1] = pt[x][y];
                    pt[x][y] = 0;
                }
                else if(y < (cols -1) && pt[x][y+1] == 0){          //��
                    exchangeButton(i,i+1);
                    pt[x][y+1] = pt[x][y];
                    pt[x][y] = 0;
                }
                else if(x < (rows -1) && pt[x+1][y] == 0){             //��
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
        windows.setTitle("ƴͼС��Ϸ");
        String nowTitle = windows.getTitle();  //��ô��ڱ�������
        //windows.getContentPane().requestFocus();
        Timer timer = new Timer();//��ʱ
        long firstTime = System.currentTimeMillis();//��ȡ��1970�꿪ʼ����ĺ�����
        timer.schedule(new TimerTask(){public void run(){
            long lastTime = System.currentTimeMillis();
            String nowTime= String.valueOf((lastTime - firstTime)/1000);
            windows.setTitle(nowTitle+"Time:"+nowTime);
        }},1000,1000);//ִ�е����񣬿�ʼ�ļ��ʱ�䣬��ʼִ�к���xx�ٴ�ִ��
        windows.setVisible(true);//��ʾ����,�������
    }
}