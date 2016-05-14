package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import DataStructure.Edge;
import DataStructure.Face;
import DataStructure.Vertex;




public class RunCC{
    //所需数据（点，线，面）
	private int vertNumber = 0;  // 初始face和vertex数量比较大的那一个 
    private Vertex[] vertGroup;
    private Face[] faceGroup;
    private Edge[] edgeGroup;
    private int fGNumber;
    private int vGNumber;    
    //C-C subdivision
    private Vertex[] faceVertex;
    private Vertex[] edgeVertex;
    private Vertex[] vertVertex;
    private int FPCount;
    private int EPCount;
    private int VPCount;
    private Face[] CCFace; //= new Face[4 * vertNumber];
    private Edge[] CCEdge; //= new Edge[8 * vertNumber];
	private int CCEdgeCount;
	private int CCFaceCount;
	
	
    //输入obj文件得到点和面数据
	public RunCC(File file) throws IOException{
		BufferedReader f = new BufferedReader(new FileReader(file));
		BufferedReader tmp = new BufferedReader(new FileReader(file));
		
		@SuppressWarnings("unused")
		StringTokenizer st = new StringTokenizer(f.readLine(), "\n");
		
		//Initialize（分割每一行数据）
	    String[] check = new String[10];

	    //通用的一个计数工具
	    int i = 0;
	    
	    tmp.readLine();
	    tmp.readLine();
	    while(tmp.readLine() != null){
	    	this.vertNumber ++;
	    }
	    this.vertNumber /= 2;
	    
	    this.vertGroup = new Vertex[this.vertNumber];
	    this.faceGroup = new Face[this.vertNumber];
	    this.edgeGroup = new Edge[2 * this.vertNumber];
	    
		//读取v数据
	    f.readLine();
	    check[0] = " ";
        while(!check[0].equals("vt")){
        	StringTokenizer st1 = new StringTokenizer(f.readLine(), "\n");
        	if(st1.hasMoreTokens()){
        		String temp = st1.nextToken();
        		check = temp.split(" ");
        	}
        	//单独处理点(v)数据
        	if(check[0].equals("v")){
        		double x, y, z;
        		x = Double.parseDouble(check[1]);
        		y = Double.parseDouble(check[2]);
        		z = Double.parseDouble(check[3]);
        	    vertGroup[i + 1] = new Vertex(x, y, z);
        	    vertGroup[i + 1].setOrder(i + 1);
        		i ++;
        	}
        }
        
        
        //去掉vt数据，不需要
        while(!check[0].equals("g")){
        	StringTokenizer st2 = new StringTokenizer(f.readLine(), "\n");
        	if(st2.hasMoreTokens()){
        		String temp = st2.nextToken();
        		check = temp.split(" ");
        	}
        }
        
        this.vGNumber = i;
        //读取f数据
		String buf;
		i = 0;
		while(null != (buf = f.readLine())){
            StringTokenizer st3 = new StringTokenizer(buf, "\n");
            
            if(st3.hasMoreTokens()){
        		String temp = st3.nextToken();
        		check = temp.split(" ");
        		//System.out.println(temp);
        		//System.out.println(check[0])；
        	}else{
        		break;
        	}
            
            if(check[0].equals("f")){
        		faceGroup[i + 1] = new Face();
        		faceGroup[i + 1].setOrder(i + 1);
            	for(int j = 0; j < check.length - 1; j ++){
            		int ord = Integer.parseInt(check[j+1].split("/")[0]);
            		faceGroup[i + 1].addVert(ord);
            		vertGroup[ord].addAdjFace(i + 1);
            	}
            	i ++;
            	
            }
        }
		
		this.fGNumber = i;
		
		//用f数据和v数据计算出edge数据
		int edgeCount = 0;
		int j = 0;
		while(faceGroup[j + 1] != null){
			int[] polygon = faceGroup[j + 1].getVert();
			for(int k = 0; k < faceGroup[j + 1].getVertNumber(); k ++){
				int repeat = 0;
				edgeGroup[edgeCount + 1] = new Edge();
				if(polygon[k] <= polygon[(k + 1) % faceGroup[j + 1].getVertNumber()]){
				   edgeGroup[edgeCount + 1].setVert1(polygon[k]);
				   edgeGroup[edgeCount + 1].setVert2(polygon[(k + 1) % faceGroup[j + 1].getVertNumber()]);
				   edgeGroup[edgeCount + 1].addFaceVec(0);
				}else{
				   edgeGroup[edgeCount + 1].setVert2(polygon[k]);
				   edgeGroup[edgeCount + 1].setVert1(polygon[(k + 1) % faceGroup[j + 1].getVertNumber()]);
				   edgeGroup[edgeCount + 1].addFaceVec(1);
				}
				if((repeat = RunCC.isOverlap(edgeGroup, edgeGroup[edgeCount + 1])) == 0){
					edgeGroup[edgeCount + 1].addAdjFace(j + 1);
					edgeGroup[edgeCount + 1].setOrder(edgeCount + 1);
					vertGroup[polygon[k]].addAdjEdge(edgeCount + 1);
					vertGroup[polygon[(k + 1) % faceGroup[j + 1].getVertNumber()]].addAdjEdge(edgeCount + 1);
					faceGroup[j + 1].addEdge(edgeCount + 1);
					edgeCount ++;
				}else{
					edgeGroup[repeat].addAdjFace(j + 1);
					edgeGroup[repeat].addFaceVec(edgeGroup[edgeCount + 1].getFaceVec()[0]);
					edgeGroup[edgeCount + 1] = null;
					faceGroup[j + 1].addEdge(repeat);
				}

			}
			j++;
		}
		//System.out.println("EdgeCount : " + Integer.toString(edgeCount));
		
		f.close();
    }

