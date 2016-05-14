package DataStructure;



public class Vertex{
	 private double  x,y,z;
	 private int order;
     private int[] adj_edge = new int[40];        //µãÏàÁÚµÄ±ßĞòºÅ
     private int[] adj_face = new int[20];
     private int adjEdgeNumber = 0;
     private int adjFaceNumber = 0;
     
     
     public Vertex(double x, double y, double z){
    	 this.x = x;
    	 this.y = y;
    	 this.z = z;
     }     
     
     public void setXYZ(double x, double y, double z){
    	 this.x = x;
    	 this.y = y;
    	 this.z = z;
     }
     
     public double getX(){
    	 return this.x;
     }
     public double getY(){
    	 return this.y;
     }
     public double getZ(){
    	 return this.z;
     }
     
     public void setAdjEdge(int[] edge){
    	 this.adj_edge = edge;
     }
     
     public void addAdjEdge(int edge) {
    	 if(this.adjEdgeNumber <= 40){
    	     this.adj_edge[this.adjEdgeNumber] = edge;
    	     this.adjEdgeNumber ++;
    	 }else{
    		 System.out.println("Overflow(Vertex:adj_edge)!");
    	 }
     }
     
     public int[] getAdjEdge(){
    	 return this.adj_edge;
     }
     
     public void setAdjFace(int[] face){
    	 this.adj_face = face;
     }
     
     public void addAdjFace(int face){
    	 if(this.adj_face.length <= 20){
    		 this.adj_face[this.adjFaceNumber] = face;
    		 this.adjFaceNumber ++;
    	 }else{
    		 System.out.println("OVerflow(Vertex:adj_face)!");
    	 }
     }
     
     public int[] getAdjFace(){
    	 return this.adj_face;
     }
     
     public void setOrder(int order){
    	 this.order = order;
     }
     
     public int getOrder(){
    	 return this.order;
     }
     
     public int getAdjEdgeNumber(){
    	 return this.adjEdgeNumber;
     }
     
     public int getAdjFaceNumber(){
    	 return this.adjFaceNumber;
     }
}