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
    //�������ݣ��㣬�ߣ��棩
	private int vertNumber = 0;  // ��ʼface��vertex�����Ƚϴ����һ�� 
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
	
	
    //����obj�ļ��õ����������
	public RunCC(File file) throws IOException{
		BufferedReader f = new BufferedReader(new FileReader(file));
		BufferedReader tmp = new BufferedReader(new FileReader(file));
		
		@SuppressWarnings("unused")
		StringTokenizer st = new StringTokenizer(f.readLine(), "\n");
		
		//Initialize���ָ�ÿһ�����ݣ�
	    String[] check = new String[10];

	    //ͨ�õ�һ����������
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
	    
		//��ȡv����
	    f.readLine();
	    check[0] = " ";
        while(!check[0].equals("vt")){
        	StringTokenizer st1 = new StringTokenizer(f.readLine(), "\n");
        	if(st1.hasMoreTokens()){
        		String temp = st1.nextToken();
        		check = temp.split(" ");
        	}
        	//���������(v)����
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
        
        
        //ȥ��vt���ݣ�����Ҫ
        while(!check[0].equals("g")){
        	StringTokenizer st2 = new StringTokenizer(f.readLine(), "\n");
        	if(st2.hasMoreTokens()){
        		String temp = st2.nextToken();
        		check = temp.split(" ");
        	}
        }
        
        this.vGNumber = i;
        //��ȡf����
		String buf;
		i = 0;
		while(null != (buf = f.readLine())){
            StringTokenizer st3 = new StringTokenizer(buf, "\n");
            
            if(st3.hasMoreTokens()){
        		String temp = st3.nextToken();
        		check = temp.split(" ");
        		//System.out.println(temp);
        		//System.out.println(check[0])��
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
		
		//��f���ݺ�v���ݼ����edge����
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

	//ʹ����������ʽ�������
	public Vertex[] calFaceVertex(int faceVertNumber, Face[] faceGroup, Vertex[] vertGroup){
		//����Catmull-Clarkϸ����������
				//���ȼ���Face Points
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
	
	//ʹ����������ʽ����ߵ�
	public Vertex[] calEdgeVertex(int edgeVertNumber, Edge[] edgeGroup, Vertex[] vertGroup, Vertex[] faceVertex){
		//���ż���Edge Points
		        Vertex[] edgeVertex = new Vertex[edgeVertNumber];
				int epCount = 0;  //Edge points�ļ�����
				
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
	
	//ʹ����������ʽ����ǵ�
    public Vertex[] calVertVertex(int vertVertNumber, Vertex[] vertGroup, Vertex[] faceVertex, Edge[] edgeGroup){
    	//������Vertex Points
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
    				
    				//����Vertex Points
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
    
    
    
  //�õ��µĶ���󣬱�����β�����
    //1��ÿ���涥�㣨Face Point��VF���Χ���ı߶�Ӧ�ı߶���(Edge Point)VE������
    //2��ÿ�����������õ����¶��㣨new vertex point��v���������ڵı��ϵĵ㣨edge point��VE������
	
    public Edge[] calCCEdge(int CCEdgeNumber, int FPCount, int EPCount, Vertex[] faceVertex, Face[] faceGroup, Vertex[] vertVertex, Vertex[] vertGroup){
    	Edge[] CCEdge = new Edge[CCEdgeNumber];
    	int ccEdgeCount = 0;
    	int[] adjEdge;
    	int fpcount = 0;     //face points������ֵ
    	//����face points�Ͱ�Χ���ߵı߶���
    	while(faceVertex[fpcount + 1] != null){
    		adjEdge = faceGroup[fpcount + 1].getEdge();
    		int adjEdgeNumber = faceGroup[fpcount + 1].getEdgeNumber();
    		for(int i = 0; i < adjEdgeNumber; i ++){
    			CCEdge[ccEdgeCount + 1] = new Edge();
    				
    			CCEdge[ccEdgeCount + 1].setVert1(fpcount + 1);   //faceVertex�еĵ㣬��Χ��1 ~ FPCount;
    			CCEdge[ccEdgeCount + 1].setVert2(FPCount + adjEdge[i]); //edgeVertex�еĵ㣬��Χ��FPCount+1  ~ FPCount+EPCount;
    			
    			CCEdge[ccEdgeCount + 1].setOrder(ccEdgeCount + 1);
    			ccEdgeCount ++;
    		}
    		fpcount ++;
    	}
    	
    	//����vertex points�����ڱ��ϵ�edge points��
    	fpcount = 0; //vertex points�ļ�����ֵ
    	while(vertVertex[fpcount + 1] != null){
    		adjEdge = vertGroup[fpcount + 1].getAdjEdge();
    		int adjEdgeNumber = vertGroup[fpcount + 1].getAdjEdgeNumber();
    		for(int i = 0; i < adjEdgeNumber; i ++){
    			CCEdge[ccEdgeCount + 1] = new Edge();
    			
    			CCEdge[ccEdgeCount + 1].setVert1(FPCount + adjEdge[i]);   //edgeVertex�еĵ㣬��Χ��FPCount+1  ~ FPCount+EPCount;
    			CCEdge[ccEdgeCount + 1].setVert2(FPCount + EPCount + fpcount + 1);   //vertVertex�еĵ㣬��Χ��FPCount+EPCount+1 ~ FPCount+EPCount+VPCount
    		    
    			CCEdge[ccEdgeCount + 1].setOrder(ccEdgeCount + 1);
    			ccEdgeCount ++;
    			
    		}
    		fpcount ++;
    	}

        	this.setCCEdgeCount(ccEdgeCount);
        
    	return CCEdge;
    	
    }
    
    //��faceVetex��faceGroup��edgeGroup�����CCFace
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
                  
    		      int VP1 = this.searchOL(adjEdge[i], adjEdge[(i + 1) % adjEdgeNumber], edgeGroup); //���Ҹ�������Ӧ��vertex point
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
    
    //ִ��һ��ϸ��
    public RunCC run(){
    	//����㣬�ߵ㣬�ǵ㡣
    	if(this.EPCount == 0){    		
    	    System.out.println("First Round.");
    	}else{
    		this.vertNumber = this.vertNumber * 4;
    		//������һ�ֵ�vertGroup��
    		this.vertGroup = this.calVertGroup(this.vertNumber, this.FPCount, this.EPCount, this.VPCount, this.faceVertex, this.edgeVertex, this.vertVertex);
    		this.vGNumber = this.FPCount + this.EPCount + this.VPCount;
    		//������һ�ֵ�faceGroup��
    		this.faceGroup = this.calFaceGroup(this.vertNumber, this.CCFaceCount, this.CCFace, this.vertGroup);
    		this.fGNumber = this.CCFaceCount;
    		//������һ�ֵ�edgeGroup��
    		this.edgeGroup = this.calEdgeGroup(2 * this.vertNumber, this.CCEdgeCount, this.faceGroup, this.vertGroup);
    	}
    	
    	
    	this.faceVertex = this.calFaceVertex(vertNumber, this.faceGroup, this.vertGroup);
        this.edgeVertex = this.calEdgeVertex(2 * vertNumber, this.edgeGroup, this.vertGroup, this.faceVertex);
        this.vertVertex = this.calVertVertex(vertNumber, this.vertGroup, this.faceVertex, edgeGroup);
        //��CCEdge��CCFace��
        this.CCEdge = this.calCCEdge(8 * vertNumber, this.FPCount, this.EPCount, this.faceVertex, this.faceGroup, this.vertVertex, this.vertGroup);
        this.CCFace = this.calCCFace(4 * vertNumber, this.FPCount, this.EPCount, this.faceVertex, this.faceGroup, this.edgeGroup);
        
        return this;
    }
    
    //��֤�¼���EdgeGroup��Edge�Ƿ����ظ���
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
	
	//�ҵ�����Edge�Ľ���
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
	
	//���һ��Edge
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
	
	//���һ��Face
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
	
	//���һ��Vertex
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
	
	//����㣬�ߵ㣬�ǵ������һ���ʼvertex Group
	public Vertex[] calVertGroup(int vertGroupNumber, int FPCount, int EPCount, int VPCount, Vertex[] faceVertex, Vertex[] edgeVertex, Vertex[] vertVertex){
		Vertex[] vertGroup = new Vertex[vertGroupNumber];
		//����face points�еĵ�
		for(int i = 0; i < FPCount; i ++){
			vertGroup[i + 1] = new Vertex(faceVertex[i + 1].getX(), faceVertex[i + 1].getY(), faceVertex[i + 1].getZ());
			vertGroup[i + 1].setOrder(i + 1);
		}
		//����edge points�еĵ�
		for(int i = 0; i < EPCount; i ++){
			vertGroup[FPCount + i + 1] = new Vertex(edgeVertex[i + 1].getX(), edgeVertex[i + 1].getY(), edgeVertex[i + 1].getZ());
			vertGroup[FPCount + i + 1].setOrder(FPCount + i + 1);
		}
		//����vertex points�еĵ�
		for(int i = 0; i < VPCount; i ++){
			vertGroup[FPCount + EPCount + i + 1] = new Vertex(vertVertex[i + 1].getX(), vertVertex[i + 1].getY(), vertVertex[i + 1].getZ());
			vertGroup[FPCount + EPCount + i + 1].setOrder(FPCount + EPCount + i + 1);
		}
		return vertGroup;
	}
	
	//��CCFace��vertGroup������һ���ʼface Group
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
	
	//��faceGroup��vertGroup������һ���ʼedge Group
	public Edge[] calEdgeGroup(int edgeGroupNumber, int CCEdgeNumber, Face[] faceGroup, Vertex[] vertGroup){
		Edge[] edgeGroup = new Edge[edgeGroupNumber];
		int i = 0;  //faceGroup�ļ���ָ��.
		int edgecount = 0; //edgeGroup�ļ���ָ��.
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
    
    //set�ڹ��캯����
    public Face[] getFaceGroup(){
    	return this.faceGroup;
    }
    //set�ڹ��캯����
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