	//使用上述规则公式计算面点
	public Vertex[] calFaceVertex(int faceVertNumber, Face[] faceGroup, Vertex[] vertGroup){
		//进行Catmull-Clark细分曲面运算
				//首先计算Face Points
				int fpCount = 0;
				Vertex[] faceVertex = new Vertex[faceVertNumber];
				while(faceGroup[fpCount + 1] != null){
					int FVertNumber = 0;
					double x = 0, y = 0, z = 0;
					int[] tempVert = faceGroup[fpCount + 1].getVert();
					while(tempVert[FVertNumber] != 0){
						x = x + vertGroup[tempVert[FVertNumber]].getX();
						y = y + vertGroup[tempVert[FVertNumber]].getY();
						z = z + vertGroup[tempVert[FVertNumber]].getZ();
						FVertNumber ++;
					}
					faceVertex[fpCount + 1] = new Vertex(x / FVertNumber, y / FVertNumber, z / FVertNumber);
					faceVertex[fpCount + 1].setOrder(fpCount + 1);
					fpCount ++;
				}

    	        this.setFPCount(fpCount);
    	      
				return faceVertex;
	}
	
	//使用上述规则公式计算边点
	public Vertex[] calEdgeVertex(int edgeVertNumber, Edge[] edgeGroup, Vertex[] vertGroup, Vertex[] faceVertex){
		//接着计算Edge Points
		        Vertex[] edgeVertex = new Vertex[edgeVertNumber];
				int epCount = 0;  //Edge points的计数。
				
				while(edgeGroup[epCount + 1] != null){
					double x = 0, y = 0, z = 0;
					int tempVert1 = edgeGroup[epCount + 1].getVert1();
					int tempVert2 = edgeGroup[epCount + 1].getVert2();
					int[] face = edgeGroup[epCount + 1].getAdjFace();
					x = vertGroup[tempVert1].getX() + vertGroup[tempVert2].getX();
					y = vertGroup[tempVert1].getY() + vertGroup[tempVert2].getY();
					z = vertGroup[tempVert1].getZ() + vertGroup[tempVert2].getZ();
					x = x + faceVertex[face[0]].getX() + faceVertex[face[1]].getX();
					y = y + faceVertex[face[0]].getY() + faceVertex[face[1]].getY();
					z = z + faceVertex[face[0]].getZ() + faceVertex[face[1]].getZ();
					
					edgeVertex[epCount + 1] = new Vertex(x / 4, y / 4, z / 4);
					edgeVertex[epCount + 1].setOrder(epCount + 1);
					epCount ++;
				}
				
    	        this.setEPCount(epCount);
    	        
				return edgeVertex;
	}
	
