import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.imageio.*;

import java.io.File;
import java.awt.image.BufferedImage;

import java.awt.Graphics;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

class minesweeper {
  public static void main(String args[]){
    JFrame f = new JFrame("minesweeper");
    panel p = new panel();
    f.addMouseListener(p);
    f.setSize(p.getSize());
    f.add(p);
    f.setVisible(true);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  }
}
class panel extends JPanel implements MouseListener {
  BufferedImage spriteSheet;
  BufferedImage[][] sprites;
  boolean[][] revealed;
  boolean[][] flagged;
  int[][] spots;
  int h = 16,w = 16;
  boolean isEnded = false;

  panel()
    {
      setSize(600,800);
      setVisible(true);
      
      try {
        spriteSheet = ImageIO.read(new File("PC Computer - Minesweeper - Everything.png"));
      } catch (Exception e){e.printStackTrace();}
      sprites = new BufferedImage[4][];
      sprites[0] = new BufferedImage[12];
      for(int i = 0; i < 12; ++i)
        sprites[0][i] = spriteSheet.getSubimage(14+14*i,146,13,23);
      sprites[1] = new BufferedImage[5];
      for(int i = 0; i < 5; ++i)  
        sprites[1][i] = spriteSheet.getSubimage(14+25*i,170,24,24);
      sprites[2] = new BufferedImage[8];
      for(int i = 0 ; i < 8; ++i)
        sprites[2][i] = spriteSheet.getSubimage(14+17*i,195,16,16);
      sprites[3] = new BufferedImage[8];
      for(int i = 0 ; i < 8; ++i)
        sprites[3][i] = spriteSheet.getSubimage(14+17*i,212,16,16);
      createBoard(30,16,99);
    }
    public void createBoard(int h,int w,int b){
      this.h = h;
      this.w = w;
      revealed = new boolean[h][w];
      flagged = new boolean[h][w];
      spots = new int[h][w];
      // choose spots for bombs
      // we will use fisher-yates shuffle to pick b random spots to be bombs.
      // making the first b spots be bombs(temporary)
      for(int i = 0; i < h; ++i)
        for(int j = 0; j < w; ++j)
          spots[i][j] = 0;
      for(int i = 0; i <= b/w; ++i)
        for(int j = 0; j < Math.min(w,b-i*w);++j)
          spots[i][j] = 17;
      for(int i = h-1; i >= 0; --i)
        for(int j = w-1; j >= 0; --j)
        {
          int temp = spots[i][j];
          int rand = (int) (Math.random() * (i*h+j));
          spots[i][j] = spots[rand/h][rand % w];
          spots[rand/h][rand%w] = temp;
        }
      for(int i = 0; i < h; ++i)
        for(int j = 0; j < w; ++j)
        {
          if(spots[i][j] > 16)
          {
            int top,bot,lef,rig;
            top = Math.max(0,i-1);
            lef = Math.max(0,j-1);
            bot = Math.min(h-1,i+1);
            rig = Math.min(w-1,j+1);
            for(int y = top; y <= bot; ++y)
              for(int x = lef; x <= rig; ++x)
                ++spots[y][x];
          }
        }
      for(int i = 0; i < h; ++i)
        for(int j = 0; j < w; ++j)
        {
          System.out.print(spots[i][j] + " ");
          if(spots[i][j] == 0)
            spots[i][j] = 1;
          else if(spots[i][j] > 16)
            spots[i][j] = 5;
          else
            spots[i][j] += 7;
        }
    }
    public void reveal(int x,int y)
    {
      if(x >= 0 && x < h && y >= 0 && y < w && !revealed[x][y] && !flagged[x][y])
      {
        revealed[x][y] = true;
        if(spots[x][y] == 5)
        {
          isEnded = true;
          spots[x][y] = 6;
          for(int i = 0; i < h; ++i)
            for(int j = 0; j < w; ++j)
              revealed[i][j] = true;
        }
        if(spots[x][y] == 1)
        {
          for(int i = x-1; i < x+2; ++i)
            for(int j = y-1; j < y+2; ++j)
              reveal(i,j);
        }
      }
    }

    public void paintComponent(Graphics g) {
      // testing around to try and view sprites
      // g.drawImage(spriteSheet,0,0,null);
      // for(int i = 0; i < 12; ++i)
      //   g.drawImage(sprites[0][i],14+14*i,146,null);
      // for(int i = 0; i < 5; ++i)
      //   g.drawImage(sprites[1][i],14+25*i,170,null);
      // for(int i = 0 ; i < 8; ++i)
      //   g.drawImage(sprites[2][i],14+17*i,195,null);
      // for(int i = 0 ; i < 8; ++i)
      //   g.drawImage(sprites[3][i],14+17*i,212,null);

      // difficulties -> easy: 9x9-10, medium: 16x16-40, expert: 30x16-99
      for(int i = 0; i < spots.length; ++i)
        for(int j = 0 ; j < spots[i].length; ++j)
        {
          if(!isEnded)
          {
            if(revealed[i][j])
            {
              int sp = spots[i][j];
              g.drawImage(sprites[2+sp/8][sp%8],50+16*i,50+16*j,null);
            }
            else if (!flagged[i][j])
            g.drawImage(sprites[2][0],50+16*i,50+16*j,null);
            else
              g.drawImage(sprites[2][2],50+16*i,50+16*j,null);
            int sp = spots[i][j];
            g.drawImage(sprites[2+sp/8][sp%8],50+16*i,50+16*(j+w+2),null);
          }
          else
          {
            if(flagged[i][j])
            {
              if(spots[i][j] == 5)
                spots[i][j] = 2;
              else
                spots[i][j] = 7;
            }
            int sp = spots[i][j];
            g.drawImage(sprites[2+sp/8][sp%8],50+16*i,50+16*j,null);
          }
          
        }
    }

    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseClicked(MouseEvent e){
      System.out.print(e.getButton());
      System.out.println(" klik at "+e.getX()+','+e.getY());
      int x = e.getX()-8;
      int y = e.getY()-31;
      if(!isEnded && x >= 50 && y >= 50 && x < 50+16*h && y < 50+16*w)
      {
        if(e.getButton() == e.BUTTON1)
          reveal((x-50)/16,(y-50)/16);
        if(e.getButton() == e.BUTTON3 && !revealed[(x-50)/16][(y-50)/16])
          flagged[(x-50)/16][(y-50)/16] = !flagged[(x-50)/16][(y-50)/16];
        repaint();
      }
    }
    public void mouseExited(MouseEvent e){}

}