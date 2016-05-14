package DataStructure;



public class Face{
     private int order ;
     private int[] vert = new int[10];
     private int[] edge = new int[20]; 
     private int vertNumber;
     private int edgeNumber;


	public void setVert(int[] vert){
    	 this.vert = vert;
     }
     
     public int[] getVert(){
    	 return this.vert;
     }
     
     public void setEdge(int[] edge){
    	 this.edge = edge;
     }
     
 	public void addEdge(int edge){
		this.edge[this.edgeNumber] = edge;
		this.edgeNumber ++;
	}
     
     public int[] getEdge(){
    	 return this.edge;
     }
     
     public void setOrder(int order){
    	 this.order = order;
     }
     
     public int getOrder(){
    	 return this.order;
     }

	public void addVert(int ord) {
		// TODO Auto-generated method stub
		this.vert[this.vertNumber] = ord;
		vertNumber ++;
	}
	
	public int getVertNumber(){
		return this.vertNumber;
	}
	
	public int getEdgeNumber(){
		return this.edgeNumber;
	}
}