	//使用上述规则公式计算角点
    public Vertex[] calVertVertex(int vertVertNumber, Vertex[] vertGroup, Vertex[] faceVertex, Edge[] edgeGroup){
    	//最后计算Vertex Points
    	        Vertex[] vertVertex = new Vertex[vertVertNumber];
    			int vpCount = 0;
    			while(vertGroup[vpCount + 1] != null){
    				int adjEdgeNumber = vertGroup[vpCount + 1].getAdjEdgeNumber();  // n
    				int adjFaceNumber = vertGroup[vpCount + 1].getAdjFaceNumber();
    				double x = 0, y = 0, z = 0;       //desired result;
    				double faceAverx = 0, faceAvery = 0, faceAverz = 0, edgeAverx = 0, edgeAvery = 0, edgeAverz = 0;
    				int[] face = vertGroup[vpCount + 1].getAdjFace();
    				int[] edge = vertGroup[vpCount + 1].getAdjEdge();
    				for(int k = 0; k < adjFaceNumber; k ++){
    					faceAverx = faceAverx + faceVertex[face[k]].getX();
    					faceAvery = faceAvery + faceVertex[face[k]].getY();
    					faceAverz = faceAverz + faceVertex[face[k]].getZ();
    				}
    				faceAverx = faceAverx / adjFaceNumber;
    				faceAvery = faceAvery / adjFaceNumber;
    				faceAverz = faceAverz / adjFaceNumber;
    				for(int k = 0; k < adjEdgeNumber; k ++){
    					edgeAverx = edgeAverx + vertGroup[edgeGroup[edge[k]].getVert1()].getX() / 2 
    							+ vertGroup[edgeGroup[edge[k]].getVert2()].getX() / 2;
    					edgeAvery = edgeAvery + vertGroup[edgeGroup[edge[k]].getVert1()].getY() / 2 
    							+ vertGroup[edgeGroup[edge[k]].getVert2()].getY() / 2;
    					edgeAverz = edgeAverz + vertGroup[edgeGroup[edge[k]].getVert1()].getZ() / 2 
    							+ vertGroup[edgeGroup[edge[k]].getVert2()].getZ() / 2;

    				}
    				edgeAverx = edgeAverx / adjEdgeNumber;
    				edgeAvery = edgeAvery / adjEdgeNumber;
    				edgeAverz = edgeAverz / adjEdgeNumber;
    				
    				//计算Vertex Points
    				x = (faceAverx + 2 * edgeAverx + (adjEdgeNumber - 3) * vertGroup[vpCount + 1].getX()) / adjEdgeNumber;
    				y = (faceAvery + 2 * edgeAvery + (adjEdgeNumber - 3) * vertGroup[vpCount + 1].getY()) / adjEdgeNumber;
    				z = (faceAverz + 2 * edgeAverz + (adjEdgeNumber - 3) * vertGroup[vpCount + 1].getZ()) / adjEdgeNumber;
    				
    				vertVertex[vpCount + 1] = new Vertex(x, y, z);
    				vertVertex[vpCount + 1].setOrder(vpCount + 1);
    				vpCount ++;
    				}

    	        this.setVPCount(vpCount);
    	       
    			return vertVertex;
    }	
    
    
    
  //得到新的顶点后，边是如何产生？
    //1：每个面顶点（Face Point）VF与包围它的边对应的边顶点(Edge Point)VE相连。
    //2：每个顶点调整后得到的新顶点（new vertex point）v’与它相邻的边上的点（edge point）VE相连。
	
