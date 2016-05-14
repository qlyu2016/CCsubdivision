package Main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;



public class MainInterface extends JFrame{
	private static File file;
	private static Main line = new Main();
    private static GLJPanel canvas = new GLJPanel();
    private static FPSAnimator animator = new FPSAnimator(canvas, Main.CANVAS_HEIGHT, false);
	
	public static void main(String args[]){
	    //记录鼠标位置   
		JFrame frame = new JFrame("Test");
	      
        //布局：BorderLayout
        BorderLayout bl = new BorderLayout();   
	    frame.setLayout(bl);

	    //选项面板，具有功能：选择文件，进行一次细分。
        JPanel menu = new JPanel();  
        JLabel title = new JLabel("Catmull-Clark Subdivision", JLabel.CENTER);
        JButton nextSub = new JButton("Next Subvision"); 
	    JButton fileSelect = new JButton("File Select");
	  
        //选择需要细分的文件
	    fileSelect.addActionListener(new ActionListener(){
		    @Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				  JFileChooser jFC = new JFileChooser();
			      jFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			      jFC.showDialog(new JLabel(), "选择");
			      file = jFC.getSelectedFile();
			      
			      if(file.isFile() && file.getName().endsWith(".obj")){ 
			    	    animator.start();
			    	    try {
							line.setFileOn(file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            System.out.println("文件夹:" + file.getAbsolutePath());  
			      }else{
			    	  System.out.println("Type Error.");
			    	  JDialog dialog = new JDialog(frame, "Warning");
			          dialog.setSize(new Dimension(300,200));
			          JLabel warning = new JLabel("Please put in a file ends with '.obj'", JLabel.CENTER);
			    	  dialog.add(warning);
			          dialog.setVisible(true);
			      } 
			        System.out.println(jFC.getSelectedFile().getName());  
			    }  
	     });
	      
	      //再执行一次细分
	      nextSub.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(file.isFile() && file.getName().endsWith(".obj")){ 
		    	    animator.stop();
		    	    line.perform();
		    	    animator.start();
		      }else{
		    	  System.out.println("Type Error.");
		    	  JDialog dialog = new JDialog(frame, "Warning");
		          dialog.setSize(new Dimension(300,200));
		          JLabel warning = new JLabel("Please put in a file ends with '.obj'", JLabel.CENTER);
		    	  dialog.add(warning);
		          dialog.setVisible(true);
		      } 
			}
	    	  
	      });
	      
	      
	      menu.setLayout(new BorderLayout());
	      menu.add(fileSelect, BorderLayout.WEST);
	      menu.add(nextSub, BorderLayout.EAST);
	      menu.add(title);
	      
	      
	      //设置JOGL界面。
	      canvas.addGLEventListener(line);
	      
	      //鼠标选中可以旋转功能（未实现）
	      MouseDrag mListener = new MouseDrag();
	      canvas.addMouseMotionListener(mListener);
	      
	      
	      frame.add(menu, BorderLayout.NORTH);
	      frame.add(canvas, BorderLayout.CENTER);
	      
	      frame.setTitle(Main.TITLE);
       	  frame.setSize(new Dimension(500,600));
	      frame.setVisible(true);
	      
	}
}
	
