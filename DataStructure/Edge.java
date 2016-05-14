package DataStructure;



public class Edge{
     private int order ;
     private int vert1 = 0, vert2 = 0;
     private int[] adjFace = new int[2];
     private int adjFaceNumber = 0;
     private int[] faceVec = new int[2];  //向量方向，0代表顺序v1->v2，1代表逆序v2->v1;
     private int faceVecNumber = 0;
     

     public void setVert1(int vert1){
    	 this.vert1 = vert1;
     }
     
     public int getVert1(){
    	 return this.vert1;
     }
     
     public void setVert2(int vert2){
    	 this.vert2 = vert2;
     }
     
     public int getVert2(){
    	 return this.vert2;
     }
     
     public void addFaceVec(int i){
    	 this.faceVec[this.faceVecNumber] = i;
    	 this.faceVecNumber ++;
     }
     
     public int[] getFaceVec(){
         return this.faceVec;
     }
     
     public void setAdjFace(int[] face){
    	 this.adjFace = face;
     }
     
     public void addAdjFace(int face){
    	 this.adjFace[this.adjFaceNumber] = face;
    	 this.adjFaceNumber ++;
     }
     
     public int[] getAdjFace(){
    	 return this.adjFace;
     }
     
     public void setOrder(int order){
    	 this.order = order;
     }
     
     public int getOrder(){
    	 return this.order;
     }  
}