    public Edge[] calCCEdge(int CCEdgeNumber, int FPCount, int EPCount, Vertex[] faceVertex, Face[] faceGroup, Vertex[] vertVertex, Vertex[] vertGroup){
    	Edge[] CCEdge = new Edge[CCEdgeNumber];
    	int ccEdgeCount = 0;
    	int[] adjEdge;
    	int fpcount = 0;     //face points计数数值
    	//连接face points和包围它边的边顶点
    	while(faceVertex[fpcount + 1] != null){
    		adjEdge = faceGroup[fpcount + 1].getEdge();
    		int adjEdgeNumber = faceGroup[fpcount + 1].getEdgeNumber();
    		for(int i = 0; i < adjEdgeNumber; i ++){
    			CCEdge[ccEdgeCount + 1] = new Edge();
    				
    			CCEdge[ccEdgeCount + 1].setVert1(fpcount + 1);   //faceVertex中的点，范围在1 ~ FPCount;
    			CCEdge[ccEdgeCount + 1].setVert2(FPCount + adjEdge[i]); //edgeVertex中的点，范围在FPCount+1  ~ FPCount+EPCount;
    			
    			CCEdge[ccEdgeCount + 1].setOrder(ccEdgeCount + 1);
    			ccEdgeCount ++;
    		}
    		fpcount ++;
    	}
    	
    	//连接vertex points和相邻边上的edge points。
    	fpcount = 0; //vertex points的计数数值
    	while(vertVertex[fpcount + 1] != null){
    		adjEdge = vertGroup[fpcount + 1].getAdjEdge();
    		int adjEdgeNumber = vertGroup[fpcount + 1].getAdjEdgeNumber();
    		for(int i = 0; i < adjEdgeNumber; i ++){
    			CCEdge[ccEdgeCount + 1] = new Edge();
    			
    			CCEdge[ccEdgeCount + 1].setVert1(FPCount + adjEdge[i]);   //edgeVertex中的点，范围在FPCount+1  ~ FPCount+EPCount;
    			CCEdge[ccEdgeCount + 1].setVert2(FPCount + EPCount + fpcount + 1);   //vertVertex中的点，范围在FPCount+EPCount+1 ~ FPCount+EPCount+VPCount
    		    
    			CCEdge[ccEdgeCount + 1].setOrder(ccEdgeCount + 1);
    			ccEdgeCount ++;
    			
    		}
    		fpcount ++;
    	}

        	this.setCCEdgeCount(ccEdgeCount);
        
    	return CCEdge;
    	
    }
    
    //用faceVetex，faceGroup和edgeGroup计算出CCFace
    public Face[] calCCFace(int CCFaceNumber, int FPCount, int EPCount, Vertex[] faceVertex, Face[] faceGroup, Edge[] edgeGroup){
    	Face[] CCFace = new Face[CCFaceNumber];
    	int ccFaceCount = 0;
    	int fpcount = 0;
    	int[] adjEdge;
    	while(faceVertex[fpcount + 1] != null){
    		adjEdge = faceGroup[fpcount + 1].getEdge();
    		int adjEdgeNumber = faceGroup[fpcount + 1].getEdgeNumber();
    		for(int i = 0; i < adjEdgeNumber; i ++){
    		      //int EP = 0;
    			  CCFace[ccFaceCount + 1] = new Face();
    		      CCFace[ccFaceCount + 1].addVert(fpcount + 1);  //Add the face point: 1 ~ FPCount
    		      CCFace[ccFaceCount + 1].addVert(FPCount + adjEdge[i]);  //Add first edge point: FPCount+1 ~ FPCount+EPCount
                  
    		      int VP1 = this.searchOL(adjEdge[i], adjEdge[(i + 1) % adjEdgeNumber], edgeGroup); //查找该面所对应的vertex point
    		      CCFace[ccFaceCount + 1].addVert(FPCount + EPCount + VP1); //Add a vertex point: FPCount+EPCount+1 ~ FPCount+EPCount+VPCount
    		      CCFace[ccFaceCount + 1].addVert(FPCount + adjEdge[(i + 1) % adjEdgeNumber]); //Add second edge point
    		      CCFace[ccFaceCount + 1].setOrder(ccFaceCount + 1);
    		      
    		      ccFaceCount ++;
    		}
    		fpcount ++;
    	}
        	this.setCCFaceCount(ccFaceCount);
        
    	return CCFace;
    }
    
