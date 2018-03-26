import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 * 
 * @author Gilad Fudim 311245450
 * @author Guy Mozes 204589725
 */
public class FibonacciHeap
{
	public int markednodes=0;
	public int size=0;//num of nodes in the heap
	public int trees=0;//num of trees in the heap
	public HeapNode minnode;//minimum node of the heap
	public static int TotalLinks=0;
	public static int TotalCuts=0;
	public FibonacciHeap(){
		this.minnode=null;
	}
	/**
	 * public boolean empty()
	 * @complexity O(1).
	 * @return true if and only if the heap is empty.
	 * @pre none.
	 * @post none.
	 */
    public boolean empty(){
    	return(this.size==0);
    }
	/** public void clear()
	 * precondition: none
	 * The method clears the Fibonacci heap
	 * @complexity O(1).
	 * @pre none.
	 * @post empty heap.
	 */
	public void clear(){
	        this.minnode= null;
	        this.size = 0;
	        this.trees = 0;
	        this.markednodes=0;
	}
    /**
     * public HeapNode insert(int key)
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
     * @complexity O(1) - add new node as a tree regardless of heap size.
     * @pre key>=0.
     * @post the heap now contain a new node with this key.
     * @return the new heap node created.
     */
    public HeapNode insert(int key){//Change potential +1 
    	HeapNode newnode =new HeapNode(key);
    	if(this.empty()){
    		this.minnode=newnode;
    	}
    	else{
    		mergeNodes(newnode,this.minnode);   		
    		if(newnode.key<this.minnode.key){
    			this.minnode=newnode;
    		}
    	}
    	this.size++;
    	this.trees++;
    	return newnode;
    }
    /**
     * public void mergeNodes(HeapNode x,HeapNode y)
     * @complexity O(1)
     * @pre x and y are Nodes at the heap
     * @post x has a pointer to y and y has to x
     */
    public void mergeNodes(HeapNode x,HeapNode y){//adds the node x to the nodelist of y
    	x.left=y;
		x.right=y.right;
		y.right=x;
		x.right.left=x; 
    }
    /**
     * public void passOverNode(HeapNode x)
     * @complexity O(1)
     * @pre x is a Node at the heap
     * @post both right and left Nodes from x will be pointing to each other 
     */
    public void passOverNode(HeapNode x){//passes over the node x
		x.left.right=x.right;
		x.right.left=x.left;
    }
    /**
     * public void successiveLinking(HeapNode tempmin)
     * Combine every tree of the same size.
     * @dependencies meldList - O(log(n)).
     * @complexity O(n) - worst case go over all 0-rank trees in the heap.
     * @post the heap now doesn't contain 2 tree of the same rank.
     */  
   public void successiveLinking(HeapNode tempmin){
	   HeapNode[] arr=new HeapNode[this.size];
	   HeapNode[] nodes=new HeapNode[this.trees];
	   boolean inserted=false;
	   HeapNode index =tempmin;
	   HeapNode last = tempmin.left;
	   boolean coveredall=false;
	   int count=0;
	   while(!coveredall){
		   if(index==last){
			   coveredall=true;
		   }
		   nodes[count]= index;
		   count++;
		   index=index.right;
	   }
		for(HeapNode current:nodes){
		   while(!inserted){
			   if(arr[current.rank]==null){
				   arr[current.rank]= current;
				   inserted =true;
			   }
			   else{
				   HeapNode samerank = arr[current.rank];
				   arr[current.rank]=null;
				   HeapNode comb = link(current,samerank);
				   current = comb;
			   } 
		   }
		   inserted=false;   
	   }
	   List<HeapNode> lst=new ArrayList<>();
	   for(int i=0;i<this.size;i++){
		   if(arr[i]!=null){
			   lst.add(arr[i]);
		   }
	   }
	   meldList(lst);
   	}
   /**
	* public void meldList(List<HeapNode> lst)
	* Insert the List to the heap.
	* @complexity O(log(n)) - go over the array and connect each item to a double linked list.
	* @param - lst is a List of HeapNode.
	* @pre lst != empty or null.
	* @post heap contain all the tree that are in the array.
	*/
   public void meldList(List<HeapNode> lst){
	   Iterator<HeapNode> it = lst.iterator();
	   HeapNode first = it.next();
	   first.right=first;
	   first.left=first;
	   HeapNode newmin = first;
	   while(it.hasNext()){
		   HeapNode current = it.next();
		   if(current.key<newmin.key){
			   newmin=current;
		   }
		   mergeNodes(current,first);
	   }
	   this.minnode=newmin;
   }
   /**
	* public void link(HeapNode x, HeapNode y)
	* Adds a new child for a HeapNode object.
	* @param  x - the new child of y.
	* @complexity O(1) 
	* @pre x and y are HeapNodes
	* @post the new child is added now to the father node.
	*/
   	public HeapNode link(HeapNode x, HeapNode y){
   		TotalLinks++;
   		HeapNode tempnode=null;
   		if(x.key<y.key){
   			tempnode=x;
   			x=y;
   			y=tempnode;
   		}
   		x.parent=y;
   		if(y.child==null){//first child of y
   			x.right=x;
   			x.left=x;
   			y.child=x;
   		}
   		else{//not the first child of y
   			HeapNode ychild=y.child;
   			mergeNodes(x,ychild);
   		}
   		if(x.mark==true){
   			x.mark=false;
   			this.markednodes--;
   		}
   		y.rank++;
   		this.trees--;
   		return y;
   	}
    /**
     * public HeapNode findMin()
     * @return the node of the heap whose key is minimal. 
     * @complexity O(1)
     * @pre - min exists 
     */
    public HeapNode findMin(){
    	return this.minnode;
    } 
    /**
     * public void meld (FibonacciHeap heap2)
     * Meld the heap with heap2
     * @complexity O(1) - connect the ends of two double link lists.
     * @pre none.
     * @post the two heaps are now combined.
     */
    public void meld (FibonacciHeap heap2){
    	if(heap2==null||heap2.empty()){
    		System.out.println("Heyyyyy! thats an empty heap why you little..!");
    	}
    	else if(this.empty()){
    		this.minnode = heap2.minnode;
    		this.size = heap2.size;
    		this.trees = heap2.trees;
    		this.markednodes = heap2.markednodes;
    	}
    	else{
    		HeapNode min1=this.minnode;
        	HeapNode min1right=this.minnode.right;
        	HeapNode min2=heap2.minnode;
        	HeapNode min2left=min2.left;
        	min1.right=min2;
        	min2.left=min1;
        	min1right.left=min2left;
        	min2left.right=min1right;
        	if(min1.key>min2.key){
    			this.minnode=min2;
    		}
        	this.size+=heap2.size;
        	this.trees+=heap2.trees;
        	this.markednodes+=heap2.markednodes;
    	}
    }
    /**
     * public int size()
     * Return the number of elements in the heap
     * @complexity O(1) - return a value which updates regularly.
     * @return number of nodes in tree.
     */
    public int size()
    {
    	return this.size;
    }
    /**
    * public int[] countersRep()
    * @return a counters array, where the value of the i-th entry is the number of trees of order i in the heap.
    * @complexity O(n) - worst case run over all 0-rank tree nodes in the heap.
    */
    public int[] countersRep() {
        if (this.size == 0) {
            return new int[] {0};
        }
        int arraySize = (int) (Math.floor(Math.log(this.size) / Math.log(2)) + 1);
        int[] arr = new int[arraySize];
        HeapNode index = this.minnode;
        for (int i = 0; i < this.trees; i++) {
            arr[index.rank] += 1;
            index = index.right;
        }
        return arr;
    }
    /**
     * public void deleteMin()
     * Delete the node containing the minimum key.
     * @dependencies - successiveLinking - O(n), moveChildToRoot - O(log(n))
     * @complexity - Amortized O(log(n))
     * @post - tree is valid and doesn't contain the previous minNode
     */
    public void deleteMin(){
    	if(!empty()){
    		HeapNode x = this.minnode;
    		passOverNode(x);
    		this.size--;
    		this.trees--;
			if(x==x.right){//one tree
				if(x.child!=null){//with children
					minnode=null;
					moveChildToRoot(x);//moves children to Root list
					successiveLinking(minnode);
				}
				else{//last node
					clear();
				}
			}
			else{//more trees
				minnode=x.right;
				if(x.child!=null){//with children	
					moveChildToRoot(x);//moves children to Root list
				}
				successiveLinking(minnode);
			}
    	}
    }
    /**
     * public void delete(HeapNode x)
     * Deletes the node x from the heap.
     * @param - x is a HeapNode.
     * @complexity - Amortized O(log(n))
	 * @dependencies decreaseKey - O(log(n)), deleteMin-  O(n)
     * @post x doesn't exist in the heap anymore and the heap is valid.
     */
    public void delete(HeapNode x){
    	if(x==null){
    	}
    	else{
    		decreaseKey(x,Integer.MAX_VALUE);
    		deleteMin();
    	}
    }
	/**
	 * public void moveChildToRoot(HeapNode x)
	 * @dependencies moveToRoot - O(1), moveChildToRoot - O(1),passOverNode - O(1)
	 * @pre node paren has to be a Heap-node
	 * @complexity O(log(n))
	 * @post The method moves the children from their father to the heap list
	 */
    public void moveChildToRoot(HeapNode paren){
	   HeapNode chil=paren.child;
	   paren.rank--;
	   if(chil.right==chil){//one child
		   moveToRoot(chil);
		   paren.child=null;
	   }
	   else{//more then one child
		   	paren.child=chil.right;
		   	passOverNode(chil);
   			moveToRoot(chil);
   			moveChildToRoot(paren);
	   }
   }
    /**
     * public void moveToRoot(HeapNode node)
     *the function moves the node to the heap list
     * @pre node has to be a Heap-node
     * @dependencies mergeNodes - O(1) 
     * @complexity O(1)
     * @post The method adds a new sub-tree from the heap to the heap list
     */
    public void moveToRoot(HeapNode node){
    	node.parent=null;
    	if(node.mark){
	    	this.markednodes--;
    	}
    	node.mark=false;
    	if(this.minnode==null){
			this.minnode=node;
			node.left=node;
			node.right=node;
		}
		else{
			mergeNodes(node,this.minnode);
		}
    	if(node.key<this.minnode.key){
    		this.minnode=node;
    	}
		this.trees++;
    }
    /**
     * public void decreaseKey(HeapNode x, int delta)
     * The function decreases the key of the node x by delta. The structure of the heap should be updated
     * @dependencies cascadingCut - O(log(n)) 
     * @complexity O(log(n))
     * @pre HeapNode x exists.
     * @post the heap is now updated according to the new key.
     */
    public void decreaseKey(HeapNode x, int delta){
	    	if(delta<0){
	    		System.out.println("We thought you wanted to decrease the key, not increase...");
	    	}
	    	else{
	    		x.key-=delta;
	    		if(x.parent!=null){
		    		if(x.key<x.parent.key){
		    			cascadingCut(x,x.parent);
		    		}
	    		}
	    		if(x.key<this.minnode.key){
	    			this.minnode=x;
	    		}
	    	}
    	}
    /**
     * public void cut(HeapNode node)
     * The function cuts the node node from its location and moves him to the heap list.
     * @dependencies moveToRoot - O(1).
     * @complexity O(1) 
     * @pre Nodes x and y exist in the heap
     * @post the node is no longer linked to its parent and moved to the heap list
     */
    public void cut(HeapNode x,HeapNode y){
    	TotalCuts++;
    	y.rank-=1;
    	if(x.right==x){
    		y.child=null;
    	}
    	else{
    		passOverNode(x);
    		y.child=x.right;
    	}
    	moveToRoot(x);
    }
    /**
     * public void cascadingCut(HeapNode node)
     * The function performs a cascading cut.
     * @dependencies cut - O(1) , cascadingCut - O(log(n)) 
     * @complexity - O(log(n))
     * @pre HeapNode node exists
     * @post cascading cut was performed, if needed.
     */
    public void cascadingCut(HeapNode x,HeapNode y){//x is child of y
    	cut(x,y);
    	if(y.parent!= null){
    		if(!y.mark){
    			y.mark=true;
    			this.markednodes++;
    		}
    		else{
    			cascadingCut(y,y.parent);
    		}
    	}
    }
    /**
     * public int potential() 
     * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
     * @returns the current potential of the heap, which is: * Potential = #trees + 2*#marked
     * @complexity O(1) - use value which are regularly being updated.
     * @pre none.
     */
    public int potential() 
    {    
    	return this.trees +2*this.markednodes;//as defined in class :) 
    }
    /**
     * public static int totalLinks() 
     * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
     * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
     * in its root.
     * @returns the total number of link operations made during the run-time of the program.
     * @complexity O(1) - use value which are regularly being updated.
     * @pre none.
     */
    public static int totalLinks()
    {    
    	return TotalLinks;
    }
    /**
     * public static int totalCuts() 
     * A cut operation is the operation which disconnects a subtree from its parent (during decreaseKey/delete methods). 
     * @returns the total number of cut operations made during the run-time of the program.
     * @complexity O(1) - use value which are regularly being updated.
     * @pre none.
     */
    public static int totalCuts()
    {    
    	return TotalCuts;
    }
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{
    	public int key;
    	public boolean mark;
    	public int rank;
    	public HeapNode child;
    	public HeapNode right;
    	public HeapNode left;
    	public HeapNode parent;
 	   /**
 	    * public HeapNode(int k) 
 	    * Constructor for a HeapNode object.
 	    * @param key - key of the node.
 	    * @complexity O(1) 
 	    * @pre none.
 	    */
    	public HeapNode(int key){
    		this.key=key;
    		this.mark=false;
    		this.rank=0;
    		this.child=null;
    		this.parent=null;
    		this.right=this;
    		this.left=this;
    	}
    	public int getKey(){
    		return this.key;
    	}
    }
}