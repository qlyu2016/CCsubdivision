package Main;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import DataStructure.Face;
import DataStructure.Vertex;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.glu.GLUtessellatorCallback;
import com.jogamp.opengl.glu.GLUtessellatorCallbackAdapter;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;






public class Main implements GLEventListener{
    private static RunCC runCC;
	static String TITLE = "LINES";
    static File file;
    static int CANVAS_WEIDTH = 500;
    static int CANVAS_HEIGHT = 600;
	private GLU glu;
    private GL2 gl;
    private float ritix = 0.0f;
    private float ritiy = 0.0f;
    //C-C subdivision�Ľ��
    private Face[] faceGroup;
    private Vertex[] vertGroup;
    private int fGNumber;
    private int vGNumber;
    
    private double centerx; //�ҵ�ͼ������
    private double centery; //�ҵ�ͼ������
    private double centerz; //�ҵ�ͼ������
    private float scale;  //ȡ��ͼ�δ�С����
    private Texture[] flat = new Texture[20];
    private static double mouseX = -1.0d;
    private static double mouseY = -1.0d;
    private static double mouseXN;
    private static double mouseYN;
    
    public Main(){
    }	
    
    public static void setMouseXY(double mouseX, double mouseY){
    	mouseXN = mouseX;
    	mouseYN = mouseY;
    }
    
    public void setFileOn(File file) throws IOException{
    	this.file = file;
    	runCC = new RunCC(file);
       	runCC.run();
    	
    	this.faceGroup = runCC.getFaceGroup();
    	this.vertGroup = runCC.getVertGroup();
    	this.fGNumber = runCC.getfGNumber();
    	this.vGNumber = runCC.getvGNumber();
    	this.calCenter(this.vertGroup, this.vGNumber);
    	this.calScale(this.vertGroup, this.vGNumber);
    	System.out.println("Centerx : " + Double.toString(this.centerx) 
    			+ "Centery : " + Double.toString(this.centery)
    			+ "Centerz : " + Double.toString(this.centerz));
    }

    //����չʾͼ�ε����ĵ㡣
    public void calCenter(Vertex[] vertGroup, int vGNumber){
    	this.centerx = 0;
    	this.centery = 0;
    	this.centerz = 0;
    	for(int i = 0; i < vGNumber; i ++){
    	    this.centerx += vertGroup[i + 1].getX();
    	    this.centery += vertGroup[i + 1].getY();
    	    this.centerz += vertGroup[i + 1].getZ();
    	}
    	this.centerx /= vGNumber;
    	this.centery /= vGNumber;
    	this.centerz /= vGNumber;
    }
    
    //����չʾͼ�δ�ŵĴ�С��
    public void calScale(Vertex[] vertGroup, int vGNumber){
    	this.scale = 0;
    	for(int i = 0; i < vGNumber; i ++){
             double temp = (this.vertGroup[i + 1].getX() - this.centerx) * (this.vertGroup[i + 1].getX() - this.centerx)
            		 +  (this.vertGroup[i + 1].getY() - this.centery) * (this.vertGroup[i + 1].getY() - this.centery)
            		 +  (this.vertGroup[i + 1].getZ() - this.centerz) * (this.vertGroup[i + 1].getZ() - this.centerz);
    	     temp = Math.pow(temp, 0.5d);
    	     if(this.scale < temp){
    	    	 this.scale = (float) temp;
    	     }
    	}
    	
    }
    
    //������һ��ϸ�֡�
    public void perform(){
    	//һ��ϸ��
       	runCC.run();
    	
    	this.faceGroup = runCC.getFaceGroup();
    	this.vertGroup = runCC.getVertGroup();
    	this.fGNumber = runCC.getfGNumber();
    	this.vGNumber = runCC.getvGNumber();
    	this.calCenter(this.vertGroup, this.vGNumber);
    	this.calScale(this.vertGroup, this.vGNumber);
    	System.out.println("Centerx : " + Double.toString(this.centerx) 
    			+ "Centery : " + Double.toString(this.centery)
    			+ "Centerz : " + Double.toString(this.centerz));
    }
    
    //����ƽ��ķ�����
    public Vertex findNormal(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3){
    	Vertex normal;
    	double vx, vy, vz, ux, uy, uz, length;
    	vx = x2 - x1;
    	vy = y2 - y1;
    	vz = z2 - z1;
    	ux = x3 - x2;
    	uy = y3 - y2;
    	uz = z3 - z2;
    	length = Math.pow((vx - ux) * (vx - ux) + (vy - uy) * (vy - uy) + (vz - uz) * (vz - uz), 0.5d);
    	
    	//�������u��v���õ���������
    	normal = new Vertex((vy * uz - vz * uy) / length, (vz * ux - vx * uz) / length, (vx * uy - vy * ux) / length);
    	
    	return normal;
    }
    