    //执行一次细分
    public RunCC run(){
    	//求面点，边点，角点。
    	if(this.EPCount == 0){    		
    	    System.out.println("First Round.");
    	}else{
    		this.vertNumber = this.vertNumber * 4;
    		//设置新一轮的vertGroup点
    		this.vertGroup = this.calVertGroup(this.vertNumber, this.FPCount, this.EPCount, this.VPCount, this.faceVertex, this.edgeVertex, this.vertVertex);
    		this.vGNumber = this.FPCount + this.EPCount + this.VPCount;
    		//设置新一轮的faceGroup面
    		this.faceGroup = this.calFaceGroup(this.vertNumber, this.CCFaceCount, this.CCFace, this.vertGroup);
    		this.fGNumber = this.CCFaceCount;
    		//设置新一轮的edgeGroup边
    		this.edgeGroup = this.calEdgeGroup(2 * this.vertNumber, this.CCEdgeCount, this.faceGroup, this.vertGroup);
    	}
    	
    	
    	this.faceVertex = this.calFaceVertex(vertNumber, this.faceGroup, this.vertGroup);
        this.edgeVertex = this.calEdgeVertex(2 * vertNumber, this.edgeGroup, this.vertGroup, this.faceVertex);
        this.vertVertex = this.calVertVertex(vertNumber, this.vertGroup, this.faceVertex, edgeGroup);
        //求CCEdge和CCFace。
        this.CCEdge = this.calCCEdge(8 * vertNumber, this.FPCount, this.EPCount, this.faceVertex, this.faceGroup, this.vertVertex, this.vertGroup);
        this.CCFace = this.calCCFace(4 * vertNumber, this.FPCount, this.EPCount, this.faceVertex, this.faceGroup, this.edgeGroup);
        
        return this;
    }
    
    //验证新加入EdgeGroup的Edge是否是重复的
	public static int isOverlap(Edge[] edgeGroup, Edge edge){
		int i = 0;
		while(edgeGroup[i + 1] != edge){
			if(edgeGroup[i + 1].getVert1() == edge.getVert1() && edgeGroup[i + 1].getVert2() == edge.getVert2()){
				return i + 1;
			}
			i ++;
		}
		return 0;
	}
	
	//找到两条Edge的交点
	public int searchOL(int EP1, int EP2, Edge[] edgeGroup){  //EP1 <= EP2 required.
		if(edgeGroup[EP1].getVert1() == edgeGroup[EP2].getVert1()){
			return edgeGroup[EP1].getVert1();
		}else if(edgeGroup[EP1].getVert2() == edgeGroup[EP2].getVert1()){
			return edgeGroup[EP1].getVert2();
		}else if(edgeGroup[EP1].getVert1() == edgeGroup[EP2].getVert2()){
			return edgeGroup[EP1].getVert1();
		}else if(edgeGroup[EP1].getVert2() == edgeGroup[EP2].getVert2()){
			return edgeGroup[EP1].getVert2();
		}else{
			System.out.println("Can't find!");
			return 0;
		}
	}
	
	//输出一组Edge
	public void writeEdgeGroup(String fileURI, Edge[] edgeGroup) throws IOException{
		FileWriter data = new FileWriter(fileURI);
		
		int count = 0;
		while(edgeGroup[count + 1] != null){
		     data.write("eG " + Integer.toString(edgeGroup[count + 1].getVert1()) + "/"
		    		 + Integer.toString(edgeGroup[count + 1].getVert2())  + " ");
		     data.write("f: ");
		     data.write(Integer.toString(edgeGroup[count + 1].getAdjFace()[0]) + " ");
		     data.write(Integer.toString(edgeGroup[count + 1].getAdjFace()[1]) + " ");
		     data.write("dir:");
		     data.write(Integer.toString(edgeGroup[count + 1].getFaceVec()[0]) + " ");
		     data.write(Integer.toString(edgeGroup[count + 1].getFaceVec()[1]) + " ");
		     data.write("\n");
		     count ++;
		}
		data.close();
	}
	
	//输出一组Face
	public void writeFaceGroup(String fileURI, Face[] faceGroup) throws IOException{
		FileWriter data = new FileWriter(fileURI);
		
		int count = 0;
	    while(faceGroup[count + 1] != null){
	    	data.write("fG ");
	    	data.write("v: ");
	    	for(int i = 0; i < faceGroup[count + 1].getVertNumber(); i ++){
	    	    data.write(faceGroup[count + 1].getVert()[i] + " ");
	    	}
	    	data.write("e: ");
	    	for(int i = 0; i < faceGroup[count + 1].getEdgeNumber(); i ++){
	    		data.write(faceGroup[count + 1].getEdge()[i] + " ");
	    	}
	    	data.write("\n");
	        count ++;
	    }
	    data.close();
	}
	
	//输出一组Vertex
	public void writeVertGroup(String fileURI, Vertex[] vertGroup) throws IOException{
		FileWriter data = new FileWriter(fileURI);
		
		int count = 0;
	    while(vertGroup[count + 1] != null){
	    	data.write("vG " + Double.toString(vertGroup[count + 1].getX()) 
	    			+ " "+ Double.toString(vertGroup[count + 1].getY())
	    			+ " " + Double.toString(vertGroup[count + 1].getZ())
	    		    );
	    	data.write(" e:");
	    	for(int i = 0; i < vertGroup[count + 1].getAdjEdgeNumber(); i ++){
	    		data.write(Integer.toString(vertGroup[count + 1].getAdjEdge()[i]) + " ");
	    	}
	    	data.write("f:");
	    	for(int i = 0; i < vertGroup[count + 1].getAdjFaceNumber(); i ++){
	    		data.write(Integer.toString(vertGroup[count + 1].getAdjFace()[i]) + " ");
	    	}
	    	data.write("\n");
	        count ++;
	    }
	    data.close();
	}
	
	//用面点，边点，角点计算下一组初始vertex Group
	public Vertex[] calVertGroup(int vertGroupNumber, int FPCount, int EPCount, int VPCount, Vertex[] faceVertex, Vertex[] edgeVertex, Vertex[] vertVertex){
		Vertex[] vertGroup = new Vertex[vertGroupNumber];
		//加入face points中的点
		for(int i = 0; i < FPCount; i ++){
			vertGroup[i + 1] = new Vertex(faceVertex[i + 1].getX(), faceVertex[i + 1].getY(), faceVertex[i + 1].getZ());
			vertGroup[i + 1].setOrder(i + 1);
		}
		//加入edge points中的点
		for(int i = 0; i < EPCount; i ++){
			vertGroup[FPCount + i + 1] = new Vertex(edgeVertex[i + 1].getX(), edgeVertex[i + 1].getY(), edgeVertex[i + 1].getZ());
			vertGroup[FPCount + i + 1].setOrder(FPCount + i + 1);
		}
		//加入vertex points中的点
		for(int i = 0; i < VPCount; i ++){
			vertGroup[FPCount + EPCount + i + 1] = new Vertex(vertVertex[i + 1].getX(), vertVertex[i + 1].getY(), vertVertex[i + 1].getZ());
			vertGroup[FPCount + EPCount + i + 1].setOrder(FPCount + EPCount + i + 1);
		}
		return vertGroup;
	}
	
	//用CCFace和vertGroup计算下一组初始face Group
	public Face[] calFaceGroup(int faceGroupNumber, int CCFaceNumber, Face[] CCFace, Vertex[] vertGroup){
		Face[] faceGroup = new Face[faceGroupNumber];
		for(int i = 0; i < CCFaceNumber; i ++){
			faceGroup[i + 1] = new Face();
			faceGroup[i + 1].setOrder(i + 1);
			int[] vert = CCFace[i + 1].getVert();
			for(int j = 0; j < CCFace[i + 1].getVertNumber(); j ++){
				faceGroup[i + 1].addVert(vert[j]);
				vertGroup[vert[j]].addAdjFace(i + 1);
			}
		}
		return faceGroup;
	}
	