	@Override
	public void display(GLAutoDrawable drawable){		
		
	        if(this.file == null){
				System.out.println("No File!");
				gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
				
				gl.glViewport(0, 0, Main.CANVAS_WEIDTH, Main.CANVAS_HEIGHT);
				gl.glMatrixMode(GL2.GL_PROJECTION);
				gl.glLoadIdentity();  
				gl.glMatrixMode(GL2.GL_MODELVIEW);
			    gl.glLoadIdentity();
			    
			    gl.glColor3f(1.0f, 0.0f, 0.0f);
			    gl.glBegin(GL.GL_LINES);
			        gl.glVertex3f(-1.0f, 0.0f, 0.0f);
			        gl.glVertex3f(1.0f, 0.0f, 0.0f);
			    gl.glEnd();
		    }else{
				
				gl = drawable.getGL().getGL2();
				//������ת����
				if(mouseX == -1.0d){
				    mouseX = mouseXN;
				    mouseY = mouseYN;
				}else{
				    ritix += mouseXN - mouseX;
				    ritiy += mouseYN - mouseY;
				    mouseX = mouseXN;
				    mouseY = mouseYN;
				    //System.out.println("mouseX : " + mouseX + " mouseY : " + mouseY);
				//ritiz = 0;
				}
				
				//����һЩ����
				gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
				
				gl.glViewport(0, 0, Main.CANVAS_WEIDTH, Main.CANVAS_HEIGHT);
				gl.glMatrixMode(GL2.GL_PROJECTION);
				gl.glLoadIdentity();              //set the current matrix to identity matrix
			    //glu.gluPerspective(45.0f, 1.0f, 0.1f, 100.0f);
				gl.glMatrixMode(GL2.GL_MODELVIEW);
			    gl.glLoadIdentity();
			    gl.glTranslated( - this.centerx / this.scale, - this.centery / this.scale, - this.centerz / this.scale);
			    
			    gl.glRotatef(ritix, 1.0f, 0.0f, 0.0f);
			    gl.glRotatef(ritiy, 0.0f, 1.0f, 0.0f);
			    //gl.glRotatef(ritiz, 0.0f, 0.0f, 1.0f);
			    gl.glScalef(1.0f / (1.5f * this.scale), 1.0f / (1.5f * this.scale), 1.0f / (1.5f * this.scale));
			    
			    //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);  

			    gl.glColor3f(0.0f, 0.0f, 1.0f);
			    
			    
			    //���õƹ�
			    float SHINE_ALL_DIRECTIONS = 1;
			    
			    float[] lightPos1 = {50.0f, 50.0f, -100.0f, SHINE_ALL_DIRECTIONS};
			    float[] lightColorAmbient1 = {0.3f, 0.3f, 0.3f, 1f};
			    float[] lightColorDiffuse1 = {0.7f, 0.7f, 0.7f, 1f};
			    
			    float[] lightPos2 = {-50.0f, -50.0f, 100.0f, 1.0f};
			    float[] lightColorAmbient2 = {0.3f, 0.3f, 0.3f, 1f};
			    float[] lightColorDiffuse2 = {0.7f, 0.7f, 0.7f, 1f};
			    
			    
			    gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos1, 0);
			    gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient1, 0);
			    gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, lightColorDiffuse1, 0);
			    gl.glEnable(GL2.GL_LIGHT1);
			    
			    gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, lightPos2, 0);
			    gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, lightColorAmbient2, 0);
			    gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, lightColorDiffuse2, 0);
			    gl.glEnable(GL2.GL_LIGHT2);
			    
			    
			    gl.glEnable(GL2.GL_LIGHTING);
			    gl.glEnable(GL2.GL_RESCALE_NORMAL);
			    
		        float[] rgba = {0.3f, 0.5f, 1f};
		        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
		        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, rgba, 0);
		        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0.5f);
		        
		        
			    //������ͼ
			    //flat[1].enable(gl);
			    //flat[1].bind(gl);

		        
		        //ʹ�÷ָ񻯷���������
		        final List<Integer> allIndices = new ArrayList<Integer>(); //��ŷָ�֮���ָ��
		    	GLUtessellator tessellator= glu.gluNewTess();  //�����ָ񻯶���
		    	//ȷ����ͬ�����������õĺ���
		    	GLUtessellatorCallback callback = new GLUtessellatorCallbackAdapter() {
		    		int type;
		    		List<Integer> indices = new ArrayList<Integer>();
		    		
		    		public List<Integer> getIndices(){
		    			return this.indices;
		    		}
		    		
					@Override
					public void begin(int type) {
						this.type = type;
						indices.clear();
						//System.out.println(Integer.toString(type));
					}
					@Override
					public void end() {
						switch(type){
						case GL.GL_TRIANGLES:
							break;
						case GL.GL_TRIANGLE_FAN:
							this.indices = fanToTriangles(indices);
							break;
						case GL.GL_TRIANGLE_STRIP:
							this.indices = stripToTriangles(indices);
				            break;
				        default: System.out.println("Default!");
						}
						allIndices.addAll(indices);
					}
					
					@Override
					public void vertex(Object data) {
						indices.add((int) data);
					}
					
                    @Override
                    public void error(int errnum) {
                        throw new RuntimeException("GLU Error " + glu.gluErrorString(errnum));
                    }
                    
                    //GL_TRIANGLE_STRIP�����
                    public List<Integer> stripToTriangles(List<Integer> strip) {
                        List<Integer> triangles = new ArrayList<>();
                        for (int i = 0; i < strip.size() - 2; i++) {
                            triangles.addAll(strip.subList(i, i + 3));
                        }
                        return triangles;
                    }
                    
                    //GL_TRIANGLE_FAN�����
                    public List<Integer> fanToTriangles(List<Integer> fan) {
                        int centralVertex = fan.get(0);
                        List<Integer> triangles = new ArrayList<>();
                        for (int i = 1; i < fan.size() - 1; i++) {
                            triangles.add(centralVertex);
                            triangles.addAll(fan.subList(i, i + 2));
                        }
                        return triangles;
                    }
		    	 };
		    	 
		    	 glu.gluTessCallback(tessellator, GLU.GLU_TESS_BEGIN, callback);
		    	 glu.gluTessCallback(tessellator, GLU.GLU_TESS_END, callback);
		    	 glu.gluTessCallback(tessellator, GLU.GLU_TESS_VERTEX, callback);
                 //tessellator׼����ϣ���ʼ��ͼ�Ρ�
		    	 
		    	 for(int j = 0; j < this.fGNumber; j ++){
		    	      glu.gluTessBeginPolygon(tessellator, this.faceGroup[j + 1]);
		    	      double[] temp = new double[this.faceGroup[j + 1].getVertNumber() * 3];
		    	      int[] vert = this.faceGroup[j + 1].getVert();
		    	      int fGcount = 0;
		    	      for(int i = 0; i < this.faceGroup[j + 1].getVertNumber(); i ++){
		    		       //������ŵ�һ��double�����С�
		    		       temp[fGcount] = this.vertGroup[vert[i]].getX();
		    		       temp[fGcount + 1] = this.vertGroup[vert[i]].getY();
		    		       temp[fGcount + 2] = this.vertGroup[vert[i]].getZ();
		    		       fGcount = fGcount + 3;
		    	       }
		    	       
		    	     glu.gluTessBeginContour(tessellator);
		    	         for(int i = 0; i < this.faceGroup[j + 1].getVertNumber(); i ++){
		    	             glu.gluTessVertex(tessellator, temp, i * 3, i);  
		    	         }
                     glu.gluTessEndContour(tessellator);
                     glu.gluTessEndPolygon(tessellator);

		    	 
                     gl.glBegin(GL.GL_TRIANGLES);
                     
                        for(int i = 0; i < (allIndices.size() / 3); i ++){
                        	Vertex normal = this.findNormal(temp[3 * allIndices.get(3 * i)], temp[3 * allIndices.get(3 * i) + 1], temp[3 * allIndices.get(3 * i) + 2], 
                            		temp[3 * allIndices.get(3 * i + 1)], temp[3 * allIndices.get(3 * i + 1) + 1], temp[3 * allIndices.get(3 * i + 1) + 2], 
                            		temp[3 * allIndices.get(3 * i + 2)], temp[3 * allIndices.get(3 * i + 2) + 1], temp[3 * allIndices.get(3 * i + 2) + 2]);
                            gl.glNormal3d(normal.getX(), normal.getY(), normal.getZ());
                	        gl.glVertex3d(temp[3 * allIndices.get(3 * i)], temp[3 * allIndices.get(3 * i) + 1], temp[3 * allIndices.get(3 * i) + 2]);
                	        gl.glVertex3d(temp[3 * allIndices.get(3 * i + 1)], temp[3 * allIndices.get(3 * i + 1) + 1], temp[3 * allIndices.get(3 * i + 1) + 2]);
                	        gl.glVertex3d(temp[3 * allIndices.get(3 * i + 2)], temp[3 * allIndices.get(3 * i + 2) + 1], temp[3 * allIndices.get(3 * i + 2) + 2]);
                        }
                     gl.glEnd();
                     
                     allIndices.clear();
		    	 }
				 
                glu.gluDeleteTess(tessellator);
				}
			    
    }

	

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
//	      int texture[] = new int[10];
		  gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
	      glu = new GLU();                         // get GL Utilities
	      gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f); // set background (clear) color
	      gl.glClearDepth(1.0f);			// ������Ȼ���
	  	  gl.glEnable(GL.GL_DEPTH_TEST);	// ������Ȳ���
	  	  gl.glDepthFunc(GL.GL_LEQUAL);		//the type of depth test
	      gl.glShadeModel(GL2.GL_SMOOTH);   // blends colors nicely, and smoothes out lighting// ----- Your OpenGL initialization code here -----
	      gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);			// ����ϵͳ��͸�ӽ�������
	      
	      for(int i = 0; i < 18; i ++){
	    	  String tmp = Integer.toString(i + 1);
	          try {
				  FileInputStream fileStream = new FileInputStream("D:/software/eclipse/workspace/OPenGLTest/" + tmp + ".jpg");
			      TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), fileStream, false, tmp);
	              this.flat[i] = TextureIO.newTexture(data);
	          } catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
	      }
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3,
			int arg4) {
	}  
      
}  