	//用faceGroup和vertGroup计算下一组初始edge Group
	public Edge[] calEdgeGroup(int edgeGroupNumber, int CCEdgeNumber, Face[] faceGroup, Vertex[] vertGroup){
		Edge[] edgeGroup = new Edge[edgeGroupNumber];
		int i = 0;  //faceGroup的计数指标.
		int edgecount = 0; //edgeGroup的计数指标.
		while(faceGroup[i + 1] != null){
			int repeat = 0;
			int[] polygon = faceGroup[i + 1].getVert();
			int vertNumber = faceGroup[i + 1].getVertNumber();
			for(int j = 0; j < vertNumber; j ++){
			    edgeGroup[edgecount + 1] = new Edge();
			    edgeGroup[edgecount + 1].setOrder(edgecount + 1);
			    if(polygon[j] <= polygon[(j + 1) % vertNumber]){
			        edgeGroup[edgecount + 1].setVert1(polygon[j]);
			        edgeGroup[edgecount + 1].setVert2(polygon[(j + 1) % vertNumber]);
			    }else{
			        edgeGroup[edgecount + 1].setVert2(polygon[j]);
			        edgeGroup[edgecount + 1].setVert1(polygon[(j + 1) % vertNumber]);
			    }
			    if((repeat = RunCC.isOverlap(edgeGroup, edgeGroup[edgecount + 1])) == 0){
			        edgeGroup[edgecount + 1].addAdjFace(i + 1);
			        vertGroup[polygon[j]].addAdjEdge(edgecount + 1);
			        vertGroup[polygon[(j + 1) % vertNumber]].addAdjEdge(edgecount + 1);
			        faceGroup[i + 1].addEdge(edgecount + 1);
				    edgecount ++;
			    }else{
			    	edgeGroup[repeat].addAdjFace(i + 1);
			    	edgeGroup[edgecount + 1] = null;
			    	faceGroup[i + 1].addEdge(repeat);
			    }
			}
		    i ++;
		}
		return edgeGroup;
	}
	
	
	public static void main(String args[]) throws IOException{
	    File file = new File("C:/Users/lvqian/Documents/maya/projects/default/scenes/QuanQuan/QuanQuan.obj");
		RunCC runCC = new RunCC(file);
	    
        runCC.run();
        runCC.run();
	    runCC.getVertGroup();
	}
	


	
    public void setFaceVertex(Vertex[] faceVertex){
    	this.faceVertex = faceVertex;
    }
    
    public Vertex[] getFaceVertex(){
    	return this.faceVertex;
    }
    
    public void setEdgeVertex(Vertex[] edgeVertex){
    	this.edgeVertex = edgeVertex;
    }
    
    public Vertex[] getEdgeVertex(){
    	return this.edgeVertex;
    }
    
    public void setVertVertex(Vertex[] vertVertex){
    	this.vertVertex = vertVertex;
    }
    
    public Vertex[] getVertVertex(){
    	return this.vertVertex;
    }
    
    public void setCCEdge(Edge[] CCEdge){
    	this.CCEdge = CCEdge;
    }
    
    public Edge[] getCCEdge(){
    	return this.CCEdge;
    }
    
    public void setCCFace(Face[] CCFace){
    	this.CCFace = CCFace;
    }
    
    public Face[] getCCFace(){
    	return this.CCFace;
    }
    
    public void setCCFaceCount(int CCFaceCount){
        this.CCFaceCount = CCFaceCount;
    }
    
    public int getCCFaceCount(){
    	return this.CCFaceCount;
    }
    
    
    public void setFPCount(int FPCount){
    	this.FPCount = FPCount;
    }
    
    public int getFPCount(){
    	return this.FPCount;
    }
    
    public void setEPCount(int EPCount){
    	this.EPCount = EPCount;
    }
    
    public int getEPCount(){
        return this.EPCount;	
    }
    
    
    public void setVPCount(int VPCount){
    	this.VPCount = VPCount;
    }
    
    public int getVPCount(){
    	return this.VPCount;
    }
   
    public void setCCEdgeCount(int CCEdgeCount){
    	this.CCEdgeCount = CCEdgeCount;
    }
    
    public int getCCEdgeCount(){
    	return this.CCEdgeCount;
    }
    
    //set在构造函数中
    public Face[] getFaceGroup(){
    	return this.faceGroup;
    }
    //set在构造函数中
    public Vertex[] getVertGroup(){
    	return this.vertGroup;
    }
    
	public int getVertNumber(){
		return this.vertNumber;
	}
	
	public int getfGNumber(){
		return this.fGNumber;
	}
	
	public int getvGNumber(){
		return this.vGNumber;
	}
}